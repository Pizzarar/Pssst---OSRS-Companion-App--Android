package com.pssst.theme.art

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsRed
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.OsrsTextPrimary
import com.pssst.theme.PssstTheme

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
                GoldLeafText("oakBeamFrame — Top Bar Timber & Straps")
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

@Preview(name = "3. Plaques, Bookmarks & Wax Seals", showBackground = true, backgroundColor = 0xFF191612, widthDp = 380)
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
                NumberPlaque(label = "3")
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

@Preview(name = "6. Empty State Vignettes", showBackground = true, backgroundColor = 0xFF191612, widthDp = 380)
@Composable
fun PreviewEmptyStateVignettes() {
    PssstTheme {
        Column(
            modifier = Modifier
                .stoneGround()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                EmptyStateVignette(EmptyStateKind.CLOSED_CHEST)
                EmptyStateVignette(EmptyStateKind.FALLOW_PATCH)
                EmptyStateVignette(EmptyStateKind.EMPTY_BIRDHOUSE)
                EmptyStateVignette(EmptyStateKind.POTION_RACK)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                EmptyStateVignette(EmptyStateKind.QUEST_SCROLL)
                EmptyStateVignette(EmptyStateKind.SUNDIAL)
                EmptyStateVignette(EmptyStateKind.LEDGER_BOOKMARK)
            }
        }
    }
}
