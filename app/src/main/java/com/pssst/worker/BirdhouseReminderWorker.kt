package com.pssst.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pssst.data.local.DataStoreUserPreferencesRepository
import com.pssst.data.local.UserPreferencesRepository
import com.pssst.notification.NotificationHelper

class BirdhouseReminderWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    var preferencesRepository: UserPreferencesRepository =
        defaultPreferences ?: DataStoreUserPreferencesRepository(appContext)

    override suspend fun doWork(): Result {
        val enabled = preferencesRepository.getNotificationsEnabled()
        if (!enabled) {
            Log.d(TAG, "Birdhouse reminder skipped: notifications disabled by user")
            return Result.success()
        }

        NotificationHelper.showNotification(
            context = appContext,
            notificationId = NOTIFICATION_ID,
            title = "Birdhouse Run Ready!",
            message = "Your Fossil Island birdhouses are ready to harvest."
        )
        Log.i(TAG, "Birdhouse reminder notification displayed")
        return Result.success()
    }

    companion object {
        const val TAG = "BirdhouseReminderWorker"
        const val NOTIFICATION_ID = 2001

        var defaultPreferences: UserPreferencesRepository? = null
    }
}
