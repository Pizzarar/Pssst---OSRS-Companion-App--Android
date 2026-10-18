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
import com.pssst.data.SnapshotIngest
import com.pssst.receiver.ReceiverHost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import com.pssst.data.network.model.StateResponse
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.URI
import java.net.UnknownHostException
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

        // Phone mode (no server URL): the RuneLite plugin sends to this phone, so this run is a short listening window.
        // There is no central Pssst server to fall back to, so no URL never means a request.
        val configuredUrl = preferencesRepository.getServerUrl()
        if (configuredUrl.isNullOrBlank()) {
            Log.i(TAG, "Phone mode: listening for RuneLite for ${listenWindowMs / 1000} s")
            listenWindow()
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
                        return@withContext if (code in 500..599) retryOrGiveUp() else Result.failure()
                    }
                }
            }

            val stateResponse = response.body()
            if (stateResponse == null) {
                Log.e(TAG, "Empty response body from /state")
                recordFailure("empty response")
                return@withContext Result.failure()
            }

            SnapshotIngest(appContext, database, preferencesRepository).ingest(uuid, stateResponse)
            Result.success()
        } catch (e: Exception) {
            if (isUnreachable(e)) {
                // Normal whenever the phone is away from the PC or the PC is off: say so quietly, keep the saved data,
                // and let the next trigger or the periodic run try again. No retry, no failure count, no alarm.
                Log.i(TAG, "Not reachable right now: ${e::class.java.simpleName}")
                preferencesRepository.saveLastSyncStatus(STATUS_UNREACHABLE)
                return@withContext Result.success()
            }
            // A dropped connection mid-transfer must never end the schedule: retry with backoff a few times, and the
            // periodic sync carries on regardless
            Log.e(TAG, "Exception during player state sync ($fullUrl)", e)
            recordFailure(e::class.java.simpleName)
            retryOrGiveUp()
        }
    }

    /** Retries with WorkManager's backoff, but only a few times: a broken server must not become a retry storm. */
    private fun retryOrGiveUp(): Result =
        if (runAttemptCount >= MAX_ATTEMPTS - 1) Result.success() else Result.retry()

    private suspend fun listenWindow() {
        ReceiverHost.acquire()
        try {
            delay(listenWindowMs)
        } finally {
            ReceiverHost.release()
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
        const val MAX_ATTEMPTS = 3
        const val STATUS_UNREACHABLE = "Not reachable right now. Showing saved data."
        const val STATUS_OLDER = "Ignored older data from the server"

        /**
         * True when the server simply could not be reached (phone away from home, PC off or asleep, no network):
         * a refused or timed-out connection, no route, or an unresolvable name. A drop mid-transfer is not this.
         */
        fun isUnreachable(error: Throwable): Boolean {
            var cause: Throwable? = error
            var depth = 0
            while (cause != null && depth < 6) {
                if (cause is ConnectException || cause is NoRouteToHostException || cause is UnknownHostException ||
                    cause is SocketTimeoutException ||
                    (cause is SocketException && cause.message?.contains("unreachable", ignoreCase = true) == true)
                ) {
                    return true
                }
                cause = cause.cause
                depth++
            }
            return false
        }

        /** How long a background run listens for RuneLite in phone mode. The plugin retries every 30 s, so 75 s always catches one. */
        @VisibleForTesting
        var listenWindowMs: Long = 75_000L

        @VisibleForTesting
        var defaultApiService: StateApiService? = null

        @VisibleForTesting
        var defaultDatabase: AppDatabase? = null

        @VisibleForTesting
        var defaultPreferences: UserPreferencesRepository? = null
    }
}
