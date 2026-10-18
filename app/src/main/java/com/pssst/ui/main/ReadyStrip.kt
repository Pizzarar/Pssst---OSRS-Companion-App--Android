package com.pssst.ui.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pssst.theme.JetBrainsMonoFontFamily
import com.pssst.theme.OsrsBorderOutline
import com.pssst.theme.OsrsHardShadow
import com.pssst.theme.OsrsHerbGreen
import com.pssst.theme.OsrsSlotEmpty
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.Typography

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
    val itemId: Int,
    val count: Int,
    val fallbackLabel: String,
    val tab: DashboardTab,
    val subTab: TimersSubTab?
)

/**
 * 4-slot quick-status strip for the right side of the top bar (Patches, Birdhouses, Dailies, GE).
 * Always shows all 4 slots so the header bar stays balanced; ready slots (> 0) light up in green.
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
        ReadyChipSpec(257, counts.farming, "Patches", DashboardTab.TIMERS, TimersSubTab.FARMING),
        ReadyChipSpec(21512, counts.birdhouses, "Birdhouses", DashboardTab.TIMERS, TimersSubTab.BIRDHOUSES),
        ReadyChipSpec(11738, counts.dailies, "Dailies", DashboardTab.TIMERS, TimersSubTab.DAILIES),
        ReadyChipSpec(995, counts.geDone, "GE", DashboardTab.GE, null)
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        chips.forEach { chip ->
            val isReady = chip.count > 0
            val iconBase64 = state.topBarIcons[chip.itemId]
            val bitmap = remember(chip.itemId, iconBase64) {
                ItemIconCache.getOrCreate(chip.itemId, iconBase64)
            }
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isReady) Color(0xFF1B2B18) else OsrsSlotEmpty)
                    .border(
                        width = 1.dp,
                        color = if (isReady) OsrsHerbGreen.copy(alpha = 0.75f) else OsrsBorderOutline,
                        shape = RoundedCornerShape(2.dp)
                    )
                    .clickable { onNavigate(chip.tab, chip.subTab) }
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = chip.fallbackLabel,
                        filterQuality = FilterQuality.None,
                        contentScale = ContentScale.Fit,
                        alpha = if (isReady) 1f else 0.65f,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = "${chip.count}",
                    color = if (isReady) OsrsHerbGreen else OsrsTextMuted.copy(alpha = 0.65f),
                    fontSize = 11.sp,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = if (isReady) FontWeight.Bold else FontWeight.Medium,
                    style = Typography.labelSmall.copy(shadow = OsrsHardShadow)
                )
            }
        }
    }
}

/** Pencil: edit the lists on this screen. */
@Composable
fun PencilVectorIcon(tint: Color, modifier: Modifier = Modifier.size(22.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val body = Path().apply {
            moveTo(w * 0.18f, h * 0.82f)
            lineTo(w * 0.26f, h * 0.58f)
            lineTo(w * 0.66f, h * 0.18f)
            lineTo(w * 0.82f, h * 0.34f)
            lineTo(w * 0.42f, h * 0.74f)
            close()
        }
        drawPath(body, tint, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round))
        drawLine(tint, Offset(w * 0.56f, h * 0.28f), Offset(w * 0.72f, h * 0.44f), strokeWidth = w * 0.09f)
    }
}

/** Check mark: done editing. */
@Composable
fun CheckVectorIcon(tint: Color, modifier: Modifier = Modifier.size(22.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.18f, h * 0.55f)
            lineTo(w * 0.40f, h * 0.76f)
            lineTo(w * 0.82f, h * 0.28f)
        }
        drawPath(path, tint, style = Stroke(width = w * 0.12f, cap = StrokeCap.Round))
    }
}
