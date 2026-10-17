package com.pssst.util

import com.pssst.data.network.model.FarmingEntryDto

/**
 * Growth timing for a farming observation. Crops advance on a fixed global grid (every tickMinutes, aligned to the
 * epoch), so progress is derived from when the stage was observed, not from when the app last synced.
 *
 * Same arithmetic as RuneLite's timetracking plugin. It ignores the per-player farm tick offset, so a result can
 * be early or late by up to one tick.
 */
object FarmingTiming {
    private const val MS_PER_MINUTE = 60_000L

    /** Epoch ms at which the crop reaches its last stage, or null when nothing is advancing. */
    fun doneAtMs(entry: FarmingEntryDto): Long? {
        if (entry.cropState == null || entry.tickMinutes <= 0) return null
        val remainingTicks = entry.stages - 1 - entry.stage
        if (remainingTicks < 0) return null
        val tickMs = entry.tickMinutes * MS_PER_MINUTE
        val currentTickStart = (entry.observedAt / tickMs) * tickMs
        return currentTickStart + remainingTicks * tickMs
    }

    /** The stage the crop should be at by [nowMs], capped at its last stage. */
    fun stageAt(entry: FarmingEntryDto, nowMs: Long): Int {
        if (entry.tickMinutes <= 0) return entry.stage
        val tickMs = entry.tickMinutes * MS_PER_MINUTE
        val ticksElapsed = (nowMs / tickMs - entry.observedAt / tickMs).coerceAtLeast(0)
        return (entry.stage + ticksElapsed).coerceAtMost((entry.stages - 1).toLong().coerceAtLeast(entry.stage.toLong())).toInt()
    }
}
