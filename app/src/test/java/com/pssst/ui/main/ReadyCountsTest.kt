package com.pssst.ui.main

import org.junit.Assert.assertEquals
import org.junit.Test

class ReadyCountsTest {
    @Test
    fun aFreshStateHasNothingReady() {
        val counts = MainDashboardUiState().readyCounts(nowMs = 1_000L)
        assertEquals(ReadyCounts(0, 0, 0, 0, 0), counts)
    }

    @Test
    fun theSlayerChipShowsKillsLeftAndNothingWithoutATask() {
        assertEquals(0, MainDashboardUiState(slayer = SlayerUiModel(amount = 0)).readyCounts(1L).slayerLeft)
        assertEquals(87, MainDashboardUiState(slayer = SlayerUiModel(taskName = "Hydras", amount = 87)).readyCounts(1L).slayerLeft)
    }

    @Test
    fun paymentsLeftIsHowManyTimesWhatYouHoldCoversThePayment() {
        fun payment(owned: Int, required: Int) = FarmingPaymentUiModel(
            cropName = "Oak", patchCategory = "Trees", farmingLevel = 15, paymentItemId = 5968,
            paymentItemName = "Tomatoes(5)", requiredQuantity = required, ownedInBankAndInv = owned
        )
        assertEquals(3, payment(owned = 7, required = 2).paymentsLeft)
        assertEquals(0, payment(owned = 7, required = 25).paymentsLeft)
        assertEquals(0, payment(owned = 7, required = 0).paymentsLeft)
    }

    @Test
    fun initialsTellTilesApartUntilTheIconArrives() {
        assertEquals("A4", itemInitials("Antidote++(4)"))
        assertEquals("BC", itemInitials("Bandos chestplate"))
        assertEquals("Ab", itemInitials("Abyssal"))
        assertEquals("?", itemInitials(""))
    }

    @Test
    fun bankSortsByTotalValueByPriceEachAndByQuantity() {
        val items = listOf(
            ItemUiModel(id = 1, quantity = 70_000, name = "Blood rune", gePrice = 344),
            ItemUiModel(id = 2, quantity = 1, name = "Bandos chestplate", gePrice = 22_000_000),
            ItemUiModel(id = 3, quantity = 2_000_000, name = "Coins", gePrice = 1)
        )
        fun order(mode: BankSortMode) = MainDashboardUiState(bankItems = items, bankSortMode = mode).filteredBankItems.map { it.name }
        // 70,000 cheap runes are worth more than one chestplate: that is why "Total value" can look odd
        assertEquals(listOf("Blood rune", "Bandos chestplate", "Coins"), order(BankSortMode.VALUE))
        assertEquals(listOf("Bandos chestplate", "Blood rune", "Coins"), order(BankSortMode.PRICE))
        assertEquals(listOf("Coins", "Blood rune", "Bandos chestplate"), order(BankSortMode.QUANTITY))
    }
}
