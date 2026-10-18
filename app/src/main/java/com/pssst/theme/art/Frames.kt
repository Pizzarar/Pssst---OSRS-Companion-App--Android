package com.pssst.theme.art

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.pssst.theme.OsrsBorderDark
import com.pssst.theme.OsrsBorderLight
import com.pssst.theme.OsrsBorderOutline
import com.pssst.theme.OsrsBrassMid
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsIronBody
import com.pssst.theme.OsrsIronEdgeDark
import com.pssst.theme.OsrsIronEdgeLight
import com.pssst.theme.OsrsOuterFrame
import com.pssst.theme.OsrsPanelDark
import com.pssst.theme.OsrsSlotEmpty
import com.pssst.theme.OsrsSlotRecessed

/**
 * Corner hardware and frame weight tiers for panel frames.
 */
enum class FrameWeight {
    /** 1-art-px outline + 1-art-px bevel on flat OsrsPanelDark (dense lists, no bitmap). */
    LIGHT,

    /** 1-art-px outline + 1-art-px bevel + 1-art-px inner groove + 3x3 corner rivets on panel stone. */
    MEDIUM,

    /** 1-art-px outline + 2-art-px wrought-iron band + 1-art-px bevel + 4x4 rivets & straps on panel stone. */
    HEAVY
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
     * Draws a top-bar oak beam bound with wrought-iron straps and seated pixel rivets.
     */
    fun drawOakBeamFrame(canvas: PixelCanvas, seed: Int = 202) {
        val w = canvas.w
        val h = canvas.h
        if (w <= 0 || h <= 0) return

        Textures.drawOakBeam(canvas, seed = seed, showPlankSeams = true, showEndBands = true)

        val outline = OsrsOuterFrame.toArgb()
        val ironDark = OsrsIronEdgeDark.toArgb()
        val ironBody = OsrsIronBody.toArgb()
        val ironLight = OsrsIronEdgeLight.toArgb()

        // Top 2-px iron band + 1-px outline
        canvas.hLine(0, 0, w, outline)
        if (h >= 4) {
            canvas.hLine(0, 1, w, ironLight)
            canvas.hLine(0, 2, w, ironBody)
            canvas.hLine(0, 3, w, ironDark)
        }
        // Bottom 2-px iron band + 1-px outline
        if (h >= 8) {
            canvas.hLine(0, h - 4, w, ironLight)
            canvas.hLine(0, h - 3, w, ironBody)
            canvas.hLine(0, h - 2, w, ironDark)
        }
        canvas.hLine(0, h - 1, w, outline)

        // Corner hinge straps and rivets
        drawPixelHingeStraps(canvas)
    }

    /**
     * Draws a bottom-nav carved stone sill with top brass/iron rail and corner brackets.
     */
    fun drawStoneSillFrame(canvas: PixelCanvas, seed: Int = 109) {
        val w = canvas.w
        val h = canvas.h
        if (w <= 0 || h <= 0) return

        Textures.drawPanelStone(canvas, seed = seed)
        val outline = OsrsOuterFrame.toArgb()
        val brass = OsrsBrassMid.toArgb()
        val dark = OsrsBorderDark.toArgb()

        canvas.rectOutline(0, 0, w, h, outline)
        if (h >= 5) {
            canvas.hLine(1, 1, w - 2, brass)
            canvas.hLine(1, 2, w - 2, dark)
        }
        drawPixelCornerLBrackets(canvas)
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
 * Top-bar frame: heavy aged dark oak beam bound with wrought-iron edge straps and pixel rivets.
 */
fun Modifier.oakBeamFrame(seed: Int = 202): Modifier =
    this.pixelArt(key = Pair("oakBeamFrame", seed)) {
        Frames.drawOakBeamFrame(this, seed)
    }

/**
 * Bottom navigation sill frame: carved stone sill with top brass/iron rail and corner brackets.
 */
fun Modifier.stoneSillFrame(seed: Int = 109): Modifier =
    this.pixelArt(key = Pair("stoneSillFrame", seed)) {
        Frames.drawStoneSillFrame(this, seed)
    }

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
