package com.pssst.theme.art

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pssst.theme.OsrsBorderDark
import com.pssst.theme.OsrsBorderLight
import com.pssst.theme.OsrsBrassMid
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsGreen
import com.pssst.theme.OsrsHerbGreen
import com.pssst.theme.OsrsLedgerRowAlt
import com.pssst.theme.OsrsOakBase
import com.pssst.theme.OsrsOuterFrame
import com.pssst.theme.OsrsRed
import com.pssst.theme.OsrsSlotEmpty
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.art.Ornaments.drawDomedRivet

/**
 * Semantic state for Timers table status cells.
 */
enum class TableStatusKind {
    READY,
    GROWING,
    EMPTY,
    WARNING
}

/**
 * Engraved dark-oak and brass header plaque for Timers tables.
 */
@Composable
fun OsrsTableHeaderPlaque(
    leftLabel: String,
    rightLabel: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .drawWithCache {
                val oak = Textures.oakBrush(seed = 212)
                val strokePx = 1.2.dp.toPx()
                val r = 1.7.dp.toPx()
                onDrawBehind {
                    val w = size.width
                    val h = size.height
                    drawRect(color = OsrsOakBase)
                    drawRect(brush = oak, alpha = 0.88f)
                    // Top & bottom brass/iron trim
                    drawLine(OsrsBrassMid.copy(alpha = 0.7f), Offset(0f, 0f), Offset(w, 0f), strokePx)
                    drawLine(OsrsOuterFrame, Offset(0f, h - strokePx), Offset(w, h - strokePx), strokePx * 1.4f)
                    drawLine(OsrsBrassMid.copy(alpha = 0.55f), Offset(0f, h - strokePx * 2.2f), Offset(w, h - strokePx * 2.2f), strokePx)
                    drawDomedRivet(Offset(6.dp.toPx(), h * 0.5f), r)
                    drawDomedRivet(Offset(w - 6.dp.toPx(), h * 0.5f), r)
                }
            }
            .padding(horizontal = 14.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        GoldLeafText(
            text = leftLabel,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
        GoldLeafText(
            text = rightLabel,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Alternating ledger table row: odd rows receive a subtle warm parchment wash ([OsrsLedgerRowAlt])
 * while preserving > 5.1:1 contrast with [OsrsTextMuted] and > 9.2:1 with primary text.
 */
@Composable
fun OsrsTableRow(
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val isAlternate = index % 2 == 1
    Row(
        modifier = modifier
            .fillMaxWidth()
            .drawWithCache {
                onDrawBehind {
                    if (isAlternate) {
                        drawRect(color = OsrsLedgerRowAlt.copy(alpha = 0.72f))
                    }
                }
            }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

/**
 * Engraved dual-line row separator for Timers tables with subtle side diamond ticks.
 */
@Composable
fun OsrsTableRowSeparator(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp)
            .padding(horizontal = 8.dp)
    ) {
        val w = size.width
        drawLine(
            color = OsrsBorderDark,
            start = Offset(0f, 0f),
            end = Offset(w, 0f),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = OsrsBorderLight.copy(alpha = 0.55f),
            start = Offset(0f, 1.dp.toPx()),
            end = Offset(w, 1.dp.toPx()),
            strokeWidth = 1.dp.toPx()
        )
    }
}

/**
 * Timers table status cell that reads at a glance and always carries both a state icon
 * and an explicit status word (e.g., "Ready", "Growing (42m)", "Empty").
 */
@Composable
fun OsrsTableStatusCell(
    text: String,
    status: TableStatusKind,
    modifier: Modifier = Modifier,
    colorOverride: Color? = null
) {
    val defaultColor = when (status) {
        TableStatusKind.READY -> OsrsGreen
        TableStatusKind.GROWING -> OsrsGold
        TableStatusKind.EMPTY -> OsrsTextMuted
        TableStatusKind.WARNING -> OsrsRed
    }
    val tint = colorOverride ?: defaultColor
    val icon = when (status) {
        TableStatusKind.READY -> ArtIcons.BadgeReady
        TableStatusKind.GROWING -> ArtIcons.BadgeGrowing
        TableStatusKind.EMPTY -> ArtIcons.BadgeEmpty
        TableStatusKind.WARNING -> ArtIcons.BadgeWarning
    }

    Row(
        modifier = modifier
            .drawWithCache {
                val cut = 3.5.dp.toPx()
                val strokePx = 1.dp.toPx()
                val borderTint = when (status) {
                    TableStatusKind.READY -> OsrsHerbGreen.copy(alpha = 0.75f)
                    TableStatusKind.GROWING -> OsrsBrassMid.copy(alpha = 0.65f)
                    TableStatusKind.EMPTY -> OsrsBorderLight.copy(alpha = 0.45f)
                    TableStatusKind.WARNING -> OsrsRed.copy(alpha = 0.75f)
                }
                val bgTint = when (status) {
                    TableStatusKind.READY -> OsrsHerbGreen.copy(alpha = 0.12f)
                    TableStatusKind.GROWING -> OsrsGold.copy(alpha = 0.10f)
                    TableStatusKind.EMPTY -> OsrsSlotEmpty.copy(alpha = 0.65f)
                    TableStatusKind.WARNING -> OsrsRed.copy(alpha = 0.12f)
                }
                onDrawBehind {
                    val w = size.width
                    val h = size.height
                    val tagPath = Path().apply {
                        moveTo(cut, 0f)
                        lineTo(w - cut, 0f)
                        lineTo(w, cut)
                        lineTo(w, h - cut)
                        lineTo(w - cut, h)
                        lineTo(cut, h)
                        lineTo(0f, h - cut)
                        lineTo(0f, cut)
                        close()
                    }
                    drawPath(tagPath, color = bgTint)
                    drawPath(
                        tagPath,
                        color = borderTint,
                        style = Stroke(width = strokePx, join = StrokeJoin.Bevel)
                    )
                }
            }
            .padding(horizontal = 7.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PixelIcon(
            sprite = icon,
            tones = when (status) {
                TableStatusKind.READY -> SpriteTones.HERB
                TableStatusKind.GROWING -> SpriteTones.STONE
                TableStatusKind.EMPTY -> SpriteTones.STONE
                TableStatusKind.WARNING -> SpriteTones.WARNING
            },
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = tint,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
