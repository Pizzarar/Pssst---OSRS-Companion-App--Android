package com.pssst.ui.main

import com.pssst.util.OsrsXpTable
import java.util.Locale

data class ItemUiModel(
    val id: Int,
    val quantity: Int,
    val name: String = "",
    val iconBase64: String? = null,
    val gePrice: Long = 0L,
    val formattedQuantity: String = formatQuantity(quantity)
) {
    val stackValue: Long get() = quantity.toLong() * gePrice
}

data class InventorySlotUiModel(
    val slotIndex: Int,
    val item: ItemUiModel? = null
)

data class MiscellaniaUiModel(
    val approvalPercent: Int = 0,
    val cofferAmount: Long = 0L,
    val formattedCoffer: String = formatGp(cofferAmount)
)

data class SlayerUiModel(
    val taskName: String = "No Active Task",
    val amount: Int = 0,
    val taskLocation: String = ""
)

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
    val estimatedProfitGp: Long = 0L
)

data class FarmingPatchUiModel(
    val patchId: String,
    val patchName: String,
    val stage: Int,
    val maxStage: Int = 8,
    val isGrown: Boolean,
    val statusText: String,
    val cropName: String = "Herb"
) {
    val progress: Float get() = if (maxStage > 0) (stage.toFloat() / maxStage).coerceIn(0f, 1f) else 0f
}

data class BirdHouseUiModel(
    val varpId: String,
    val spaceName: String,
    val houseType: String,
    val stateText: String,
    val isReady: Boolean,
    val isSeeded: Boolean
)

enum class BankSortMode(val label: String) {
    DEFAULT("Slot Order"),
    VALUE("Value (High)"),
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
    val virtualLevel: Int = OsrsXpTable.levelForXp(xp),
    val nextLevelXp: Long = OsrsXpTable.xpForLevel(virtualLevel + 1),
    val xpRemaining: Long = OsrsXpTable.xpRemainingToNextLevel(xp, virtualLevel),
    val progress: Float = OsrsXpTable.progressToNextLevel(xp, virtualLevel)
)

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
)

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
    val lastUpdated: Long = 0L,
    val lastSyncedAt: Long = 0L,
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
    val notificationsEnabled: Boolean = true,
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val potionSearchQuery: String = "",
    val questSearchQuery: String = "",
    val questFilter: QuestFilter = QuestFilter.ALL,
    val bankSortMode: BankSortMode = BankSortMode.DEFAULT
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

fun formatGp(gp: Long): String {
    return when {
        gp >= 1_000_000_000L -> String.format(Locale.US, "%.2fB GP", gp / 1_000_000_000.0)
        gp >= 1_000_000L -> String.format(Locale.US, "%.1fM GP", gp / 1_000_000.0)
        gp >= 1_000L -> String.format(Locale.US, "%.1fK GP", gp / 1_000.0)
        else -> "$gp GP"
    }
}
