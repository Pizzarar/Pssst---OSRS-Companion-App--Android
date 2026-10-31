package com.pssst.theme.art

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pssst.theme.OsrsRed
import com.pssst.theme.OsrsSegmentedTabs
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.OsrsTextPrimary
import com.pssst.theme.PssstTheme
import com.pssst.ui.main.BankScreen
import com.pssst.ui.main.DashboardTab
import com.pssst.ui.main.GrandExchangeScreen
import com.pssst.ui.main.InventoryScreen
import com.pssst.ui.main.MiscellaniaCard
import com.pssst.ui.main.MiscellaniaUiModel
import com.pssst.ui.main.OverviewScreen
import com.pssst.ui.main.PotionStorageScreen
import com.pssst.ui.main.QuestsSubScreen
import com.pssst.ui.main.SkillsSubScreen
import com.pssst.ui.main.SlayerTaskCard
import com.pssst.ui.main.SlayerUiModel
import com.pssst.ui.main.TimersScreen
import com.pssst.ui.main.TimersSubTab
import com.pssst.ui.setup.SetupContent

// ============================================================================
// PART 1: INDIVIDUAL ASSET PREVIEWS
// ============================================================================

@Preview(name = "1. Procedural Textures", showBackground = true, backgroundColor = 0xFF191612, widthDp = 380)
@Composable
fun PreviewProceduralTextures() {
    PssstTheme {
        Column(
            modifier = Modifier
                .stoneGround()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GoldLeafText("PROCEDURAL MATERIALS", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f).height(56.dp).carvedStone().padding(8.dp)) {
                    Text("Carved Stone", color = OsrsTextPrimary, style = MaterialTheme.typography.labelMedium)
                }
                Box(modifier = Modifier.weight(1f).height(56.dp).oakBeam().padding(8.dp)) {
                    Text("Aged Dark Oak", color = OsrsTextPrimary, style = MaterialTheme.typography.labelMedium)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f).height(56.dp).parchment().padding(8.dp)) {
                    Text("Weathered Parchment", color = OsrsTextPrimary, style = MaterialTheme.typography.labelMedium)
                }
                Box(modifier = Modifier.weight(1f).height(56.dp).leather().padding(8.dp)) {
                    Text("Worn Leather", color = OsrsTextPrimary, style = MaterialTheme.typography.labelMedium)
                }
            }
            Box(modifier = Modifier.fillMaxWidth().height(48.dp).hammeredIron().padding(8.dp)) {
                Text("Hammered Wrought Iron", color = OsrsTextPrimary, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Preview(name = "2. Frames & Hardware Weights", showBackground = true, backgroundColor = 0xFF191612, widthDp = 380)
@Composable
fun PreviewFramesAndHardware() {
    PssstTheme {
        Column(
            modifier = Modifier
                .stoneGround()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth().ledgerPanelFrame(FrameWeight.LIGHT).padding(12.dp)) {
                Text("FrameWeight.LIGHT — Corner Rivet Studs", color = OsrsTextPrimary)
            }
            Box(modifier = Modifier.fillMaxWidth().ledgerPanelFrame(FrameWeight.MEDIUM).padding(12.dp)) {
                Text("FrameWeight.MEDIUM — Iron L-Brackets", color = OsrsTextPrimary)
            }
            Box(modifier = Modifier.fillMaxWidth().ledgerPanelFrame(FrameWeight.HEAVY).padding(14.dp)) {
                Text("FrameWeight.HEAVY — Spearhead Hinge Straps", color = OsrsTextPrimary)
            }
            Box(modifier = Modifier.fillMaxWidth().oakBeamFrame().padding(12.dp)) {
                GoldLeafText("oakBeamFrame — Top Bar Carved Stone Header")
            }
            Box(modifier = Modifier.fillMaxWidth().parchmentNotice().padding(12.dp)) {
                Text("parchmentNotice — Pinned Notice Strip", color = OsrsTextPrimary)
            }
            Box(modifier = Modifier.fillMaxWidth().leatherBoundPanel().padding(12.dp)) {
                Text("leatherBoundPanel — Stitched Hide Tray", color = OsrsTextPrimary)
            }
        }
    }
}

@Preview(name = "3. Plaques, Bookmarks & Number Badges", showBackground = true, backgroundColor = 0xFF191612, widthDp = 380)
@Composable
fun PreviewPlaquesAndSeals() {
    PssstTheme {
        Column(
            modifier = Modifier
                .stoneGround()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TitlePlaque(title = "Pssst", subtitle = "A WELL-WORN ADVENTURER'S LEDGER")
            SectionHeaderPlaque(title = "PINNED GOALS")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier.weight(1f).bookmarkTab(selected = true).padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    GoldLeafText("BANK", style = MaterialTheme.typography.labelLarge)
                }
                Box(
                    modifier = Modifier.weight(1f).bookmarkTab(selected = false).padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("INVENTORY", color = OsrsTextMuted, style = MaterialTheme.typography.labelLarge)
                }
                Box(
                    modifier = Modifier.weight(1f).bookmarkTab(selected = false).padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("POTIONS", color = OsrsTextMuted, style = MaterialTheme.typography.labelLarge)
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NumberPlaque(label = "1")
                NumberPlaque(label = "2")
                NumberPlaque(label = "IP")
                IronGripHandle()
                BulletGlyph()
            }
        }
    }
}

@Preview(name = "4. Timers Engraved Table", showBackground = true, backgroundColor = 0xFF191612, widthDp = 380)
@Composable
fun PreviewTimersTable() {
    PssstTheme {
        Column(
            modifier = Modifier
                .stoneGround()
                .padding(12.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth().ledgerPanelFrame(FrameWeight.MEDIUM)) {
                OsrsTableHeaderPlaque(leftLabel = "PATCH / LOCATION", rightLabel = "STATUS")
                OsrsTableRow(index = 0) {
                    Text("Herb — Hosidius", color = OsrsTextPrimary, style = MaterialTheme.typography.bodyMedium)
                    OsrsTableStatusCell(text = "Ready", status = TableStatusKind.READY)
                }
                OsrsTableRowSeparator()
                OsrsTableRow(index = 1) {
                    Text("Herb — Catherby", color = OsrsTextPrimary, style = MaterialTheme.typography.bodyMedium)
                    OsrsTableStatusCell(text = "Growing (38m)", status = TableStatusKind.GROWING)
                }
                OsrsTableRowSeparator()
                OsrsTableRow(index = 2) {
                    Text("Herb — Ardougne", color = OsrsTextMuted, style = MaterialTheme.typography.bodyMedium)
                    OsrsTableStatusCell(text = "Empty", status = TableStatusKind.EMPTY)
                }
                OsrsTableRowSeparator()
                OsrsTableRow(index = 3) {
                    Text("Tree — Varrock", color = OsrsRed, style = MaterialTheme.typography.bodyMedium)
                    OsrsTableStatusCell(text = "Diseased", status = TableStatusKind.WARNING)
                }
            }
        }
    }
}

@Preview(name = "5. Ornaments, Icons & Badges", showBackground = true, backgroundColor = 0xFF191612, widthDp = 380)
@Composable
fun PreviewOrnamentsAndIcons() {
    PssstTheme {
        Column(
            modifier = Modifier
                .stoneGround()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            EngravedRule(withEndCaps = true, withCenterFlourish = true)
            EngravedRule(withEndCaps = true, withCenterFlourish = false)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PixelIcon(ArtIcons.OverviewShield, SpriteTones.GOLD)
                PixelIcon(ArtIcons.StorageChest, SpriteTones.GOLD)
                PixelIcon(ArtIcons.ExchangeCoins, SpriteTones.GOLD)
                PixelIcon(ArtIcons.ProgressStar, SpriteTones.GOLD)
                PixelIcon(ArtIcons.TimersHourglass, SpriteTones.GOLD)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PixelIcon(ArtIcons.Birdhouse, SpriteTones.WOOD)
                PixelIcon(ArtIcons.HerbSprig, SpriteTones.HERB)
                PixelIcon(ArtIcons.TreeCrown, SpriteTones.HERB)
                PixelIcon(ArtIcons.DrawerLedgerMenu, SpriteTones.GOLD)
                PixelIcon(ArtIcons.EditQuill, SpriteTones.GOLD)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PixelIcon(ArtIcons.BadgeReady, SpriteTones.HERB)
                PixelIcon(ArtIcons.BadgeGrowing, SpriteTones.STONE)
                PixelIcon(ArtIcons.BadgeEmpty, SpriteTones.STONE)
                PixelIcon(ArtIcons.BadgeWarning, SpriteTones.WARNING)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .ledgerWellSlot(empty = true),
                contentAlignment = Alignment.Center
            ) {
                WatermarkScale()
                Text("Slot 1 — Empty", color = OsrsTextMuted, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Preview(name = "6. Empty State Vignettes", showBackground = true, backgroundColor = 0xFF191612, widthDp = 380, heightDp = 760)
@Composable
fun PreviewEmptyStateVignettes() {
    PssstTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .stoneGround()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            EmptyStateKind.entries.chunked(2).forEach { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    pair.forEach { kind ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            EmptyStateVignette(kind)
                            Text(
                                text = kind.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = OsrsTextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "7. Overview Vignette States", showBackground = true, backgroundColor = 0xFF191612, widthDp = 380, heightDp = 760)
@Composable
fun PreviewOverviewVignetteStates() {
    val healthy = PreviewFixtures.populatedState.miscellania
    val active = PreviewFixtures.populatedState.slayer
    PssstTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .stoneGround()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MiscellaniaCard(healthy)
            MiscellaniaCard(MiscellaniaUiModel(approvalPercent = 72, cofferAmount = 410_000L))
            SlayerTaskCard(active)
            SlayerTaskCard(SlayerUiModel(points = 840, streak = 176, storageUnlocked = true))
        }
    }
}

// ============================================================================
// PART 2: FULL PHONE-SIZE SCREEN PREVIEWS (380dp x 760dp)
// ============================================================================

@Preview(name = "Screen 01 — Setup", showBackground = true, backgroundColor = 0xFF191612, widthDp = 380, heightDp = 760)
@Composable
fun PreviewScreenSetup() {
    PssstTheme {
        SetupContent(
            tokenInput = "goblin-whip",
            onTokenInputChange = {},
            rememberMe = true,
            onRememberMeChange = {},
            errorMessage = null,
            phoneAddresses = PreviewFixtures.sampleAddresses,
            onRefreshAddresses = {},
            onCopyAddress = {},
            isWaitingForHandshake = false,
            isRejectedByTokenMismatch = false,
            activeToken = "goblin-whip",
            onConnect = {},
            onCancelHandshake = {}
        )
    }
}

@Preview(name = "Screen 02 — Overview", showBackground = true, backgroundColor = 0xFF191612, widthDp = 380, heightDp = 760)
@Composable
fun PreviewScreenOverview() {
    PssstTheme {
        PhoneScreenShell(selectedTab = DashboardTab.OVERVIEW, showEditIcon = true) {
            OverviewScreen(
                state = PreviewFixtures.populatedState,
                onNavigateToTab = {}
            )
        }
    }
}

@Preview(name = "Screen 03 — Storage: Bank", showBackground = true, backgroundColor = 0xFF191612, widthDp = 380, heightDp = 760)
@Composable
fun PreviewScreenBank() {
    val state = PreviewFixtures.populatedState
    PssstTheme {
        PhoneScreenShell(selectedTab = DashboardTab.STORAGE) {
            Column(modifier = Modifier.fillMaxSize()) {
                OsrsSegmentedTabs(
                    tabs = listOf("Bank (16)" to 0, "Inventory (20/28)" to 0, "Potions (5)" to 0),
                    selectedIndex = 0,
                    onTabSelected = {},
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                )
                BankScreen(state = state, onSearchQueryChanged = {})
            }
        }
    }
}

@Preview(name = "Screen 04 — Storage: Inventory", showBackground = true, backgroundColor = 0xFF191612, widthDp = 380, heightDp = 760)
@Composable
fun PreviewScreenInventory() {
    val state = PreviewFixtures.populatedState
    PssstTheme {
        PhoneScreenShell(selectedTab = DashboardTab.STORAGE) {
            Column(modifier = Modifier.fillMaxSize()) {
                OsrsSegmentedTabs(
                    tabs = listOf("Bank (16)" to 0, "Inventory (20/28)" to 0, "Potions (5)" to 0),
                    selectedIndex = 1,
                    onTabSelected = {},
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                )
                InventoryScreen(state = state)
            }
        }
    }
}

@Preview(name = "Screen 05 — Storage: Potions", showBackground = true, backgroundColor = 0xFF191612, widthDp = 380, heightDp = 760)
@Composable
fun PreviewScreenPotions() {
    val state = PreviewFixtures.populatedState
    PssstTheme {
        PhoneScreenShell(selectedTab = DashboardTab.STORAGE) {
            Column(modifier = Modifier.fillMaxSize()) {
                OsrsSegmentedTabs(
                    tabs = listOf("Bank (16)" to 0, "Inventory (20/28)" to 0, "Potions (5)" to 0),
                    selectedIndex = 2,
                    onTabSelected = {},
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                )
                PotionStorageScreen(state = state, onPotionSearchQueryChanged = {})
            }
        }
    }
}

@Preview(name = "Screen 06 — Grand Exchange", showBackground = true, backgroundColor = 0xFF191612, widthDp = 380, heightDp = 760)
@Composable
fun PreviewScreenGrandExchange() {
    PssstTheme {
        PhoneScreenShell(selectedTab = DashboardTab.GE) {
            GrandExchangeScreen(state = PreviewFixtures.populatedState)
        }
    }
}

@Preview(name = "Screen 07 — Progression: Skills", showBackground = true, backgroundColor = 0xFF191612, widthDp = 380, heightDp = 760)
@Composable
fun PreviewScreenSkills() {
    val state = PreviewFixtures.populatedState
    PssstTheme {
        PhoneScreenShell(selectedTab = DashboardTab.PROGRESSION) {
            Column(modifier = Modifier.fillMaxSize()) {
                OsrsSegmentedTabs(
                    tabs = listOf("Skills (Total: ${state.totalLevel})" to 0, "Quests (5/8)" to 0),
                    selectedIndex = 0,
                    onTabSelected = {},
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
                SkillsSubScreen(state = state)
            }
        }
    }
}

@Preview(name = "Screen 08 — Progression: Quests", showBackground = true, backgroundColor = 0xFF191612, widthDp = 380, heightDp = 760)
@Composable
fun PreviewScreenQuests() {
    val state = PreviewFixtures.populatedState
    PssstTheme {
        PhoneScreenShell(selectedTab = DashboardTab.PROGRESSION) {
            Column(modifier = Modifier.fillMaxSize()) {
                OsrsSegmentedTabs(
                    tabs = listOf("Skills (Total: ${state.totalLevel})" to 0, "Quests (5/8)" to 0),
                    selectedIndex = 1,
                    onTabSelected = {},
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
                QuestsSubScreen(
                    state = state,
                    onQuestSearchQueryChanged = {},
                    onQuestFilterChanged = {}
                )
            }
        }
    }
}

@Preview(name = "Screen 09 — Timers: Dailies", showBackground = true, backgroundColor = 0xFF191612, widthDp = 380, heightDp = 760)
@Composable
fun PreviewScreenTimersDailies() {
    PssstTheme {
        PhoneScreenShell(selectedTab = DashboardTab.TIMERS, showEditIcon = true) {
            TimersScreen(
                state = PreviewFixtures.populatedState,
                selectedTab = TimersSubTab.DAILIES,
                onSelectedTabChange = {},
                editing = false
            )
        }
    }
}

@Preview(name = "Screen 10 — Timers: Birdhouses", showBackground = true, backgroundColor = 0xFF191612, widthDp = 380, heightDp = 760)
@Composable
fun PreviewScreenTimersBirdhouses() {
    PssstTheme {
        PhoneScreenShell(selectedTab = DashboardTab.TIMERS, showEditIcon = true) {
            TimersScreen(
                state = PreviewFixtures.populatedState,
                selectedTab = TimersSubTab.BIRDHOUSES,
                onSelectedTabChange = {},
                editing = false
            )
        }
    }
}

@Preview(name = "Screen 11 — Timers: Farming Patches", showBackground = true, backgroundColor = 0xFF191612, widthDp = 380, heightDp = 760)
@Composable
fun PreviewScreenTimersFarming() {
    PssstTheme {
        PhoneScreenShell(selectedTab = DashboardTab.TIMERS, showEditIcon = true) {
            TimersScreen(
                state = PreviewFixtures.populatedState,
                selectedTab = TimersSubTab.FARMING,
                onSelectedTabChange = {},
                editing = false
            )
        }
    }
}

@Preview(name = "Screen 12 — Timers: Payments", showBackground = true, backgroundColor = 0xFF191612, widthDp = 380, heightDp = 760)
@Composable
fun PreviewScreenTimersPayments() {
    PssstTheme {
        PhoneScreenShell(selectedTab = DashboardTab.TIMERS, showEditIcon = false) {
            TimersScreen(
                state = PreviewFixtures.populatedState,
                selectedTab = TimersSubTab.PAYMENTS,
                onSelectedTabChange = {},
                editing = false
            )
        }
    }
}

@Preview(name = "Screen 13 — Navigation Drawer", showBackground = true, backgroundColor = 0xFF191612, widthDp = 380, heightDp = 760)
@Composable
fun PreviewScreenDrawer() {
    PssstTheme {
        DrawerSheetPreviewContent(state = PreviewFixtures.populatedState)
    }
}

@Preview(name = "Screen 14 — Empty States Gallery", showBackground = true, backgroundColor = 0xFF191612, widthDp = 380, heightDp = 760)
@Composable
fun PreviewScreenEmptyStates() {
    PssstTheme {
        PhoneScreenShell(selectedTab = DashboardTab.TIMERS, state = PreviewFixtures.emptyState) {
            TimersScreen(
                state = PreviewFixtures.emptyState,
                selectedTab = TimersSubTab.FARMING,
                onSelectedTabChange = {},
                editing = false
            )
        }
    }
}

@Preview(name = "Screen 15 — Goal Picker Dialog", showBackground = true, backgroundColor = 0xFF191612, widthDp = 380, heightDp = 760)
@Composable
fun PreviewScreenGoalDialog() {
    val state = PreviewFixtures.populatedState
    PssstTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .stoneGround()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            com.pssst.ui.main.GoalSettingsDialogContent(
                state = state,
                goalModeTab = 0,
                itemSearchText = "bow"
            )
        }
    }
}
