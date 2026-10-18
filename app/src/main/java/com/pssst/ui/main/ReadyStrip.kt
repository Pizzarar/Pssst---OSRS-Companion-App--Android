package com.pssst.ui.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pssst.theme.OsrsHerbGreen
import com.pssst.theme.OsrsSlotEmpty
import com.pssst.theme.OsrsTextMuted
import com.pssst.util.IconWishlist

/** What is waiting for the player right now. One place for the counts the top bar, Overview and Timers all show. */
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

/** A slim row under the title with small item icons and counts; tapping one jumps to where it lives. Empty when nothing is ready. */
@Composable
fun ReadyStrip(
    state: MainDashboardUiState,
    nowMs: Long,
    onOpen: (DashboardTab, TimersSubTab?) -> Unit,
    modifier: Modifier = Modifier
) {
    val ready = state.readyCounts(nowMs)
    val chips = listOf(
        Triple(IconWishlist.RANARR_WEED, ready.farming, "Patches") to { onOpen(DashboardTab.TIMERS, TimersSubTab.FARMING) },
        Triple(IconWishlist.BIRD_HOUSE, ready.birdhouses, "Birdhouses") to { onOpen(DashboardTab.TIMERS, TimersSubTab.BIRDHOUSES) },
        Triple(IconWishlist.COINS, ready.geDone, "Exchange") to { onOpen(DashboardTab.GE, null) },
        Triple(IconWishlist.SLAYER_HELMET, ready.slayerLeft, "Slayer") to { onOpen(DashboardTab.OVERVIEW, null) }
    ).filter { it.first.second > 0 }
    if (chips.isEmpty()) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        chips.forEach { (spec, open) ->
            val (itemId, count, name) = spec
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(OsrsSlotEmpty)
                    .clickable { open() }
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ItemIcon(itemId, state.wishlistIcons[itemId], name, size = 20.dp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = OsrsHerbGreen
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

/** Two arrows chasing each other: refresh. Turned by [rotation] degrees so the caller can spin it. */
@Composable
fun RefreshVectorIcon(tint: Color, rotation: Float = 0f, modifier: Modifier = Modifier.size(22.dp)) {
    Canvas(modifier = modifier.rotate(rotation)) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = w * 0.1f, cap = StrokeCap.Round)
        val inset = w * 0.18f
        val arcSize = Size(w - 2 * inset, h - 2 * inset)
        drawArc(tint, startAngle = 20f, sweepAngle = 140f, useCenter = false, topLeft = Offset(inset, inset), size = arcSize, style = stroke)
        drawArc(tint, startAngle = 200f, sweepAngle = 140f, useCenter = false, topLeft = Offset(inset, inset), size = arcSize, style = stroke)
        val head = Path().apply {
            moveTo(w * 0.72f, h * 0.10f); lineTo(w * 0.86f, h * 0.30f); lineTo(w * 0.62f, h * 0.30f); close()
            moveTo(w * 0.28f, h * 0.90f); lineTo(w * 0.14f, h * 0.70f); lineTo(w * 0.38f, h * 0.70f); close()
        }
        drawPath(head, tint)
    }
}
