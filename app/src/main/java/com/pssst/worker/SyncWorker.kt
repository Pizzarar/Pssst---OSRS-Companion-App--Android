package com.pssst.worker

import android.content.Context
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.pssst.data.local.AppDatabase
import com.pssst.data.local.DataStoreUserPreferencesRepository
import com.pssst.data.local.ItemManifestEntity
import com.pssst.data.local.PlayerStateEntity
import com.pssst.data.local.UserPreferencesRepository
import com.pssst.data.network.DynamicStateApiService
import com.pssst.data.network.StateApiService
import com.pssst.notification.NotificationHelper
import com.pssst.util.StateDiffAnalyzer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URI
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SyncWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val gson = Gson()

    var database: AppDatabase = defaultDatabase ?: AppDatabase.getDatabase(appContext)
    var preferencesRepository: UserPreferencesRepository =
        defaultPreferences ?: DataStoreUserPreferencesRepository(appContext)
    var apiService: StateApiService = defaultApiService ?: StateApiService.create(preferencesRepository = preferencesRepository)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val uuid = inputData.getString(KEY_UUID) ?: preferencesRepository.getUuid()
        if (uuid.isNullOrBlank()) {
            Log.w(TAG, "SyncWorker skipped: no Sync Token is configured")
            return@withContext Result.failure()
        }

        // Self-hosted by design: there is no central Pssst server to fall back to, so no URL means no request
        val configuredUrl = preferencesRepository.getServerUrl()
        if (configuredUrl.isNullOrBlank()) {
            Log.w(TAG, "SyncWorker skipped: no server URL is configured")
            preferencesRepository.saveLastSyncStatus("No server configured")
            return@withContext Result.success()
        }
        val fullUrl = DynamicStateApiService.buildStateUrl(configuredUrl)

        try {
            Log.i(TAG, "GET $fullUrl starting")
            val response = apiService.getState("Bearer $uuid")
            if (!response.isSuccessful) {
                when (val code = response.code()) {
                    404 -> {
                        Log.w(TAG, "No state on server yet (404)")
                        preferencesRepository.saveSyncFailures(0)
                        preferencesRepository.saveLastSyncStatus("Connected, no state for this token yet")
                        return@withContext Result.success()
                    }
                    401 -> {
                        // A rejected token is a setup problem, not a flaky connection: show it straight away
                        Log.e(TAG, "API error fetching state: 401 ${response.message()}")
                        preferencesRepository.saveLastSyncStatus("Unauthorized (token rejected)")
                        return@withContext Result.failure()
                    }
                    403, 429 -> {
                        // Told to stop: no retry loop. The next scheduled run is the only retry.
                        Log.w(TAG, "Server said stop ($code); skipping until the next scheduled sync")
                        recordFailure("HTTP $code")
                        return@withContext Result.failure()
                    }
                    else -> {
                        Log.e(TAG, "API error fetching state: $code ${response.message()}")
                        recordFailure("HTTP $code")
                        return@withContext if (code in 500..599) Result.retry() else Result.failure()
                    }
                }
            }

            val stateResponse = response.body()
            if (stateResponse == null) {
                Log.e(TAG, "Empty response body from /state")
                recordFailure("empty response")
                return@withContext Result.failure()
            }

            val dao = database.playerStateDao()
            val previousEntity = dao.getStateByUuid(uuid)

            val notificationsEnabled = preferencesRepository.getNotificationsEnabled()

            // Detect completed patches or timers and trigger notifications if enabled
            if (notificationsEnabled) {
                val alerts = StateDiffAnalyzer.findCompletedRunsOrPatches(previousEntity, stateResponse)
                alerts.forEachIndexed { index, alert ->
                    NotificationHelper.showNotification(
                        context = appContext,
                        notificationId = NOTIFICATION_BASE_ID + index,
                        title = alert.title,
                        message = alert.message
                    )
                }
            }

            // Keep timed reminders (growing farming patches, birdhouses) in step with the latest observations
            SyncScheduler.syncReminders(
                appContext,
                stateResponse.data?.farming.orEmpty(),
                stateResponse.data?.birdhouses.orEmpty(),
                notificationsEnabled
            )

            // Insert/update in Room database
            val rawJson = gson.toJson(stateResponse)
            val data = stateResponse.data
            // Sources disagree on units: Cloudflare worker sends seconds, plugin WS + mock send ms
            val updatedAtMs = stateResponse.updatedAt.let { if (it in 1 until 100_000_000_000L) it * 1000 else it }
            val entity = PlayerStateEntity(
                uuid,
                updatedAtMs,
                rawJson,
                gson.toJson(data?.varbits ?: emptyMap<String, Int>()),
                data?.slayer?.let { gson.toJson(it) } ?: "{}",
                gson.toJson(data?.inventory ?: emptyList<Any>()),
                gson.toJson(data?.bank ?: emptyList<Any>()),
                System.currentTimeMillis(),
                gson.toJson(data?.geOffers ?: emptyList<Any>()),
                gson.toJson(data?.skills ?: emptyList<Any>()),
                gson.toJson(data?.potionStorage ?: emptyList<Any>()),
                gson.toJson(data?.quests ?: emptyMap<String, String>())
            )

            dao.upsert(entity)

            // Prices come with the snapshot, from the player's own RuneLite client; nobody else is asked
            val prices = data?.prices.orEmpty().mapNotNull { (id, price) -> id.toIntOrNull()?.let { it to price } }.toMap()
            if (prices.isNotEmpty()) {
                database.itemManifestDao().applyPrices(prices, if (updatedAtMs > 0) updatedAtMs else System.currentTimeMillis())
            }

            val invCount = data?.inventory?.size ?: 0
            val bankCount = data?.bank?.size ?: 0
            val slayerTaskName = data?.slayer?.taskName?.takeIf { it.isNotBlank() } ?: "none"
            val timeStr = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
            preferencesRepository.saveSyncFailures(0)
            preferencesRepository.saveLastSyncStatus("OK $timeStr inv=$invCount bank=$bankCount slayer=$slayerTaskName")
            Log.i(TAG, "Synced: inventory=$invCount bank=$bankCount varbits=${data?.varbits?.size ?: 0} slayer=$slayerTaskName")
            Log.i(TAG, "Successfully synced player state")
            Result.success()
        } catch (e: Exception) {
            // A dropped connection must never end the schedule: WorkManager retries with backoff and the periodic
            // sync carries on regardless
            Log.e(TAG, "Exception during player state sync ($fullUrl)", e)
            recordFailure(e::class.java.simpleName)
            Result.retry()
        }
    }

    /**
     * Counts a failed sync. One or two in a row (flaky Wi-Fi, a server restart) change nothing the user sees; the
     * "unreachable" status appears only after [FAILURES_BEFORE_UNREACHABLE] consecutive failures.
     */
    private suspend fun recordFailure(reason: String) {
        val failures = preferencesRepository.getSyncFailures() + 1
        preferencesRepository.saveSyncFailures(failures)
        if (failures >= FAILURES_BEFORE_UNREACHABLE) {
            preferencesRepository.saveLastSyncStatus("Server unreachable ($failures failed syncs in a row, $reason)")
        }
    }

    companion object {
        const val TAG = "SyncWorker"
        const val KEY_UUID = "key_uuid"
        const val NOTIFICATION_BASE_ID = 1000
        const val FAILURES_BEFORE_UNREACHABLE = 3

        @VisibleForTesting
        var defaultApiService: StateApiService? = null

        @VisibleForTesting
        var defaultDatabase: AppDatabase? = null

        @VisibleForTesting
        var defaultPreferences: UserPreferencesRepository? = null
    }
}
