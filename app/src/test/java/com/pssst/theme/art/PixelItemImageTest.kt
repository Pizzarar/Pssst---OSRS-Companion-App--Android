package com.pssst.theme.art

import androidx.compose.ui.unit.IntSize
import org.junit.Assert.assertEquals
import org.junit.Test

class PixelItemImageTest {

    @Test
    fun `computeItemSpriteDrawSize uses exact integer multiples when box is at least 36x32`() {
        // 1x exact fit (e.g. 40x36 px box -> 36x32 at 1x)
        assertEquals(
            IntSize(36, 32),
            computeItemSpriteDrawSize(srcWidth = 36, srcHeight = 32, boxWidthPx = 40, boxHeightPx = 36)
        )
        // 2x integer multiple (e.g. 96x96 px box -> 72x64 at 2x, NOT fractional 96x85)
        assertEquals(
            IntSize(72, 64),
            computeItemSpriteDrawSize(srcWidth = 36, srcHeight = 32, boxWidthPx = 96, boxHeightPx = 96)
        )
        // 3x integer multiple (e.g. 120x108 px box -> 108x96 at 3x)
        assertEquals(
            IntSize(108, 96),
            computeItemSpriteDrawSize(srcWidth = 36, srcHeight = 32, boxWidthPx = 120, boxHeightPx = 108)
        )
    }

    @Test
    fun `computeItemSpriteDrawSize scales proportionally when box is smaller than sprite`() {
        val size = computeItemSpriteDrawSize(srcWidth = 36, srcHeight = 32, boxWidthPx = 18, boxHeightPx = 18)
        assertEquals(IntSize(18, 16), size)
    }

    @Test
    fun `combatLevel computes standard OSRS combat level`() {
        // Fresh level 3 account (10 HP, 1 in everything else)
        val fresh = listOf(
            com.pssst.ui.main.SkillUiModel(name = "Hitpoints", level = 10, xp = 1154L)
        )
        assertEquals(3, com.pssst.ui.main.combatLevel(fresh))

        // Maxed combat (99 all) -> 126
        val maxed = listOf(
            com.pssst.ui.main.SkillUiModel(name = "Attack", level = 99),
            com.pssst.ui.main.SkillUiModel(name = "Strength", level = 99),
            com.pssst.ui.main.SkillUiModel(name = "Defence", level = 99),
            com.pssst.ui.main.SkillUiModel(name = "Hitpoints", level = 99),
            com.pssst.ui.main.SkillUiModel(name = "Prayer", level = 99),
            com.pssst.ui.main.SkillUiModel(name = "Ranged", level = 99),
            com.pssst.ui.main.SkillUiModel(name = "Magic", level = 99)
        )
        assertEquals(126, com.pssst.ui.main.combatLevel(maxed))
    }
}
