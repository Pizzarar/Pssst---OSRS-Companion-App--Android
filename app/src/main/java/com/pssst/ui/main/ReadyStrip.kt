package com.pssst.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pssst.theme.JetBrainsMonoFontFamily
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsHardShadow
import com.pssst.theme.OsrsHerbGreen
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.Typography
import com.pssst.theme.art.ArtIcons
import com.pssst.theme.art.ledgerWellSlot

/** What is waiting for the player right now. One place for the counts Overview and Timers show. */
data class ReadyCounts(
    val farming: Int,
    val birdhouses: Int,
    val dailies: Int,
    val geDone: Int,
    /** Kills left on the current Slayer task, 0 with no task. */
    val slayerLeft: Int
)

fun MainDashboardUiState.readyCounts(nowMs: Long) = ReadyCounts(
    farming = farmingPatches.count { it.isGrown(nowMs) },
    birdhouses = birdHouses.count { it.isReady(nowMs) },
    dailies = dailyTasks.count { it.isAvailable },
    geDone = geOffers.count { it.isComplete },
    slayerLeft = if (slayer.amount > 0) slayer.amount else 0
)

private data class ReadyChipSpec(
    val icon: ImageVector,
    val count: Int,
    val fallbackLabel: String,
    val tab: DashboardTab,
    val subTab: TimersSubTab?
)

/**
 * 4-slot quick-status strip for the right side of the top bar (Patches, Birdhouses, Dailies, GE).
 * Always shows all 4 recessed ledger wells so the header bar stays balanced; ready slots (> 0) light up.
 */
@Composable
fun TopBarReadyRow(
    state: MainDashboardUiState,
    nowMs: Long,
    onNavigate: (DashboardTab, TimersSubTab?) -> Unit,
    modifier: Modifier = Modifier
) {
    val counts = remember(state, nowMs) { state.readyCounts(nowMs) }
    val chips = listOf(
        ReadyChipSpec(ArtIcons.HerbSprig, counts.farming, "Patches", DashboardTab.TIMERS, TimersSubTab.FARMING),
        ReadyChipSpec(ArtIcons.Birdhouse, counts.birdhouses, "Birdhouses", DashboardTab.TIMERS, TimersSubTab.BIRDHOUSES),
        ReadyChipSpec(ArtIcons.TimersHourglass, counts.dailies, "Dailies", DashboardTab.TIMERS, TimersSubTab.DAILIES),
        ReadyChipSpec(ArtIcons.ExchangeCoins, counts.geDone, "GE", DashboardTab.GE, null)
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        chips.forEach { chip ->
            val isReady = chip.count > 0
            val iconTint = if (isReady) OsrsGold else OsrsTextMuted.copy(alpha = 0.65f)
            Row(
                modifier = Modifier
                    .width(48.dp)
                    .height(40.dp)
                    .ledgerWellSlot(
                        empty = false,
                        highlighted = isReady
                    )
                    .clickable { onNavigate(chip.tab, chip.subTab) }
                    .padding(horizontal = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = chip.icon,
                    contentDescription = chip.fallbackLabel,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "${chip.count}",
                    color = if (isReady) OsrsHerbGreen else OsrsTextMuted,
                    fontSize = 13.sp,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    style = Typography.labelSmall.copy(shadow = OsrsHardShadow)
                )
            }
        }
    }
}

/** Scribe's quill: edit the lists on this screen. */
@Composable
fun PencilVectorIcon(tint: Color, modifier: Modifier = Modifier.size(22.dp)) {
    Icon(
        imageVector = ArtIcons.EditQuill,
        contentDescription = null,
        tint = tint,
        modifier = modifier
    )
}

/** Chiseled seal check mark: done editing. */
@Composable
fun CheckVectorIcon(tint: Color, modifier: Modifier = Modifier.size(22.dp)) {
    Icon(
        imageVector = ArtIcons.BadgeReady,
        contentDescription = null,
        tint = tint,
        modifier = modifier
    )
}

