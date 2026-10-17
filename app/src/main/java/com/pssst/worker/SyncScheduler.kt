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
import com.pssst.data.network.model.BirdhouseEntryDto
import com.pssst.data.network.model.FarmingEntryDto
import com.pssst.util.ReminderPlanner
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

    /**
     * Stops everything Pssst has scheduled: the periodic and immediate sync, queued reminders, and the price-refresh
     * jobs older builds scheduled. Used on Disconnect so nothing keeps calling out with nobody paired. A network
     * drop never calls this; those are ridden out by retries and the normal schedule.
     */
    fun cancelAll(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(SYNC_WORK_NAME)
        workManager.cancelUniqueWork(SYNC_NOW_WORK_NAME)
        workManager.cancelAllWorkByTag(SyncWorker.TAG)
        workManager.cancelAllWorkByTag(ReminderWorker.TAG)
        cancelLegacyPriceWork(context)
    }

    /** Prices used to be fetched from the Wiki by a background job; drop it on installs that had scheduled it. */
    fun cancelLegacyPriceWork(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork("pssst_price_refresh")
        workManager.cancelUniqueWork("pssst_price_refresh_now")
        workManager.cancelAllWorkByTag("PriceRefreshWorker")
    }

    fun cancelSync(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(SYNC_WORK_NAME)
    }

    /**
     * Makes the queued reminders match the latest observations: one per growing farming patch at its estimated
     * finish, and one for the birdhouses when the last seeded one is ready. Reminders are keyed by (key, finish
     * time), so an unchanged one is left alone on every sync and one whose observation moved is cancelled and
     * re-queued. With notifications off, everything queued is cancelled.
     * Never throws: a reminder problem must not fail a sync.
     */
    fun syncReminders(
        context: Context,
        farming: List<FarmingEntryDto>,
        birdhouses: List<BirdhouseEntryDto>,
        notificationsEnabled: Boolean
    ) {
        try {
            val workManager = WorkManager.getInstance(context)
            val nowMs = System.currentTimeMillis()
            val desired = if (notificationsEnabled) {
                ReminderPlanner.farmingReminders(farming, nowMs) + ReminderPlanner.birdhouseReminders(birdhouses, nowMs)
            } else {
                emptyList()
            }

            val queued = workManager.getWorkInfosByTag(ReminderWorker.TAG).get()
                .filter { !it.state.isFinished }
                .mapNotNull { info ->
                    val key = info.tags.firstOrNull { it.startsWith(ReminderWorker.TAG_KEY_PREFIX) }
                        ?.removePrefix(ReminderWorker.TAG_KEY_PREFIX)
                    val eta = info.tags.firstOrNull { it.startsWith(ReminderWorker.TAG_ETA_PREFIX) }
                        ?.removePrefix(ReminderWorker.TAG_ETA_PREFIX)?.toLongOrNull()
                    if (key == null || eta == null) null else QueuedReminder(info.id.toString(), key, eta)
                }

            val changes = ReminderPlanner.diff(desired, queued)
            changes.cancelWorkIds.forEach { workManager.cancelWorkById(UUID.fromString(it)) }
            changes.schedule.forEach { reminder ->
                val request = OneTimeWorkRequestBuilder<ReminderWorker>()
                    .setInitialDelay(reminder.delayMs, TimeUnit.MILLISECONDS)
                    .setInputData(
                        workDataOf(
                            ReminderWorker.KEY_REMINDER_KEY to reminder.key,
                            ReminderWorker.KEY_TITLE to reminder.title,
                            ReminderWorker.KEY_MESSAGE to reminder.message
                        )
                    )
                    .addTag(ReminderWorker.TAG)
                    .addTag(ReminderWorker.TAG_KEY_PREFIX + reminder.key)
                    .addTag(ReminderWorker.TAG_ETA_PREFIX + reminder.doneAtMs)
                    .build()
                workManager.enqueue(request)
            }
        } catch (e: Exception) {
            Log.w("SyncScheduler", "Could not sync farming reminders", e)
        }
    }
}
