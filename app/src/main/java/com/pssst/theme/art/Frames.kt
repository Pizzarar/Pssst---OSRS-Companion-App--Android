package com.pssst.theme.art

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.pssst.theme.OsrsBorderDark
import com.pssst.theme.OsrsBorderLight
import com.pssst.theme.OsrsBorderOutline
import com.pssst.theme.OsrsBrassMid
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsGoldHighlight
import com.pssst.theme.OsrsGoldShadow
import com.pssst.theme.OsrsIronBody
import com.pssst.theme.OsrsIronEdgeDark
import com.pssst.theme.OsrsIronEdgeLight
import com.pssst.theme.OsrsOuterFrame
import com.pssst.theme.OsrsPanelDark
import com.pssst.theme.OsrsSlateBevel
import com.pssst.theme.OsrsSlateCatchLight
import com.pssst.theme.OsrsSlateDark
import com.pssst.theme.OsrsSlateLit
import com.pssst.theme.OsrsSlateMid
import com.pssst.theme.OsrsSlateMortar
import com.pssst.theme.OsrsSlateUndercut
import com.pssst.theme.OsrsSlotEmpty
import com.pssst.theme.OsrsSlotRecessed
import com.pssst.theme.OsrsStonePoreDark

/**
 * Corner hardware and frame weight tiers for panel frames.
 */
enum class FrameWeight {
    /** 1-art-px outline + 1-art-px bevel on flat OsrsPanelDark (dense lists, no bitmap). */
    LIGHT,

    /** 1-art-px outline + 1-art-px bevel + 1-art-px inner groove + 3x3 corner rivets on panel stone. */
    MEDIUM,

    /** 1-art-px outline + 2-art-px wrought-iron band + 1-art-px bevel + 4x4 rivets & straps on panel stone. */
    HEAVY;

    /** Art pixels occupied on each edge by the frame border, bevel, inner groove, and corner brackets. */
    fun insetArtPx(): Int = when (this) {
        LIGHT -> 2
        MEDIUM -> 3
        HEAVY -> 4
    }
}

/**
 * Pixel-honest hardware and frame drawing routines on [PixelCanvas].
 */
object Frames {

    /**
     * Draws a carved-stone panel frame with pixel hardware according to [weight].
     */
    fun drawLedgerPanel(
        canvas: PixelCanvas,
        weight: FrameWeight,
        seed: Int = 101,
        accentBorderArgb: Int? = null
    ) {
        val w = canvas.w
        val h = canvas.h
        if (w <= 0 || h <= 0) return

        val outline = OsrsOuterFrame.toArgb()
        val light = accentBorderArgb ?: OsrsBorderLight.toArgb()
        val dark = OsrsBorderDark.toArgb()
        val groove = OsrsBorderOutline.toArgb()
        val brass = accentBorderArgb ?: OsrsBrassMid.toArgb()

        when (weight) {
            FrameWeight.LIGHT -> {
                canvas.bevelBox(
                    x = 0,
                    y = 0,
                    bw = w,
                    bh = h,
                    outline = outline,
                    light = light,
                    dark = dark,
                    fill = OsrsPanelDark.toArgb(),
                    raised = true
                )
            }

            FrameWeight.MEDIUM -> {
                Textures.drawPanelStone(canvas, seed)
                canvas.bevelBox(0, 0, w, h, outline = outline, light = light, dark = dark, raised = true)
                if (w > 6 && h > 6) {
                    canvas.rectOutline(2, 2, w - 4, h - 4, groove)
                }
                drawPixelCornerLBrackets(canvas)
            }

            FrameWeight.HEAVY -> {
                Textures.drawPanelStone(canvas, seed)
                // 1-px outer outline
                canvas.rectOutline(0, 0, w, h, outline)
                // 2-px wrought-iron band
                if (w > 6 && h > 6) {
                    val ironDark = OsrsIronEdgeDark.toArgb()
                    val ironBody = OsrsIronBody.toArgb()
                    val ironLight = OsrsIronEdgeLight.toArgb()
                    canvas.rectOutline(1, 1, w - 2, h - 2, ironBody)
                    canvas.rectOutline(2, 2, w - 4, h - 4, ironBody)
                    canvas.hLine(1, 1, w - 3, ironLight)
                    canvas.vLine(1, 1, h - 3, ironLight)
                    canvas.hLine(2, h - 2, w - 3, ironDark)
                    canvas.vLine(w - 2, 2, h - 3, ironDark)
                }
                // 1-px inner bevel
                if (w > 8 && h > 8) {
                    canvas.hLine(3, h - 4, w - 6, dark)
                    canvas.vLine(w - 4, 3, h - 6, dark)
                    canvas.hLine(3, 3, w - 7, brass)
                    canvas.vLine(3, 3, h - 7, brass)
                }
                drawPixelHingeStraps(canvas)
            }
        }
    }

    /**
     * Cool slate login-screen stone for bars and plaques: 2-art-px chiselled bevel, slate faces,
     * deep undercut and mortar. Contrasts with the warm brown panels below.
     */
    internal val BAR_ASHLAR: Textures.AshlarStyle
        get() = Textures.AshlarStyle(
            mortar = OsrsSlateMortar.toArgb(),
            undercut = OsrsSlateUndercut.toArgb(),
            faces = intArrayOf(OsrsSlateDark.toArgb(), OsrsSlateMid.toArgb(), OsrsSlateLit.toArgb()),
            topBevel = intArrayOf(OsrsSlateCatchLight.toArgb(), OsrsSlateBevel.toArgb()),
            leftBevel = intArrayOf(OsrsSlateCatchLight.toArgb(), OsrsSlateBevel.toArgb())
        )

    /** Warm brazier-gold cornice rail, 3 art px: highlight, gold, brass. */
    private fun goldRail(canvas: PixelCanvas, y: Int) {
        canvas.hLine(0, y, canvas.w, OsrsGoldHighlight.toArgb())
        canvas.hLine(0, y + 1, canvas.w, OsrsGold.toArgb())
        canvas.hLine(0, y + 2, canvas.w, OsrsBrassMid.toArgb())
    }

    /**
     * Top status bar: a heavy OSRS login-screen carved slate header. Chiselled grey ashlar
     * courses between warm brazier-gold cornice rails, with brass rivets at the corners.
     */
    fun drawOakBeamFrame(canvas: PixelCanvas, seed: Int = 202) {
        val w = canvas.w
        val h = canvas.h
        if (w <= 0 || h <= 0) return

        canvas.clear(OsrsSlateMid.toArgb())
        val body = (h - 10).coerceAtLeast(1)
        Textures.drawAshlar(
            canvas = canvas,
            style = BAR_ASHLAR,
            seed = seed,
            courseMin = if (body >= 24) body / 2 else body,
            courseRange = 1,
            blockMin = 30,
            blockRange = 14,
            top = 5,
            bottom = (h - 5).coerceAtLeast(6)
        )

        val outline = OsrsOuterFrame.toArgb()
        canvas.hLine(0, 0, w, outline)
        if (h >= 12) {
            goldRail(canvas, 1)
            canvas.hLine(0, 4, w, OsrsSlateMortar.toArgb())
            goldRail(canvas, h - 5)
            canvas.hLine(0, h - 2, w, OsrsSlateMortar.toArgb())
        }
        canvas.hLine(0, h - 1, w, outline)
        if (w >= 16 && h >= 16) {
            for ((rx, ry) in listOf(2 to 6, w - 5 to 6, 2 to h - 9, w - 5 to h - 9)) {
                brassRivet(canvas, rx, ry)
            }
        }
    }

    /**
     * Bottom navigation bar: an OSRS login-screen carved slate footer. One course of chiselled grey
     * ashlar whose joints line up with the five tab columns, under a warm brazier-gold rail.
     */
    fun drawStoneSillFrame(canvas: PixelCanvas, seed: Int = 109) {
        val w = canvas.w
        val h = canvas.h
        if (w <= 0 || h <= 0) return

        canvas.clear(OsrsSlateMid.toArgb())
        val joints = IntArray(5) { i -> if (i < 4) (w * (i + 1)) / 5 + 1 else w }
        Textures.drawAshlar(
            canvas = canvas,
            style = BAR_ASHLAR,
            seed = seed,
            courseMin = h,
            courseRange = 1,
            blockMin = 20,
            blockRange = 1,
            top = 5,
            bottom = (h - 2).coerceAtLeast(6),
            jointsX = joints
        )
        val outline = OsrsOuterFrame.toArgb()
        canvas.hLine(0, 0, w, outline)
        if (h >= 8) {
            goldRail(canvas, 1)
            canvas.hLine(0, 4, w, OsrsSlateMortar.toArgb())
        }
        canvas.hLine(0, h - 2, w, OsrsSlateMortar.toArgb())
        canvas.hLine(0, h - 1, w, outline)
        if (w >= 16 && h >= 16) {
            brassRivet(canvas, 2, 6)
            brassRivet(canvas, w - 5, 6)
        }
    }

    /** 3x3 seated brass rivet: dark rim, lit top-left, brass body. */
    internal fun brassRivet(canvas: PixelCanvas, x: Int, y: Int) {
        canvas.fill(x, y, 3, 3, OsrsBorderDark.toArgb())
        canvas.set(x + 1, y, OsrsBrassMid.toArgb())
        canvas.set(x, y + 1, OsrsBrassMid.toArgb())
        canvas.set(x + 1, y + 1, OsrsGoldHighlight.toArgb())
        canvas.set(x + 2, y + 1, OsrsGoldShadow.toArgb())
        canvas.set(x + 1, y + 2, OsrsGoldShadow.toArgb())
    }

    /**
     * Draws a weathered parchment notice slip with 1-art-px nicked dark outline and 3x3 iron corner tacks.
     */
    fun drawParchmentNotice(
        canvas: PixelCanvas,
        accentBorderArgb: Int = OsrsBrassMid.toArgb(),
        seed: Int = 303
    ) {
        val w = canvas.w
        val h = canvas.h
        if (w <= 0 || h <= 0) return

        Textures.drawParchment(canvas, seed = seed, showStainVignette = true)

        val outline = OsrsOuterFrame.toArgb()
        val dark = OsrsBorderDark.toArgb()
        canvas.rectOutline(0, 0, w, h, outline)
        if (w > 4 && h > 4) {
            canvas.hLine(1, h - 2, w - 2, dark)
            canvas.vLine(w - 2, 1, h - 2, dark)
            canvas.hLine(1, 1, w - 3, accentBorderArgb)
            canvas.vLine(1, 1, h - 3, accentBorderArgb)
        }

        // Occasional 1-px edge nicks
        for (x in 4 until w - 4) {
            if (PixelNoise.hash(x, 0, seed + 19) < 16) {
                canvas.set(x, 1, outline)
            }
            if (PixelNoise.hash(x, 1, seed + 29) < 16) {
                canvas.set(x, h - 2, outline)
            }
        }

        // 4 iron corner tacks (3x3 pixel studs)
        if (w >= 14 && h >= 10) {
            canvas.rivet(2, 2, size = 3)
            canvas.rivet(w - 5, 2, size = 3)
            canvas.rivet(2, h - 5, size = 3)
            canvas.rivet(w - 5, h - 5, size = 3)
        }
    }

    /**
     * Draws a bound worn-leather panel with cellular pebble grain, single-art-px stitch line,
     * and corner wrought-iron brackets.
     */
    fun drawLeatherBoundPanel(canvas: PixelCanvas, seed: Int = 404) {
        val w = canvas.w
        val h = canvas.h
        if (w <= 0 || h <= 0) return

        Textures.drawLeather(canvas, seed = seed, stitched = true)
        canvas.bevelBox(
            x = 0,
            y = 0,
            bw = w,
            bh = h,
            outline = OsrsOuterFrame.toArgb(),
            light = OsrsBorderLight.toArgb(),
            dark = OsrsBorderDark.toArgb(),
            raised = true
        )
        drawPixelCornerLBrackets(canvas)
    }

    internal fun drawPixelCornerLBrackets(canvas: PixelCanvas) {
        val w = canvas.w
        val h = canvas.h
        if (w < 16 || h < 12) return

        val outline = OsrsIronEdgeDark.toArgb()
        val body = OsrsIronBody.toArgb()
        val light = OsrsIronEdgeLight.toArgb()
        val arm = 7

        // Top-left L-bracket
        canvas.fill(1, 1, arm, 3, body)
        canvas.fill(1, 1, 3, arm, body)
        canvas.hLine(1, 1, arm, light)
        canvas.vLine(1, 1, arm, light)
        canvas.hLine(3, 3, arm - 3, outline)
        canvas.vLine(3, 3, arm - 3, outline)
        canvas.set(arm, 1, outline)
        canvas.set(arm, 2, outline)
        canvas.set(1, arm, outline)
        canvas.set(2, arm, outline)
        canvas.rivet(2, 2, size = 3)

        // Top-right L-bracket
        canvas.fill(w - 1 - arm, 1, arm, 3, body)
        canvas.fill(w - 4, 1, 3, arm, body)
        canvas.hLine(w - 1 - arm, 1, arm, light)
        canvas.vLine(w - 2, 1, arm, outline)
        canvas.hLine(w - 1 - arm, 3, arm - 3, outline)
        canvas.vLine(w - 4, 3, arm - 3, light)
        canvas.set(w - 2 - arm, 1, outline)
        canvas.set(w - 2 - arm, 2, outline)
        canvas.set(w - 3, arm, outline)
        canvas.set(w - 2, arm, outline)
        canvas.rivet(w - 5, 2, size = 3)

        // Bottom-left L-bracket
        canvas.fill(1, h - 4, arm, 3, body)
        canvas.fill(1, h - 1 - arm, 3, arm, body)
        canvas.vLine(1, h - 1 - arm, arm, light)
        canvas.hLine(1, h - 2, arm, outline)
        canvas.hLine(3, h - 4, arm - 3, light)
        canvas.vLine(3, h - 1 - arm, arm - 3, outline)
        canvas.set(1, h - 2 - arm, outline)
        canvas.set(2, h - 2 - arm, outline)
        canvas.set(arm, h - 3, outline)
        canvas.set(arm, h - 2, outline)
        canvas.rivet(2, h - 5, size = 3)

        // Bottom-right L-bracket
        canvas.fill(w - 1 - arm, h - 4, arm, 3, body)
        canvas.fill(w - 4, h - 1 - arm, 3, arm, body)
        canvas.hLine(w - 1 - arm, h - 2, arm, outline)
        canvas.vLine(w - 2, h - 1 - arm, arm, outline)
        canvas.hLine(w - 1 - arm, h - 4, arm - 3, light)
        canvas.vLine(w - 4, h - 1 - arm, arm - 3, light)
        canvas.rivet(w - 5, h - 5, size = 3)
    }

    internal fun drawPixelHingeStraps(canvas: PixelCanvas) {
        val w = canvas.w
        val h = canvas.h
        if (w < 26 || h < 14) {
            drawPixelCornerLBrackets(canvas)
            return
        }

        val outline = OsrsIronEdgeDark.toArgb()
        val body = OsrsIronBody.toArgb()
        val light = OsrsIronEdgeLight.toArgb()
        val strapLen = 11
        val strapH = 4

        val yPositions = intArrayOf(1, h - 1 - strapH)
        for (sy in yPositions) {
            // Left horizontal strap with stepped spearhead tip
            canvas.fill(1, sy, strapLen - 1, strapH, body)
            canvas.hLine(1, sy, strapLen - 1, light)
            canvas.hLine(1, sy + strapH - 1, strapLen - 1, outline)
            canvas.vLine(strapLen, sy + 1, strapH - 2, outline)
            canvas.set(strapLen - 1, sy, outline)
            canvas.set(strapLen - 1, sy + strapH - 1, outline)
            canvas.rivet(2, sy, size = 4)
            canvas.rivet(7, sy + 1, size = 3)

            // Right horizontal strap with stepped spearhead tip
            val rx = w - strapLen
            canvas.fill(rx, sy, strapLen - 1, strapH, body)
            canvas.hLine(rx, sy, strapLen - 1, light)
            canvas.hLine(rx, sy + strapH - 1, strapLen - 1, outline)
            canvas.vLine(rx - 1, sy + 1, strapH - 2, outline)
            canvas.set(rx, sy, outline)
            canvas.set(rx, sy + strapH - 1, outline)
            canvas.rivet(w - 6, sy, size = 4)
            canvas.rivet(w - 10, sy + 1, size = 3)
        }
    }
}

/**
 * Carved-stone panel frame with pixel bevels and wrought-iron hardware in [weight].
 *
 * [FrameWeight.LIGHT] uses [pixelBevelBox] (zero bitmap allocation) for dense lists.
 */
fun Modifier.ledgerPanelFrame(
    weight: FrameWeight = FrameWeight.MEDIUM,
    seed: Int = 101,
    accentBorder: Color? = null
): Modifier {
    if (weight == FrameWeight.LIGHT) {
        return this.pixelBevelBox(
            fill = OsrsPanelDark,
            outline = OsrsOuterFrame,
            light = accentBorder ?: OsrsBorderLight,
            dark = OsrsBorderDark,
            raised = true
        )
    }
    val accentArgb = accentBorder?.toArgb()
    return this.pixelArt(key = Triple("ledgerPanel", weight, seed to accentArgb)) {
        Frames.drawLedgerPanel(
            canvas = this,
            weight = weight,
            seed = seed,
            accentBorderArgb = accentArgb
        )
    }
}

/**
 * Top-bar frame: heavy OSRS login-screen carved stone header with iron/brass cornices and rivets.
 * (Name kept for call-site stability; it no longer draws oak.)
 */
fun Modifier.oakBeamFrame(seed: Int = 202): Modifier =
    this.pixelArt(key = Pair("oakBeamFrame", seed)) {
        Frames.drawOakBeamFrame(this, seed)
    }

/**
 * Bottom navigation frame: carved stone footer with joints aligned to the five tab columns.
 */
fun Modifier.stoneSillFrame(seed: Int = 109): Modifier =
    this.pixelArt(key = Pair("stoneSillFrame", seed)) {
        Frames.drawStoneSillFrame(this, seed)
    }

/**
 * Bottom-bar tab button: selected tabs are a raised slate tablet with a gold catch-light,
 * unselected tabs a recessed dark slate well. Includes the slot's outer margin.
 */
fun Modifier.stoneTabSlot(selected: Boolean): Modifier = this
    .padding(horizontal = 3.dp, vertical = 4.dp)
    .pixelBevelBox(
        fill = if (selected) OsrsSlateMid else OsrsSlateUndercut,
        outline = OsrsSlateMortar,
        light = if (selected) OsrsGold else OsrsSlateBevel,
        dark = if (selected) OsrsSlateUndercut else OsrsSlateMortar,
        raised = selected
    )

/**
 * Recessed carved-stone well for item slots, skill cells, and top-bar status wells.
 * Drawn as integer-aligned pixel rects with zero bitmap allocation.
 */
fun Modifier.ledgerWellSlot(
    empty: Boolean = false,
    highlighted: Boolean = false
): Modifier = this.pixelBevelBox(
    fill = if (empty) OsrsSlotEmpty else OsrsSlotRecessed,
    outline = OsrsOuterFrame,
    light = if (highlighted) OsrsGold else OsrsBorderLight,
    dark = OsrsBorderDark,
    raised = false
)

/**
 * Weathered parchment notice strip pinned with 3x3 iron corner tacks.
 */
fun Modifier.parchmentNotice(
    accentBorder: Color = OsrsBrassMid,
    seed: Int = 303
): Modifier {
    val accentArgb = accentBorder.toArgb()
    return this.pixelArt(key = Triple("parchmentNotice", seed, accentArgb)) {
        Frames.drawParchmentNotice(this, accentBorderArgb = accentArgb, seed = seed)
    }
}

/**
 * Bound worn-leather panel with cellular pebble grain, single-art-px stitch line, and corner brackets.
 */
fun Modifier.leatherBoundPanel(seed: Int = 404): Modifier =
    this.pixelArt(key = Pair("leatherBoundPanel", seed)) {
        Frames.drawLeatherBoundPanel(this, seed = seed)
    }

/**
 * Pads and clips inner content to the exact integer art-pixel boundary inside a [ledgerPanelFrame]
 * of [weight], keeping the outer outline, bevel, inner groove, and corner hardware unbroken.
 */
@Composable
fun Modifier.ledgerPanelInset(weight: FrameWeight = FrameWeight.MEDIUM): Modifier {
    val density = LocalDensity.current
    val insetDp = with(density) { (weight.insetArtPx() * computeArtPx(density.density)).toDp() }
    return this
        .padding(insetDp)
        .clipToBounds()
}

