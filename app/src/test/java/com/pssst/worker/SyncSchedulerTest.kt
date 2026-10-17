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
