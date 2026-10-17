package com.pssst.util

import org.junit.Assert.assertEquals
import org.junit.Test

/** Values taken from RuneLite's net.runelite.api.Experience.getXpForLevel (levels up to its MAX_VIRT_LEVEL, 126). */
class OsrsXpTableTest {
    private val runelite = mapOf(
        2 to 83L, 10 to 1_154L, 50 to 101_333L, 92 to 6_517_253L, 99 to 13_034_431L,
        100 to 14_391_160L, 120 to 104_273_167L, 126 to 188_884_740L
    )

    @Test
    fun xpForLevelMatchesRuneLite() {
        for ((level, xp) in runelite) {
            assertEquals("level $level", xp, OsrsXpTable.xpForLevel(level))
        }
    }

    @Test
    fun levelForXpIsTheInverseAtTheBoundaries() {
        for ((level, xp) in runelite) {
            assertEquals("exactly $xp xp", level, OsrsXpTable.levelForXp(xp))
            assertEquals("one xp short of level $level", level - 1, OsrsXpTable.levelForXp(xp - 1))
        }
    }
}
