package com.pssst.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pssst.theme.OsrsBorderOutline
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsHerbGreen
import com.pssst.theme.OsrsPanelLight
import com.pssst.theme.OsrsSlotEmpty
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.OsrsTextPrimary
import com.pssst.theme.OsrsYellow
import com.pssst.theme.osrsPanel

@Composable
fun OverviewScreen(
    state: MainDashboardUiState,
    onNavigateToTab: (DashboardTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val readyDailiesCount = state.dailyTasks.count { it.isAvailable }
    val nowMs by rememberNowMs()
    val readyBirdhousesCount = state.birdHouses.count { it.isReady(nowMs) }
    val readyFarmingCount = state.farmingPatches.count { it.isGrown(nowMs) }
    val activeGeCount = state.geOffers.count { !it.isEmpty }
    val completeGeCount = state.geOffers.count { it.isComplete }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 0. Phone mode: the address RuneLite needs, always in view (it changes if the router hands out a new one)
        if (state.serverUrl.isBlank()) {
            item {
                PhoneAddressCard(state)
            }
        }

        // 1. Total Net Wealth Card
        item {
            TotalWealthCard(state)
        }

        // 2. Active Run Status Chips
        item {
            QuickActionsBanner(
                readyDailies = readyDailiesCount,
                readyBirdhouses = readyBirdhousesCount,
                readyFarming = readyFarmingCount,
                activeGe = activeGeCount,
                completeGe = completeGeCount,
                onNavigateToTab = onNavigateToTab
            )
        }

        // 3. Slayer Task Card (moved out of Timers to free up vertical space)
        item {
            SlayerTaskCard(state.slayer)
        }

        // 4. Kingdom of Miscellania Card (moved out of Timers)
        item {
            MiscellaniaCard(state.miscellania)
        }
    }
}

@Composable
fun TotalWealthCard(state: MainDashboardUiState, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .osrsPanel()
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Account Wealth",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OsrsGold
                )
                Text(
                    text = state.formattedTotalAccountWealth,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = OsrsYellow
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Bank", style = MaterialTheme.typography.bodySmall, color = OsrsTextMuted)
                    Text(state.formattedBankValue, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = OsrsTextPrimary)
                }
                Column {
                    Text("Inventory", style = MaterialTheme.typography.bodySmall, color = OsrsTextMuted)
                    Text(state.formattedInventoryValue, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = OsrsTextPrimary)
                }
                Column {
                    Text("Potions", style = MaterialTheme.typography.bodySmall, color = OsrsTextMuted)
                    Text(formatGp(state.totalPotionStorageValue), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = OsrsTextPrimary)
                }
                Column {
                    Text("GE Offers", style = MaterialTheme.typography.bodySmall, color = OsrsTextMuted)
                    Text(formatGp(state.totalActiveGeValue), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = OsrsTextPrimary)
                }
            }
        }
    }
}

@Composable
fun QuickActionsBanner(
    readyDailies: Int,
    readyBirdhouses: Int,
    readyFarming: Int,
    activeGe: Int,
    completeGe: Int,
    onNavigateToTab: (DashboardTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .osrsPanel()
            .padding(12.dp)
    ) {
        Text(
            text = "Activity Status",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = OsrsGold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SuggestionChip(
                onClick = { onNavigateToTab(DashboardTab.TIMERS) },
                label = { Text("Farming: $readyFarming", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = if (readyFarming > 0) OsrsHerbGreen else OsrsSlotEmpty,
                    labelColor = if (readyFarming > 0) Color.Black else OsrsTextMuted
                ),
                border = if (readyFarming > 0) null else SuggestionChipDefaults.suggestionChipBorder(enabled = true, borderColor = OsrsBorderOutline)
            )
            SuggestionChip(
                onClick = { onNavigateToTab(DashboardTab.TIMERS) },
                label = { Text("Birdhouses: $readyBirdhouses", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = if (readyBirdhouses > 0) OsrsHerbGreen else OsrsSlotEmpty,
                    labelColor = if (readyBirdhouses > 0) Color.Black else OsrsTextMuted
                ),
                border = if (readyBirdhouses > 0) null else SuggestionChipDefaults.suggestionChipBorder(enabled = true, borderColor = OsrsBorderOutline)
            )
            SuggestionChip(
                onClick = { onNavigateToTab(DashboardTab.TIMERS) },
                label = { Text("Dailies: $readyDailies", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = if (readyDailies > 0) OsrsHerbGreen else OsrsSlotEmpty,
                    labelColor = if (readyDailies > 0) Color.Black else OsrsTextMuted
                ),
                border = if (readyDailies > 0) null else SuggestionChipDefaults.suggestionChipBorder(enabled = true, borderColor = OsrsBorderOutline)
            )
            if (activeGe > 0) {
                SuggestionChip(
                    onClick = { onNavigateToTab(DashboardTab.GE) },
                    label = { Text("GE: $completeGe/$activeGe", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (completeGe > 0) OsrsHerbGreen else OsrsSlotEmpty,
                        labelColor = if (completeGe > 0) Color.Black else OsrsYellow
                    ),
                    border = if (completeGe > 0) null else SuggestionChipDefaults.suggestionChipBorder(enabled = true, borderColor = OsrsBorderOutline)
                )
            }
        }
    }
}
