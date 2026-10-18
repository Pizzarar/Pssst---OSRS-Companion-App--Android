package com.pssst.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pssst.theme.JetBrainsMonoFontFamily
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsHardShadow
import com.pssst.theme.OsrsHeaderDivider
import com.pssst.theme.OsrsHerbGreen
import com.pssst.theme.OsrsProgressBar
import com.pssst.theme.OsrsRed
import com.pssst.theme.OsrsSegmentedTabs
import com.pssst.theme.OsrsSlotEmpty
import com.pssst.theme.OsrsStatusBadge
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.OsrsTextPrimary
import com.pssst.theme.OsrsYellow
import com.pssst.theme.osrsPanel
import com.pssst.util.TimeFormat

enum class TimersSubTab(val title: String) {
    DAILIES("Dailies"),
    BIRDHOUSES("Birdhouses"),
    FARMING("Patches"),
    PAYMENTS("Payments")
}

@Composable
fun TimersScreen(
    state: MainDashboardUiState,
    selectedTab: TimersSubTab,
    onSelectedTabChange: (TimersSubTab) -> Unit,
    editing: Boolean,
    onToggleNotifications: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val nowMs by rememberNowMs()
    val ready = state.readyCounts(nowMs)
    val readyDailiesCount = ready.dailies
    val readyBirdhousesCount = ready.birdhouses
    val readyFarmingCount = ready.farming

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Sub-Tab Navigation Bar
        TimersSubTabBar(
            selectedTab = selectedTab,
            onTabSelected = onSelectedTabChange,
            readyDailiesCount = readyDailiesCount,
            readyBirdhousesCount = readyBirdhousesCount,
            readyFarmingCount = readyFarmingCount
        )

        // Sub-Tab Content Area
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                TimersSubTab.DAILIES -> {
                    if (state.dailyTasks.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No daily tasks tracked yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OsrsTextMuted
                            )
                        }
                    } else {
                        EditableList(
                            listId = "dailies",
                            items = state.dailyTasks,
                            key = { it.id },
                            label = { it.title },
                            editing = editing,
                            spacing = 0.dp,
                            showDividers = true
                        ) { task ->
                            DailyTaskCard(task)
                        }
                    }
                }

                TimersSubTab.BIRDHOUSES -> {
                    if (state.birdHouses.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No birdhouses observed yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OsrsTextMuted
                            )
                        }
                    } else {
                        EditableList(
                            listId = "birdhouses",
                            items = state.birdHouses,
                            key = { it.varpId },
                            label = { it.spaceName },
                            editing = editing,
                            spacing = 0.dp,
                            showDividers = true
                        ) { house ->
                            BirdHouseCard(house, nowMs)
                        }
                    }
                }

                TimersSubTab.FARMING -> {
                    if (state.farmingPatches.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No farming patches observed yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OsrsTextMuted
                            )
                        }
                    } else {
                        // Default order: grown first, then soonest. Once rearranged, the player's order wins.
                        val ordered = state.farmingPatches.sortedWith(
                            compareBy({ !it.isGrown(nowMs) }, { it.doneAtMs ?: Long.MAX_VALUE })
                        )
                        EditableList(
                            listId = "farming",
                            items = ordered,
                            key = { it.patchId },
                            label = { listOfNotNull(it.regionName, it.cropName).joinToString(" · ") },
                            editing = editing,
                            spacing = 0.dp,
                            showDividers = true
                        ) { patch ->
                            FarmingPatchCard(patch, nowMs)
                        }
                    }
                }

                TimersSubTab.PAYMENTS -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item(key = "unverified_note") {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Payment amounts are unverified: they are not read from the game.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OsrsTextMuted,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                                )
                                OsrsHeaderDivider()
                            }
                        }
                        items(state.farmingPayments, key = { "${it.cropName}_${it.paymentItemId}" }) { payment ->
                            Column(modifier = Modifier.fillMaxWidth()) {
                                FarmingPaymentCard(payment)
                                OsrsHeaderDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimersSubTabBar(
    selectedTab: TimersSubTab,
    onTabSelected: (TimersSubTab) -> Unit,
    readyDailiesCount: Int,
    readyBirdhousesCount: Int,
    readyFarmingCount: Int,
    modifier: Modifier = Modifier
) {
    val tabs = TimersSubTab.entries
    OsrsSegmentedTabs(
        tabs = tabs.map { tab ->
            val count = when (tab) {
                TimersSubTab.DAILIES -> readyDailiesCount
                TimersSubTab.BIRDHOUSES -> readyBirdhousesCount
                TimersSubTab.FARMING -> readyFarmingCount
                TimersSubTab.PAYMENTS -> 0
            }
            tab.title to count
        },
        selectedIndex = tabs.indexOf(selectedTab),
        onTabSelected = { onTabSelected(tabs[it]) },
        modifier = modifier
    )
}

@Composable
fun FarmingPaymentCard(payment: FarmingPaymentUiModel, modifier: Modifier = Modifier) {
    val bitmap = remember(payment.paymentItemId, payment.paymentIconBase64) {
        if (payment.paymentItemId > 0) ItemIconCache.getOrCreate(payment.paymentItemId, payment.paymentIconBase64) else null
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Unboxed Item Icon
        Box(
            modifier = Modifier.size(28.dp),
            contentAlignment = Alignment.Center
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = payment.paymentItemName,
                    filterQuality = FilterQuality.None,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(26.dp)
                )
            } else {
                Text(
                    text = itemInitials(payment.paymentItemName),
                    color = OsrsTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall.copy(shadow = OsrsHardShadow)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Details
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${payment.cropName} (Lvl ${payment.farmingLevel})",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = OsrsTextPrimary
            )
            Text(
                text = "${payment.requiredQuantity} × ${payment.paymentItemName} • ${payment.patchCategory}",
                style = MaterialTheme.typography.bodySmall,
                color = OsrsTextMuted
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        val statusLabel = when {
            payment.paymentsLeft == 1 -> "1 payment"
            payment.hasEnough -> "${payment.paymentsLeft} payments"
            else -> "${payment.ownedInBankAndInv}/${payment.requiredQuantity}"
        }
        Text(
            text = statusLabel,
            fontFamily = JetBrainsMonoFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (payment.hasEnough) OsrsHerbGreen else OsrsRed
        )
    }
}

@Composable
fun MiscellaniaCard(miscellania: MiscellaniaUiModel, modifier: Modifier = Modifier) {
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
                    text = "Kingdom of Miscellania",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OsrsGold
                )
                Text(
                    text = "${miscellania.approvalPercent}% Approval",
                    fontWeight = FontWeight.SemiBold,
                    color = if (miscellania.approvalPercent >= 75) OsrsHerbGreen else OsrsRed
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            OsrsHeaderDivider()
            Spacer(modifier = Modifier.height(8.dp))

            OsrsProgressBar(
                progress = (miscellania.approvalPercent / 100f).coerceIn(0f, 1f),
                color = if (miscellania.approvalPercent >= 75) OsrsHerbGreen else OsrsRed
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Coffer Funds:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OsrsTextMuted
                )
                Text(
                    text = miscellania.formattedCoffer,
                    fontWeight = FontWeight.Bold,
                    color = OsrsYellow
                )
            }
        }
    }
}

@Composable
fun SlayerTaskCard(slayer: SlayerUiModel, modifier: Modifier = Modifier) {
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
                    text = "Active Slayer Task",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OsrsGold
                )
                if (slayer.taskLocation.isNotBlank()) {
                    Text(
                        text = slayer.taskLocation,
                        style = MaterialTheme.typography.bodySmall,
                        color = OsrsTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            OsrsHeaderDivider()
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = slayer.taskName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = OsrsTextPrimary
                )

                OsrsStatusBadge(
                    text = if (slayer.hasTask) "${slayer.amount} remaining" else "No task",
                    containerColor = OsrsSlotEmpty,
                    contentColor = if (slayer.hasTask) OsrsYellow else OsrsTextMuted
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            val progress = if (slayer.hasTask && slayer.initialAmount > 0) "of ${slayer.initialAmount} \u2022 " else ""
            Text(
                text = "${progress}${slayer.points} points \u2022 ${slayer.streak} streak",
                style = MaterialTheme.typography.labelSmall,
                color = OsrsTextMuted
            )
        }
    }
}

@Composable
fun DailyTaskCard(task: DailyTaskUiModel, modifier: Modifier = Modifier) {
    val bitmap = remember(task.itemId, task.iconBase64) {
        if (task.itemId > 0) ItemIconCache.getOrCreate(task.itemId, task.iconBase64) else null
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Unboxed Item Icon
        Box(
            modifier = Modifier.size(28.dp),
            contentAlignment = Alignment.Center
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = task.title,
                    filterQuality = FilterQuality.None,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(26.dp)
                )
            } else {
                Text(
                    text = itemInitials(task.title),
                    color = OsrsTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall.copy(shadow = OsrsHardShadow)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (task.isAvailable) OsrsGold else OsrsTextPrimary
                )
                if (task.category == ResetCategory.WEEKLY) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "[Weekly]",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = OsrsYellow
                    )
                }
            }
            Text(
                text = "${task.subtitle} • ${task.statusText}",
                style = MaterialTheme.typography.bodySmall,
                color = OsrsTextMuted
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = if (task.isAvailable) "READY" else "DONE",
            fontFamily = JetBrainsMonoFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (task.isAvailable) OsrsHerbGreen else OsrsTextMuted
        )
    }
}

@Composable
fun FarmingPatchCard(patch: FarmingPatchUiModel, nowMs: Long, modifier: Modifier = Modifier) {
    val bitmap = remember(patch.itemId, patch.iconBase64) {
        if (patch.itemId > 0) ItemIconCache.getOrCreate(patch.itemId, patch.iconBase64) else null
    }
    val status = patch.status(nowMs)
    val isGrown = patch.isGrown(nowMs)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Unboxed Crop Icon
            Box(
                modifier = Modifier.size(28.dp),
                contentAlignment = Alignment.Center
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = patch.cropName ?: patch.patchLabel,
                        filterQuality = FilterQuality.None,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(26.dp)
                    )
                } else {
                    Text(
                        text = itemInitials(patch.cropName ?: patch.patchLabel),
                        color = OsrsTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall.copy(shadow = OsrsHardShadow)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = patch.patchLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isGrown) OsrsHerbGreen else OsrsTextPrimary
                )
                Text(
                    text = "${patch.statusText(nowMs)} • Seen ${TimeFormat.formatDuration(nowMs - patch.observedAtMs)} ago",
                    style = MaterialTheme.typography.bodySmall,
                    color = OsrsTextMuted
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            val rightText = when {
                isGrown -> "GROWN"
                (status == FarmingStatus.GROWING || status == FarmingStatus.REGROWING) && patch.doneAtMs != null ->
                    "~${TimeFormat.formatDuration(patch.doneAtMs - nowMs)}"
                status == FarmingStatus.DISEASED -> "DISEASED"
                status == FarmingStatus.DEAD -> "DEAD"
                else -> "EMPTY"
            }
            val rightColor = when {
                isGrown -> OsrsHerbGreen
                status == FarmingStatus.GROWING || status == FarmingStatus.REGROWING -> OsrsYellow
                status == FarmingStatus.DISEASED || status == FarmingStatus.DEAD -> OsrsRed
                else -> OsrsTextMuted
            }
            Text(
                text = rightText,
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = rightColor
            )
        }

        if (status == FarmingStatus.GROWING || status == FarmingStatus.REGROWING) {
            Spacer(modifier = Modifier.height(6.dp))
            OsrsProgressBar(
                progress = patch.progress(nowMs),
                color = OsrsHerbGreen,
                thin = true,
                modifier = Modifier.padding(start = 40.dp)
            )
        }
    }
}

@Composable
fun BirdHouseCard(house: BirdHouseUiModel, nowMs: Long, modifier: Modifier = Modifier) {
    val bitmap = remember(house.itemId, house.iconBase64) {
        if (house.itemId > 0) ItemIconCache.getOrCreate(house.itemId, house.iconBase64) else null
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Unboxed Birdhouse Icon
        Box(
            modifier = Modifier.size(28.dp),
            contentAlignment = Alignment.Center
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = house.houseType ?: house.spaceName,
                    filterQuality = FilterQuality.None,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(26.dp)
                )
            } else {
                Text(
                    text = itemInitials(house.houseType ?: house.spaceName),
                    color = OsrsTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall.copy(shadow = OsrsHardShadow)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = house.spaceName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (house.isReady(nowMs)) OsrsHerbGreen else OsrsTextPrimary
            )
            Text(
                text = listOfNotNull(house.houseType, house.statusText(nowMs)).joinToString(" \u2022 "),
                style = MaterialTheme.typography.bodySmall,
                color = OsrsTextMuted
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        val (statusText, statusColor) = when (house.status(nowMs)) {
            BirdHouseStatus.READY -> "READY" to OsrsHerbGreen
            BirdHouseStatus.SEEDED -> {
                val readyAt = house.readyAtMs
                val label = if (readyAt != null && readyAt > nowMs) "~${TimeFormat.formatDuration(readyAt - nowMs)}" else "ACTIVE"
                label to OsrsYellow
            }
            BirdHouseStatus.BUILT -> "BUILT" to OsrsTextPrimary
            BirdHouseStatus.EMPTY -> "EMPTY" to OsrsTextMuted
            BirdHouseStatus.UNKNOWN -> "?" to OsrsTextMuted
        }

        Text(
            text = statusText,
            fontFamily = JetBrainsMonoFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = statusColor
        )
    }
}
