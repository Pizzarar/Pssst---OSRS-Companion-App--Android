package com.pssst.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.pssst.theme.OsrsButton
import com.pssst.theme.OsrsButtonVariant
import com.pssst.theme.OsrsDarkStone
import androidx.compose.ui.draw.drawBehind
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsHardShadow
import com.pssst.theme.OsrsHeaderDivider
import com.pssst.theme.OsrsHerbGreen
import com.pssst.theme.OsrsOuterFrame
import com.pssst.theme.OsrsPanelDark
import com.pssst.theme.OsrsPanelLight
import com.pssst.theme.OsrsRed
import com.pssst.theme.OsrsSlotEmpty
import com.pssst.theme.OsrsSwitch
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.OsrsTextPrimary
import com.pssst.theme.OsrsYellow
import com.pssst.theme.osrsPanel
import com.pssst.theme.osrsSlot
import com.pssst.util.TimeFormat
import com.pssst.worker.SyncScheduler
import kotlinx.coroutines.launch

enum class DashboardTab(val title: String) {
    OVERVIEW("Overview"),
    STORAGE("Storage"),
    GE("Exchange"),
    PROGRESSION("Progress"),
    TIMERS("Timers")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    state: MainDashboardUiState,
    onSearchQueryChanged: (String) -> Unit,
    onBankSortModeChanged: (BankSortMode) -> Unit,
    onPotionSearchQueryChanged: (String) -> Unit,
    onQuestSearchQueryChanged: (String) -> Unit,
    onQuestFilterChanged: (QuestFilter) -> Unit,
    onDisconnect: () -> Unit,
    onToggleNotifications: (Boolean) -> Unit = {},
    onSaveGoalConfig: (com.pssst.data.local.GoalConfig) -> Unit = {},
    onSearchGoalItem: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    var timersSubTab by rememberSaveable { mutableStateOf(TimersSubTab.DAILIES) }
    var editing by rememberSaveable { mutableStateOf(false) }
    val tabs = DashboardTab.entries
    val currentTab = tabs[selectedTabIndex]
    val nowMs by rememberNowMs()
    // The pencil shows where there is a list to arrange (not on Payments, which has nothing to hide)
    val canEdit = currentTab == DashboardTab.OVERVIEW ||
        (currentTab == DashboardTab.TIMERS && timersSubTab != TimersSubTab.PAYMENTS)
    LaunchedEffect(selectedTabIndex, timersSubTab) { editing = false }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onToggleNotifications(isGranted)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = OsrsPanelDark,
                drawerContentColor = OsrsTextPrimary,
                modifier = Modifier.width(310.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        // Drawer Title
                        Text(
                            text = "Pssst Companion",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = OsrsGold
                        )
                        Text(
                            text = "OSRS Offline-First Tracker",
                            style = MaterialTheme.typography.bodySmall,
                            color = OsrsTextMuted
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        OsrsHeaderDivider()
                        Spacer(modifier = Modifier.height(14.dp))

                        // Connection Details Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .osrsPanel()
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Connection Status",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = OsrsGold
                                )
                                if (state.uuid.isNotBlank()) {
                                    val displayToken = if (state.uuid.length <= 16) {
                                        state.uuid
                                    } else {
                                        "${state.uuid.take(6)}...${state.uuid.takeLast(6)}"
                                    }
                                    Text(
                                        text = "Sync Token: $displayToken",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OsrsTextMuted
                                    )
                                }
                                val phoneAddresses = remember { com.pssst.util.PhoneAddresses.forDisplay(context) }
                                Text(
                                    text = if (phoneAddresses.isEmpty()) "Phone address: connect to Wi-Fi to see it"
                                    else "Phone address for RuneLite: " + phoneAddresses.joinToString(" or ") { it.host },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OsrsTextMuted
                                )
                                state.lastSyncStatus?.let { status ->
                                    val isOk = status.startsWith("OK")
                                    val isQuiet = !isOk && !status.startsWith(com.pssst.worker.SyncWorker.STATUS_OLDER)
                                    Text(
                                        text = status,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isOk) OsrsHerbGreen else if (isQuiet) OsrsTextMuted else OsrsRed
                                    )
                                    if (state.lastSyncedAt > 0L) {
                                        Text(
                                            text = "Last update received " + TimeFormat.ageLabel(nowMs - state.lastSyncedAt),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = OsrsTextMuted
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Where item prices come from (read-only: nothing to choose, nothing contacted)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .osrsPanel()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Item prices",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = OsrsTextPrimary
                                )
                                Text(
                                    text = "As of ${TimeFormat.formatDate(state.pricesAsOfMs)} \u2022 refreshed from your RuneLite client on each sync",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OsrsTextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Notification Toggle Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .osrsPanel()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Notifications",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = OsrsTextPrimary
                                )
                                Text(
                                    text = if (state.notificationsEnabled) "Active alerts" else "Disabled",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OsrsTextMuted
                                )
                            }
                            OsrsSwitch(
                                checked = state.notificationsEnabled,
                                onCheckedChange = { enabled ->
                                    if (enabled) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            val hasPerm = ContextCompat.checkSelfPermission(
                                                context,
                                                Manifest.permission.POST_NOTIFICATIONS
                                            ) == PackageManager.PERMISSION_GRANTED
                                            if (hasPerm) {
                                                onToggleNotifications(true)
                                            } else {
                                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                            }
                                        } else {
                                            onToggleNotifications(true)
                                        }
                                    } else {
                                        onToggleNotifications(false)
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Navigation Jump Links
                        Text(
                            text = "Jump to Section",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = OsrsGold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        tabs.forEachIndexed { index, tab ->
                            val isSelected = selectedTabIndex == index
                            val navMod = if (isSelected) {
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .osrsSlot(hasItem = false, cornerRadius = 2.dp, highlightBorderColor = OsrsGold)
                                    .clickable {
                                        selectedTabIndex = index
                                        scope.launch { drawerState.close() }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            } else {
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .clickable {
                                        selectedTabIndex = index
                                        scope.launch { drawerState.close() }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            }
                            Box(modifier = navMod) {
                                Text(
                                    text = tab.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) OsrsGold else OsrsTextPrimary
                                )
                            }
                        }
                    }

                    // Bottom Drawer Actions
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OsrsButton(
                            text = "Listen Now",
                            onClick = {
                                SyncScheduler.syncNow(context)
                                scope.launch { drawerState.close() }
                            },
                            variant = OsrsButtonVariant.STONE,
                            textColor = OsrsYellow,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OsrsButton(
                            text = "Disconnect / Change Token",
                            onClick = {
                                scope.launch { drawerState.close() }
                                onDisconnect()
                            },
                            variant = OsrsButtonVariant.DANGER,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = OsrsDarkStone,
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(listOf(OsrsPanelLight, OsrsPanelDark))
                        )
                        .statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 6.dp, end = 10.dp, top = 10.dp, bottom = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.size(40.dp)
                        ) {
                            HamburgerVectorIcon(tint = OsrsGold, modifier = Modifier.size(25.dp))
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(1.dp)
                        ) {
                            Text(
                                text = currentTab.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = OsrsGold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (state.gameObservedAt > 0L) {
                                    "Synced " + TimeFormat.ageLabel(nowMs - state.gameObservedAt)
                                } else {
                                    "Offline snapshot"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 11.5.sp,
                                color = OsrsTextMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TopBarReadyRow(
                            state = state,
                            nowMs = nowMs,
                            onNavigate = { targetTab, targetSubTab ->
                                selectedTabIndex = tabs.indexOf(targetTab)
                                if (targetSubTab != null) {
                                    timersSubTab = targetSubTab
                                }
                            }
                        )
                        if (canEdit) {
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = { editing = !editing },
                                modifier = Modifier.size(38.dp)
                            ) {
                                if (editing) {
                                    CheckVectorIcon(tint = OsrsHerbGreen, modifier = Modifier.size(22.dp))
                                } else {
                                    PencilVectorIcon(tint = OsrsGold, modifier = Modifier.size(22.dp))
                                }
                            }
                        }
                    }
                    OsrsHeaderDivider()
                }
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(OsrsPanelDark, OsrsSlotEmpty)))
                        .navigationBarsPadding()
                ) {
                    OsrsHeaderDivider()
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        tabs.forEachIndexed { index, tab ->
                            val isSelected = selectedTabIndex == index
                            val tint = if (isSelected) OsrsGold else OsrsTextMuted
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (isSelected) OsrsSlotEmpty else Color.Transparent)
                                    .clickable { selectedTabIndex = index }
                                    .drawBehind {
                                        if (index > 0) {
                                            drawLine(
                                                color = OsrsOuterFrame,
                                                start = Offset(0f, 8.dp.toPx()),
                                                end = Offset(0f, size.height - 8.dp.toPx()),
                                                strokeWidth = 1.dp.toPx()
                                            )
                                        }
                                        if (isSelected) {
                                            val indH = 3.dp.toPx()
                                            drawRect(
                                                color = OsrsGold,
                                                topLeft = Offset(0f, 0f),
                                                size = Size(size.width, indH)
                                            )
                                        }
                                    }
                                    .padding(vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                when (tab) {
                                    DashboardTab.OVERVIEW -> ShieldVectorIcon(tint = tint, modifier = Modifier.size(24.dp))
                                    DashboardTab.STORAGE -> ChestVectorIcon(tint = tint, modifier = Modifier.size(24.dp))
                                    DashboardTab.GE -> CoinsVectorIcon(tint = tint, modifier = Modifier.size(24.dp))
                                    DashboardTab.PROGRESSION -> StarVectorIcon(tint = tint, modifier = Modifier.size(24.dp))
                                    DashboardTab.TIMERS -> HourglassVectorIcon(tint = tint, modifier = Modifier.size(24.dp))
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = tab.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = tint,
                                    fontSize = 11.5.sp,
                                    maxLines = 1,
                                    style = MaterialTheme.typography.labelSmall.copy(shadow = OsrsHardShadow)
                                )
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(OsrsDarkStone)
            ) {
                when (currentTab) {
                    DashboardTab.OVERVIEW -> OverviewScreen(
                        state = state,
                        onNavigateToTab = { selectedTabIndex = tabs.indexOf(it) },
                        onSaveGoalConfig = onSaveGoalConfig,
                        onSearchGoalItem = onSearchGoalItem,
                        editing = editing
                    )
                    DashboardTab.STORAGE -> StorageScreen(
                        state = state,
                        onSearchQueryChanged = onSearchQueryChanged,
                        onBankSortModeChanged = onBankSortModeChanged,
                        onPotionSearchQueryChanged = onPotionSearchQueryChanged
                    )
                    DashboardTab.GE -> GrandExchangeScreen(
                        state = state
                    )
                    DashboardTab.PROGRESSION -> ProgressionScreen(
                        state = state,
                        onQuestSearchQueryChanged = onQuestSearchQueryChanged,
                        onQuestFilterChanged = onQuestFilterChanged
                    )
                    DashboardTab.TIMERS -> TimersScreen(
                        state = state,
                        selectedTab = timersSubTab,
                        onSelectedTabChange = { timersSubTab = it },
                        editing = editing,
                        onToggleNotifications = onToggleNotifications
                    )
                }
            }
        }
    }
}

@Composable
fun HamburgerVectorIcon(tint: Color, modifier: Modifier = Modifier.size(24.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val barH = h * 0.12f
        val stroke = barH
        drawLine(tint, Offset(w * 0.15f, h * 0.25f), Offset(w * 0.85f, h * 0.25f), strokeWidth = stroke)
        drawLine(tint, Offset(w * 0.15f, h * 0.50f), Offset(w * 0.85f, h * 0.50f), strokeWidth = stroke)
        drawLine(tint, Offset(w * 0.15f, h * 0.75f), Offset(w * 0.85f, h * 0.75f), strokeWidth = stroke)
    }
}

@Composable
fun ShieldVectorIcon(tint: Color, modifier: Modifier = Modifier.size(24.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.15f)
            lineTo(w * 0.85f, h * 0.25f)
            lineTo(w * 0.80f, h * 0.65f)
            lineTo(w * 0.5f, h * 0.88f)
            lineTo(w * 0.20f, h * 0.65f)
            lineTo(w * 0.15f, h * 0.25f)
            close()
        }
        drawPath(path, color = tint, style = Stroke(width = w * 0.08f))
    }
}

@Composable
fun ChestVectorIcon(tint: Color, modifier: Modifier = Modifier.size(24.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawRect(tint, Offset(w * 0.18f, h * 0.35f), Size(w * 0.64f, h * 0.50f), style = Stroke(width = w * 0.08f))
        drawLine(tint, Offset(w * 0.18f, h * 0.52f), Offset(w * 0.82f, h * 0.52f), strokeWidth = w * 0.08f)
        drawRect(tint, Offset(w * 0.44f, h * 0.48f), Size(w * 0.12f, h * 0.12f))
    }
}

@Composable
fun CoinsVectorIcon(tint: Color, modifier: Modifier = Modifier.size(24.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawCircle(tint, radius = w * 0.28f, center = Offset(w * 0.45f, h * 0.55f), style = Stroke(width = w * 0.08f))
        drawCircle(tint, radius = w * 0.28f, center = Offset(w * 0.58f, h * 0.40f), style = Stroke(width = w * 0.08f))
    }
}

@Composable
fun StarVectorIcon(tint: Color, modifier: Modifier = Modifier.size(24.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.12f)
            lineTo(w * 0.62f, h * 0.38f)
            lineTo(w * 0.90f, h * 0.38f)
            lineTo(w * 0.67f, h * 0.55f)
            lineTo(w * 0.76f, h * 0.82f)
            lineTo(w * 0.5f, h * 0.66f)
            lineTo(w * 0.24f, h * 0.82f)
            lineTo(w * 0.33f, h * 0.55f)
            lineTo(w * 0.10f, h * 0.38f)
            lineTo(w * 0.38f, h * 0.38f)
            close()
        }
        drawPath(path, color = tint, style = Stroke(width = w * 0.08f))
    }
}

@Composable
fun HourglassVectorIcon(tint: Color, modifier: Modifier = Modifier.size(24.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.08f
        drawRect(tint, Offset(w * 0.15f, h * 0.12f), Size(w * 0.7f, h * 0.08f))
        drawRect(tint, Offset(w * 0.15f, h * 0.80f), Size(w * 0.7f, h * 0.08f))

        val glassPath = Path().apply {
            moveTo(w * 0.22f, h * 0.2f)
            lineTo(w * 0.78f, h * 0.2f)
            lineTo(w * 0.5f, h * 0.5f)
            lineTo(w * 0.78f, h * 0.8f)
            lineTo(w * 0.22f, h * 0.8f)
            lineTo(w * 0.5f, h * 0.5f)
            close()
        }
        drawPath(glassPath, color = tint, style = Stroke(width = stroke))

        val sandPath = Path().apply {
            moveTo(w * 0.3f, h * 0.78f)
            lineTo(w * 0.7f, h * 0.78f)
            lineTo(w * 0.5f, h * 0.62f)
            close()
        }
        drawPath(sandPath, color = tint)
    }
}
