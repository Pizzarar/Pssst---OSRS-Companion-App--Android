package com.pssst.theme.art

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import com.pssst.theme.OsrsDarkStone
import com.pssst.theme.OsrsIronBody
import com.pssst.theme.OsrsIronEdgeDark
import com.pssst.theme.OsrsIronEdgeLight
import com.pssst.theme.OsrsLeatherBase
import com.pssst.theme.OsrsLeatherCrease
import com.pssst.theme.OsrsLeatherHighlight
import com.pssst.theme.OsrsLeatherStitch
import com.pssst.theme.OsrsOakBase
import com.pssst.theme.OsrsOakGrainDark
import com.pssst.theme.OsrsOakGrainLight
import com.pssst.theme.OsrsOakMid
import com.pssst.theme.OsrsPanelDark
import com.pssst.theme.OsrsPanelMid
import com.pssst.theme.OsrsParchmentDark
import com.pssst.theme.OsrsParchmentStain
import com.pssst.theme.OsrsParchmentWarm
import com.pssst.theme.OsrsSlotEmpty
import com.pssst.theme.OsrsStoneMid
import com.pssst.theme.OsrsStonePoreDark
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Low-resolution procedural pixel-art material generators for Pssst.
 *
 * Every material is drawn into a [PixelCanvas] at exact art-pixel dimensions using fixed-point
 * integer noise ([PixelNoise]) and 4x4 Bayer ordered dithering across a strict 3-to-5-tone ramp
 * from `Color.kt`.
 */
object Textures {

    /** 5-tone stone ground ramp (darkest mortar to lightest block top-left edge). */
    val STONE_GROUND_TONES: IntArray = intArrayOf(
        OsrsStonePoreDark.toArgb(),
        OsrsDarkStone.toArgb(),
        OsrsStoneMid.toArgb(),
        OsrsSlotEmpty.toArgb(),
        OsrsPanelDark.toArgb()
    )

    /** 3-tone carved panel stone ramp (keeps OsrsRed >= 4.64:1 and OsrsHerbGreen >= 5.45:1). */
    val PANEL_STONE_TONES: IntArray = intArrayOf(
        OsrsSlotEmpty.toArgb(),
        OsrsPanelMid.toArgb(),
        OsrsPanelDark.toArgb()
    )

    /** 4-tone aged dark oak plank ramp. */
    val OAK_TONES: IntArray = intArrayOf(
        OsrsOakGrainDark.toArgb(),
        OsrsOakBase.toArgb(),
        OsrsOakMid.toArgb(),
        OsrsOakGrainLight.toArgb()
    )

    /** 3-tone forged wrought-iron ramp. */
    val IRON_TONES: IntArray = intArrayOf(
        OsrsIronEdgeDark.toArgb(),
        OsrsIronBody.toArgb(),
        OsrsIronEdgeLight.toArgb()
    )

    /** 3-tone dark-UI weathered parchment ramp. */
    val PARCHMENT_TONES: IntArray = intArrayOf(
        OsrsParchmentStain.toArgb(),
        OsrsParchmentDark.toArgb(),
        OsrsParchmentWarm.toArgb()
    )

    /** 3-tone cellular pebble-grain leather hide ramp (plus stitch thread when stitched). */
    val LEATHER_TONES: IntArray = intArrayOf(
        OsrsLeatherCrease.toArgb(),
        OsrsLeatherBase.toArgb(),
        OsrsLeatherHighlight.toArgb()
    )

    val LEATHER_STITCH_TONES: IntArray = intArrayOf(
        OsrsLeatherCrease.toArgb(),
        OsrsLeatherBase.toArgb(),
        OsrsLeatherHighlight.toArgb(),
        OsrsLeatherStitch.toArgb()
    )

    /**
     * OSRS login-screen dungeon wall: monumental ashlar blocks in [STONE_GROUND_TONES].
     *
     * - Courses 11-16 art px high, blocks 20-36 art px wide, laid in running bond.
     * - 1-art-px dark mortar with chamfered (rounded-off) block corners.
     * - Chiselled top/left catch-light, weathered so roughly a third of it is worn away.
     * - Calm, broad slab mottling with the odd hairline fissure instead of per-pixel noise.
     * - A torchlit falloff: blocks near the upper centre lean lighter, edges settle darker.
     */
    fun drawStoneGround(canvas: PixelCanvas, seed: Int = 101) {
        drawAshlar(
            canvas = canvas,
            ramp = STONE_GROUND_TONES,
            seed = seed,
            courseMin = 11,
            courseRange = 6,
            blockMin = 20,
            blockRange = 17,
            torchlight = true
        )
    }

    /**
     * Draws ashlar masonry into rows [top] until [bottom] of [canvas].
     *
     * [ramp] runs darkest to lightest: `ramp[0]` is mortar, the last entry is the chiselled
     * catch-light, and everything between is a block base tone. Every pixel is a [ramp] member.
     * [jointsX], when given, fixes the vertical joints of a single course instead of random
     * block widths, e.g. to line joints up with navigation columns.
     */
    internal fun drawAshlar(
        canvas: PixelCanvas,
        ramp: IntArray,
        seed: Int,
        courseMin: Int,
        courseRange: Int,
        blockMin: Int,
        blockRange: Int,
        torchlight: Boolean = false,
        top: Int = 0,
        bottom: Int = canvas.h,
        jointsX: IntArray? = null
    ) {
        val w = canvas.w
        val h = canvas.h
        if (w <= 0 || bottom <= top) return
        require(ramp.size >= 4) { "ashlar ramp needs mortar, two base tones and a catch-light" }
        val mortar = ramp[0]
        val lastBase = ramp.size - 2
        val baseCount = lastBase

        var y = top
        var courseIdx = 0
        var prevEnds = IntArray(0)
        var prevTones = IntArray(0)
        while (y < bottom) {
            val courseH = if (jointsX != null) {
                bottom - top
            } else {
                courseMin + PixelNoise.hash(courseIdx, 0, seed) % courseRange
            }
            val mortarY = (y + courseH - 1).coerceAtMost(bottom - 1)
            val curEnds = ArrayList<Int>()
            val curTones = ArrayList<Int>()
            var x = if (jointsX != null) 0 else -(PixelNoise.hash(courseIdx, 1, seed + 7) % blockMin)
            var blockIdx = 0
            var leftTone = -1
            while (x < w) {
                var xEnd = if (jointsX != null) {
                    jointsX.getOrNull(blockIdx) ?: w
                } else {
                    x + blockMin + PixelNoise.hash(blockIdx, courseIdx, seed + 19) % blockRange
                }
                // Running bond: never stack a joint within 3 art px of the joint in the course above.
                if (jointsX == null && prevEnds.any { abs(it - xEnd) <= 3 }) xEnd += 6

                val midX = (x.coerceAtLeast(0) + xEnd.coerceAtMost(w)) / 2
                val upperIdx = prevEnds.indexOfFirst { midX < it }
                val upperTone = if (upperIdx >= 0) prevTones[upperIdx] else -1
                val preferred = if (torchlight) {
                    val dx = (midX - w / 2).toFloat() / w
                    val dy = ((y + mortarY) / 2 - h / 4).toFloat() / h
                    val d = sqrt(dx * dx + dy * dy * 1.6f)
                    (baseCount - 1 - (d * baseCount * 1.6f).toInt()).coerceIn(0, baseCount - 1)
                } else {
                    PixelNoise.hash(blockIdx, courseIdx, seed + 37) % baseCount
                }
                var toneIdx = preferred
                for (step in intArrayOf(0, -1, 1, -2, 2)) {
                    val candidate = preferred + step
                    if (candidate in 0 until baseCount && candidate != leftTone && candidate != upperTone) {
                        toneIdx = candidate
                        break
                    }
                }
                leftTone = toneIdx
                curEnds.add(xEnd)
                curTones.add(toneIdx)

                val b = 1 + toneIdx
                val catchLight = ramp[b + 1]
                val shadow = ramp[b - 1]
                val lighter = ramp[(b + 1).coerceAtMost(lastBase)]
                val blockW = xEnd - x
                val hasFissure = PixelNoise.hash(blockIdx, courseIdx, seed + 5) % 4 == 0 &&
                    blockW > 14 && courseH > 10
                val fx = x + 4 + PixelNoise.hash(blockIdx, courseIdx, seed + 11) % (blockW - 10).coerceAtLeast(1)
                val fy = y + 3 + PixelNoise.hash(blockIdx, courseIdx, seed + 13) % (courseH - 7).coerceAtLeast(1)

                for (py in y..mortarY) {
                    for (px in x.coerceAtLeast(0) until xEnd.coerceAtMost(w)) {
                        val corner = (px == x || px == xEnd - 2) && (py == y || py == mortarY - 1)
                        val worn = PixelNoise.hash(px, py, seed + 53) < 77
                        val color = when {
                            py == mortarY || px == xEnd - 1 || corner -> mortar
                            py == y || px == x -> if (worn) ramp[b] else catchLight
                            py == mortarY - 1 || px == xEnd - 2 -> shadow
                            hasFissure && px - fx in 0..3 && py - fy == (px - fx) / 2 -> mortar
                            else -> {
                                val v = PixelNoise.value(px, py, 8, seed + 71)
                                val threshold = PixelNoise.bayer4(px, py) * 16 + 8
                                when {
                                    v > 180 && (v - 180) * 3 > threshold -> lighter
                                    v < 60 && (60 - v) * 3 > threshold -> shadow
                                    else -> ramp[b]
                                }
                            }
                        }
                        canvas.set(px, py, color)
                    }
                }
                x = xEnd
                blockIdx++
            }
            prevEnds = curEnds.toIntArray()
            prevTones = curTones.toIntArray()
            y += courseH
            courseIdx++
        }
    }

    /**
     * Draws a calm chiselled tablet surface for panel interiors behind text: broad slab grain
     * with faint horizontal chisel strokes, using only [PANEL_STONE_TONES] with Bayer dithering.
     */
    fun drawPanelStone(canvas: PixelCanvas, seed: Int = 101) {
        val w = canvas.w
        val h = canvas.h
        for (y in 0 until h) {
            for (x in 0 until w) {
                val slab = PixelNoise.value(x, y, 9, seed)
                val chisel = PixelNoise.value(x / 4, y * 3, 3, seed + 29)
                val jitter = (PixelNoise.hash(x, y, seed + 61) - 128) / 10
                val v = ((slab * 3 + chisel) / 4) + jitter
                canvas.set(x, y, PixelNoise.quantise(v, PANEL_STONE_TONES, x, y))
            }
        }
    }

    /**
     * Draws clean planed dark-oak planks (5-9 art px) with long horizontal grain, a lit top edge
     * and dark seam per plank, and optional wrought-iron end straps. No knots or blots.
     */
    fun drawOakBeam(
        canvas: PixelCanvas,
        seed: Int = 202,
        showPlankSeams: Boolean = true,
        showEndBands: Boolean = false
    ) {
        val w = canvas.w
        val h = canvas.h
        if (w <= 0 || h <= 0) return

        val seamTone = OAK_TONES[0]
        val ridgeTone = OAK_TONES[3]
        val grain = intArrayOf(OAK_TONES[1], OAK_TONES[2])

        var y = 0
        var plankIdx = 0
        while (y < h) {
            val plankH = if (showPlankSeams) 5 + PixelNoise.hash(plankIdx, 0, seed) % 5 else h
            val yEnd = min(h, y + plankH)
            val seamY = y + plankH - 1
            val buttJointX = if (showPlankSeams && w > 48 && PixelNoise.hash(plankIdx, 1, seed) % 2 == 0) {
                18 + PixelNoise.hash(plankIdx, 2, seed + 13) % (w - 36).coerceAtLeast(1)
            } else {
                -10
            }
            for (py in y until yEnd) {
                for (px in 0 until w) {
                    val tone = when {
                        showPlankSeams && py == seamY && yEnd < h -> seamTone
                        px == buttJointX -> seamTone
                        px == buttJointX + 1 -> ridgeTone
                        showPlankSeams && py == y && y > 0 -> ridgeTone
                        else -> {
                            // Long planed grain: slow along the board, changing row to row.
                            val v = PixelNoise.value(px, py * 7 + plankIdx * 53, 16, seed + 59)
                            PixelNoise.quantise(v, grain, px, py)
                        }
                    }
                    canvas.set(px, py, tone)
                }
            }
            y += plankH
            plankIdx++
        }

        if (showEndBands && w >= 12) {
            val bandW = 3
            fillIronBand(canvas, 0, 0, bandW, h, seed + 91)
            fillIronBand(canvas, w - bandW, 0, bandW, h, seed + 97)
        }
    }

    private fun fillIronBand(canvas: PixelCanvas, x: Int, y: Int, bw: Int, bh: Int, seed: Int) {
        val dark = IRON_TONES[0]
        val body = IRON_TONES[1]
        val light = IRON_TONES[2]
        canvas.fill(x, y, bw, bh, body)
        canvas.vLine(x, y, bh, light)
        canvas.vLine(x + bw - 1, y, bh, dark)
        for (py in y + 1 until y + bh - 1) {
            if (PixelNoise.hash(x, py, seed) < 24) {
                canvas.set(x + 1, py, light)
            }
        }
    }

    /**
     * Draws hammered wrought iron: flat [OsrsIronBody] with sparse (~3%) irregular light/dark
     * hammer-mark pixel pairs and no regular dot grid.
     */
    fun drawHammeredIron(canvas: PixelCanvas, seed: Int = 505, withBevel: Boolean = true) {
        val w = canvas.w
        val h = canvas.h
        if (w <= 0 || h <= 0) return

        val dark = IRON_TONES[0]
        val body = IRON_TONES[1]
        val light = IRON_TONES[2]

        canvas.clear(body)

        // Sparse irregular hammer dimples (~3% of interior pixels)
        for (y in 1 until h - 2) {
            for (x in 1 until w - 2) {
                val hVal = PixelNoise.hash(x, y, seed)
                if (hVal < 8) {
                    canvas.set(x, y, light)
                    canvas.set(x + 1, y + 1, dark)
                } else if (hVal == 8) {
                    canvas.set(x, y, dark)
                }
            }
        }

        if (withBevel && w >= 2 && h >= 2) {
            canvas.hLine(0, 0, w, light)
            canvas.vLine(0, 0, h, light)
            canvas.hLine(0, h - 1, w, dark)
            canvas.vLine(w - 1, 0, h, dark)
        }
    }

    /**
     * Draws dark aged vellum parchment with horizontal linen fibre streaks and edge foxing
     * quantised strictly to [PARCHMENT_TONES].
     */
    fun drawParchment(canvas: PixelCanvas, seed: Int = 303, showStainVignette: Boolean = true) {
        val w = canvas.w
        val h = canvas.h
        if (w <= 0 || h <= 0) return

        val foxingZone = min(min(w, h) / 3, 8).coerceAtLeast(2)

        for (y in 0 until h) {
            for (x in 0 until w) {
                // Short horizontal fibre streaks + vertical weave
                val warp = PixelNoise.value(x / 5, y, 2, seed)
                val weft = PixelNoise.value(x, y / 4, 2, seed + 17)
                val mottle = PixelNoise.value(x, y, 6, seed + 33)
                var v = (warp * 2 + weft + mottle) / 4

                if (showStainVignette) {
                    val edgeDist = min(min(x, w - 1 - x), min(y, h - 1 - y))
                    if (edgeDist < foxingZone) {
                        val foxBias = ((foxingZone - edgeDist) * 110) / foxingZone
                        val stainPatch = (PixelNoise.value(x, y, 5, seed + 51) - 128) / 2
                        v = (v - foxBias + stainPatch).coerceIn(0, 255)
                    }
                }

                canvas.set(x, y, PixelNoise.quantise(v, PARCHMENT_TONES, x, y))
            }
        }
    }

    /**
     * Draws worn binding leather with cellular (Worley) pebble grain quantised to [LEATHER_TONES]
     * and an optional 2-on/2-off single-art-px stitch line of [OsrsLeatherStitch] inset 3 art px.
     */
    fun drawLeather(canvas: PixelCanvas, seed: Int = 404, stitched: Boolean = true) {
        val w = canvas.w
        val h = canvas.h
        if (w <= 0 || h <= 0) return

        val crease = LEATHER_TONES[0]
        val stitch = OsrsLeatherStitch.toArgb()

        for (y in 0 until h) {
            for (x in 0 until w) {
                val cellVal = PixelNoise.cellular(x, y, 4, seed)
                val jitter = (PixelNoise.hash(x, y, seed + 23) - 128) / 5
                // Bias slightly dark so highlight only hits pebble crowns
                val v = ((cellVal * 210) / 255) + jitter
                canvas.set(x, y, PixelNoise.quantise(v, LEATHER_TONES, x, y))
            }
        }

        if (stitched && w > 10 && h > 10) {
            val inset = 3
            val x0 = inset
            val y0 = inset
            val x1 = w - 1 - inset
            val y1 = h - 1 - inset

            for (px in x0..x1) {
                val on = ((px - x0) and 3) < 2
                val c = if (on) stitch else crease
                canvas.set(px, y0, c)
                canvas.set(px, y1, c)
            }
            for (py in y0..y1) {
                val on = ((py - y0) and 3) < 2
                val c = if (on) stitch else crease
                canvas.set(x0, py, c)
                canvas.set(x1, py, c)
            }
        }
    }
}

/**
 * Full-screen or container ground in OSRS login-screen ashlar stone at integer art-pixel resolution.
 */
fun Modifier.stoneGround(seed: Int = 101): Modifier =
    this.pixelArt(key = Pair("stoneGround", seed)) {
        Textures.drawStoneGround(this, seed)
    }

/**
 * Carved stone panel interior texture with low-contrast 3-tone Bayer-dithered mottling.
 */
fun Modifier.carvedStone(seed: Int = 101): Modifier =
    this.pixelArt(key = Pair("carvedStone", seed)) {
        Textures.drawPanelStone(this, seed)
    }

/**
 * Clean planed dark-oak beam texture with horizontal planks, long grain, and optional seams.
 */
fun Modifier.oakBeam(seed: Int = 202, showPlankSeams: Boolean = true): Modifier =
    this.pixelArt(key = Triple("oakBeam", seed, showPlankSeams)) {
        Textures.drawOakBeam(this, seed = seed, showPlankSeams = showPlankSeams, showEndBands = false)
    }

/**
 * Dark-mode weathered parchment surface with horizontal linen fibre streaks and edge foxing.
 */
fun Modifier.parchment(seed: Int = 303, showStainVignette: Boolean = true): Modifier =
    this.pixelArt(key = Triple("parchment", seed, showStainVignette)) {
        Textures.drawParchment(this, seed = seed, showStainVignette = showStainVignette)
    }

/**
 * Worn oxblood-brown binding leather surface with cellular pebble grain and single-art-px stitching.
 */
fun Modifier.leather(seed: Int = 404, stitched: Boolean = true): Modifier =
    this.pixelArt(key = Triple("leather", seed, stitched)) {
        Textures.drawLeather(this, seed = seed, stitched = stitched)
    }

/**
 * Hammered wrought-iron surface with sparse forge dimples and 1-art-px bevel.
 */
fun Modifier.hammeredIron(seed: Int = 505): Modifier =
    this.pixelArt(key = Pair("hammeredIron", seed)) {
        Textures.drawHammeredIron(this, seed = seed, withBevel = true)
    }
