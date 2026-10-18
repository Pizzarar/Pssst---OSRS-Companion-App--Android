package com.pssst.theme.art

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pssst.theme.OsrsBorderDark
import com.pssst.theme.OsrsBorderLight
import com.pssst.theme.OsrsBrassMid
import com.pssst.theme.OsrsHerbGreen
import com.pssst.theme.OsrsOuterFrame
import com.pssst.theme.OsrsPanelMid
import com.pssst.theme.OsrsRed
import com.pssst.theme.OsrsSlotEmpty
import com.pssst.theme.OsrsTextMuted

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
 * Engraved dark-oak and brass pixel header plaque for Timers tables.
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
            .pixelArt(key = "table_header_plaque_212") {
                Textures.drawOakBeam(this, seed = 212)
                if (w > 2 && h > 3) {
                    hLine(0, 0, w, OsrsBrassMid.toArgb())
                    hLine(0, h - 2, w, OsrsBrassMid.toArgb())
                    hLine(0, h - 1, w, OsrsOuterFrame.toArgb())
                    val ry = (h / 2 - 1).coerceAtLeast(1)
                    rivet(2, ry, size = 3)
                    if (w >= 8) {
                        rivet(w - 5, ry, size = 3)
                    }
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
 * Alternating ledger table row: odd rows render as a recessed, darker Bayer-dithered stone band
 * ([OsrsSlotEmpty] `#221D16` + [OsrsPanelMid] `#27211A` with 1-art-px side/top shadow), keeping
 * >= 4.67:1 contrast with [OsrsRed], >= 5.49:1 with [OsrsHerbGreen], and >= 12.0:1 with [com.pssst.theme.OsrsTextPrimary].
 */
@Composable
fun OsrsTableRow(
    index: Int,
    modifier: Modifier = Modifier,
    bottomContent: (@Composable () -> Unit)? = null,
    content: @Composable RowScope.() -> Unit
) {
    val isAlternate = index % 2 == 1
    val bandModifier = if (isAlternate) {
        Modifier.pixelArt(key = "table_row_recessed_band") {
            val darkTone = OsrsSlotEmpty.toArgb()
            val midTone = OsrsPanelMid.toArgb()
            for (py in 0 until h) {
                for (px in 0 until w) {
                    val tone = if (PixelNoise.bayer4(px, py) < 11) darkTone else midTone
                    set(px, py, tone)
                }
            }
            if (w > 2 && h > 1) {
                val edgeShadow = OsrsBorderDark.toArgb()
                hLine(0, 0, w, edgeShadow)
                vLine(0, 0, h, edgeShadow)
                vLine(w - 1, 0, h, edgeShadow)
            }
        }
    } else {
        Modifier
    }

    androidx.compose.foundation.layout.Column(
        modifier = modifier
            .fillMaxWidth()
            .then(bandModifier)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
        bottomContent?.invoke()
    }
}

/**
 * Engraved two-pixel-row stone groove separator for Timers tables, spanning the full inner width
 * inside [ledgerPanelInset].
 */
@Composable
fun OsrsTableRowSeparator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp)
            .pixelArt(key = "table_row_sep") {
                if (w > 0 && h > 0) {
                    val midY = (h / 2 - 1).coerceAtLeast(0)
                    hLine(0, midY, w, OsrsBorderDark.toArgb())
                    if (midY + 1 < h) {
                        hLine(0, midY + 1, w, OsrsBorderLight.toArgb())
                    }
                }
            }
    )
}

/**
 * Timers table status cell inside a square recessed stone well, pairing a 16x16 pixel
 * badge sprite with an explicit status word.
 *
 * Never uses stack-count neon green (`#00FF80`) or yellow (`#FFFF00`):
 * - [TableStatusKind.READY] -> [OsrsHerbGreen] (`#4CAF50`) + [ArtIcons.BadgeReady]
 * - [TableStatusKind.GROWING] -> [OsrsTextMuted] (`#B8A88A`) + [ArtIcons.BadgeGrowing]
 * - [TableStatusKind.EMPTY] -> [OsrsTextMuted] (`#B8A88A`) + [ArtIcons.BadgeEmpty]
 * - [TableStatusKind.WARNING] -> [OsrsRed] (`#FF4D4D`) + [ArtIcons.BadgeWarning]
 */
@Composable
fun OsrsTableStatusCell(
    text: String,
    status: TableStatusKind,
    modifier: Modifier = Modifier,
    colorOverride: Color? = null
) {
    val defaultColor = when (status) {
        TableStatusKind.READY -> OsrsHerbGreen
        TableStatusKind.GROWING -> OsrsTextMuted
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
            .pixelBevelBox(
                fill = OsrsSlotEmpty,
                raised = false
            )
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

