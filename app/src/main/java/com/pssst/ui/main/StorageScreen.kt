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
import com.pssst.theme.OsrsSlotEmpty
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.osrsPanel

enum class StorageSubTab(val title: String) {
    BANK("Bank"),
    INVENTORY("Inventory"),
    POTIONS("Potion Storage")
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

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Sub-Tab Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .osrsPanel()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            StorageSubTab.entries.forEach { tab ->
                val isSelected = tab == selectedTab
                val label = when (tab) {
                    StorageSubTab.BANK -> "Bank (${state.bankItems.size})"
                    StorageSubTab.INVENTORY -> "Inventory (${state.inventorySlots.count { it.item != null }}/28)"
                    StorageSubTab.POTIONS -> "Potions (${state.potionStorageItems.size})"
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = if (isSelected) OsrsSlotEmpty else Color.Transparent,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable { selectedTab = tab }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) OsrsGold else OsrsTextMuted
                    )
                }
            }
        }

        // Sub-Tab Content
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                StorageSubTab.BANK -> BankScreen(
                    state = state,
                    onSearchQueryChanged = onSearchQueryChanged,
                    onBankSortModeChanged = onBankSortModeChanged
                )
                StorageSubTab.INVENTORY -> InventoryScreen(
                    state = state
                )
                StorageSubTab.POTIONS -> PotionStorageScreen(
                    state = state,
                    onPotionSearchQueryChanged = onPotionSearchQueryChanged
                )
            }
        }
    }
}
