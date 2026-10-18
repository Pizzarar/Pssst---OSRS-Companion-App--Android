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

    /** "just now" under a minute, otherwise "42m ago", "3h 05m ago". */
    fun ageLabel(ms: Long): String =
        if (ms < 60_000L) "just now" else "${formatDuration(ms)} ago"

    /** "17 Oct 2026", in the device's time zone, or "unknown" for a zero/negative timestamp. */
    fun formatDate(epochMs: Long): String {
        if (epochMs <= 0) return "unknown"
        return java.text.SimpleDateFormat("d MMM yyyy", java.util.Locale.US).format(java.util.Date(epochMs))
    }
}
