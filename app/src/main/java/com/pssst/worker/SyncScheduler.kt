package com.pssst.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object SyncScheduler {
    const val SYNC_WORK_NAME = "pssst_periodic_sync"
    const val SYNC_NOW_WORK_NAME = "pssst_sync_now"

    fun schedulePeriodicSync(
        context: Context,
        existingWorkPolicy: ExistingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP
    ) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            repeatInterval = 15,
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag(SyncWorker.TAG)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SYNC_WORK_NAME,
            existingWorkPolicy,
            periodicWorkRequest
        )
    }

    fun syncNow(context: Context) {
        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .addTag(SyncWorker.TAG)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            SYNC_NOW_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            oneTimeWorkRequest
        )
    }

    fun cancelSync(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(SYNC_WORK_NAME)
    }

    const val BIRDHOUSE_REMINDER_WORK_NAME = "pssst_birdhouse_reminder"

    fun scheduleBirdhouseReminder(context: Context, delaySeconds: Long) {
        if (delaySeconds <= 0) return

        val request = OneTimeWorkRequestBuilder<BirdhouseReminderWorker>()
            .setInitialDelay(delaySeconds, TimeUnit.SECONDS)
            .addTag(BirdhouseReminderWorker.TAG)
            .build()

        // KEEP, not REPLACE: SyncWorker calls this on every sync while a house is seeded,
        // so REPLACE pushed the 50-min reminder back every 15 min and it never fired.
        WorkManager.getInstance(context).enqueueUniqueWork(
            BIRDHOUSE_REMINDER_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    fun cancelBirdhouseReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(BIRDHOUSE_REMINDER_WORK_NAME)
    }
}
