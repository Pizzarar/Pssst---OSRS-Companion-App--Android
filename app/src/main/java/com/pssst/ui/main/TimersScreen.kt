package com.pssst.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.pssst.util.TimeFormat
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.pssst.theme.OsrsBorderOutline
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsHardShadow
import com.pssst.theme.OsrsHerbGreen
import com.pssst.theme.OsrsRed
import com.pssst.theme.OsrsSlotEmpty
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.OsrsTextPrimary
import com.pssst.theme.OsrsYellow
import com.pssst.theme.osrsPanel
import com.pssst.theme.osrsSlot

enum class TimersSubTab(val title: String) {
    DAILIES("Dailies"),
    BIRDHOUSES("Birdhouses"),
    FARMING("Patches"),
    PAYMENTS("Payments")
}

@Composable
fun TimersScreen(
    state: MainDashboardUiState,
    onToggleNotifications: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(TimersSubTab.DAILIES) }

    val readyDailiesCount = state.dailyTasks.count { it.isAvailable }
    val nowMs by rememberNowMs()
    val readyBirdhousesCount = state.birdHouses.count { it.isReady(nowMs) }
    val readyFarmingCount = state.farmingPatches.count { it.isGrown(nowMs) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Sub-Tab Navigation Bar (pinned cards moved to OverviewScreen for full un-squished height)
        TimersSubTabBar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
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
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.dailyTasks, key = { it.id }) { task ->
                                DailyTaskCard(task)
                            }
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
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.birdHouses, key = { it.varpId }) { house ->
                                BirdHouseCard(house, nowMs)
                            }
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
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val ordered = state.farmingPatches.sortedWith(
                                compareBy({ !it.isGrown(nowMs) }, { it.doneAtMs ?: Long.MAX_VALUE })
                            )
                            items(ordered, key = { it.patchId }) { patch ->
                                FarmingPatchCard(patch, nowMs)
                            }
                        }
                    }
                }

                TimersSubTab.PAYMENTS -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item(key = "unverified_note") {
                            // The amounts are bundled with the app, not read from the game, and have not been checked
                            Text(
                                text = "Payment amounts are unverified: they are not read from the game.",
                                style = MaterialTheme.typography.bodySmall,
                                color = OsrsTextMuted
                            )
                        }
                        items(state.farmingPayments, key = { "${it.cropName}_${it.paymentItemId}" }) { payment ->
                            FarmingPaymentCard(payment)
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
    Row(
        modifier = modifier
            .fillMaxWidth()
            .osrsPanel()
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TimersSubTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            val count = when (tab) {
                TimersSubTab.DAILIES -> readyDailiesCount
                TimersSubTab.BIRDHOUSES -> readyBirdhousesCount
                TimersSubTab.FARMING -> readyFarmingCount
                TimersSubTab.PAYMENTS -> 0
            }
            val titleWithCount = if (count > 0) "${tab.title} ($count)" else tab.title

            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = if (isSelected) OsrsSlotEmpty else Color.Transparent,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = titleWithCount,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = when {
                        isSelected -> OsrsGold
                        count > 0 -> OsrsHerbGreen
                        else -> OsrsTextMuted
                    }
                )
            }
        }
    }
}

@Composable
fun FarmingPaymentCard(payment: FarmingPaymentUiModel, modifier: Modifier = Modifier) {
    val bitmap = remember(payment.paymentItemId, payment.paymentIconBase64) {
        if (payment.paymentItemId > 0) ItemIconCache.getOrCreate(payment.paymentItemId, payment.paymentIconBase64) else null
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .osrsPanel()
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Item Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .osrsSlot(hasItem = true)
                    .padding(3.dp),
                contentAlignment = Alignment.Center
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = payment.paymentItemName,
                        filterQuality = FilterQuality.None,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    Text(
                        text = "#${payment.paymentItemId}",
                        color = OsrsTextPrimary,
                        fontSize = 9.sp,
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

            // Owned count chip
            val chipBg = if (payment.hasEnough) OsrsHerbGreen else OsrsSlotEmpty
            val chipFg = if (payment.hasEnough) Color.Black else OsrsRed
            val chipLabel = if (payment.hasEnough) "Owned: ${payment.ownedInBankAndInv}" else "Short (${payment.ownedInBankAndInv}/${payment.requiredQuantity})"

            SuggestionChip(
                onClick = {},
                label = { Text(chipLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = chipBg,
                    labelColor = chipFg
                ),
                border = if (payment.hasEnough) null else SuggestionChipDefaults.suggestionChipBorder(enabled = true, borderColor = OsrsBorderOutline)
            )
        }
    }
}

@Composable
fun MiscellaniaCard(miscellania: MiscellaniaUiModel, modifier: Modifier = Modifier) {
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

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { (miscellania.approvalPercent / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = if (miscellania.approvalPercent >= 75) OsrsHerbGreen else OsrsRed,
                trackColor = OsrsSlotEmpty
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

                SuggestionChip(
                    onClick = {},
                    label = {
                        Text(
                            text = if (slayer.hasTask) "${slayer.amount} remaining" else "No task",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = OsrsSlotEmpty,
                        labelColor = if (slayer.hasTask) OsrsYellow else OsrsTextMuted
                    ),
                    border = SuggestionChipDefaults.suggestionChipBorder(
                        enabled = true,
                        borderColor = OsrsBorderOutline
                    )
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
    Box(
        modifier = modifier
            .fillMaxWidth()
            .osrsPanel()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = if (task.isAvailable) OsrsGold else OsrsTextPrimary
                    )
                    if (task.category == ResetCategory.WEEKLY) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "[Weekly]",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OsrsYellow
                        )
                    }
                }
                Text(
                    text = task.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = OsrsTextMuted
                )
                Text(
                    text = task.statusText,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (task.isAvailable) OsrsHerbGreen else OsrsTextMuted
                )
            }

            if (task.isAvailable) {
                SuggestionChip(
                    onClick = {},
                    label = { Text("READY", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = OsrsHerbGreen,
                        labelColor = Color.Black
                    ),
                    border = null
                )
            } else {
                SuggestionChip(
                    onClick = {},
                    label = { Text("DONE", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = OsrsSlotEmpty,
                        labelColor = OsrsTextMuted
                    ),
                    border = SuggestionChipDefaults.suggestionChipBorder(
                        enabled = true,
                        borderColor = OsrsBorderOutline
                    )
                )
            }
        }
    }
}

@Composable
fun FarmingPatchCard(patch: FarmingPatchUiModel, nowMs: Long, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .osrsPanel()
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = patch.patchLabel,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = if (patch.isGrown(nowMs)) OsrsHerbGreen else OsrsTextPrimary
                    )
                    Text(
                        text = patch.statusText(nowMs),
                        style = MaterialTheme.typography.bodySmall,
                        color = OsrsTextMuted
                    )
                }

                if (patch.isGrown(nowMs)) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text("GROWN", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = OsrsHerbGreen,
                            labelColor = Color.Black
                        ),
                        border = null
                    )
                }
            }

            val status = patch.status(nowMs)
            if (status == FarmingStatus.GROWING || status == FarmingStatus.REGROWING) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { patch.progress(nowMs) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = OsrsHerbGreen,
                    trackColor = OsrsSlotEmpty
                )
            }

            // The finish time is an estimate (it ignores the per-player farm tick offset), hence the "~"
            val timing = buildString {
                val doneAt = patch.doneAtMs
                if ((status == FarmingStatus.GROWING || status == FarmingStatus.REGROWING) && doneAt != null) {
                    append("Done in ~${TimeFormat.formatDuration(doneAt - nowMs)} • ")
                }
                append("Seen ${TimeFormat.formatDuration(nowMs - patch.observedAtMs)} ago")
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = timing,
                style = MaterialTheme.typography.labelSmall,
                color = OsrsTextMuted
            )
        }
    }
}

@Composable
fun BirdHouseCard(house: BirdHouseUiModel, nowMs: Long, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .osrsPanel()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = house.spaceName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = if (house.isReady(nowMs)) OsrsHerbGreen else OsrsTextPrimary
                )
                Text(
                    text = listOfNotNull(house.houseType, house.statusText(nowMs)).joinToString(" \u2022 "),
                    style = MaterialTheme.typography.bodySmall,
                    color = OsrsTextMuted
                )
            }

            val (chipText, chipBg, chipTextCol) = when (house.status(nowMs)) {
                BirdHouseStatus.READY -> Triple("READY", OsrsHerbGreen, Color.Black)
                BirdHouseStatus.SEEDED -> Triple("ACTIVE", OsrsSlotEmpty, OsrsYellow)
                BirdHouseStatus.BUILT -> Triple("BUILT", OsrsSlotEmpty, OsrsTextPrimary)
                BirdHouseStatus.EMPTY -> Triple("EMPTY", OsrsSlotEmpty, OsrsTextMuted)
                BirdHouseStatus.UNKNOWN -> Triple("?", OsrsSlotEmpty, OsrsTextMuted)
            }

            SuggestionChip(
                onClick = {},
                label = { Text(chipText, fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = chipBg,
                    labelColor = chipTextCol
                ),
                border = SuggestionChipDefaults.suggestionChipBorder(
                    enabled = true,
                    borderColor = OsrsBorderOutline
                )
            )
        }
    }
}
