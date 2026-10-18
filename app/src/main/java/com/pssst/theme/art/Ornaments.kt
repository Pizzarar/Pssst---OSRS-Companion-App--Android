package com.pssst.theme.art

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pssst.theme.OsrsBorderDark
import com.pssst.theme.OsrsBorderLight
import com.pssst.theme.OsrsBorderOutline
import com.pssst.theme.OsrsBrassMid
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsGoldDark
import com.pssst.theme.OsrsGoldHighlight
import com.pssst.theme.OsrsIronBody
import com.pssst.theme.OsrsIronEdgeDark
import com.pssst.theme.OsrsIronEdgeLight
import com.pssst.theme.OsrsOuterFrame
import com.pssst.theme.OsrsRivetDark
import com.pssst.theme.OsrsRivetLight
import com.pssst.theme.OsrsSlotRecessed
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Pixel-honest rules, diamond studs, grip handles, corner marks, and empty-slot watermarks.
 *
 * Every ornament is drawn on an integer art-pixel grid without antialiasing or smooth curves.
 */
object Ornaments {

    /**
     * Low-contrast recessed stone tones for the 16x16 empty-GE-slot balance scale sprite.
     */
    val WATERMARK_TONES = SpriteTones(
        outline = OsrsBorderDark.toArgb(),
        highlight = OsrsBorderLight.toArgb(),
        base = OsrsBorderOutline.toArgb(),
        shadow = OsrsSlotRecessed.toArgb(),
        accent = OsrsBorderLight.toArgb(),
        accentDark = OsrsBorderDark.toArgb()
    )

    /**
     * 16x16 pixel sprite of a merchant's balance scale for empty Grand Exchange slots.
     */
    val WatermarkScaleSprite = Sprite(
        ".......oo.......",
        "......ohbo......",
        "..oooooooooooo..",
        ".ohbbbbhhbbbbso.",
        "..o..o.oo.o..o..",
        "..o..o.oo.o..o..",
        ".o...o.oo.o...o.",
        ".o...o.oo.o...o.",
        "ooooooo..ooooooo",
        ".ohbsbo..ohbsbo.",
        "..oooo....oooo..",
        ".......oo.......",
        ".......oo.......",
        ".....oooooo.....",
        "....ohbbbbso....",
        "....oooooooo...."
    )

    /**
     * 5x5 pixel diamond sprite used for section bullets.
     */
    val BulletDiamondSprite = Sprite(
        "..o..",
        ".ohb.",
        "ohbso",
        ".obs.",
        "..o.."
    )

    /**
     * Draws a stepped integer pixel diamond centered at ([cx], [cy]) with Manhattan [radius].
     */
    fun drawPixelDiamond(
        canvas: PixelCanvas,
        cx: Int,
        cy: Int,
        radius: Int = 1,
        outline: Int = OsrsOuterFrame.toArgb(),
        fill: Int = OsrsBrassMid.toArgb(),
        highlight: Int = OsrsGoldHighlight.toArgb(),
        shadow: Int = OsrsBorderDark.toArgb()
    ) {
        if (radius <= 1) {
            // 3x3 pixel diamond
            canvas.set(cx, cy - 1, highlight)
            canvas.set(cx - 1, cy, highlight)
            canvas.set(cx, cy, fill)
            canvas.set(cx + 1, cy, shadow)
            canvas.set(cx, cy + 1, shadow)
        } else {
            for (dy in -radius..radius) {
                for (dx in -radius..radius) {
                    val manhattan = abs(dx) + abs(dy)
                    when {
                        manhattan == radius -> canvas.set(cx + dx, cy + dy, outline)
                        manhattan < radius -> {
                            val c = when {
                                dx + dy < 0 -> highlight
                                dx + dy > 0 -> shadow
                                else -> fill
                            }
                            canvas.set(cx + dx, cy + dy, c)
                        }
                    }
                }
            }
        }
    }

    /**
     * Draws an engraved horizontal pixel rule: 1-art-px dark groove over a 1-art-px light ridge,
     * with 3x3 stepped diamond end caps and an optional center 5x5 diamond flourish.
     */
    fun drawEngravedRule(
        canvas: PixelCanvas,
        withEndCaps: Boolean = true,
        withCenterFlourish: Boolean = false,
        accentColor: Int = OsrsBrassMid.toArgb()
    ) {
        val w = canvas.w
        val h = canvas.h
        if (w <= 0 || h <= 0) return

        val cy = h / 2
        val dark = OsrsBorderDark.toArgb()
        val light = OsrsBorderLight.toArgb()
        val highlight = OsrsGoldHighlight.toArgb()

        val startX = if (withEndCaps) 4 else 0
        val endX = if (withEndCaps) (w - 4).coerceAtLeast(startX) else w
        val span = endX - startX
        if (span > 0) {
            val midX = w / 2
            if (withCenterFlourish && span > 14) {
                val leftLen = (midX - 4 - startX).coerceAtLeast(0)
                val rightStart = midX + 5
                val rightLen = (endX - rightStart).coerceAtLeast(0)
                canvas.hLine(startX, (cy - 1).coerceAtLeast(0), leftLen, dark)
                canvas.hLine(startX, cy, leftLen, light)
                canvas.hLine(rightStart, (cy - 1).coerceAtLeast(0), rightLen, dark)
                canvas.hLine(rightStart, cy, rightLen, light)
                drawPixelDiamond(
                    canvas = canvas,
                    cx = midX,
                    cy = cy,
                    radius = 2,
                    fill = accentColor,
                    highlight = highlight,
                    shadow = dark
                )
            } else {
                canvas.hLine(startX, (cy - 1).coerceAtLeast(0), span, dark)
                canvas.hLine(startX, cy, span, light)
            }
        }

        if (withEndCaps && w >= 7) {
            drawPixelDiamond(canvas, cx = 2, cy = cy, radius = 1, fill = accentColor, highlight = highlight, shadow = dark)
            drawPixelDiamond(canvas, cx = w - 3, cy = cy, radius = 1, fill = accentColor, highlight = highlight, shadow = dark)
        }
    }

    /**
     * Draws 3-art-px gold pixel L-marks inside the four corners of a canvas (used on maxed skills).
     */
    fun drawCornerMarks(
        canvas: PixelCanvas,
        inset: Int = 2,
        arm: Int = 3,
        baseColor: Int = OsrsGold.toArgb(),
        highlightColor: Int = OsrsGoldHighlight.toArgb()
    ) {
        val w = canvas.w
        val h = canvas.h
        if (w < inset * 2 + arm * 2 || h < inset * 2 + arm * 2) return

        // Top-left
        canvas.hLine(inset, inset, arm, baseColor)
        canvas.vLine(inset, inset, arm, baseColor)
        canvas.set(inset, inset, highlightColor)

        // Top-right
        canvas.hLine(w - inset - arm, inset, arm, baseColor)
        canvas.vLine(w - 1 - inset, inset, arm, baseColor)
        canvas.set(w - 1 - inset, inset, highlightColor)

        // Bottom-left
        canvas.hLine(inset, h - 1 - inset, arm, baseColor)
        canvas.vLine(inset, h - inset - arm, arm, baseColor)
        canvas.set(inset, h - 1 - inset, highlightColor)

        // Bottom-right
        canvas.hLine(w - inset - arm, h - 1 - inset, arm, baseColor)
        canvas.vLine(w - 1 - inset, h - inset - arm, arm, baseColor)
        canvas.set(w - 1 - inset, h - 1 - inset, highlightColor)
    }

    /**
     * Draws a 3-bar wrought-iron grip handle with side pixel rivets into [canvas].
     */
    fun drawIronGrip(canvas: PixelCanvas) {
        val w = canvas.w
        val h = canvas.h
        if (w < 8 || h < 7) return

        val dark = OsrsIronEdgeDark.toArgb()
        val light = OsrsIronEdgeLight.toArgb()
        val barX = 5
        val barW = (w - 10).coerceAtLeast(2)

        val rows = intArrayOf(h / 4, h / 2, (h * 3) / 4)
        for (ry in rows) {
            canvas.hLine(barX, (ry - 1).coerceAtLeast(0), barW, light)
            canvas.hLine(barX, ry.coerceAtMost(h - 1), barW, dark)
        }
        val rivetY = (h / 2 - 1).coerceAtLeast(0)
        canvas.rivet(1, rivetY, size = 3)
        canvas.rivet((w - 4).coerceAtLeast(1), rivetY, size = 3)
    }
}

/**
 * Modifier that draws 3-art-px gold pixel corner L-marks when [enabled] is true (used on level-99 skills).
 */
fun Modifier.maxedSkillCorners(enabled: Boolean = true): Modifier =
    if (!enabled) {
        this
    } else {
        this.drawWithCache {
            val p = computeArtPx(density).toFloat()
            val inset = 2f * p
            val arm = 3f * p
            val w = size.width.roundToInt().toFloat()
            val h = size.height.roundToInt().toFloat()
            onDrawBehind {
                if (w < inset * 2 + arm * 2 || h < inset * 2 + arm * 2) return@onDrawBehind
                // Top-left
                drawRect(OsrsGold, Offset(inset, inset), Size(arm, p))
                drawRect(OsrsGold, Offset(inset, inset), Size(p, arm))
                drawRect(OsrsGoldHighlight, Offset(inset, inset), Size(p, p))
                // Top-right
                drawRect(OsrsGold, Offset(w - inset - arm, inset), Size(arm, p))
                drawRect(OsrsGold, Offset(w - inset - p, inset), Size(p, arm))
                drawRect(OsrsGoldHighlight, Offset(w - inset - p, inset), Size(p, p))
                // Bottom-left
                drawRect(OsrsGold, Offset(inset, h - inset - p), Size(arm, p))
                drawRect(OsrsGold, Offset(inset, h - inset - arm), Size(p, arm))
                drawRect(OsrsGoldHighlight, Offset(inset, h - inset - p), Size(p, p))
                // Bottom-right
                drawRect(OsrsGold, Offset(w - inset - arm, h - inset - p), Size(arm, p))
                drawRect(OsrsGold, Offset(w - inset - p, h - inset - arm), Size(p, arm))
                drawRect(OsrsGoldHighlight, Offset(w - inset - p, h - inset - p), Size(p, p))
            }
        }
    }

/**
 * Engraved pixel rule divider composable with 3x3 stepped diamond end caps and optional center diamond.
 */
@Composable
fun EngravedRule(
    modifier: Modifier = Modifier,
    withEndCaps: Boolean = true,
    withCenterFlourish: Boolean = false,
    accentColor: Color = OsrsBrassMid
) {
    val heightDp = if (withCenterFlourish) 7.dp else 5.dp
    val accentArgb = accentColor.toArgb()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(heightDp)
            .pixelArt(key = Triple("EngravedRule", withEndCaps to withCenterFlourish, accentArgb)) {
                Ornaments.drawEngravedRule(
                    canvas = this,
                    withEndCaps = withEndCaps,
                    withCenterFlourish = withCenterFlourish,
                    accentColor = accentArgb
                )
            }
    )
}

/**
 * 5x5 pixel diamond bullet glyph replacing vector diamond paths.
 */
@Composable
fun BulletGlyph(
    modifier: Modifier = Modifier,
    size: Dp = 5.dp,
    color: Color = OsrsGoldDark
) {
    val tones = SpriteTones(
        outline = OsrsOuterFrame.toArgb(),
        highlight = OsrsGoldHighlight.toArgb(),
        base = color.toArgb(),
        shadow = OsrsBorderDark.toArgb()
    )
    PixelIcon(
        sprite = Ornaments.BulletDiamondSprite,
        tones = tones,
        modifier = modifier.size(size)
    )
}

/**
 * Low-resolution wrought-iron 3-bar grip handle with pixel rivets for reordering rows.
 */
@Composable
fun IronGripHandle(
    modifier: Modifier = Modifier,
    width: Dp = 18.dp,
    height: Dp = 14.dp
) {
    Box(
        modifier = modifier
            .size(width, height)
            .pixelArt(key = "IronGripHandle") {
                Ornaments.drawIronGrip(this)
            }
    )
}

/**
 * 16x16 low-contrast pixel balance-scale watermark for empty Grand Exchange slots.
 */
@Composable
fun WatermarkScale(
    modifier: Modifier = Modifier
) {
    PixelIcon(
        sprite = Ornaments.WatermarkScaleSprite,
        tones = Ornaments.WATERMARK_TONES,
        modifier = modifier.size(16.dp)
    )
}
