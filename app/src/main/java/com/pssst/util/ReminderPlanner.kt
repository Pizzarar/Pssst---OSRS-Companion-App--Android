package com.pssst.util

import com.pssst.data.network.model.BirdhouseEntryDto
import com.pssst.data.network.model.FarmingEntryDto

/** A one-shot notification to fire at [doneAtMs], identified by [key] (e.g. a farming patch, or "birdhouses"). */
data class PlannedReminder(
    val key: String,
    val doneAtMs: Long,
    val delayMs: Long,
    val title: String,
    val message: String
)

/** A reminder already queued in WorkManager, as recovered from its tags. */
data class QueuedReminder(
    val workId: String,
    val key: String,
    val doneAtMs: Long
)

data class ReminderChanges(
    val cancelWorkIds: List<String>,
    val schedule: List<PlannedReminder>
)

/**
 * Decides which timed reminders should exist, and what to cancel or add to get there.
 * Pure logic; SyncScheduler applies the result to WorkManager.
 */
object ReminderPlanner {
    const val BIRDHOUSES_KEY = "birdhouses"

    // Compost bins and weeds also tick, but a notification for them is noise
    private val EXCLUDED_PATCH_TYPES = setOf("COMPOST", "BIG_COMPOST")

    fun farmingReminders(entries: List<FarmingEntryDto>, nowMs: Long): List<PlannedReminder> =
        entries.mapNotNull { entry ->
            if (entry.cropState != "GROWING" || entry.patchType in EXCLUDED_PATCH_TYPES || entry.cropName == "Weeds") {
                return@mapNotNull null
            }
            val doneAt = FarmingTiming.doneAtMs(entry) ?: return@mapNotNull null
            if (doneAt <= nowMs) return@mapNotNull null // already grown; nothing left to wait for
            val type = entry.patchType.lowercase().replace('_', ' ')
            val where = if (entry.patchName.isBlank()) entry.regionName else "${entry.regionName} (${entry.patchName})"
            PlannedReminder(
                key = entry.patchKey,
                doneAtMs = doneAt,
                delayMs = doneAt - nowMs,
                title = "Farming patch grown",
                // Worded as an estimate: the farm tick offset is not tracked, so this can be a tick early or late
                message = "$where $type (${entry.cropName ?: "crop"}) should be fully grown by now."
            )
        }

    /**
     * One reminder for all birdhouses, at the moment the last seeded one is ready (as RuneLite does), so four
     * houses seeded together do not produce four notifications.
     */
    fun birdhouseReminders(entries: List<BirdhouseEntryDto>, nowMs: Long): List<PlannedReminder> {
        val lastReadyAt = entries.filter { it.state == "SEEDED" && it.readyAt > 0 }.maxOfOrNull { it.readyAt }
            ?: return emptyList()
        if (lastReadyAt <= nowMs) return emptyList()
        return listOf(
            PlannedReminder(
                key = BIRDHOUSES_KEY,
                doneAtMs = lastReadyAt,
                delayMs = lastReadyAt - nowMs,
                title = "Birdhouses ready",
                message = "Your Fossil Island birdhouses should be ready by now."
            )
        )
    }

    fun diff(desired: List<PlannedReminder>, queued: List<QueuedReminder>): ReminderChanges {
        val wanted = desired.map { it.key to it.doneAtMs }.toSet()
        val present = queued.map { it.key to it.doneAtMs }.toSet()
        return ReminderChanges(
            cancelWorkIds = queued.filter { (it.key to it.doneAtMs) !in wanted }.map { it.workId },
            schedule = desired.filter { (it.key to it.doneAtMs) !in present }
        )
    }
}
