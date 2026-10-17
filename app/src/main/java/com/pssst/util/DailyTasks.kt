package com.pssst.util

import com.pssst.ui.main.DailyTaskUiModel
import com.pssst.ui.main.ResetCategory
import com.pssst.ui.main.formatQuantity

/**
 * Which free daily rewards the player can claim, and which they already have.
 *
 * A port of the conditions in RuneLite's Daily Tasks plugin (net.runelite.client.plugins.dailytaskindicators.
 * DailyTasksPlugin, its checkHerbBoxes / checkStaves / ... methods). The constants and varbit ids are pinned
 * against that class by DailyTasksParityTest in the plugin repo. Where that plugin has no rule, this shows nothing
 * invented: no reward quantities, no "worth it", no where-to-go.
 *
 * Varbits used (names from RuneLite's gameval VarbitID): IRONMAN 1777, HANDSAND_QUEST 1527, NZONE_HERBBOXES_PURCHASED
 * 3961, ZAFF_LAST_CLAIMED 4539, ARDOUGNE_FREE_ESSENCE 4547, LUNDAIL_LAST_CLAIMED 4540, YANILLE_SAND_CLAIMED 4549,
 * SEERS_FREE_FLAX 4559, WESTERN_RANTZ_ARROWS 4563, MORYTANIA_SLIME_CLAIMED 4543, KOUREND_FREE_DYNAMITE 7939, and the
 * *_DIARY_*_COMPLETE gates listed per task below. NZONE_REWARDPOINTS (varp 1060) is shown, not used for eligibility.
 */
object DailyTasks {
    const val HERB_BOX_MAX = 15
    const val BONEMEAL_PER_DIARY = 13
    const val SAND_QUEST_COMPLETE = 160

    private const val IRONMAN = "1777"
    private const val NOT_IRONMAN = 0
    private const val ULTIMATE_IRONMAN = 2

    private data class DiaryDaily(
        val id: String,
        val title: String,
        val gateVarbit: String,
        val gateText: String,
        val claimedVarbit: String,
        val claimedText: String
    )

    // Each reward unlocks with one diary tier; it is available when that tier is done and today's claim flag is 0
    private val DIARY_DAILIES = listOf(
        DiaryDaily("zaff_staves", "Zaff's Battlestaves", "4479", "Varrock Diary (Easy)", "4539", "Claimed today"),
        DiaryDaily("ardougne_essence", "Pure essence (Ardougne)", "4459", "Ardougne Diary (Medium)", "4547", "Claimed today"),
        DiaryDaily("lundail_runes", "Lundail's runes (Wilderness)", "4466", "Wilderness Diary (Easy)", "4540", "Collected today"),
        DiaryDaily("seers_flax", "Flax (Seers' Village)", "4475", "Kandarin Diary (Easy)", "4559", "Exchanged today"),
        DiaryDaily("rantz_arrows", "Ogre arrows (Rantz)", "4471", "Western Diary (Easy)", "4563", "Collected today"),
        DiaryDaily("kourend_dynamite", "Dynamite (Kourend)", "7926", "Kourend Diary (Medium)", "7939", "Claimed today")
    )

    fun evaluate(varbits: Map<String, Int>, varps: Map<String, Int>): List<DailyTaskUiModel> {
        fun v(id: String) = varbits[id] ?: 0
        val tasks = mutableListOf<DailyTaskUiModel>()

        // NMZ herb boxes: not for ironmen; up to 15 purchases a day
        val ironman = v(IRONMAN)
        val purchased = v("3961")
        val points = varps["1060"] ?: 0
        tasks += when {
            ironman != NOT_IRONMAN -> DailyTaskUiModel(
                "nmz_herb_boxes", "NMZ herb boxes", "Not available to ironmen", "Not available", false, ResetCategory.DAILY
            )
            else -> DailyTaskUiModel(
                id = "nmz_herb_boxes",
                title = "NMZ herb boxes",
                subtitle = "Up to $HERB_BOX_MAX a day",
                statusText = if (purchased >= HERB_BOX_MAX) "All $HERB_BOX_MAX claimed today"
                else "$purchased/$HERB_BOX_MAX claimed today • ${formatQuantity(points)} points",
                isAvailable = purchased < HERB_BOX_MAX,
                category = ResetCategory.DAILY
            )
        }

        for (daily in DIARY_DAILIES) {
            val unlocked = v(daily.gateVarbit) == 1
            val claimed = v(daily.claimedVarbit) != 0
            tasks += DailyTaskUiModel(
                id = daily.id,
                title = daily.title,
                subtitle = daily.gateText,
                statusText = when {
                    !unlocked -> "Requires ${daily.gateText}"
                    claimed -> daily.claimedText
                    else -> "Available"
                },
                isAvailable = unlocked && !claimed,
                category = ResetCategory.DAILY
            )
        }

        // Bert's sand: not for ultimate ironmen; needs Hand in the Sand
        val sandUnlocked = ironman != ULTIMATE_IRONMAN && v("1527") >= SAND_QUEST_COMPLETE
        val sandClaimed = v("4549") != 0
        tasks += DailyTaskUiModel(
            id = "bert_sand",
            title = "Bert's sand",
            subtitle = "Requires Hand in the Sand",
            statusText = when {
                !sandUnlocked -> "Not available"
                sandClaimed -> "Delivered today"
                else -> "Available"
            },
            isAvailable = sandUnlocked && !sandClaimed,
            category = ResetCategory.DAILY
        )

        // Morytanian bonemeal: 13 for the Medium diary, 13 more for Hard, 13 more for Elite (Elite counts only with Hard)
        val boneUnlocked = v("4488") == 1
        val boneMax = if (v("4489") == 1) {
            BONEMEAL_PER_DIARY * (if (v("4490") == 1) 3 else 2)
        } else {
            BONEMEAL_PER_DIARY
        }
        val boneCollected = v("4543")
        tasks += DailyTaskUiModel(
            id = "morytania_bonemeal",
            title = "Bonemeal and slime (Morytania)",
            subtitle = "Morytania Diary (Medium or better)",
            statusText = when {
                !boneUnlocked -> "Requires Morytania Diary (Medium)"
                boneCollected >= boneMax -> "All $boneMax collected today"
                else -> "$boneCollected/$boneMax collected today"
            },
            isAvailable = boneUnlocked && boneCollected < boneMax,
            category = ResetCategory.DAILY
        )

        // Tears of Guthix: RuneLite has no rule for these two values, so they are shown as they are, not interpreted
        tasks += DailyTaskUiModel(
            id = "tears_of_guthix",
            title = "Tears of Guthix",
            subtitle = "Raw values (meaning unverified)",
            statusText = "Countdown: ${v("5099")} • Quest points check: ${v("456")}",
            isAvailable = false,
            category = ResetCategory.WEEKLY
        )

        return tasks.sortedWith(
            compareByDescending<DailyTaskUiModel> { it.isAvailable }
                .thenBy { it.category }
                .thenBy { it.title }
        )
    }
}
