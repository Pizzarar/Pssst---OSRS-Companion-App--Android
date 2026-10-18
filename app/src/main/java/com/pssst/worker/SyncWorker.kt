package com.pssst.worker

import android.content.Context
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pssst.data.local.DataStoreUserPreferencesRepository
import com.pssst.data.local.UserPreferencesRepository
import com.pssst.receiver.ReceiverHost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * The background half of receiving: the RuneLite plugin sends to this phone, and this job opens a short listening
 * window ([ReceiverHost]) so data the plugin couldn't deliver while the app was closed lands within one run. Runs every
 * 15 minutes (Android decides exactly when), on app open, and when the phone joins a new network. It never makes a
 * request: there is no server to ask.
 */
class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    var preferencesRepository: UserPreferencesRepository =
        defaultPreferences ?: DataStoreUserPreferencesRepository(appContext)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val uuid = inputData.getString(KEY_UUID) ?: preferencesRepository.getUuid()
        if (uuid.isNullOrBlank()) {
            Log.w(TAG, "Not paired: no Sync Token saved")
            return@withContext Result.failure()
        }
        Log.i(TAG, "Listening for RuneLite for ${listenWindowMs / 1000} s")
        ReceiverHost.acquire()
        try {
            delay(listenWindowMs)
        } finally {
            ReceiverHost.release()
        }
        Result.success()
    }

    companion object {
        const val TAG = "SyncWorker"
        const val KEY_UUID = "key_uuid"
        const val NOTIFICATION_BASE_ID = 1000
        const val STATUS_OLDER = "Ignored older data from your PC"

        /** How long a background run listens. The plugin retries every 30 s, so 75 s always catches one. */
        @VisibleForTesting
        var listenWindowMs: Long = 75_000L

        @VisibleForTesting
        var defaultPreferences: UserPreferencesRepository? = null
    }
}
