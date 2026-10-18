package com.pssst.ui.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.pssst.data.local.GoalConfig
import com.pssst.theme.JetBrainsMonoFontFamily
import com.pssst.theme.OsrsBorderDark
import com.pssst.theme.OsrsBorderLight
import com.pssst.theme.OsrsButton
import com.pssst.theme.OsrsButtonVariant
import com.pssst.theme.OsrsDarkStone
import com.pssst.theme.OsrsFilterChip
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsHeaderDivider
import com.pssst.theme.OsrsHerbGreen
import com.pssst.theme.OsrsOuterFrame
import com.pssst.theme.OsrsPanelDark
import com.pssst.theme.OsrsProgressBar
import com.pssst.theme.OsrsRed
import com.pssst.theme.OsrsSegmentedTabs
import com.pssst.theme.OsrsSlotEmpty
import com.pssst.theme.OsrsSlotRecessed
import com.pssst.theme.OsrsTextField
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.OsrsTextPrimary
import com.pssst.theme.OsrsYellow
import com.pssst.theme.osrsPanel
import com.pssst.theme.osrsSlot
import com.pssst.util.GoalMath
import com.pssst.util.GoalPriceMode
import com.pssst.util.TimeFormat
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GearVectorIcon(tint: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2f
        val center = Offset(size.width / 2f, size.height / 2f)
        val toothCount = 8
        val outerR = radius * 0.95f
        val innerR = radius * 0.65f
        val holeR = radius * 0.32f

        for (i in 0 until toothCount) {
            val angle = (i.toDouble() * 2.0 * Math.PI / toothCount).toFloat()
            val start = Offset(
                center.x + (innerR * cos(angle)),
                center.y + (innerR * sin(angle))
            )
            val end = Offset(
                center.x + (outerR * cos(angle)),
                center.y + (outerR * sin(angle))
            )
            drawLine(
                color = tint,
                start = start,
                end = end,
                strokeWidth = radius * 0.35f,
                cap = StrokeCap.Round
            )
        }
        drawCircle(color = tint, radius = innerR, center = center)
        drawCircle(color = OsrsPanelDark, radius = holeR, center = center)
    }
}

@Composable
fun GoalCard(
    state: MainDashboardUiState,
    onSaveGoalConfig: (GoalConfig) -> Unit,
    onSearchGoalItem: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val goalConfig = state.goalConfig
    val activePriceMode = goalConfig.priceMode ?: if (state.isIronman) GoalPriceMode.HA else GoalPriceMode.GE

    val targetGp = when {
        goalConfig.targetGp != null && goalConfig.targetGp > 0L -> goalConfig.targetGp
        state.goalTargetItem != null -> {
            if (activePriceMode == GoalPriceMode.GE) state.goalTargetItem.gePrice else state.goalTargetItem.haPrice
        }
        else -> 0L
    }

    val progress = remember(state.bankItems, goalConfig.source, activePriceMode, targetGp) {
        GoalMath.progress(state.bankItems, goalConfig.source, activePriceMode, targetGp)
    }

    val isCompleted = progress.target > 0L && progress.current >= progress.target
    val timeAgo = remember(state.gameObservedAt) {
        TimeFormat.ageLabel((System.currentTimeMillis() - state.gameObservedAt).coerceAtLeast(0L))
    }

    var showSettingsDialog by remember { mutableStateOf(false) }

    if (showSettingsDialog) {
        GoalSettingsDialog(
            state = state,
            activePriceMode = activePriceMode,
            onDismiss = { showSettingsDialog = false },
            onSave = { updated ->
                onSaveGoalConfig(updated)
                showSettingsDialog = false
            },
            onSearchQueryChanged = onSearchGoalItem
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .osrsPanel(showRivets = true)
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row: Title on left, [GE|HA] toggle + Settings icon on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Goal Tracker",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OsrsGold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // GE | HA Price Mode Toggle
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(2.dp))
                            .background(OsrsSlotEmpty)
                            .border(1.dp, OsrsOuterFrame, RoundedCornerShape(2.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .background(if (activePriceMode == GoalPriceMode.GE) OsrsBorderDark else Color.Transparent)
                                .clickable { onSaveGoalConfig(goalConfig.copy(priceMode = GoalPriceMode.GE)) }
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "GE",
                                fontSize = 11.sp,
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = if (activePriceMode == GoalPriceMode.GE) OsrsGold else OsrsTextMuted
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(if (activePriceMode == GoalPriceMode.HA) OsrsBorderDark else Color.Transparent)
                                .clickable { onSaveGoalConfig(goalConfig.copy(priceMode = GoalPriceMode.HA)) }
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "HA",
                                fontSize = 11.sp,
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = if (activePriceMode == GoalPriceMode.HA) OsrsGold else OsrsTextMuted
                            )
                        }
                    }

                    // Settings Button
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(OsrsSlotEmpty)
                            .border(1.dp, OsrsOuterFrame, RoundedCornerShape(2.dp))
                            .clickable { showSettingsDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        GearVectorIcon(tint = OsrsGold, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            OsrsHeaderDivider()
            Spacer(modifier = Modifier.height(10.dp))

            if (!goalConfig.isConfigured) {
                // Empty state: prompt to set a goal
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Track your bank value against an item or GP target.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OsrsTextMuted
                    )
                    OsrsButton(
                        text = "Set a goal",
                        onClick = { showSettingsDialog = true },
                        variant = OsrsButtonVariant.GOLD,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                // Goal Target Row (Icon + Name + Subtitle)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .osrsSlot(hasItem = true, cornerRadius = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.goalTargetItem != null) {
                            ItemIcon(
                                id = state.goalTargetItem.id,
                                base64 = state.goalTargetItem.iconBase64,
                                name = state.goalTargetItem.name,
                                size = 36.dp
                            )
                        } else {
                            ItemIcon(
                                id = 995,
                                base64 = null,
                                name = "Coins",
                                size = 36.dp
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        val title = state.goalTargetItem?.name ?: "${formatGp(targetGp)} GP Goal"
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isCompleted) OsrsHerbGreen else OsrsTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        val sourceLabel = GoalMath.tabLabel(state.bankItems, goalConfig.source)
                        Text(
                            text = "from $sourceLabel (${progress.itemCount} items)",
                            style = MaterialTheme.typography.bodySmall,
                            color = OsrsTextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = "${(progress.fraction * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isCompleted) OsrsHerbGreen else OsrsYellow
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Bar
                OsrsProgressBar(
                    progress = progress.fraction,
                    color = if (isCompleted) OsrsHerbGreen else OsrsGold,
                    height = 8.dp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Value numbers row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${formatGp(progress.current)} / ${formatGp(progress.target)}",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isCompleted) OsrsHerbGreen else OsrsTextPrimary
                    )

                    Text(
                        text = if (isCompleted) "Goal reached! 🎉" else "${formatGp(progress.remaining)} to go",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) OsrsHerbGreen else OsrsYellow
                    )
                }

                // Top items summary
                if (progress.top.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    val topText = progress.top.joinToString(" · ") {
                        "${it.name} ${formatGp(GoalMath.stackValue(it, activePriceMode))}"
                    }
                    Text(
                        text = "Top: $topText",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = OsrsTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Footer status line
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val statusText = buildString {
                        append("Bank seen $timeAgo")
                        if (!state.bankShared) {
                            append(" (Bank sharing disabled)")
                        } else if (!progress.tabsKnown && goalConfig.source != GoalMath.WHOLE_BANK) {
                            append(" · Open bank in-game once to read tabs")
                        }
                    }
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.5.sp,
                        color = if (!state.bankShared) OsrsRed else OsrsTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GoalSettingsDialog(
    state: MainDashboardUiState,
    activePriceMode: GoalPriceMode,
    onDismiss: () -> Unit,
    onSave: (GoalConfig) -> Unit,
    onSearchQueryChanged: (String) -> Unit
) {
    val currentConfig = state.goalConfig
    var selectedSource by remember { mutableIntStateOf(currentConfig.source) }
    var selectedPriceMode by remember { mutableStateOf(currentConfig.priceMode ?: activePriceMode) }

    var goalModeTab by remember {
        mutableIntStateOf(if (currentConfig.targetGp != null && currentConfig.targetGp > 0L) 1 else 0)
    }

    var selectedItem by remember { mutableStateOf(state.goalTargetItem) }
    var itemSearchText by remember { mutableStateOf("") }
    var customGpText by remember {
        mutableStateOf(currentConfig.targetGp?.let { formatGp(it).replace(" ", "") } ?: "")
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(OsrsDarkStone)
                .osrsPanel(showRivets = true)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Dialog Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Goal Tracker Settings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OsrsGold
                    )
                    Text(
                        text = "✕",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OsrsTextMuted,
                        modifier = Modifier
                            .clickable { onDismiss() }
                            .padding(4.dp)
                    )
                }

                OsrsHeaderDivider()

                // Section 1: Count value from
                Text(
                    text = "Count bank value from:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = OsrsTextPrimary
                )

                val availableTabs = remember(state.bankItems) {
                    val tabsWithItems = state.bankItems.mapNotNull { it.tab }.distinct().sorted()
                    val result = mutableListOf(GoalMath.WHOLE_BANK, 0)
                    if (tabsWithItems.isNotEmpty()) {
                        result.addAll(tabsWithItems.filter { it > 0 })
                    } else {
                        result.addAll(1..9)
                    }
                    result.distinct()
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    availableTabs.forEach { tab ->
                        val isSelected = selectedSource == tab
                        val label = GoalMath.tabLabel(state.bankItems, tab)
                        OsrsFilterChip(
                            text = label,
                            selected = isSelected,
                            onClick = { selectedSource = tab }
                        )
                    }
                }

                OsrsHeaderDivider()

                // Section 2: Goal Target (Search Item vs Custom GP)
                Text(
                    text = "Target goal:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = OsrsTextPrimary
                )

                OsrsSegmentedTabs(
                    tabs = listOf("Search Item" to 0, "Custom GP" to 0),
                    selectedIndex = goalModeTab,
                    onTabSelected = { goalModeTab = it }
                )

                if (goalModeTab == 0) {
                    // Item search
                    if (selectedItem != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(2.dp))
                                .background(OsrsSlotRecessed)
                                .border(1.dp, OsrsOuterFrame, RoundedCornerShape(2.dp))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ItemIcon(
                                id = selectedItem!!.id,
                                base64 = selectedItem!!.iconBase64,
                                name = selectedItem!!.name,
                                size = 32.dp
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = selectedItem!!.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = OsrsTextPrimary
                                )
                                val price = if (selectedPriceMode == GoalPriceMode.GE) selectedItem!!.gePrice else selectedItem!!.haPrice
                                Text(
                                    text = "${selectedPriceMode.name}: ${formatGp(price)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = JetBrainsMonoFontFamily,
                                    color = OsrsGold
                                )
                            }
                            Text(
                                text = "Clear",
                                style = MaterialTheme.typography.labelSmall,
                                color = OsrsRed,
                                modifier = Modifier
                                    .clickable { selectedItem = null }
                                    .padding(4.dp)
                            )
                        }
                    }

                    OsrsTextField(
                        value = itemSearchText,
                        onValueChange = {
                            itemSearchText = it
                            onSearchQueryChanged(it)
                        },
                        label = "",
                        placeholder = "Search item name (e.g. Twisted bow)...",
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Search results dropdown list
                    if (state.goalItemSearchResults.isNotEmpty() && itemSearchText.trim().length >= 2) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 140.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(OsrsSlotRecessed)
                                .border(1.dp, OsrsBorderDark, RoundedCornerShape(2.dp))
                        ) {
                            items(state.goalItemSearchResults) { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedItem = item
                                            itemSearchText = ""
                                            onSearchQueryChanged("")
                                        }
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    ItemIcon(id = item.id, base64 = item.iconBase64, name = item.name, size = 26.dp)
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OsrsTextPrimary,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = formatGp(item.gePrice),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = JetBrainsMonoFontFamily,
                                        color = OsrsGold
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Custom GP input
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        OsrsTextField(
                            value = customGpText,
                            onValueChange = { customGpText = it },
                            label = "",
                            placeholder = "e.g. 150m, 1.2b, 500k",
                            modifier = Modifier.fillMaxWidth()
                        )
                        val parsed = GoalMath.parseGp(customGpText)
                        if (parsed != null) {
                            Text(
                                text = "Target: ${formatGp(parsed)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = JetBrainsMonoFontFamily,
                                color = OsrsGold
                            )
                        } else if (customGpText.isNotBlank()) {
                            Text(
                                text = "Invalid GP expression",
                                style = MaterialTheme.typography.bodySmall,
                                color = OsrsRed
                            )
                        }
                    }
                }

                OsrsHeaderDivider()

                // Section 3: Pricing Mode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Valuation mode:",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = OsrsTextPrimary
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OsrsFilterChip(
                            text = "Grand Exchange",
                            selected = selectedPriceMode == GoalPriceMode.GE,
                            onClick = { selectedPriceMode = GoalPriceMode.GE }
                        )
                        OsrsFilterChip(
                            text = "High Alchemy",
                            selected = selectedPriceMode == GoalPriceMode.HA,
                            onClick = { selectedPriceMode = GoalPriceMode.HA }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Bottom Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (currentConfig.isConfigured) {
                        OsrsButton(
                            text = "Clear",
                            onClick = {
                                onSave(GoalConfig())
                            },
                            variant = OsrsButtonVariant.DANGER,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OsrsButton(
                        text = "Cancel",
                        onClick = onDismiss,
                        variant = OsrsButtonVariant.STONE,
                        modifier = Modifier.weight(1f)
                    )

                    OsrsButton(
                        text = "Save",
                        onClick = {
                            val newConfig = if (goalModeTab == 0) {
                                GoalConfig(
                                    source = selectedSource,
                                    priceMode = selectedPriceMode,
                                    targetItemId = selectedItem?.id,
                                    targetGp = null
                                )
                            } else {
                                val parsed = GoalMath.parseGp(customGpText)
                                GoalConfig(
                                    source = selectedSource,
                                    priceMode = selectedPriceMode,
                                    targetItemId = null,
                                    targetGp = parsed
                                )
                            }
                            onSave(newConfig)
                        },
                        variant = OsrsButtonVariant.GOLD,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
