package com.pssst.ui.main

import org.junit.Assert.assertEquals
import org.junit.Test

class FormatXpTest {
    @Test
    fun formatsBoundariesAndPreventsIntOverflow() {
        assertEquals("9999", formatXp(9_999L))
        assertEquals("10K", formatXp(10_000L))
        assertEquals("850K", formatXp(850_400L))
        assertEquals("1.00M", formatXp(1_000_000L))
        assertEquals("1.74M", formatXp(1_739_400L))
        assertEquals("13.0M", formatXp(13_034_431L))
        assertEquals("131.4M", formatXp(131_420_000L))
        assertEquals("4.60B", formatXp(4_600_000_000L))
    }
}
