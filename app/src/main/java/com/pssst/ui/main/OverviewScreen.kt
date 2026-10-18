package com.pssst.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsHeaderDivider
import com.pssst.theme.OsrsHerbGreen
import com.pssst.theme.OsrsSlotEmpty
import com.pssst.theme.OsrsStatusBadge
import com.pssst.theme.OsrsTextMuted
import com.pssst.data.local.GoalConfig
import com.pssst.theme.OsrsTextPrimary
import com.pssst.theme.OsrsYellow
import com.pssst.theme.osrsPanel

/** The Overview tab's cards, in their default order. */
enum class OverviewCard(val title: String) {
    GOAL("Goal tracker"),
    WEALTH("Total account wealth"),
    STATUS("Ready now"),
    SLAYER("Slayer task"),
    MISCELLANIA("Kingdom of Miscellania"),
    PHONE("Phone address")
}

@Composable
fun OverviewScreen(
    state: MainDashboardUiState,
    onNavigateToTab: (DashboardTab) -> Unit,
    onSaveGoalConfig: (GoalConfig) -> Unit = {},
    onSearchGoalItem: (String) -> Unit = {},
    editing: Boolean = false,
    modifier: Modifier = Modifier
) {
    val nowMs by rememberNowMs()
    val ready = state.readyCounts(nowMs)
    val readyDailiesCount = ready.dailies
    val readyBirdhousesCount = ready.birdhouses
    val readyFarmingCount = ready.farming
    val activeGeCount = state.geOffers.count { !it.isEmpty }
    val completeGeCount = ready.geDone

    val context = androidx.compose.ui.platform.LocalContext.current
    val lastDeliveredTo by com.pssst.receiver.ReceiverPrefs.lastDeliveredTo(context).collectAsState()
    val changedTo = remember(lastDeliveredTo) {
        com.pssst.util.AddressChange.changedTo(lastDeliveredTo, com.pssst.util.PhoneAddresses.forDisplay(context))
    }
    var showAddressHelp by remember { mutableStateOf(false) }
    if (showAddressHelp) {
        KeepAddressDialog(onDismiss = { showAddressHelp = false })
    }

    // The player can reorder and hide these cards (Edit list)
    EditableList(
        listId = "overview",
        items = OverviewCard.entries.toList(),
        key = { it.name },
        label = { it.title },
        editing = editing,
        modifier = modifier.padding(16.dp),
        spacing = 12.dp,
        header = {
            // Not hideable: RuneLite can't reach the phone until this is fixed
            if (changedTo != null) {
                item(key = "__address_changed") {
                    AddressChangedBanner(changedTo, onHelp = { showAddressHelp = true })
                }
            }
        }
    ) { card ->
        when (card) {
            OverviewCard.GOAL -> GoalCard(
                state = state,
                onSaveGoalConfig = onSaveGoalConfig,
                onSearchGoalItem = onSearchGoalItem
            )
            OverviewCard.PHONE -> PhoneAddressCard(state, onHelp = { showAddressHelp = true })
            OverviewCard.WEALTH -> TotalWealthCard(state)
            OverviewCard.STATUS -> QuickActionsBanner(
                readyDailies = readyDailiesCount,
                readyBirdhouses = readyBirdhousesCount,
                readyFarming = readyFarmingCount,
                activeGe = activeGeCount,
                completeGe = completeGeCount,
                onNavigateToTab = onNavigateToTab
            )
            OverviewCard.SLAYER -> SlayerTaskCard(state.slayer)
            OverviewCard.MISCELLANIA -> MiscellaniaCard(state.miscellania)
        }
    }
}

@Composable
fun TotalWealthCard(state: MainDashboardUiState, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .osrsPanel(showRivets = true)
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

            Spacer(modifier = Modifier.height(6.dp))
            OsrsHeaderDivider()
            Spacer(modifier = Modifier.height(8.dp))

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
            .osrsPanel(showRivets = true)
            .padding(12.dp)
    ) {
        Text(
            text = "Activity Status",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = OsrsGold
        )
        Spacer(modifier = Modifier.height(6.dp))
        OsrsHeaderDivider()
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OsrsStatusBadge(
                text = "Farming: $readyFarming",
                containerColor = if (readyFarming > 0) OsrsHerbGreen else OsrsSlotEmpty,
                contentColor = if (readyFarming > 0) Color.Black else OsrsTextMuted,
                onClick = { onNavigateToTab(DashboardTab.TIMERS) }
            )
            OsrsStatusBadge(
                text = "Birdhouses: $readyBirdhouses",
                containerColor = if (readyBirdhouses > 0) OsrsHerbGreen else OsrsSlotEmpty,
                contentColor = if (readyBirdhouses > 0) Color.Black else OsrsTextMuted,
                onClick = { onNavigateToTab(DashboardTab.TIMERS) }
            )
            OsrsStatusBadge(
                text = "Dailies: $readyDailies",
                containerColor = if (readyDailies > 0) OsrsHerbGreen else OsrsSlotEmpty,
                contentColor = if (readyDailies > 0) Color.Black else OsrsTextMuted,
                onClick = { onNavigateToTab(DashboardTab.TIMERS) }
            )
            if (activeGe > 0) {
                OsrsStatusBadge(
                    text = "GE: $completeGe/$activeGe",
                    containerColor = if (completeGe > 0) OsrsHerbGreen else OsrsSlotEmpty,
                    contentColor = if (completeGe > 0) Color.Black else OsrsYellow,
                    onClick = { onNavigateToTab(DashboardTab.GE) }
                )
            }
        }
    }
}
