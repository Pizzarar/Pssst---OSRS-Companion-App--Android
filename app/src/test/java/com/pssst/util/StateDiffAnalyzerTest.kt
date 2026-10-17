package com.pssst.util

import com.google.gson.Gson
import com.pssst.data.local.PlayerStateEntity
import com.pssst.data.network.model.SlayerEntryDto
import com.pssst.data.network.model.StateDataDto
import com.pssst.data.network.model.StateResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StateDiffAnalyzerTest {

    private val gson = Gson()
    private val testUuid = "test-uuid"

    @Test
    fun findCompletedRunsOrPatches_ignoresLegacyFixedPatchVarbits() {
        // 4771/7904 are per-region slots, so a change in them says nothing without a region. Farming alerts
        // come from timed reminders (FarmingReminderPlanner), never from diffing these.
        val prevEntity = PlayerStateEntity(
            testUuid, 1000L, "{}", gson.toJson(mapOf("4771" to 4, "7904" to 2)), "{}", "[]", "[]", 1000L
        )
        val currResponse = StateResponse(
            updatedAt = 2000L,
            data = StateDataDto(varbits = mapOf("4771" to 8, "7904" to 5))
        )

        assertTrue(StateDiffAnalyzer.findCompletedRunsOrPatches(prevEntity, currResponse).isEmpty())
    }

    @Test
    fun findCompletedRunsOrPatches_detectsDailyResetsAndKingdomWarning() {
        val prevVarbits = mapOf(
            "4539" to 1,     // Staves claimed
            "3961" to 15,    // Herb boxes claimed
            "5099" to 3,     // TOG on cooldown
            "72" to 120,     // Kingdom approval ~94%
            "74" to 1000000  // Coffer 1M GP
        )
        val prevEntity = PlayerStateEntity(
            testUuid,
            1000L,
            "{}",
            gson.toJson(prevVarbits),
            "{}",
            "[]",
            "[]",
            1000L
        )

        val currVarbits = mapOf(
            "4539" to 0,     // Staves reset!
            "3961" to 0,     // Herb boxes reset!
            "5099" to 0,     // TOG ready!
            "72" to 100,     // Kingdom approval drops to ~78% (< 90%)
            "74" to 600000   // Coffer drops to 600k (< 750k)
        )
        val currResponse = StateResponse(
            updatedAt = 2000L,
            data = StateDataDto(varbits = currVarbits)
        )

        val alerts = StateDiffAnalyzer.findCompletedRunsOrPatches(prevEntity, currResponse)

        assertTrue(alerts.any { it.title == "Daily Reset: Zaff's Battlestaves" })
        assertTrue(alerts.any { it.title == "Daily Reset: NMZ Herb Boxes" })
        assertTrue(alerts.any { it.title == "Tears of Guthix Ready!" })
        assertTrue(alerts.any { it.title == "Kingdom Approval Warning" })
        assertTrue(alerts.any { it.title == "Kingdom Coffer Low" })
    }

    @Test
    fun findCompletedRunsOrPatches_detectsSlayerTaskCompletion() {
        val prevEntity = PlayerStateEntity(
            testUuid,
            1000L,
            "{}",
            "{}",
            """{"taskName": "Gargoyles", "amount": 15}""",
            "[]",
            "[]",
            1000L
        )

        val currResponse = StateResponse(
            updatedAt = 2000L,
            data = StateDataDto(slayer = SlayerEntryDto(amount = 0))
        )

        val alerts = StateDiffAnalyzer.findCompletedRunsOrPatches(prevEntity, currResponse)
        assertEquals(1, alerts.size)
        assertEquals("Slayer Task Complete!", alerts[0].title)
    }
}
