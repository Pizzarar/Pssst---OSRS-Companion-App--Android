package com.pssst.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pssst.data.local.PlayerStateEntity
import com.pssst.data.network.model.StateResponse

data class NotificationAlert(
    val title: String,
    val message: String
)

object StateDiffAnalyzer {
    private val gson = Gson()

    /**
     * Compares previous and current state to identify
     * daily resets, kingdom warnings, or completed slayer tasks.
     */
    fun findCompletedRunsOrPatches(
        previousEntity: PlayerStateEntity?,
        currentState: StateResponse
    ): List<NotificationAlert> {
        val currentData = currentState.data ?: return emptyList()
        val alerts = mutableListOf<NotificationAlert>()

        val previousVarbits: Map<String, Int> = previousEntity?.let {
            try {
                val mapType = object : TypeToken<Map<String, Int>>() {}.type
                gson.fromJson<Map<String, Int>>(it.varbitsJson, mapType) ?: emptyMap()
            } catch (e: Exception) {
                emptyMap()
            }
        } ?: emptyMap()

        val currentVarbits = currentData.varbits

        // 1. Check Daily / Weekly Resets
        // Zaff's Battlestaves: was claimed (1) and now reset (0)
        val prevStaves = previousVarbits["4539"]
        val currStaves = currentVarbits["4539"]
        if (prevStaves == 1 && currStaves == 0) {
            alerts.add(
                NotificationAlert(
                    title = "Daily Reset: Zaff's Battlestaves",
                    message = "Discounted battlestaves are available in Varrock Square."
                )
            )
        }

        // NMZ Herb Boxes: was claimed (> 0) and now reset (0)
        val prevHerbBoxes = previousVarbits["3961"]
        val currHerbBoxes = currentVarbits["3961"]
        if (prevHerbBoxes != null && prevHerbBoxes > 0 && currHerbBoxes == 0) {
            alerts.add(
                NotificationAlert(
                    title = "Daily Reset: NMZ Herb Boxes",
                    message = "Daily herb boxes have reset in Nightmare Zone."
                )
            )
        }

        // Tears of Guthix (Weekly): was on cooldown (> 0) and now 0 (ready)
        val prevTog = previousVarbits["5099"]
        val currTog = currentVarbits["5099"]
        if (prevTog != null && prevTog > 0 && currTog == 0) {
            alerts.add(
                NotificationAlert(
                    title = "Tears of Guthix Ready!",
                    message = "The Chasm of Tears is open for your weekly visit."
                )
            )
        }

        // 4. Kingdom of Miscellania Warnings
        val prevApproval = extractKingdomApproval(previousVarbits)
        val currApproval = extractKingdomApproval(currentVarbits)
        if (prevApproval != null && currApproval != null && prevApproval >= 90 && currApproval < 90) {
            alerts.add(
                NotificationAlert(
                    title = "Kingdom Approval Warning",
                    message = "Your Miscellania approval has dropped below 90% (now $currApproval%)."
                )
            )
        }

        val prevCoffer = extractKingdomCoffer(previousVarbits)
        val currCoffer = extractKingdomCoffer(currentVarbits)
        if (prevCoffer != null && currCoffer != null && prevCoffer >= 750_000L && currCoffer < 750_000L) {
            alerts.add(
                NotificationAlert(
                    title = "Kingdom Coffer Low",
                    message = "Your Miscellania coffer has dropped below 750,000 GP."
                )
            )
        }

        // 5. Check Slayer tasks
        val prevSlayerAmount = previousEntity?.let { extractSlayerAmount(it.slayerJson) } ?: -1
        val currSlayerAmount = extractSlayerAmountFromMap(currentData.slayer)
        if (prevSlayerAmount > 0 && currSlayerAmount == 0) {
            alerts.add(
                NotificationAlert(
                    title = "Slayer Task Complete!",
                    message = "You have finished your Slayer assignment."
                )
            )
        }

        return alerts
    }

    private fun extractKingdomApproval(varbits: Map<String, Int>): Int? {
        val v72 = varbits["72"]
        if (v72 != null) return (v72 * 100) / 127
        return varbits["3948"] ?: varbits["4000"]
    }

    private fun extractKingdomCoffer(varbits: Map<String, Int>): Long? {
        val v74 = varbits["74"]
        if (v74 != null) return v74.toLong()
        val legacy = varbits["3949"] ?: varbits["4001"]
        return legacy?.toLong()
    }

    private fun extractSlayerAmount(slayerJson: String): Int {
        return try {
            val type = object : TypeToken<Map<String, Any?>>() {}.type
            val map: Map<String, Any?> = gson.fromJson(slayerJson, type) ?: return -1
            extractSlayerAmountFromMap(map)
        } catch (e: Exception) {
            -1
        }
    }

    private fun extractSlayerAmountFromMap(map: Map<String, Any?>): Int {
        val amount = map["amount"] ?: return -1
        return when (amount) {
            is Number -> amount.toInt()
            is String -> amount.toIntOrNull() ?: -1
            else -> -1
        }
    }
}
