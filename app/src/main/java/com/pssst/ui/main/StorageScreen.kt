package com.pssst.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsSegmentedTabs
import com.pssst.theme.OsrsSlotEmpty
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.osrsPanel

enum class StorageSubTab(val title: String) {
    BANK("Bank"),
    INVENTORY("Inventory"),
    POTIONS("Potion Storage")
}

/** Shown instead of the bank when the plugin's Include Bank setting is off. */
@Composable
private fun BankSharingOff() {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(
            text = "Bank sharing is off. Turn on Include Bank in RuneLite \u2192 Pssst Companion \u2192 Phone to see your bank and potion storage here.",
            style = MaterialTheme.typography.bodyMedium,
            color = OsrsTextMuted
        )
    }
}

@Composable
fun StorageScreen(
    state: MainDashboardUiState,
    onSearchQueryChanged: (String) -> Unit,
    onBankSortModeChanged: (BankSortMode) -> Unit,
    onPotionSearchQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable { mutableStateOf(StorageSubTab.BANK) }
    val tabs = StorageSubTab.entries

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Sub-Tab Switcher
        OsrsSegmentedTabs(
            tabs = tabs.map { tab ->
                val label = when (tab) {
                    StorageSubTab.BANK -> "Bank (${state.bankItems.size})"
                    StorageSubTab.INVENTORY -> "Inventory (${state.inventorySlots.count { it.item != null }}/28)"
                    StorageSubTab.POTIONS -> "Potions (${state.potionStorageItems.size})"
                }
                label to 0
            },
            selectedIndex = tabs.indexOf(selectedTab),
            onTabSelected = { selectedTab = tabs[it] },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Sub-Tab Content
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                StorageSubTab.BANK -> if (!state.bankShared) BankSharingOff() else BankScreen(
                    state = state,
                    onSearchQueryChanged = onSearchQueryChanged,
                    onBankSortModeChanged = onBankSortModeChanged
                )
                StorageSubTab.INVENTORY -> InventoryScreen(
                    state = state
                )
                StorageSubTab.POTIONS -> if (!state.bankShared) BankSharingOff() else PotionStorageScreen(
                    state = state,
                    onPotionSearchQueryChanged = onPotionSearchQueryChanged
                )
            }
        }
    }
}
