package com.pssst.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import android.util.Log
import com.pssst.data.network.model.FarmingEntryDto
import com.pssst.util.FarmingReminderPlanner
import com.pssst.util.QueuedReminder
import java.util.UUID
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

    /**
     * Makes the queued farming reminders match [entries]: one per growing patch at its estimated finish time.
     * Reminders are keyed by (patch, finish time), so an unchanged patch is left alone on every sync and one whose
     * observation moved is cancelled and re-queued. With notifications off, everything queued is cancelled.
     * Never throws: a reminder problem must not fail a sync.
     */
    fun syncFarmingReminders(context: Context, entries: List<FarmingEntryDto>, notificationsEnabled: Boolean) {
        try {
            val workManager = WorkManager.getInstance(context)
            val desired = if (notificationsEnabled) {
                FarmingReminderPlanner.desiredReminders(entries, System.currentTimeMillis())
            } else {
                emptyList()
            }

            val queued = workManager.getWorkInfosByTag(FarmingReminderWorker.TAG).get()
                .filter { !it.state.isFinished }
                .mapNotNull { info ->
                    val patch = info.tags.firstOrNull { it.startsWith(FarmingReminderWorker.TAG_PATCH_PREFIX) }
                        ?.removePrefix(FarmingReminderWorker.TAG_PATCH_PREFIX)
                    val eta = info.tags.firstOrNull { it.startsWith(FarmingReminderWorker.TAG_ETA_PREFIX) }
                        ?.removePrefix(FarmingReminderWorker.TAG_ETA_PREFIX)?.toLongOrNull()
                    if (patch == null || eta == null) null else QueuedReminder(info.id.toString(), patch, eta)
                }

            val changes = FarmingReminderPlanner.diff(desired, queued)
            changes.cancelWorkIds.forEach { workManager.cancelWorkById(UUID.fromString(it)) }
            changes.schedule.forEach { reminder ->
                val request = OneTimeWorkRequestBuilder<FarmingReminderWorker>()
                    .setInitialDelay(reminder.delayMs, TimeUnit.MILLISECONDS)
                    .setInputData(
                        workDataOf(
                            FarmingReminderWorker.KEY_PATCH_KEY to reminder.patchKey,
                            FarmingReminderWorker.KEY_TITLE to reminder.title,
                            FarmingReminderWorker.KEY_MESSAGE to reminder.message
                        )
                    )
                    .addTag(FarmingReminderWorker.TAG)
                    .addTag(FarmingReminderWorker.TAG_PATCH_PREFIX + reminder.patchKey)
                    .addTag(FarmingReminderWorker.TAG_ETA_PREFIX + reminder.doneAtMs)
                    .build()
                workManager.enqueue(request)
            }
        } catch (e: Exception) {
            Log.w("SyncScheduler", "Could not sync farming reminders", e)
        }
    }
}
