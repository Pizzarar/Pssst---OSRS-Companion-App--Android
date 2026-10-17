package com.pssst.util

import com.pssst.ui.main.DailyTaskUiModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Cases follow RuneLite's DailyTasksPlugin conditions (see DailyTasks.kt); ids per DailyTasksParityTest in the plugin repo. */
class DailyTasksTest {

    private fun tasks(varbits: Map<String, Int> = emptyMap(), varps: Map<String, Int> = emptyMap()): Map<String, DailyTaskUiModel> =
        DailyTasks.evaluate(varbits, varps).associateBy { it.id }

    @Test
    fun aDailyIsLockedUntilItsDiaryTierIsDone() {
        val locked = tasks()["zaff_staves"]!!
        assertFalse(locked.isAvailable)
        assertEquals("Requires Varrock Diary (Easy)", locked.statusText)

        // A higher tier alone does not count: RuneLite gates staves on the Easy flag only
        assertFalse(tasks(mapOf("4481" to 1))["zaff_staves"]!!.isAvailable)

        assertTrue(tasks(mapOf("4479" to 1))["zaff_staves"]!!.isAvailable)
    }

    @Test
    fun aNonZeroClaimFlagMeansAlreadyClaimedToday() {
        val claimed = tasks(mapOf("4479" to 1, "4539" to 1))["zaff_staves"]!!
        assertFalse(claimed.isAvailable)
        assertEquals("Claimed today", claimed.statusText)
    }

    @Test
    fun everyDiaryDailyUsesItsOwnGateAndClaimFlag() {
        val gates = mapOf(
            "ardougne_essence" to ("4459" to "4547"),
            "lundail_runes" to ("4466" to "4540"),
            "seers_flax" to ("4475" to "4559"),
            "rantz_arrows" to ("4471" to "4563"),
            "kourend_dynamite" to ("7926" to "7939")
        )
        for ((id, ids) in gates) {
            val (gate, claim) = ids
            assertFalse("$id locked", tasks()[id]!!.isAvailable)
            assertTrue("$id open", tasks(mapOf(gate to 1))[id]!!.isAvailable)
            assertFalse("$id claimed", tasks(mapOf(gate to 1, claim to 1))[id]!!.isAvailable)
        }
    }

    @Test
    fun herbBoxesStopAtFifteenAndAreNotForIronmen() {
        val some = tasks(mapOf("3961" to 5), mapOf("1060" to 50_000))["nmz_herb_boxes"]!!
        assertTrue(some.isAvailable)
        assertEquals("5/15 claimed today \u2022 50K points", some.statusText)

        val all = tasks(mapOf("3961" to 15))["nmz_herb_boxes"]!!
        assertFalse(all.isAvailable)
        assertEquals("All 15 claimed today", all.statusText)

        assertFalse(tasks(mapOf("1777" to 1))["nmz_herb_boxes"]!!.isAvailable)
    }

    @Test
    fun sandNeedsHandInTheSandDoneAndNotAnUltimateIronman() {
        assertFalse(tasks(mapOf("1527" to 159))["bert_sand"]!!.isAvailable)
        assertTrue(tasks(mapOf("1527" to 160))["bert_sand"]!!.isAvailable)
        assertFalse(tasks(mapOf("1527" to 160, "1777" to 2))["bert_sand"]!!.isAvailable)
        assertTrue("a regular ironman may still get sand", tasks(mapOf("1527" to 160, "1777" to 1))["bert_sand"]!!.isAvailable)
        assertFalse(tasks(mapOf("1527" to 160, "4549" to 1))["bert_sand"]!!.isAvailable)
    }

    @Test
    fun bonemealAllowanceGrowsWithDiaryTiersAndEliteOnlyCountsWithHard() {
        fun bone(vararg flags: Pair<String, Int>, collected: Int = 0) =
            tasks(mapOf("4488" to 1, "4543" to collected) + flags)["morytania_bonemeal"]!!

        assertEquals("0/13 collected today", bone().statusText)
        assertEquals("0/26 collected today", bone("4489" to 1).statusText)
        assertEquals("0/39 collected today", bone("4489" to 1, "4490" to 1).statusText)
        assertEquals("elite without hard adds nothing", "0/13 collected today", bone("4490" to 1).statusText)

        assertTrue(bone("4489" to 1, collected = 25).isAvailable)
        assertFalse(bone("4489" to 1, collected = 26).isAvailable)
        assertEquals("All 13 collected today", bone(collected = 13).statusText)
        assertFalse(tasks()["morytania_bonemeal"]!!.isAvailable)
    }

    @Test
    fun tearsOfGuthixShowsRawValuesBecauseNoRuleIsKnown() {
        val tog = tasks(mapOf("5099" to 7, "456" to 3))["tears_of_guthix"]!!
        assertEquals("Countdown: 7 \u2022 Quest points check: 3", tog.statusText)
        assertFalse(tog.isAvailable)
    }

    @Test
    fun noRewardAmountsOrAdviceAppearAnywhere() {
        val everything = DailyTasks.evaluate(
            mapOf("4479" to 1, "4459" to 1, "4466" to 1, "4475" to 1, "4471" to 1, "4488" to 1, "7926" to 1, "1527" to 160),
            mapOf("1060" to 99_000)
        )
        val text = everything.joinToString(" ") { "${it.title} ${it.subtitle} ${it.statusText}" }.lowercase()
        for (word in listOf("profit", "buy", "sell", "should", "recommend", "worth", "talk to", "go to")) {
            assertFalse("found \"$word\" in: $text", text.contains(word))
        }
    }
}
