package com.pssst.theme.art

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pssst.theme.OsrsBorderDark
import com.pssst.theme.OsrsBorderLight
import com.pssst.theme.OsrsBrassMid
import com.pssst.theme.OsrsGoldDark
import com.pssst.theme.OsrsGoldHighlight
import com.pssst.theme.OsrsIronBody
import com.pssst.theme.OsrsIronEdgeDark
import com.pssst.theme.OsrsIronEdgeLight
import com.pssst.theme.OsrsOuterFrame
import com.pssst.theme.OsrsRivetDark
import com.pssst.theme.OsrsRivetLight

/**
 * Engraved rules, end caps, flourishes, corner ornaments, bullet glyphs, and watermarks
 * in one consistent 45-degree chiseled family.
 */
object Ornaments {

    /**
     * Draws a forged dome rivet with top-left specular crescent and bottom-right shadow.
     */
    fun DrawScope.drawDomedRivet(
        center: Offset,
        radius: Float,
        bodyColor: Color = OsrsIronBody,
        highlightColor: Color = OsrsRivetLight,
        shadowColor: Color = OsrsRivetDark
    ) {
        // Outer shadow rim
        drawCircle(
            color = OsrsOuterFrame,
            radius = radius + 0.8f,
            center = center
        )
        // Shadow base
        drawCircle(
            color = shadowColor,
            radius = radius,
            center = center
        )
        // Forged body
        drawCircle(
            color = bodyColor,
            radius = radius * 0.85f,
            center = Offset(center.x - radius * 0.1f, center.y - radius * 0.1f)
        )
        // Top-left glint
        drawCircle(
            color = highlightColor,
            radius = radius * 0.42f,
            center = Offset(center.x - radius * 0.28f, center.y - radius * 0.28f)
        )
    }

    /**
     * Draws a 45-degree faceted diamond stud or bullet at [center].
     */
    fun DrawScope.drawFacetedDiamond(
        center: Offset,
        halfSize: Float,
        fill: Color = OsrsBrassMid,
        highlight: Color = OsrsGoldHighlight,
        shadow: Color = OsrsBorderDark
    ) {
        val outer = Path().apply {
            moveTo(center.x, center.y - halfSize)
            lineTo(center.x + halfSize, center.y)
            lineTo(center.x, center.y + halfSize)
            lineTo(center.x - halfSize, center.y)
            close()
        }
        drawPath(outer, color = fill)
        // Top-left highlight edges
        drawLine(
            color = highlight,
            start = Offset(center.x - halfSize, center.y),
            end = Offset(center.x, center.y - halfSize),
            strokeWidth = 1.2f
        )
        drawLine(
            color = highlight,
            start = Offset(center.x, center.y - halfSize),
            end = Offset(center.x + halfSize, center.y),
            strokeWidth = 1.2f
        )
        // Bottom-right shadow edges
        drawLine(
            color = shadow,
            start = Offset(center.x + halfSize, center.y),
            end = Offset(center.x, center.y + halfSize),
            strokeWidth = 1.2f
        )
        drawLine(
            color = shadow,
            start = Offset(center.x, center.y + halfSize),
            end = Offset(center.x - halfSize, center.y),
            strokeWidth = 1.2f
        )
    }

    /**
     * Draws an engraved horizontal rule with chiseled 45-degree diamond end-caps
     * and an optional center diamond-and-chevron flourish.
     */
    fun DrawScope.drawEngravedRule(
        y: Float = size.height * 0.5f,
        startX: Float = 0f,
        endX: Float = size.width,
        withEndCaps: Boolean = true,
        withCenterFlourish: Boolean = false,
        accentColor: Color = OsrsBrassMid
    ) {
        val capSize = 3.dp.toPx()
        val lineStart = if (withEndCaps) startX + capSize * 2.2f else startX
        val lineEnd = if (withEndCaps) endX - capSize * 2.2f else endX
        if (lineEnd <= lineStart) return

        val midX = (startX + endX) * 0.5f
        val flourishGap = if (withCenterFlourish) 14.dp.toPx() else 0f

        if (withCenterFlourish && (lineEnd - lineStart) > flourishGap * 3f) {
            // Left groove segment
            drawLine(OsrsBorderDark, Offset(lineStart, y - 0.7f), Offset(midX - flourishGap, y - 0.7f), 1.2.dp.toPx())
            drawLine(OsrsBorderLight, Offset(lineStart, y + 0.8f), Offset(midX - flourishGap, y + 0.8f), 1.dp.toPx())
            // Right groove segment
            drawLine(OsrsBorderDark, Offset(midX + flourishGap, y - 0.7f), Offset(lineEnd, y - 0.7f), 1.2.dp.toPx())
            drawLine(OsrsBorderLight, Offset(midX + flourishGap, y + 0.8f), Offset(lineEnd, y + 0.8f), 1.dp.toPx())

            // Left & right chevrons flanking center diamond
            val chev = 4.dp.toPx()
            val leftChev = Path().apply {
                moveTo(midX - flourishGap + chev, y - chev * 0.75f)
                lineTo(midX - flourishGap, y)
                lineTo(midX - flourishGap + chev, y + chev * 0.75f)
            }
            val rightChev = Path().apply {
                moveTo(midX + flourishGap - chev, y - chev * 0.75f)
                lineTo(midX + flourishGap, y)
                lineTo(midX + flourishGap - chev, y + chev * 0.75f)
            }
            drawPath(leftChev, accentColor, style = Stroke(width = 1.3.dp.toPx()))
            drawPath(rightChev, accentColor, style = Stroke(width = 1.3.dp.toPx()))
            drawFacetedDiamond(Offset(midX, y), halfSize = 4.dp.toPx(), fill = accentColor)
        } else {
            drawLine(OsrsBorderDark, Offset(lineStart, y - 0.7f), Offset(lineEnd, y - 0.7f), 1.2.dp.toPx())
            drawLine(OsrsBorderLight, Offset(lineStart, y + 0.8f), Offset(lineEnd, y + 0.8f), 1.dp.toPx())
        }

        if (withEndCaps) {
            drawFacetedDiamond(
                center = Offset(startX + capSize, y),
                halfSize = capSize,
                fill = accentColor.copy(alpha = 0.85f)
            )
            drawFacetedDiamond(
                center = Offset(endX - capSize, y),
                halfSize = capSize,
                fill = accentColor.copy(alpha = 0.85f)
            )
        }
    }

    /**
     * Draws subtle chiseled gold-leaf corner flourishes inside a plaque or level-99 cell.
     */
    fun DrawScope.drawCornerFlourishes(
        inset: Float = 3.dp.toPx(),
        armLength: Float = 8.dp.toPx(),
        color: Color = OsrsBrassMid
    ) {
        val stroke = 1.25.dp.toPx()
        val w = size.width
        val h = size.height
        // Top-left
        drawLine(color, Offset(inset, inset), Offset(inset + armLength, inset), stroke)
        drawLine(color, Offset(inset, inset), Offset(inset, inset + armLength), stroke)
        // Top-right
        drawLine(color, Offset(w - inset, inset), Offset(w - inset - armLength, inset), stroke)
        drawLine(color, Offset(w - inset, inset), Offset(w - inset, inset + armLength), stroke)
        // Bottom-left
        drawLine(color, Offset(inset, h - inset), Offset(inset + armLength, h - inset), stroke)
        drawLine(color, Offset(inset, h - inset), Offset(inset, h - inset - armLength), stroke)
        // Bottom-right
        drawLine(color, Offset(w - inset, h - inset), Offset(w - inset - armLength, h - inset), stroke)
        drawLine(color, Offset(w - inset, h - inset), Offset(w - inset, h - inset - armLength), stroke)
    }

    /**
     * Draws a low-contrast engraved merchant balance scale watermark inside empty GE offer slots.
     */
    fun DrawScope.drawWatermarkScale(
        center: Offset = Offset(size.width * 0.5f, size.height * 0.5f),
        radius: Float = minOf(size.width, size.height) * 0.28f,
        color: Color = OsrsBorderLight.copy(alpha = 0.16f)
    ) {
        val stroke = Stroke(width = 1.3.dp.toPx(), cap = StrokeCap.Square)
        // Central pillar
        drawLine(
            color = color,
            start = Offset(center.x, center.y - radius * 0.85f),
            end = Offset(center.x, center.y + radius * 0.85f),
            strokeWidth = stroke.width
        )
        // Base plinth
        drawLine(
            color = color,
            start = Offset(center.x - radius * 0.55f, center.y + radius * 0.85f),
            end = Offset(center.x + radius * 0.55f, center.y + radius * 0.85f),
            strokeWidth = stroke.width * 1.3f
        )
        // Balance beam
        val beamY = center.y - radius * 0.55f
        val beamHalf = radius * 0.85f
        drawLine(
            color = color,
            start = Offset(center.x - beamHalf, beamY),
            end = Offset(center.x + beamHalf, beamY),
            strokeWidth = stroke.width
        )
        // Left & right pans
        val panY = center.y + radius * 0.15f
        val panHalf = radius * 0.32f
        for (dir in intArrayOf(-1, 1)) {
            val px = center.x + dir * beamHalf
            drawLine(color, Offset(px, beamY), Offset(px - panHalf, panY), stroke.width * 0.8f)
            drawLine(color, Offset(px, beamY), Offset(px + panHalf, panY), stroke.width * 0.8f)
            drawLine(color, Offset(px - panHalf, panY), Offset(px + panHalf, panY), stroke.width * 1.2f)
        }
        // Fulcrum diamond
        drawFacetedDiamond(
            center = Offset(center.x, beamY),
            halfSize = 2.5.dp.toPx(),
            fill = color,
            highlight = color,
            shadow = OsrsBorderDark.copy(alpha = 0.2f)
        )
    }
}

/**
 * Engraved rule divider composable with diamond end-caps and optional center flourish.
 */
@Composable
fun EngravedRule(
    modifier: Modifier = Modifier,
    withEndCaps: Boolean = true,
    withCenterFlourish: Boolean = false,
    accentColor: Color = OsrsBrassMid
) {
    val heightDp = if (withCenterFlourish) 10.dp else 6.dp
    Canvas(modifier = modifier.fillMaxWidth().height(heightDp)) {
        with(Ornaments) {
            drawEngravedRule(
                y = size.height * 0.5f,
                startX = 0f,
                endX = size.width,
                withEndCaps = withEndCaps,
                withCenterFlourish = withCenterFlourish,
                accentColor = accentColor
            )
        }
    }
}

/**
 * Faceted 45-degree diamond bullet glyph replacing plain text bullet characters.
 */
@Composable
fun BulletGlyph(
    modifier: Modifier = Modifier,
    size: Dp = 7.dp,
    color: Color = OsrsGoldDark
) {
    Canvas(modifier = modifier.size(size)) {
        with(Ornaments) {
            drawFacetedDiamond(
                center = Offset(this@Canvas.size.width * 0.5f, this@Canvas.size.height * 0.5f),
                halfSize = this@Canvas.size.minDimension * 0.42f,
                fill = color,
                highlight = OsrsGoldHighlight,
                shadow = OsrsBorderDark
            )
        }
    }
}

/**
 * Forged wrought-iron 3-bar grip handle with side rivets for reordering rows in EditableList.
 */
@Composable
fun IronGripHandle(
    modifier: Modifier = Modifier,
    width: Dp = 20.dp,
    height: Dp = 16.dp
) {
    Canvas(modifier = modifier.size(width, height)) {
        val barCount = 3
        val padX = 2.dp.toPx()
        val barWidth = size.width - padX * 2
        val barHeight = 2.dp.toPx()
        for (i in 0 until barCount) {
            val y = size.height * (0.22f + i * 0.28f)
            // Recessed groove
            drawRect(
                color = OsrsIronEdgeDark,
                topLeft = Offset(padX, y),
                size = Size(barWidth, barHeight)
            )
            // Iron ridge glint
            drawLine(
                color = OsrsIronEdgeLight,
                start = Offset(padX, y),
                end = Offset(padX + barWidth, y),
                strokeWidth = 1.dp.toPx()
            )
        }
        with(Ornaments) {
            drawDomedRivet(Offset(2.5.dp.toPx(), size.height * 0.5f), radius = 1.6.dp.toPx())
            drawDomedRivet(Offset(size.width - 2.5.dp.toPx(), size.height * 0.5f), radius = 1.6.dp.toPx())
        }
    }
}
