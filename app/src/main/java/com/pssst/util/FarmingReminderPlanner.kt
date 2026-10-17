package com.pssst.util

import com.pssst.data.network.model.FarmingEntryDto

/** A one-shot "this patch should be fully grown" notification to fire at [doneAtMs]. */
data class PlannedReminder(
    val patchKey: String,
    val doneAtMs: Long,
    val delayMs: Long,
    val title: String,
    val message: String
)

/** A reminder already queued in WorkManager, as recovered from its tags. */
data class QueuedReminder(
    val workId: String,
    val patchKey: String,
    val doneAtMs: Long
)

data class ReminderChanges(
    val cancelWorkIds: List<String>,
    val schedule: List<PlannedReminder>
)

/**
 * Decides which farming reminders should exist, and what to cancel or add to get there.
 * Pure logic; SyncScheduler applies the result to WorkManager.
 */
object FarmingReminderPlanner {
    // Compost bins and weeds also tick, but a notification for them is noise
    private val EXCLUDED_PATCH_TYPES = setOf("COMPOST", "BIG_COMPOST")

    fun desiredReminders(entries: List<FarmingEntryDto>, nowMs: Long): List<PlannedReminder> =
        entries.mapNotNull { entry ->
            if (entry.cropState != "GROWING" || entry.patchType in EXCLUDED_PATCH_TYPES || entry.cropName == "Weeds") {
                return@mapNotNull null
            }
            val doneAt = FarmingTiming.doneAtMs(entry) ?: return@mapNotNull null
            if (doneAt <= nowMs) return@mapNotNull null // already grown; nothing left to wait for
            val type = entry.patchType.lowercase().replace('_', ' ')
            val where = if (entry.patchName.isBlank()) entry.regionName else "${entry.regionName} (${entry.patchName})"
            PlannedReminder(
                patchKey = entry.patchKey,
                doneAtMs = doneAt,
                delayMs = doneAt - nowMs,
                title = "Farming patch grown",
                // Worded as an estimate: the farm tick offset is not tracked, so this can be a tick early or late
                message = "$where $type (${entry.cropName ?: "crop"}) should be fully grown by now."
            )
        }

    fun diff(desired: List<PlannedReminder>, queued: List<QueuedReminder>): ReminderChanges {
        val wanted = desired.map { it.patchKey to it.doneAtMs }.toSet()
        val present = queued.map { it.patchKey to it.doneAtMs }.toSet()
        return ReminderChanges(
            cancelWorkIds = queued.filter { (it.patchKey to it.doneAtMs) !in wanted }.map { it.workId },
            schedule = desired.filter { (it.patchKey to it.doneAtMs) !in present }
        )
    }
}
