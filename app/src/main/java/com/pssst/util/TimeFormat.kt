package com.pssst.util

object TimeFormat {
    /** Compact duration for status lines: "<1m", "42m", "3h 05m", "2d 4h". Negative values clamp to "<1m". */
    fun formatDuration(ms: Long): String {
        val totalMinutes = (ms / 60_000L).coerceAtLeast(0)
        val days = totalMinutes / (24 * 60)
        val hours = (totalMinutes % (24 * 60)) / 60
        val minutes = totalMinutes % 60
        return when {
            totalMinutes < 1 -> "<1m"
            days > 0 -> "${days}d ${hours}h"
            hours > 0 -> "${hours}h ${minutes.toString().padStart(2, '0')}m"
            else -> "${minutes}m"
        }
    }
}
