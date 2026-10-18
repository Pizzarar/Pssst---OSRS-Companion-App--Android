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
    val streak: Int = 0
) {
    val hasTask: Boolean get() = amount > 0
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
