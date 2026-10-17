package com.pssst.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pssst.data.local.DataStoreUserPreferencesRepository
import com.pssst.data.local.UserPreferencesRepository
import com.pssst.notification.NotificationHelper

class FarmingReminderWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    var preferencesRepository: UserPreferencesRepository =
        defaultPreferences ?: DataStoreUserPreferencesRepository(appContext)

    override suspend fun doWork(): Result {
        if (!preferencesRepository.getNotificationsEnabled()) {
            Log.d(TAG, "Farming reminder skipped: notifications disabled by user")
            return Result.success()
        }

        val patchKey = inputData.getString(KEY_PATCH_KEY) ?: return Result.failure()
        val message = inputData.getString(KEY_MESSAGE) ?: return Result.failure()
        NotificationHelper.showNotification(
            context = appContext,
            // One notification slot per patch, so a repeat for the same patch replaces rather than stacks
            notificationId = NOTIFICATION_BASE_ID + (patchKey.hashCode() and 0xFFFF),
            title = inputData.getString(KEY_TITLE) ?: "Farming patch grown",
            message = message
        )
        return Result.success()
    }

    companion object {
        const val TAG = "FarmingReminderWorker"
        const val KEY_PATCH_KEY = "patch_key"
        const val KEY_TITLE = "title"
        const val KEY_MESSAGE = "message"
        const val TAG_PATCH_PREFIX = "farm:"
        const val TAG_ETA_PREFIX = "eta:"
        private const val NOTIFICATION_BASE_ID = 30_000

        var defaultPreferences: UserPreferencesRepository? = null
    }
}
