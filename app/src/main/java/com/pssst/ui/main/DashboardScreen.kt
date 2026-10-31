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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.pssst.theme.OsrsButton
import com.pssst.theme.OsrsButtonVariant
import com.pssst.theme.OsrsDarkStone
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsHardShadow
import com.pssst.theme.OsrsHeaderDivider
import com.pssst.theme.OsrsHerbGreen
import com.pssst.theme.OsrsOuterFrame
import com.pssst.theme.OsrsPanelDark
import com.pssst.theme.OsrsRed
import com.pssst.theme.OsrsSlotEmpty
import com.pssst.theme.OsrsSwitch
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.OsrsTextPrimary
import com.pssst.theme.art.ArtIcons
import com.pssst.theme.art.BulletGlyph
import com.pssst.theme.art.FrameWeight
import com.pssst.theme.art.GoldLeafText
import com.pssst.theme.art.PixelIcon
import com.pssst.theme.art.SectionHeaderPlaque
import com.pssst.theme.art.SpriteTones
import com.pssst.theme.art.TitlePlaque
import com.pssst.theme.art.leatherBoundPanel
import com.pssst.theme.art.oakBeamFrame
import com.pssst.theme.art.stoneGround
import com.pssst.theme.art.stoneSillFrame
import com.pssst.theme.art.stoneTabSlot
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
                        .leatherBoundPanel()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        TitlePlaque(
                            title = "Pssst Companion",
                            subtitle = "OSRS Offline-First Tracker"
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        OsrsHeaderDivider(withCenterFlourish = true)
                        Spacer(modifier = Modifier.height(14.dp))

                        // Connection Details Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .osrsPanel(weight = FrameWeight.MEDIUM)
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                GoldLeafText(
                                    text = "Connection Status",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
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
                                .osrsPanel(weight = FrameWeight.LIGHT)
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
                                .osrsPanel(weight = FrameWeight.LIGHT)
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
                        SectionHeaderPlaque(title = "Jump to Section")
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
                            Row(
                                modifier = navMod,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BulletGlyph(
                                    size = 6.dp,
                                    color = if (isSelected) OsrsGold else OsrsTextMuted.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
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
                            textColor = OsrsGold,
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

                        Text(
                            text = "Unofficial fan app. Old School RuneScape is a trademark of Jagex Ltd; Pssst is not affiliated with or endorsed by Jagex.",
                            style = MaterialTheme.typography.labelSmall,
                            color = OsrsTextMuted.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
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
                        .oakBeamFrame()
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
                            GoldLeafText(
                                text = currentTab.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                                fontWeight = FontWeight.ExtraBold
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                BulletGlyph(size = 5.dp, color = OsrsGold)
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
                }
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .stoneSillFrame()
                        .navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        tabs.forEachIndexed { index, tab ->
                            val isSelected = selectedTabIndex == index
                            val tint = if (isSelected) OsrsGold else OsrsTextMuted
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .stoneTabSlot(selected = isSelected)
                                    .clickable { selectedTabIndex = index }
                                    .padding(vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                when (tab) {
                                    DashboardTab.OVERVIEW -> ShieldVectorIcon(selected = isSelected, tint = tint)
                                    DashboardTab.STORAGE -> ChestVectorIcon(selected = isSelected, tint = tint)
                                    DashboardTab.GE -> CoinsVectorIcon(selected = isSelected, tint = tint)
                                    DashboardTab.PROGRESSION -> StarVectorIcon(selected = isSelected, tint = tint)
                                    DashboardTab.TIMERS -> HourglassVectorIcon(selected = isSelected, tint = tint)
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
                    .stoneGround()
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
fun HamburgerVectorIcon(tint: Color = OsrsGold, modifier: Modifier = Modifier.size(16.dp)) {
    PixelIcon(
        sprite = ArtIcons.DrawerLedgerMenu,
        tones = SpriteTones.GOLD,
        modifier = modifier
    )
}

@Composable
fun ShieldVectorIcon(selected: Boolean = true, tint: Color = OsrsGold, modifier: Modifier = Modifier.size(16.dp)) {
    PixelIcon(
        sprite = ArtIcons.OverviewShield,
        tones = if (selected) SpriteTones.GOLD else SpriteTones.STONE,
        modifier = modifier
    )
}

@Composable
fun ChestVectorIcon(selected: Boolean = true, tint: Color = OsrsGold, modifier: Modifier = Modifier.size(16.dp)) {
    PixelIcon(
        sprite = ArtIcons.StorageChest,
        tones = if (selected) SpriteTones.GOLD else SpriteTones.STONE,
        modifier = modifier
    )
}

@Composable
fun CoinsVectorIcon(selected: Boolean = true, tint: Color = OsrsGold, modifier: Modifier = Modifier.size(16.dp)) {
    PixelIcon(
        sprite = ArtIcons.ExchangeCoins,
        tones = if (selected) SpriteTones.GOLD else SpriteTones.STONE,
        modifier = modifier
    )
}

@Composable
fun StarVectorIcon(selected: Boolean = true, tint: Color = OsrsGold, modifier: Modifier = Modifier.size(16.dp)) {
    PixelIcon(
        sprite = ArtIcons.ProgressStar,
        tones = if (selected) SpriteTones.GOLD else SpriteTones.STONE,
        modifier = modifier
    )
}

@Composable
fun HourglassVectorIcon(selected: Boolean = true, tint: Color = OsrsGold, modifier: Modifier = Modifier.size(16.dp)) {
    PixelIcon(
        sprite = ArtIcons.TimersHourglass,
        tones = if (selected) SpriteTones.GOLD else SpriteTones.STONE,
        modifier = modifier
    )
}

