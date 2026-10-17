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
            Log.w(TAG, "SyncWorker skipped: user UUID is not configured")
            return@withContext Result.failure()
        }

        val configuredUrl = preferencesRepository.getServerUrl()
        val rawBase = if (!configuredUrl.isNullOrBlank()) configuredUrl else StateApiService.DEFAULT_BASE_URL
        val fullUrl = DynamicStateApiService.buildStateUrl(rawBase)

        try {
            Log.i(TAG, "GET $fullUrl starting (token=${uuid.take(4)}…)")
            val response = apiService.getState("Bearer $uuid")
            if (!response.isSuccessful) {
                when (response.code()) {
                    404 -> {
                        Log.w(TAG, "No state on server yet (404)")
                        preferencesRepository.saveLastSyncStatus("Connected, no state for this token yet")
                        return@withContext Result.success()
                    }
                    401 -> {
                        val errorBody = response.errorBody()?.string()
                        Log.e(TAG, "API error fetching state: 401 ${response.message()}: $errorBody")
                        preferencesRepository.saveLastSyncStatus("Unauthorized (token rejected)")
                        return@withContext Result.failure()
                    }
                    else -> {
                        val errorBody = response.errorBody()?.string()
                        Log.e(TAG, "API error fetching state: ${response.code()} ${response.message()}: $errorBody")
                        preferencesRepository.saveLastSyncStatus("HTTP ${response.code()}")
                        return@withContext if (response.code() in 500..599) Result.retry() else Result.failure()
                    }
                }
            }

            val stateResponse = response.body()
            if (stateResponse == null) {
                Log.e(TAG, "Empty response body from /state")
                preferencesRepository.saveLastSyncStatus("HTTP ${response.code()}")
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

            val invCount = data?.inventory?.size ?: 0
            val bankCount = data?.bank?.size ?: 0
            val slayerTaskName = data?.slayer?.taskName?.takeIf { it.isNotBlank() } ?: "none"
            val timeStr = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
            preferencesRepository.saveLastSyncStatus("OK $timeStr inv=$invCount bank=$bankCount slayer=$slayerTaskName")
            Log.i(TAG, "Synced: inventory=$invCount bank=$bankCount varbits=${data?.varbits?.size ?: 0} slayer=$slayerTaskName")
            Log.i(TAG, "Successfully synced player state for UUID: $uuid")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Exception during player state sync ($fullUrl)", e)
            preferencesRepository.saveLastSyncStatus("${e::class.java.simpleName}: ${e.message}")
            Result.retry()
        }
    }

    companion object {
        const val TAG = "SyncWorker"
        const val KEY_UUID = "key_uuid"
        const val NOTIFICATION_BASE_ID = 1000

        @VisibleForTesting
        var defaultApiService: StateApiService? = null

        @VisibleForTesting
        var defaultDatabase: AppDatabase? = null

        @VisibleForTesting
        var defaultPreferences: UserPreferencesRepository? = null
    }
}
