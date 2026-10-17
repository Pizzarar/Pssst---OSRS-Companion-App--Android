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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.pssst.data.network.StateApiService
import com.pssst.theme.OsrsBorderOutline
import com.pssst.theme.OsrsDarkStone
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsHerbGreen
import com.pssst.theme.OsrsPanelDark
import com.pssst.theme.OsrsPanelLight
import com.pssst.theme.OsrsRed
import com.pssst.theme.OsrsSlotEmpty
import com.pssst.theme.OsrsSlotRecessed
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.OsrsTextPrimary
import com.pssst.theme.OsrsYellow
import com.pssst.theme.osrsPanel
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
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabs = DashboardTab.entries
    val currentTab = tabs[selectedTabIndex]
    val displayServerUrl = state.serverUrl.ifBlank { StateApiService.DEFAULT_BASE_URL }

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

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = OsrsBorderOutline, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))

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
                                Text(
                                    text = "Server: $displayServerUrl",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OsrsTextMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (state.uuid.isNotBlank()) {
                                    Text(
                                        text = "Token: ${state.uuid.take(6)}...${state.uuid.takeLast(6)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OsrsTextMuted
                                    )
                                }
                                state.lastSyncStatus?.let { status ->
                                    val isOk = status.startsWith("OK") || status.startsWith("Connected")
                                    Text(
                                        text = status,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isOk) OsrsHerbGreen else OsrsRed
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Notification Toggle Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .osrsPanel()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
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
                            Switch(
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
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.Black,
                                    checkedTrackColor = OsrsHerbGreen,
                                    uncheckedThumbColor = OsrsTextMuted,
                                    uncheckedTrackColor = OsrsSlotEmpty
                                )
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
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isSelected) OsrsSlotRecessed else Color.Transparent)
                                    .clickable {
                                        selectedTabIndex = index
                                        scope.launch { drawerState.close() }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
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
                        TextButton(
                            onClick = {
                                SyncScheduler.syncNow(context)
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Sync Now",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = OsrsYellow
                            )
                        }

                        TextButton(
                            onClick = {
                                scope.launch { drawerState.close() }
                                onDisconnect()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Disconnect / Change Token",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = OsrsRed
                            )
                        }
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
                ) {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Pssst • ${currentTab.title}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = OsrsGold
                                )
                                if (state.lastSyncedAt > 0L) {
                                    val rel = android.text.format.DateUtils.getRelativeTimeSpanString(state.lastSyncedAt)
                                    Text(
                                        text = "($rel)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = OsrsTextMuted,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                HamburgerVectorIcon(tint = OsrsGold)
                            }
                        },
                        actions = {
                            TextButton(
                                onClick = { SyncScheduler.syncNow(context) }
                            ) {
                                Text(
                                    text = "Sync",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = OsrsYellow
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )
                    HorizontalDivider(color = OsrsBorderOutline, thickness = 1.dp)
                }
            },
            bottomBar = {
                Column {
                    HorizontalDivider(color = OsrsBorderOutline, thickness = 1.dp)
                    NavigationBar(
                        containerColor = OsrsPanelDark,
                        contentColor = OsrsTextMuted
                    ) {
                        tabs.forEachIndexed { index, tab ->
                            val isSelected = selectedTabIndex == index
                            val tint = if (isSelected) OsrsGold else OsrsTextMuted
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { selectedTabIndex = index },
                                label = {
                                    Text(
                                        text = tab.title,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = tint,
                                        fontSize = 11.sp
                                    )
                                },
                                icon = {
                                    when (tab) {
                                        DashboardTab.OVERVIEW -> ShieldVectorIcon(tint = tint)
                                        DashboardTab.STORAGE -> ChestVectorIcon(tint = tint)
                                        DashboardTab.GE -> CoinsVectorIcon(tint = tint)
                                        DashboardTab.PROGRESSION -> StarVectorIcon(tint = tint)
                                        DashboardTab.TIMERS -> HourglassVectorIcon(tint = tint)
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = OsrsGold,
                                    selectedTextColor = OsrsGold,
                                    indicatorColor = OsrsSlotRecessed,
                                    unselectedIconColor = OsrsTextMuted,
                                    unselectedTextColor = OsrsTextMuted
                                )
                            )
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
                        onNavigateToTab = { selectedTabIndex = tabs.indexOf(it) }
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
