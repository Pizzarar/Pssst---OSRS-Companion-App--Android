package com.pssst.worker

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import com.pssst.data.network.model.BirdhouseEntryDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SyncSchedulerTest {

    private lateinit var context: Context
    private lateinit var workManager: WorkManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        WorkManagerTestInitHelper.initializeTestWorkManager(context)
        workManager = WorkManager.getInstance(context)
    }

    private fun live(tag: String) = workManager.getWorkInfosByTag(tag).get().filter { !it.state.isFinished }

    private fun cachedEntity(raw: String) =
        com.pssst.data.local.PlayerStateEntity("token-1234567890abcdef", 1L, raw, "{}", "{}", "[]", "[]", 0L)

    @Test
    fun replanFromEntity_queuesRemindersFromTheSavedSnapshotWithNoNetworkInvolved() {
        val readyAt = System.currentTimeMillis() + 45 * 60_000L
        val raw = """{"data":{"birdhouses":[{"space":"MEADOW_NORTH","state":"SEEDED","readyAt":$readyAt}]},"updated_at":1}"""

        SyncScheduler.replanFromEntity(context, cachedEntity(raw), notificationsEnabled = true)
        assertEquals(1, live(ReminderWorker.TAG).size)

        SyncScheduler.replanFromEntity(context, cachedEntity(raw), notificationsEnabled = true)
        assertEquals("replanning again does not duplicate", 1, live(ReminderWorker.TAG).size)

        SyncScheduler.replanFromEntity(context, cachedEntity(raw), notificationsEnabled = false)
        assertEquals("turning notifications off clears them", 0, live(ReminderWorker.TAG).size)
    }

    @Test
    fun syncNow_waitsForANetworkInsteadOfFailing() {
        SyncScheduler.syncNow(context)
        val info = workManager.getWorkInfosForUniqueWork(SyncScheduler.SYNC_NOW_WORK_NAME).get().single()
        assertEquals(androidx.work.NetworkType.CONNECTED, info.constraints.requiredNetworkType)
    }

    @Test
    fun cancelAll_stopsTheSyncAndEveryQueuedReminder() {
        SyncScheduler.schedulePeriodicSync(context)
        SyncScheduler.syncNow(context)
        val seededUntil = System.currentTimeMillis() + 30 * 60_000L
        SyncScheduler.syncReminders(
            context,
            farming = emptyList(),
            birdhouses = listOf(BirdhouseEntryDto(space = "MEADOW_NORTH", state = "SEEDED", readyAt = seededUntil)),
            notificationsEnabled = true
        )
        assertTrue("setup should have queued sync work", live(SyncWorker.TAG).isNotEmpty())
        assertEquals("and one birdhouse reminder", 1, live(ReminderWorker.TAG).size)

        SyncScheduler.cancelAll(context)

        assertEquals(emptyList<WorkInfo>(), live(SyncWorker.TAG))
        assertEquals(emptyList<WorkInfo>(), live(ReminderWorker.TAG))
    }
}
