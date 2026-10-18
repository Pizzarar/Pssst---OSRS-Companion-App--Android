package com.pssst.util

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeFormatTest {
    @Test
    fun formatDuration_picksTheTwoMostSignificantUnits() {
        assertEquals("<1m", TimeFormat.formatDuration(-5_000))
        assertEquals("<1m", TimeFormat.formatDuration(59_999))
        assertEquals("42m", TimeFormat.formatDuration(42 * 60_000L))
        assertEquals("3h 05m", TimeFormat.formatDuration((3 * 60 + 5) * 60_000L))
        assertEquals("2d 4h", TimeFormat.formatDuration((2 * 24 * 60 + 4 * 60 + 30) * 60_000L))
    }

    @Test
    fun ageLabel_saysJustNowUnderAMinuteAndAgoAfter() {
        assertEquals("just now", TimeFormat.ageLabel(0))
        assertEquals("just now", TimeFormat.ageLabel(59_999))
        assertEquals("just now", TimeFormat.ageLabel(-10_000))
        assertEquals("42m ago", TimeFormat.ageLabel(42 * 60_000L))
        assertEquals("3h 05m ago", TimeFormat.ageLabel((3 * 60 + 5) * 60_000L))
    }

    @Test
    fun formatDate_isReadableAndHandlesUnknown() {
        assertEquals("unknown", TimeFormat.formatDate(0))
        assertEquals("unknown", TimeFormat.formatDate(-5))
        // mid-day UTC so every time zone lands on the same calendar day
        assertEquals("15 Jun 2026", TimeFormat.formatDate(1_781_524_800_000L))
    }
}
