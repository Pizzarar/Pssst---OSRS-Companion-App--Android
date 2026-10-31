package com.pssst.ui.main

import com.pssst.data.local.GoalConfig
import com.pssst.data.network.model.BirdhouseEntryDto
import com.pssst.data.network.model.FarmingEntryDto
import com.pssst.util.FarmingTiming
import com.pssst.util.TimeFormat
import com.pssst.util.OsrsXpTable
import java.util.Locale

data class ItemUiModel(
    val id: Int,
    val quantity: Int,
    val name: String = "",
    val iconBase64: String? = null,
    val gePrice: Long = 0L,
    val haPrice: Long = 0L,
    val tab: Int? = null,
    val formattedQuantity: String = formatQuantity(quantity)
) {
    val stackValue: Long get() = quantity.toLong() * gePrice
    val stackHaValue: Long get() = quantity.toLong() * haPrice
}

data class InventorySlotUiModel(
    val slotIndex: Int,
    val item: ItemUiModel? = null
)

data class MiscellaniaUiModel(
    val approvalPercent: Int = 0,
    val cofferAmount: Long = 0L,
    val formattedCoffer: String = formatGp(cofferAmount),
    val blastFurnaceCoffer: Long = 0L,
    val nmzCoffer: Long = 0L,
    val nmzRewardPoints: Int = 0,
    val pohServantUses: Int = 0,
    val lmsCoffer: Long = 0L
) {
    val formattedBlastFurnaceCoffer: String get() = formatGp(blastFurnaceCoffer)
    val blastFurnaceTimeLabel: String
        get() {
            if (blastFurnaceCoffer <= 0L) return "Empty (72k GP/hr)"
            val totalSeconds = ((blastFurnaceCoffer.toDouble() / 72_000.0) * 3600.0).toLong()
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            return if (hours > 0) "${hours}h ${minutes}m at 72k/hr" else "${minutes}m at 72k/hr"
        }
    val formattedNmzCoffer: String get() = formatGp(nmzCoffer)
    val formattedNmzPoints: String get() = formatQuantity(nmzRewardPoints)
    val formattedLmsCoffer: String get() = formatGp(lmsCoffer)
}

data class SlayerUiModel(
    val taskName: String = "No Active Task",
    val amount: Int = 0,
    val taskLocation: String = "",
    val initialAmount: Int = 0,
    val points: Int = 0,
    val streak: Int = 0,
    val itemId: Int = ENCHANTED_GEM_ITEM_ID,
    val iconBase64: String? = null,
    val storageUnlocked: Boolean = false,
    val storedTaskName: String = "",
    val storedAmount: Int = 0,
    val storedItemId: Int = ENCHANTED_GEM_ITEM_ID,
    val storedIconBase64: String? = null
) {
    val hasTask: Boolean get() = amount > 0

    /**
     * The Slayer emblem turns gold once a task is finished and the player is between masters.
     * `amount == 0` is indistinguishable from "never assigned", so a fresh account (no points and
     * no streak) keeps the plain emblem.
     */
    val isEmblemGilded: Boolean get() = !hasTask && (points > 0 || streak > 0)
    val hasStoredTask: Boolean get() = storedAmount > 0 && storedTaskName.isNotBlank()
    val progress: Float
        get() = when {
            !hasTask -> 0f
            initialAmount > 0 && initialAmount >= amount ->
                ((initialAmount - amount).toFloat() / initialAmount.toFloat()).coerceIn(0f, 1f)
            else -> 1f
        }

    companion object {
        const val ENCHANTED_GEM_ITEM_ID: Int = 4155

        fun itemIdForTask(taskName: String?, explicitItemId: Int = 0): Int {
            if (explicitItemId > 0) return explicitItemId
            if (taskName.isNullOrBlank()) return ENCHANTED_GEM_ITEM_ID
            return TASK_ITEM_IDS[taskName.trim().lowercase(Locale.ROOT)] ?: ENCHANTED_GEM_ITEM_ID
        }

        private val TASK_ITEM_IDS: Map<String, Int> = mapOf(
            "aberrant spectres" to 4144,
            "abyssal demons" to 4149,
            "the abyssal sire" to 13262,
            "the alchemical hydra" to 22746,
            "ankou" to 20095,
            "aquanites" to 32885,
            "araxxor" to 29836,
            "araxytes" to 29788,
            "aviansies" to 13504,
            "bandits" to 4625,
            "banshees" to 4135,
            "barrows brothers" to 4732,
            "basilisks" to 4139,
            "bats" to 20875,
            "bears" to 13462,
            "birds" to 314,
            "black demons" to 20026,
            "black dragons" to 12524,
            "black knights" to 1165,
            "bloodveld" to 4141,
            "blue dragons" to 12520,
            "brine rats" to 11047,
            "callisto" to 13178,
            "catablepon" to 9008,
            "cave bugs" to 4521,
            "cave crawlers" to 4134,
            "cave horrors" to 8900,
            "cave kraken" to 3272,
            "cave slimes" to 4520,
            "cerberus" to 13247,
            "chaos druids" to 20595,
            "the chaos elemental" to 11995,
            "the chaos fanatic" to 4675,
            "cockatrice" to 4137,
            "cows" to 11919,
            "crabs" to 7537,
            "crawling hands" to 4133,
            "crazy archaeologists" to 11990,
            "crocodiles" to 10149,
            "custodian stalkers" to 30994,
            "dagannoth" to 8141,
            "dagannoth kings" to 12644,
            "dark beasts" to 6637,
            "dark warriors" to 1151,
            "deranged archaeologist" to 21629,
            "dogs" to 8132,
            "drakes" to 23041,
            "duke sucellus" to 28250,
            "dust devils" to 4145,
            "dwarves" to 11200,
            "earth warriors" to 12221,
            "elves" to 6105,
            "ents" to 8174,
            "fever spiders" to 6709,
            "fire giants" to 30622,
            "fleshcrawlers" to 13459,
            "fossil island wyverns" to 21507,
            "frost dragons" to 31729,
            "gargoyles" to 4147,
            "general graardor" to 12650,
            "ghosts" to 552,
            "ghouls" to 6722,
            "the giant mole" to 12646,
            "goblins" to 13447,
            "greater demons" to 20023,
            "green dragons" to 12518,
            "the grotesque guardians" to 21750,
            "gryphons" to 32884,
            "harpie bug swarms" to 7050,
            "hellhounds" to 8137,
            "hill giants" to 13474,
            "hobgoblins" to 8133,
            "hydras" to 23042,
            "icefiends" to 4671,
            "ice giants" to 30624,
            "ice warriors" to 12293,
            "infernal mages" to 4140,
            "tztok-jad" to 13225,
            "jellies" to 4142,
            "jungle horrors" to 13486,
            "kalphites" to 8139,
            "the kalphite queen" to 12647,
            "killerwatts" to 7160,
            "the king black dragon" to 12653,
            "the cave kraken boss" to 12655,
            "kree'arra" to 12649,
            "k'ril tsutsaroth" to 12652,
            "kurask" to 4146,
            "lava dragons" to 11992,
            "lesser demons" to 20020,
            "lesser nagua" to 28823,
            "lizardmen" to 13391,
            "lizards" to 6695,
            "the maggot king" to 33642,
            "magic axes" to 1363,
            "mammoths" to 10516,
            "metal dragons" to 8142,
            "minotaurs" to 13456,
            "mogres" to 6661,
            "molanisks" to 10997,
            "monkeys" to 13450,
            "moss giants" to 22374,
            "mutated zygomites" to 7420,
            "nechryael" to 4148,
            "ogres" to 13477,
            "otherworldly beings" to 6109,
            "the phantom muspah" to 27590,
            "pirates" to 8950,
            "pyrefiends" to 4138,
            "rats" to 300,
            "red dragons" to 8134,
            "revenants" to 21816,
            "rockslugs" to 4136,
            "rogues" to 5554,
            "sarachnis" to 23495,
            "scabarites" to 9028,
            "scorpia" to 13181,
            "scorpions" to 13459,
            "sea snakes" to 7576,
            "shades" to 546,
            "shadow warriors" to 1165,
            "the shellbane gryphon" to 31287,
            "skeletal wyverns" to 6811,
            "skeletons" to 8131,
            "smoke devils" to 5349,
            "sourhogs" to 24944,
            "spiders" to 8135,
            "spiritual creatures" to 11840,
            "suqahs" to 9079,
            "terror dogs" to 10591,
            "the leviathan" to 28252,
            "the whisperer" to 28246,
            "the thermonuclear smoke devil" to 12648,
            "trolls" to 8136,
            "turoth" to 4143,
            "tzhaar" to 13498,
            "vampyres" to 1549,
            "vardorvis" to 28248,
            "venators" to 33826,
            "venenatis" to 13177,
            "vet'ion" to 13179,
            "vorkath" to 21992,
            "wall beasts" to 4519,
            "warped creatures" to 28582,
            "waterfiends" to 571,
            "werewolves" to 2952,
            "wolves" to 958,
            "wyrms" to 23040,
            "commander zilyana" to 12651,
            "zombies" to 6722,
            "tzkal-zuk" to 22319,
            "zulrah" to 12921
        )
    }
}

enum class ResetCategory {
    DAILY,
    WEEKLY
}

data class DailyTaskUiModel(
    val id: String,
    val title: String,
    val subtitle: String,
    val statusText: String,
    val isAvailable: Boolean,
    val category: ResetCategory = ResetCategory.DAILY,
    val itemId: Int = 0,
    val iconBase64: String? = null
)

enum class FarmingStatus { UNKNOWN, WEEDS, EMPTY, GROWING, REGROWING, FILLING, GROWN, DISEASED, DEAD }

/**
 * A farming patch as last observed in-game. Status and progress depend on the current time, so they are functions of
 * `nowMs` (the UI supplies a ticking clock) instead of being baked in when the state is parsed.
 */
data class FarmingPatchUiModel(
    val patchId: String,
    val regionName: String,
    val patchLabel: String,
    val cropName: String?,
    val itemId: Int,
    val iconBase64: String? = null,
    val cropState: String?,
    val stage: Int,
    val stages: Int,
    val tickMinutes: Int,
    val observedAtMs: Long,
    val doneAtMs: Long?
) {
    fun status(nowMs: Long): FarmingStatus = when {
        cropState == null -> FarmingStatus.UNKNOWN
        cropName == "Weeds" -> FarmingStatus.WEEDS
        cropState == "EMPTY" -> FarmingStatus.EMPTY
        cropState == "DISEASED" -> FarmingStatus.DISEASED
        cropState == "DEAD" -> FarmingStatus.DEAD
        cropState == "FILLING" -> FarmingStatus.FILLING
        cropState == "GROWING" -> if (doneAtMs != null && nowMs >= doneAtMs) FarmingStatus.GROWN else FarmingStatus.GROWING
        cropState == "HARVESTABLE" -> if (doneAtMs != null && nowMs < doneAtMs) FarmingStatus.REGROWING else FarmingStatus.GROWN
        else -> FarmingStatus.UNKNOWN
    }

    fun isGrown(nowMs: Long): Boolean = status(nowMs) == FarmingStatus.GROWN

    fun progress(nowMs: Long): Float = when (status(nowMs)) {
        FarmingStatus.GROWN -> 1f
        FarmingStatus.GROWING, FarmingStatus.REGROWING -> {
            val lastStage = (stages - 1).coerceAtLeast(1)
            (FarmingTiming.stageAt(toDto(), nowMs).toFloat() / lastStage).coerceIn(0f, 1f)
        }
        else -> 0f
    }

    fun statusText(nowMs: Long): String {
        val crop = cropName ?: "Unknown crop"
        return when (status(nowMs)) {
            FarmingStatus.UNKNOWN -> "Unrecognised state"
            FarmingStatus.WEEDS -> "Weeds"
            FarmingStatus.EMPTY -> "Empty"
            FarmingStatus.DISEASED -> "$crop • Diseased"
            FarmingStatus.DEAD -> "$crop • Dead"
            FarmingStatus.FILLING -> "$crop • Filling"
            FarmingStatus.GROWN -> "$crop • Fully grown"
            FarmingStatus.REGROWING -> "$crop • Regrowing"
            FarmingStatus.GROWING -> "$crop • Stage ${FarmingTiming.stageAt(toDto(), nowMs) + 1}/${(stages - 1).coerceAtLeast(1)}"
        }
    }

    // Timing only needs these fields; rebuilding the DTO keeps the arithmetic in one place (FarmingTiming)
    private fun toDto() = FarmingEntryDto(
        observedAt = observedAtMs, cropState = cropState, stage = stage, stages = stages, tickMinutes = tickMinutes
    )

    companion object {
        fun from(dto: FarmingEntryDto, iconBase64: String? = null): FarmingPatchUiModel {
            val type = dto.patchType.lowercase().replace('_', ' ').replaceFirstChar { it.uppercase() }
            val suffix = if (dto.patchName.isBlank()) "" else " (${dto.patchName})"
            val resolvedItemId = if (dto.itemId > 0) dto.itemId else 6055
            return FarmingPatchUiModel(
                patchId = dto.patchKey,
                regionName = dto.regionName,
                patchLabel = "${dto.regionName} $type$suffix",
                cropName = dto.cropName,
                itemId = resolvedItemId,
                iconBase64 = iconBase64,
                cropState = dto.cropState,
                stage = dto.stage,
                stages = dto.stages,
                tickMinutes = dto.tickMinutes,
                observedAtMs = dto.observedAt,
                doneAtMs = FarmingTiming.doneAtMs(dto)
            )
        }
    }
}

enum class BirdHouseStatus { UNKNOWN, EMPTY, BUILT, SEEDED, READY }

/**
 * A birdhouse space as last seen on Fossil Island. Whether it is ready depends on the current time (50 minutes after
 * the seeded value was first observed, which the plugin supplies as readyAtMs), so status is a function of `nowMs`.
 */
data class BirdHouseUiModel(
    val varpId: String,
    val spaceName: String,
    val houseType: String?,
    val itemId: Int = 21512,
    val iconBase64: String? = null,
    val state: String,
    val observedAtMs: Long,
    val readyAtMs: Long
) {
    fun status(nowMs: Long): BirdHouseStatus = when (state) {
        "SEEDED" -> if (nowMs >= readyAtMs) BirdHouseStatus.READY else BirdHouseStatus.SEEDED
        "BUILT" -> BirdHouseStatus.BUILT
        "EMPTY" -> BirdHouseStatus.EMPTY
        else -> BirdHouseStatus.UNKNOWN
    }

    fun isReady(nowMs: Long): Boolean = status(nowMs) == BirdHouseStatus.READY

    fun statusText(nowMs: Long): String = when (status(nowMs)) {
        BirdHouseStatus.READY -> "Seeded \u2022 ready"
        BirdHouseStatus.SEEDED -> "Seeded \u2022 ready in ~${TimeFormat.formatDuration(readyAtMs - nowMs)}"
        BirdHouseStatus.BUILT -> "Built, not seeded"
        BirdHouseStatus.EMPTY -> "No birdhouse"
        BirdHouseStatus.UNKNOWN -> "Unrecognised state"
    }

    companion object {
        val ALL_BIRDHOUSE_ITEM_IDS = listOf(21512, 21515, 21518, 21521, 22192, 22195, 22198, 22201, 22204)

        fun itemIdForType(rawType: String?): Int = when (rawType?.uppercase()) {
            "OAK" -> 21515
            "WILLOW" -> 21518
            "TEAK" -> 21521
            "MAPLE" -> 22192
            "MAHOGANY" -> 22195
            "YEW" -> 22198
            "MAGIC" -> 22201
            "REDWOOD" -> 22204
            else -> 21512
        }

        fun from(dto: BirdhouseEntryDto, iconBase64: String? = null): BirdHouseUiModel {
            val type = when (dto.houseType) {
                null -> null
                "NORMAL" -> "Regular birdhouse"
                else -> "${dto.houseType.lowercase().replaceFirstChar { it.uppercase() }} birdhouse"
            }
            val itemId = itemIdForType(dto.houseType)
            return BirdHouseUiModel(
                varpId = dto.varpId.toString(),
                spaceName = dto.spaceName,
                houseType = type,
                itemId = itemId,
                iconBase64 = iconBase64,
                state = dto.state,
                observedAtMs = dto.observedAt,
                readyAtMs = dto.readyAt
            )
        }
    }
}

enum class BankSortMode(val label: String) {
    DEFAULT("Slot order"),
    /** Quantity x price: a big stack of cheap runes can outrank one expensive item. */
    VALUE("Total value"),
    /** Price each: the most expensive item first. */
    PRICE("Price each"),
    QUANTITY("Quantity")
}

enum class GeOfferState {
    EMPTY,
    BUYING,
    SELLING,
    BOUGHT,
    SOLD,
    CANCELLED_BUY,
    CANCELLED_SELL
}

data class GeOfferSlotUiModel(
    val slot: Int,
    val itemId: Int = 0,
    val itemName: String = "",
    val iconBase64: String? = null,
    val quantitySold: Int = 0,
    val totalQuantity: Int = 0,
    val price: Int = 0,
    val spent: Long = 0L,
    val state: GeOfferState = GeOfferState.EMPTY
) {
    val isBuy: Boolean get() = state == GeOfferState.BUYING || state == GeOfferState.BOUGHT || state == GeOfferState.CANCELLED_BUY
    val isSell: Boolean get() = state == GeOfferState.SELLING || state == GeOfferState.SOLD || state == GeOfferState.CANCELLED_SELL
    val isComplete: Boolean get() = state == GeOfferState.BOUGHT || state == GeOfferState.SOLD
    val isEmpty: Boolean get() = state == GeOfferState.EMPTY
    val progress: Float get() = if (totalQuantity > 0) (quantitySold.toFloat() / totalQuantity).coerceIn(0f, 1f) else 0f
    val totalOfferValue: Long get() = totalQuantity.toLong() * price.toLong()
}

data class SkillUiModel(
    val name: String,
    val level: Int = 1,
    val boostedLevel: Int = 1,
    val xp: Long = 0L,
    val itemId: Int = SKILL_ITEM_IDS[name] ?: 0,
    val iconBase64: String? = null,
    val virtualLevel: Int = OsrsXpTable.levelForXp(xp),
    val nextLevelXp: Long = OsrsXpTable.xpForLevel(virtualLevel + 1),
    val xpRemaining: Long = OsrsXpTable.xpRemainingToNextLevel(xp, virtualLevel),
    val progress: Float = OsrsXpTable.progressToNextLevel(xp, virtualLevel)
) {
    companion object {
        val SKILL_ITEM_IDS: Map<String, Int> = mapOf(
            "Attack" to 4151,
            "Strength" to 11802,
            "Defence" to 11283,
            "Ranged" to 853,
            "Prayer" to 536,
            "Magic" to 556,
            "Runecraft" to 561,
            "Construction" to 8794,
            "Hitpoints" to 9769,
            "Agility" to 11849,
            "Herblore" to 257,
            "Thieving" to 1523,
            "Crafting" to 1755,
            "Fletching" to 314,
            "Slayer" to 4155,
            "Hunter" to 10034,
            "Mining" to 12797,
            "Smithing" to 2347,
            "Fishing" to 311,
            "Cooking" to 385,
            "Firemaking" to 590,
            "Woodcutting" to 1357,
            "Farming" to 5331
        )
    }
}

data class PotionStorageItemUiModel(
    val itemId: Int,
    val name: String = "",
    val iconBase64: String? = null,
    val totalDoses: Int = 0,
    val gePrice: Long = 0L
) {
    val fourDoseEquivalent: Int get() = totalDoses / 4
    val remainingDoses: Int get() = totalDoses % 4
    val estimatedValue: Long get() = (fourDoseEquivalent.toLong() * gePrice) + ((remainingDoses * gePrice) / 4)
}

data class FarmingPaymentUiModel(
    val cropName: String,
    val patchCategory: String,
    val farmingLevel: Int,
    val paymentItemId: Int,
    val paymentItemName: String,
    val paymentIconBase64: String? = null,
    val requiredQuantity: Int,
    val ownedInBankAndInv: Int,
    val hasEnough: Boolean = ownedInBankAndInv >= requiredQuantity
) {
    /** How many payments what you hold covers. */
    val paymentsLeft: Int get() = if (requiredQuantity > 0) ownedInBankAndInv / requiredQuantity else 0
}

enum class QuestState {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED
}

data class QuestUiModel(
    val name: String,
    val state: QuestState = QuestState.NOT_STARTED
)

enum class QuestFilter(val label: String) {
    ALL("All"),
    COMPLETED("Completed"),
    IN_PROGRESS("In Progress"),
    NOT_STARTED("Not Started")
}

data class MainDashboardUiState(
    val uuid: String = "",
    val rememberedUuid: String = "",
    val serverUrl: String = "",
    val hasConfiguredUuid: Boolean = false,
    val hasSyncedData: Boolean = false,
    val lastUpdated: Long = 0L,
    val lastSyncedAt: Long = 0L,
    /** When the game last fed the snapshot (observedAt), falling back to the server's updated_at. */
    val gameObservedAt: Long = 0L,
    /** False when the player turned off Include Bank in RuneLite: the snapshot has no bank or potion storage. */
    val bankShared: Boolean = true,
    val lastSyncStatus: String? = null,
    val bankItems: List<ItemUiModel> = emptyList(),
    val inventorySlots: List<InventorySlotUiModel> = (0 until 28).map { InventorySlotUiModel(it) },
    val miscellania: MiscellaniaUiModel = MiscellaniaUiModel(),
    val slayer: SlayerUiModel = SlayerUiModel(),
    val farmingPatches: List<FarmingPatchUiModel> = emptyList(),
    val birdHouses: List<BirdHouseUiModel> = emptyList(),
    val dailyTasks: List<DailyTaskUiModel> = emptyList(),
    val geOffers: List<GeOfferSlotUiModel> = (1..8).map { GeOfferSlotUiModel(it) },
    val skills: List<SkillUiModel> = emptyList(),
    val potionStorageItems: List<PotionStorageItemUiModel> = emptyList(),
    val farmingPayments: List<FarmingPaymentUiModel> = emptyList(),
    val quests: List<QuestUiModel> = emptyList(),
    val topBarIcons: Map<Int, String?> = emptyMap(),
    val notificationsEnabled: Boolean = true,
    /** When the newest item price was measured (epoch ms), 0 if unknown */
    val pricesAsOfMs: Long = 0L,
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val potionSearchQuery: String = "",
    val questSearchQuery: String = "",
    val questFilter: QuestFilter = QuestFilter.ALL,
    val bankSortMode: BankSortMode = BankSortMode.DEFAULT,
    val isIronman: Boolean = false,
    val goalConfig: GoalConfig = GoalConfig(),
    val goalTargetItem: ItemUiModel? = null,
    val goalItemSearchResults: List<ItemUiModel> = emptyList()
) {
    val totalBankValue: Long
        get() = bankItems.sumOf { it.stackValue }

    val formattedBankValue: String
        get() = formatGp(totalBankValue)

    val totalInventoryValue: Long
        get() = inventorySlots.mapNotNull { it.item }.sumOf { it.stackValue }

    val formattedInventoryValue: String
        get() = formatGp(totalInventoryValue)

    val totalPotionStorageValue: Long
        get() = potionStorageItems.sumOf { it.estimatedValue }

    val totalActiveGeValue: Long
        get() = geOffers.filter { !it.isEmpty }.sumOf { it.totalOfferValue }

    val totalAccountWealth: Long
        get() = totalBankValue + totalInventoryValue + totalPotionStorageValue + totalActiveGeValue

    val formattedTotalAccountWealth: String
        get() = formatGp(totalAccountWealth)

    val totalLevel: Int
        get() = skills.sumOf { it.level }

    val totalXp: Long
        get() = skills.sumOf { it.xp }

    val filteredBankItems: List<ItemUiModel>
        get() {
            val list = if (searchQuery.isBlank()) {
                bankItems
            } else {
                val q = searchQuery.trim()
                bankItems.filter {
                    it.id.toString().contains(q) || (it.name.isNotBlank() && it.name.contains(q, ignoreCase = true))
                }
            }
            return when (bankSortMode) {
                BankSortMode.DEFAULT -> list
                BankSortMode.VALUE -> list.sortedByDescending { it.stackValue }
                BankSortMode.PRICE -> list.sortedByDescending { it.gePrice }
                BankSortMode.QUANTITY -> list.sortedByDescending { it.quantity }
            }
        }

    val filteredPotionStorageItems: List<PotionStorageItemUiModel>
        get() {
            if (potionSearchQuery.isBlank()) return potionStorageItems
            val q = potionSearchQuery.trim()
            return potionStorageItems.filter {
                it.itemId.toString().contains(q) || (it.name.isNotBlank() && it.name.contains(q, ignoreCase = true))
            }
        }

    val filteredQuests: List<QuestUiModel>
        get() {
            val byFilter = when (questFilter) {
                QuestFilter.ALL -> quests
                QuestFilter.COMPLETED -> quests.filter { it.state == QuestState.COMPLETED }
                QuestFilter.IN_PROGRESS -> quests.filter { it.state == QuestState.IN_PROGRESS }
                QuestFilter.NOT_STARTED -> quests.filter { it.state == QuestState.NOT_STARTED }
            }
            if (questSearchQuery.isBlank()) return byFilter
            val q = questSearchQuery.trim()
            return byFilter.filter { it.name.contains(q, ignoreCase = true) }
        }
}

fun formatQuantity(quantity: Int): String {
    return when {
        quantity >= 10_000_000 -> "${quantity / 1_000_000}M"
        quantity >= 100_000 -> "${quantity / 1_000}K"
        quantity >= 10_000 -> "${quantity / 1_000}K"
        else -> quantity.toString()
    }
}

fun formatXp(xp: Long): String {
    return when {
        xp >= 1_000_000_000L -> String.format(Locale.US, "%.2fB", xp / 1_000_000_000.0)
        xp >= 10_000_000L -> String.format(Locale.US, "%.1fM", xp / 1_000_000.0)
        xp >= 1_000_000L -> String.format(Locale.US, "%.2fM", xp / 1_000_000.0)
        xp >= 10_000L -> "${xp / 1_000L}K"
        else -> xp.toString()
    }
}

fun formatGp(gp: Long): String {
    return when {
        gp >= 1_000_000_000L -> String.format(Locale.US, "%.2fB GP", gp / 1_000_000_000.0)
        gp >= 1_000_000L -> String.format(Locale.US, "%.1fM GP", gp / 1_000_000.0)
        gp >= 1_000L -> String.format(Locale.US, "%.1fK GP", gp / 1_000.0)
        else -> "$gp GP"
    }
}
