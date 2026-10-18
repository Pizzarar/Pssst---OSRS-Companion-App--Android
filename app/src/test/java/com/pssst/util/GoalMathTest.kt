package com.pssst.util

import com.pssst.ui.main.ItemUiModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GoalMathTest {

    @Test
    fun parseGpValidFormats() {
        assertEquals(150_000_000L, GoalMath.parseGp("150m"))
        assertEquals(1_200_000_000L, GoalMath.parseGp("1.2b"))
        assertEquals(500_000L, GoalMath.parseGp("500k"))
        assertEquals(2_500_000L, GoalMath.parseGp("2,500,000"))
        assertEquals(1_000_000L, GoalMath.parseGp("1000000"))
        assertEquals(50_000_000L, GoalMath.parseGp(" 50M "))
        assertEquals(20_000_000L, GoalMath.parseGp("20.0M GP"))
        assertEquals(150_000_000L, GoalMath.parseGp("150m gp"))
        assertEquals(1_200_000_000L, GoalMath.parseGp("1.2b coins"))
    }

    @Test
    fun parseGpInvalidReturnsNull() {
        assertNull(GoalMath.parseGp(""))
        assertNull(GoalMath.parseGp("   "))
        assertNull(GoalMath.parseGp("0"))
        assertNull(GoalMath.parseGp("-5m"))
        assertNull(GoalMath.parseGp("abc"))
        assertNull(GoalMath.parseGp("1.2.3m"))
    }

    @Test
    fun unitValueEvaluatesSpecialCurrencyAndModes() {
        val coins = ItemUiModel(id = 995, quantity = 5000, gePrice = 10L, haPrice = 0L)
        val plat = ItemUiModel(id = 13204, quantity = 2, gePrice = 1005L, haPrice = 0L)
        val whip = ItemUiModel(id = 4151, quantity = 1, gePrice = 1_500_000L, haPrice = 72_000L)

        assertEquals(1L, GoalMath.unitValue(coins, GoalPriceMode.GE))
        assertEquals(1L, GoalMath.unitValue(coins, GoalPriceMode.HA))

        assertEquals(1_000L, GoalMath.unitValue(plat, GoalPriceMode.GE))
        assertEquals(1_000L, GoalMath.unitValue(plat, GoalPriceMode.HA))

        assertEquals(1_500_000L, GoalMath.unitValue(whip, GoalPriceMode.GE))
        assertEquals(72_000L, GoalMath.unitValue(whip, GoalPriceMode.HA))
    }

    @Test
    fun progressCalculatesWholeBankAndSingleTab() {
        val item1 = ItemUiModel(id = 4151, quantity = 1, name = "Abyssal whip", gePrice = 1_500_000L, haPrice = 72_000L, tab = 1)
        val item2 = ItemUiModel(id = 11802, quantity = 1, name = "Armadyl godsword", gePrice = 10_000_000L, haPrice = 750_000L, tab = 2)
        val bank = listOf(item1, item2)

        // Whole bank in GE mode
        val wholeGe = GoalMath.progress(bank, GoalMath.WHOLE_BANK, GoalPriceMode.GE, 20_000_000L)
        assertEquals(11_500_000L, wholeGe.current)
        assertEquals(8_500_000L, wholeGe.remaining)
        assertEquals(0.575f, wholeGe.fraction, 0.001f)
        assertEquals(2, wholeGe.itemCount)
        assertTrue(wholeGe.tabsKnown)

        // Tab 1 only in GE mode
        val tab1Ge = GoalMath.progress(bank, 1, GoalPriceMode.GE, 2_000_000L)
        assertEquals(1_500_000L, tab1Ge.current)
        assertEquals(500_000L, tab1Ge.remaining)
        assertEquals(0.75f, tab1Ge.fraction, 0.001f)
        assertEquals(1, tab1Ge.itemCount)

        // Tab 1 in HA mode
        val tab1Ha = GoalMath.progress(bank, 1, GoalPriceMode.HA, 100_000L)
        assertEquals(72_000L, tab1Ha.current)
        assertEquals(28_000L, tab1Ha.remaining)
    }

    @Test
    fun progressClampsAtTargetCompletion() {
        val item = ItemUiModel(id = 4151, quantity = 2, gePrice = 1_000_000L, tab = 0)
        val p = GoalMath.progress(listOf(item), GoalMath.WHOLE_BANK, GoalPriceMode.GE, 1_500_000L)
        assertEquals(2_000_000L, p.current)
        assertEquals(0L, p.remaining)
        assertEquals(1f, p.fraction, 0.001f)
    }

    @Test
    fun tabLabelsFormatProperly() {
        val item = ItemUiModel(id = 536, quantity = 100, name = "Dragon bones", tab = 6)
        val bank = listOf(item)

        assertEquals("Whole bank", GoalMath.tabLabel(bank, GoalMath.WHOLE_BANK))
        assertEquals("Main tab", GoalMath.tabLabel(bank, 0))
        assertEquals("Tab 6 · Dragon bones", GoalMath.tabLabel(bank, 6))
        assertEquals("Tab 3", GoalMath.tabLabel(bank, 3))
    }

    @Test
    fun tabsKnownIsFalseWhenTabsAreNull() {
        val item = ItemUiModel(id = 4151, quantity = 1, tab = null)
        val p = GoalMath.progress(listOf(item), GoalMath.WHOLE_BANK, GoalPriceMode.GE, 1_000_000L)
        assertFalse(p.tabsKnown)
    }
}
