package com.pssst.util

import com.pssst.data.network.model.BirdhouseEntryDto
import com.pssst.data.network.model.FarmingEntryDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReminderPlannerTest {
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

        val desired = ReminderPlanner.farmingReminders(entries, nowMs = observedAt + 60_000L)

        assertEquals(1, desired.size)
        assertEquals("11062.4774", desired[0].key)
        assertEquals(doneAt, desired[0].doneAtMs)
        assertEquals(doneAt - (observedAt + 60_000L), desired[0].delayMs)
        assertTrue(desired[0].message.contains("Catherby"))
        assertTrue(desired[0].message.contains("Ranarr"))
    }

    @Test
    fun desired_skipsPatchesWhoseEstimatedFinishHasPassed() {
        assertTrue(ReminderPlanner.farmingReminders(listOf(ranarr), nowMs = doneAt).isEmpty())
    }

    @Test
    fun diff_leavesUnchangedRemindersAloneAndReplacesMovedOnes() {
        val desired = ReminderPlanner.farmingReminders(listOf(ranarr), nowMs = observedAt)
        val unchanged = QueuedReminder("w1", "11062.4774", doneAt)
        val stale = QueuedReminder("w2", "11062.4774", doneAt - tick)
        val orphan = QueuedReminder("w3", "12083.4774", doneAt)

        val noChange = ReminderPlanner.diff(desired, listOf(unchanged))
        assertTrue(noChange.cancelWorkIds.isEmpty())
        assertTrue(noChange.schedule.isEmpty())

        val moved = ReminderPlanner.diff(desired, listOf(stale, orphan))
        assertEquals(listOf("w2", "w3"), moved.cancelWorkIds)
        assertEquals(1, moved.schedule.size)
    }

    @Test
    fun diff_cancelsEverythingWhenNothingIsDesired() {
        val changes = ReminderPlanner.diff(emptyList(), listOf(QueuedReminder("w1", "a.1", 5L)))
        assertEquals(listOf("w1"), changes.cancelWorkIds)
        assertTrue(changes.schedule.isEmpty())
    }

    @Test
    fun birdhouses_oneReminderAtTheLastSeededHousesReadyTime() {
        val now = 1_000_000L
        val entries = listOf(
            BirdhouseEntryDto(space = "MEADOW_NORTH", state = "SEEDED", readyAt = now + 600_000L),
            BirdhouseEntryDto(space = "MEADOW_SOUTH", state = "SEEDED", readyAt = now + 1_800_000L),
            BirdhouseEntryDto(space = "VALLEY_NORTH", state = "BUILT", readyAt = 0L),
            BirdhouseEntryDto(space = "VALLEY_SOUTH", state = "EMPTY", readyAt = 0L)
        )

        val reminders = ReminderPlanner.birdhouseReminders(entries, now)

        assertEquals(1, reminders.size)
        assertEquals(ReminderPlanner.BIRDHOUSES_KEY, reminders[0].key)
        assertEquals(now + 1_800_000L, reminders[0].doneAtMs)
        assertEquals(1_800_000L, reminders[0].delayMs)
    }

    @Test
    fun birdhouses_noReminderWhenNothingIsSeededOrAllAreAlreadyReady() {
        val now = 1_000_000L
        assertTrue(ReminderPlanner.birdhouseReminders(emptyList(), now).isEmpty())
        assertTrue(
            ReminderPlanner.birdhouseReminders(
                listOf(BirdhouseEntryDto(state = "BUILT", readyAt = 0L)), now
            ).isEmpty()
        )
        assertTrue(
            ReminderPlanner.birdhouseReminders(
                listOf(BirdhouseEntryDto(state = "SEEDED", readyAt = now - 1)), now
            ).isEmpty()
        )
    }
}
