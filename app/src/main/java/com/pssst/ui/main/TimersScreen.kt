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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.pssst.theme.art.BulletGlyph
import com.pssst.theme.art.EmptyStateKind
import com.pssst.theme.art.FrameWeight
import com.pssst.theme.art.GoldLeafText
import com.pssst.theme.art.OsrsEmptyState
import com.pssst.theme.art.OsrsTableHeaderPlaque
import com.pssst.theme.art.OsrsTableRow
import com.pssst.theme.art.OsrsTableRowSeparator
import com.pssst.theme.art.OsrsTableStatusCell
import com.pssst.theme.art.TableStatusKind
import com.pssst.theme.art.ledgerPanelInset
import com.pssst.theme.art.parchmentNotice
import com.pssst.theme.osrsPanel
import com.pssst.theme.osrsSlot
import com.pssst.util.TimeFormat
import androidx.compose.ui.text.style.TextOverflow

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
                            OsrsEmptyState(
                                kind = EmptyStateKind.SUNDIAL,
                                message = "No daily tasks tracked yet."
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .then(
                                    if (editing) {
                                        Modifier
                                    } else {
                                        Modifier
                                            .osrsPanel(weight = FrameWeight.MEDIUM)
                                            .ledgerPanelInset(FrameWeight.MEDIUM)
                                    }
                                )
                        ) {
                            EditableList(
                                listId = "dailies",
                                items = state.dailyTasks,
                                key = { it.id },
                                label = { it.title },
                                editing = editing,
                                spacing = 0.dp,
                                showDividers = true,
                                header = if (!editing) {
                                    {
                                        item(key = "__header_dailies") {
                                            OsrsTableHeaderPlaque(
                                                leftLabel = "Task",
                                                rightLabel = "Status"
                                            )
                                        }
                                    }
                                } else null,
                                indexedItemContent = { index, task ->
                                    DailyTaskCard(task, index = index)
                                }
                            )
                        }
                    }
                }

                TimersSubTab.BIRDHOUSES -> {
                    if (state.birdHouses.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            OsrsEmptyState(
                                kind = EmptyStateKind.EMPTY_BIRDHOUSE,
                                message = "No birdhouses observed yet."
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .then(
                                    if (editing) {
                                        Modifier
                                    } else {
                                        Modifier
                                            .osrsPanel(weight = FrameWeight.MEDIUM)
                                            .ledgerPanelInset(FrameWeight.MEDIUM)
                                    }
                                )
                        ) {
                            EditableList(
                                listId = "birdhouses",
                                items = state.birdHouses,
                                key = { it.varpId },
                                label = { it.spaceName },
                                editing = editing,
                                spacing = 0.dp,
                                showDividers = true,
                                header = if (!editing) {
                                    {
                                        item(key = "__header_birdhouses") {
                                            OsrsTableHeaderPlaque(
                                                leftLabel = "Location",
                                                rightLabel = "Status"
                                            )
                                        }
                                    }
                                } else null,
                                indexedItemContent = { index, house ->
                                    BirdHouseCard(house, nowMs, index = index)
                                }
                            )
                        }
                    }
                }

                TimersSubTab.FARMING -> {
                    if (state.farmingPatches.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            OsrsEmptyState(
                                kind = EmptyStateKind.FALLOW_PATCH,
                                message = "No farming patches observed yet."
                            )
                        }
                    } else {
                        // Default order: grown first, then soonest. Once rearranged, the player's order wins.
                        val ordered = state.farmingPatches.sortedWith(
                            compareBy({ !it.isGrown(nowMs) }, { it.doneAtMs ?: Long.MAX_VALUE })
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .then(
                                    if (editing) {
                                        Modifier
                                    } else {
                                        Modifier
                                            .osrsPanel(weight = FrameWeight.MEDIUM)
                                            .ledgerPanelInset(FrameWeight.MEDIUM)
                                    }
                                )
                        ) {
                            EditableList(
                                listId = "farming",
                                items = ordered,
                                key = { it.patchId },
                                label = { listOfNotNull(it.regionName, it.cropName).joinToString(" · ") },
                                editing = editing,
                                spacing = 0.dp,
                                showDividers = true,
                                header = if (!editing) {
                                    {
                                        item(key = "__header_farming") {
                                            OsrsTableHeaderPlaque(
                                                leftLabel = "Patch & Crop",
                                                rightLabel = "Status"
                                            )
                                        }
                                    }
                                } else null,
                                indexedItemContent = { index, patch ->
                                    FarmingPatchCard(patch, nowMs, index = index)
                                }
                            )
                        }
                    }
                }

                TimersSubTab.PAYMENTS -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .osrsPanel(weight = FrameWeight.MEDIUM)
                            .ledgerPanelInset(FrameWeight.MEDIUM)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item(key = "unverified_note") {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .parchmentNotice()
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = "Payment amounts are unverified: they are not read from the game.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = OsrsTextMuted
                                        )
                                    }
                                    OsrsTableHeaderPlaque(
                                        leftLabel = "Crop & Payment",
                                        rightLabel = "Owned"
                                    )
                                }
                            }
                            itemsIndexed(
                                state.farmingPayments,
                                key = { _, it -> "${it.cropName}_${it.paymentItemId}" }
                            ) { index, payment ->
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    FarmingPaymentCard(payment, index = index)
                                    OsrsTableRowSeparator()
                                }
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
fun FarmingPaymentCard(
    payment: FarmingPaymentUiModel,
    modifier: Modifier = Modifier,
    index: Int = 0
) {
    val bitmap = remember(payment.paymentItemId, payment.paymentIconBase64) {
        if (payment.paymentItemId > 0) ItemIconCache.getOrCreate(payment.paymentItemId, payment.paymentIconBase64) else null
    }

    OsrsTableRow(
        index = index,
        modifier = modifier
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
        OsrsTableStatusCell(
            text = statusLabel,
            status = if (payment.hasEnough) TableStatusKind.READY else TableStatusKind.WARNING,
            colorOverride = if (payment.hasEnough) OsrsHerbGreen else OsrsRed
        )
    }
}

private enum class CofferTab(val label: String) {
    KINGDOM("Kingdom"),
    BLAST_FURNACE("Furnace"),
    NMZ("NMZ"),
    OTHER("POH / LMS")
}

@Composable
fun MiscellaniaCard(miscellania: MiscellaniaUiModel, modifier: Modifier = Modifier) {
    var selectedTab by rememberSaveable { mutableStateOf(CofferTab.KINGDOM) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .osrsPanel(weight = FrameWeight.MEDIUM)
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BulletGlyph(size = 7.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    GoldLeafText(
                        text = "Kingdom & Coffers",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                when (selectedTab) {
                    CofferTab.KINGDOM -> Text(
                        text = "${miscellania.approvalPercent}% Approval",
                        fontWeight = FontWeight.SemiBold,
                        color = if (miscellania.approvalPercent >= 75) OsrsHerbGreen else OsrsRed
                    )
                    CofferTab.BLAST_FURNACE -> Text(
                        text = miscellania.blastFurnaceTimeLabel,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (miscellania.blastFurnaceCoffer > 0L) OsrsHerbGreen else OsrsTextMuted
                    )
                    CofferTab.NMZ -> Text(
                        text = "${miscellania.formattedNmzPoints} pts",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = OsrsGold
                    )
                    CofferTab.OTHER -> Text(
                        text = "House & Enclave",
                        style = MaterialTheme.typography.labelSmall,
                        color = OsrsTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            OsrsHeaderDivider()
            Spacer(modifier = Modifier.height(8.dp))

            // Compact 4-tab selector row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CofferTab.entries.forEach { tab ->
                    com.pssst.theme.OsrsFilterChip(
                        text = tab.label,
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (selectedTab) {
                CofferTab.KINGDOM -> {
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
                            text = "Royal Coffer:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OsrsTextMuted
                        )
                        Text(
                            text = miscellania.formattedCoffer,
                            fontWeight = FontWeight.Bold,
                            color = OsrsGold
                        )
                    }
                }
                CofferTab.BLAST_FURNACE -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Foreman Coffer:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OsrsTextMuted
                        )
                        Text(
                            text = miscellania.formattedBlastFurnaceCoffer,
                            fontWeight = FontWeight.Bold,
                            color = OsrsGold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Operating Time:",
                            style = MaterialTheme.typography.bodySmall,
                            color = OsrsTextMuted
                        )
                        Text(
                            text = miscellania.blastFurnaceTimeLabel,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (miscellania.blastFurnaceCoffer > 0L) OsrsHerbGreen else OsrsTextMuted
                        )
                    }
                }
                CofferTab.NMZ -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Dominic's Coffer:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OsrsTextMuted
                        )
                        Text(
                            text = miscellania.formattedNmzCoffer,
                            fontWeight = FontWeight.Bold,
                            color = OsrsGold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Reward Points:",
                            style = MaterialTheme.typography.bodySmall,
                            color = OsrsTextMuted
                        )
                        Text(
                            text = miscellania.formattedNmzPoints,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = OsrsTextPrimary
                        )
                    }
                }
                CofferTab.OTHER -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "POH Servant Bag:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OsrsTextMuted
                        )
                        Text(
                            text = if (miscellania.pohServantUses > 0) "${miscellania.pohServantUses} payments left" else "Empty",
                            fontWeight = FontWeight.Bold,
                            color = if (miscellania.pohServantUses > 0) OsrsHerbGreen else OsrsTextMuted
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "LMS Coffer:",
                            style = MaterialTheme.typography.bodySmall,
                            color = OsrsTextMuted
                        )
                        Text(
                            text = miscellania.formattedLmsCoffer,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = OsrsGold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SlayerTaskCard(slayer: SlayerUiModel, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .osrsPanel(weight = FrameWeight.MEDIUM)
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row: Slayer Assignment on left, Points & Streak on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BulletGlyph(size = 7.dp, color = OsrsGold)
                    Spacer(modifier = Modifier.width(6.dp))
                    GoldLeafText(
                        text = "Slayer Assignment",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "${slayer.points} pts \u2022 ${slayer.streak} streak",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = OsrsGold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            OsrsHeaderDivider()
            Spacer(modifier = Modifier.height(10.dp))

            // Main Assignment Row: Monster Icon Slot + Name/Location + Remaining Count
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .osrsSlot(hasItem = slayer.hasTask, cornerRadius = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ItemIcon(
                        id = slayer.itemId,
                        base64 = slayer.iconBase64,
                        name = slayer.taskName,
                        size = 36.dp
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = slayer.taskName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (slayer.hasTask) OsrsTextPrimary else OsrsTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    val locationSubtitle = when {
                        !slayer.hasTask -> "Visit a Slayer Master"
                        slayer.taskLocation.isNotBlank() -> slayer.taskLocation
                        else -> "Any location"
                    }
                    Text(
                        text = locationSubtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = OsrsTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (slayer.hasTask) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (slayer.initialAmount > 0) {
                                "${slayer.amount} / ${slayer.initialAmount}"
                            } else {
                                "${slayer.amount}"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            color = OsrsGold
                        )
                        Text(
                            text = if (slayer.initialAmount > 0) {
                                val killed = (slayer.initialAmount - slayer.amount).coerceAtLeast(0)
                                "$killed slain"
                            } else {
                                "remaining"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = JetBrainsMonoFontFamily,
                            color = OsrsTextMuted
                        )
                    }
                } else {
                    OsrsStatusBadge(
                        text = "None",
                        containerColor = OsrsSlotEmpty,
                        contentColor = OsrsTextMuted
                    )
                }
            }

            if (slayer.hasTask) {
                Spacer(modifier = Modifier.height(10.dp))
                OsrsProgressBar(
                    progress = slayer.progress,
                    color = OsrsGold,
                    height = 8.dp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Banked / Stored Task Footer Slot
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .osrsSlot(hasItem = slayer.hasStoredTask, cornerRadius = 2.dp)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ItemIcon(
                    id = slayer.storedItemId,
                    base64 = slayer.storedIconBase64,
                    name = slayer.storedTaskName.ifBlank { "Banked Task" },
                    size = 22.dp
                )

                Text(
                    text = "Banked Task",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = OsrsTextMuted
                )

                Spacer(modifier = Modifier.weight(1f))

                val storedText = when {
                    slayer.hasStoredTask -> "${slayer.storedAmount} \u00D7 ${slayer.storedTaskName}"
                    slayer.storageUnlocked -> "Empty"
                    else -> "None"
                }
                Text(
                    text = storedText,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = if (slayer.hasStoredTask) FontWeight.Bold else FontWeight.Normal,
                    color = if (slayer.hasStoredTask) OsrsTextPrimary else OsrsTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun DailyTaskCard(
    task: DailyTaskUiModel,
    modifier: Modifier = Modifier,
    index: Int = 0
) {
    val bitmap = remember(task.itemId, task.iconBase64) {
        if (task.itemId > 0) ItemIconCache.getOrCreate(task.itemId, task.iconBase64) else null
    }

    OsrsTableRow(
        index = index,
        modifier = modifier
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
                        color = OsrsGold
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

        OsrsTableStatusCell(
            text = if (task.isAvailable) "READY" else "DONE",
            status = if (task.isAvailable) TableStatusKind.READY else TableStatusKind.EMPTY,
            colorOverride = if (task.isAvailable) OsrsHerbGreen else OsrsTextMuted
        )
    }
}

@Composable
fun FarmingPatchCard(
    patch: FarmingPatchUiModel,
    nowMs: Long,
    modifier: Modifier = Modifier,
    index: Int = 0
) {
    val bitmap = remember(patch.itemId, patch.iconBase64) {
        if (patch.itemId > 0) ItemIconCache.getOrCreate(patch.itemId, patch.iconBase64) else null
    }
    val status = patch.status(nowMs)
    val isGrown = patch.isGrown(nowMs)

    OsrsTableRow(
        index = index,
        modifier = modifier.fillMaxWidth(),
        bottomContent = if (status == FarmingStatus.GROWING || status == FarmingStatus.REGROWING) {
            {
                OsrsProgressBar(
                    progress = patch.progress(nowMs),
                    color = OsrsHerbGreen,
                    thin = true,
                    modifier = Modifier
                        .padding(start = 52.dp, end = 12.dp, bottom = 8.dp)
                )
            }
        } else {
            null
        }
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
            status == FarmingStatus.GROWING || status == FarmingStatus.REGROWING -> OsrsTextMuted
            status == FarmingStatus.DISEASED || status == FarmingStatus.DEAD -> OsrsRed
            else -> OsrsTextMuted
        }
        val statusKind = when {
            isGrown -> TableStatusKind.READY
            status == FarmingStatus.GROWING || status == FarmingStatus.REGROWING -> TableStatusKind.GROWING
            status == FarmingStatus.DISEASED || status == FarmingStatus.DEAD -> TableStatusKind.WARNING
            else -> TableStatusKind.EMPTY
        }
        OsrsTableStatusCell(
            text = rightText,
            status = statusKind,
            colorOverride = rightColor
        )
    }
}

@Composable
fun BirdHouseCard(
    house: BirdHouseUiModel,
    nowMs: Long,
    modifier: Modifier = Modifier,
    index: Int = 0
) {
    val bitmap = remember(house.itemId, house.iconBase64) {
        if (house.itemId > 0) ItemIconCache.getOrCreate(house.itemId, house.iconBase64) else null
    }

    OsrsTableRow(
        index = index,
        modifier = modifier
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

        val (statusText, statusColor, statusKind) = when (house.status(nowMs)) {
            BirdHouseStatus.READY -> Triple("READY", OsrsHerbGreen, TableStatusKind.READY)
            BirdHouseStatus.SEEDED -> {
                val readyAt = house.readyAtMs
                val label = if (readyAt != null) "~${TimeFormat.formatDuration(readyAt - nowMs)}" else "ACTIVE"
                Triple(label, OsrsTextMuted, TableStatusKind.GROWING)
            }
            BirdHouseStatus.BUILT -> Triple("BUILT", OsrsTextPrimary, TableStatusKind.GROWING)
            BirdHouseStatus.EMPTY -> Triple("EMPTY", OsrsTextMuted, TableStatusKind.EMPTY)
            BirdHouseStatus.UNKNOWN -> Triple("?", OsrsTextMuted, TableStatusKind.EMPTY)
        }

        OsrsTableStatusCell(
            text = statusText,
            status = statusKind,
            colorOverride = statusColor
        )
    }
}
