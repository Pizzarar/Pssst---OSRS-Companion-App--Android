package com.pssst.util

import com.pssst.data.network.model.FarmingEntryDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FarmingReminderPlannerTest {
    private val tick = 20 * 60_000L
    private val observedAt = 833 * tick + 7 * 60_000L
    private val doneAt = 833 * tick + 3 * tick

    private val ranarr = FarmingEntryDto(
        regionId = 11062, regionName = "Catherby", patchType = "HERB", varbitId = 4774, observedAt = observedAt,
        cropName = "Ranarr", cropState = "GROWING", stage = 1, stages = 5, tickMinutes = 20
    )

    @Test
    fun desired_remindsOnlyForGrowingPatchesThatHaveNotFinished() {
        val entries = listOf(
            ranarr,
            ranarr.copy(varbitId = 1, cropState = "HARVESTABLE", tickMinutes = 0),
            ranarr.copy(varbitId = 2, cropState = "DISEASED", tickMinutes = 0),
            ranarr.copy(varbitId = 3, cropState = null),
            ranarr.copy(varbitId = 4, patchType = "COMPOST"),
            ranarr.copy(varbitId = 5, cropName = "Weeds")
        )

        val desired = FarmingReminderPlanner.desiredReminders(entries, nowMs = observedAt + 60_000L)

        assertEquals(1, desired.size)
        assertEquals("11062.4774", desired[0].patchKey)
        assertEquals(doneAt, desired[0].doneAtMs)
        assertEquals(doneAt - (observedAt + 60_000L), desired[0].delayMs)
        assertTrue(desired[0].message.contains("Catherby"))
        assertTrue(desired[0].message.contains("Ranarr"))
    }

    @Test
    fun desired_skipsPatchesWhoseEstimatedFinishHasPassed() {
        assertTrue(FarmingReminderPlanner.desiredReminders(listOf(ranarr), nowMs = doneAt).isEmpty())
    }

    @Test
    fun diff_leavesUnchangedRemindersAloneAndReplacesMovedOnes() {
        val desired = FarmingReminderPlanner.desiredReminders(listOf(ranarr), nowMs = observedAt)
        val unchanged = QueuedReminder("w1", "11062.4774", doneAt)
        val stale = QueuedReminder("w2", "11062.4774", doneAt - tick)
        val orphan = QueuedReminder("w3", "12083.4774", doneAt)

        val noChange = FarmingReminderPlanner.diff(desired, listOf(unchanged))
        assertTrue(noChange.cancelWorkIds.isEmpty())
        assertTrue(noChange.schedule.isEmpty())

        val moved = FarmingReminderPlanner.diff(desired, listOf(stale, orphan))
        assertEquals(listOf("w2", "w3"), moved.cancelWorkIds)
        assertEquals(1, moved.schedule.size)
    }

    @Test
    fun diff_cancelsEverythingWhenNothingIsDesired() {
        val changes = FarmingReminderPlanner.diff(emptyList(), listOf(QueuedReminder("w1", "a.1", 5L)))
        assertEquals(listOf("w1"), changes.cancelWorkIds)
        assertTrue(changes.schedule.isEmpty())
    }
}
