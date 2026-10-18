package com.pssst.theme.art

import android.graphics.Bitmap
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
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
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.min

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
     * Draws irregular coursed stone blocks into [canvas]:
     * - Courses 6–10 art-px high with 1-art-px dark mortar ([OsrsStonePoreDark]).
     * - Randomised block widths 10–22 art-px, offset per course.
     * - No two adjacent blocks (left or upper) share the same base tone.
     * - 1-art-px top/left light edge and 1-art-px bottom/right shadow edge on each block.
     * - Bayer-dithered value noise inside each block.
     */
    fun drawStoneGround(canvas: PixelCanvas, seed: Int = 101) {
        val w = canvas.w
        val h = canvas.h
        if (w <= 0 || h <= 0) return

        val mortar = STONE_GROUND_TONES[0]
        val baseTones = intArrayOf(
            STONE_GROUND_TONES[1],
            STONE_GROUND_TONES[2],
            STONE_GROUND_TONES[3]
        )

        var y = 0
        var courseIdx = 0
        // Track upper course block spans and their chosen tone index (0..2) so vertical neighbors differ
        var prevCourseEnds = IntArray(0)
        var prevCourseTones = IntArray(0)

        while (y < h) {
            val courseH = 6 + (PixelNoise.hash(courseIdx, 0, seed) % 5) // 6..10 art px
            val yEnd = min(h, y + courseH)
            val mortarY = y + courseH - 1

            var x = -(PixelNoise.hash(courseIdx, 1, seed + 7) % 9)
            var blockIdx = 0
            var leftToneIdx = -1

            val curEnds = ArrayList<Int>()
            val curTones = ArrayList<Int>()

            while (x < w) {
                val blockW = 10 + (PixelNoise.hash(blockIdx, courseIdx, seed + 19) % 13) // 10..22 art px
                val xEnd = x + blockW
                val midX = (x.coerceAtLeast(0) + xEnd.coerceAtMost(w)) / 2

                // Find upper neighbor tone index at midX
                var upperToneIdx = -1
                for (i in prevCourseEnds.indices) {
                    if (midX < prevCourseEnds[i]) {
                        upperToneIdx = prevCourseTones[i]
                        break
                    }
                }

                // Pick a tone index in 0..2 that differs from both leftToneIdx and upperToneIdx
                val rawPick = PixelNoise.hash(blockIdx, courseIdx, seed + 37) % 3
                var toneIdx = rawPick
                for (attempt in 0 until 3) {
                    val candidate = (rawPick + attempt) % 3
                    if (candidate != leftToneIdx && candidate != upperToneIdx) {
                        toneIdx = candidate
                        break
                    }
                }
                leftToneIdx = toneIdx
                curEnds.add(xEnd)
                curTones.add(toneIdx)

                // Pair of tones for block interior dithering
                val blockPair = intArrayOf(
                    STONE_GROUND_TONES[toneIdx + 1],
                    STONE_GROUND_TONES[(toneIdx + 2).coerceAtMost(3)]
                )
                val topLeftEdgeTone = STONE_GROUND_TONES[(toneIdx + 2).coerceAtMost(4)]
                val bottomRightEdgeTone = STONE_GROUND_TONES[toneIdx]

                val bx0 = x.coerceIn(0, w)
                val bx1 = xEnd.coerceIn(0, w)
                for (py in y until yEnd) {
                    for (px in bx0 until bx1) {
                        val color = when {
                            py == mortarY || px == xEnd - 1 -> mortar
                            py == y || px == x -> {
                                // 1-art-px top/left chiseled catch-light with occasional nick
                                if (PixelNoise.hash(px, py, seed + 53) < 28) baseTones[toneIdx] else topLeftEdgeTone
                            }
                            py == mortarY - 1 || px == xEnd - 2 -> bottomRightEdgeTone
                            else -> {
                                val n = PixelNoise.value(px, py, 5, seed + 71)
                                val jitter = (PixelNoise.hash(px, py, seed + 89) - 128) / 3
                                PixelNoise.quantise(n + jitter, blockPair, px, py)
                            }
                        }
                        canvas.set(px, py, color)
                    }
                }

                x = xEnd
                blockIdx++
            }

            prevCourseEnds = curEnds.toIntArray()
            prevCourseTones = curTones.toIntArray()
            y += courseH
            courseIdx++
        }
    }

    /**
     * Draws low-contrast carved stone mottling into [canvas] for panel interiors behind text.
     * Uses only [PANEL_STONE_TONES] (`#221D16`, `#27211A`, `#2B251D`) with Bayer 4x4 dithering.
     */
    fun drawPanelStone(canvas: PixelCanvas, seed: Int = 101) {
        val w = canvas.w
        val h = canvas.h
        for (y in 0 until h) {
            for (x in 0 until w) {
                val coarse = PixelNoise.value(x, y, 7, seed)
                val fine = PixelNoise.value(x, y, 3, seed + 29)
                val jitter = (PixelNoise.hash(x, y, seed + 61) - 128) / 4
                val v = ((coarse * 3 + fine) / 4) + jitter
                canvas.set(x, y, PixelNoise.quantise(v, PANEL_STONE_TONES, x, y))
            }
        }
    }

    /**
     * Draws horizontal dark-oak planks of varying height (5–9 art px) with stretched 8:1 grain,
     * dark plank seams, occasional small pixel knots, and optional wrought-iron end straps.
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
        val knotRingTone = OAK_TONES[3]
        val grainPair = intArrayOf(OAK_TONES[1], OAK_TONES[2], OAK_TONES[3])

        var y = 0
        var plankIdx = 0
        while (y < h) {
            val plankH = if (showPlankSeams) {
                5 + (PixelNoise.hash(plankIdx, 0, seed) % 5) // 5..9 art px
            } else {
                h
            }
            val yEnd = min(h, y + plankH)
            val seamY = y + plankH - 1

            // Optional butt-joint seam on wider beams
            val hasButtJoint = showPlankSeams && w > 48 && (PixelNoise.hash(plankIdx, 1, seed) % 2 == 0)
            val buttJointX = if (hasButtJoint) {
                18 + (PixelNoise.hash(plankIdx, 2, seed + 13) % (w - 36).coerceAtLeast(1))
            } else {
                -10
            }

            // Optional knot every ~120 art px of plank
            val hasKnot = w >= 24 && (PixelNoise.hash(plankIdx, 3, seed + 31) % 2 == 0)
            val knotX = if (hasKnot) {
                10 + (PixelNoise.hash(plankIdx, 4, seed + 47) % (w - 20).coerceAtLeast(1))
            } else {
                -100
            }
            val knotY = y + plankH / 2

            for (py in y until yEnd) {
                for (px in 0 until w) {
                    if (showPlankSeams && (py == seamY && yEnd < h || px == buttJointX)) {
                        canvas.set(px, py, seamTone)
                        continue
                    }

                    // Check knot (small 5x3 pixel ellipse)
                    val dx = px - knotX
                    val dy = py - knotY
                    val knotDist = dx * dx + dy * dy * 4
                    if (hasKnot && knotDist <= 4) {
                        canvas.set(px, py, seamTone)
                        continue
                    } else if (hasKnot && knotDist in 5..9) {
                        canvas.set(px, py, knotRingTone)
                        continue
                    }

                    // 8:1 horizontally stretched wood grain noise
                    val stretched = PixelNoise.value(px / 8, py * 2 + plankIdx * 11, 2, seed + 59)
                    val streak = (PixelNoise.hash(px / 4, py + plankIdx * 7, seed + 83) - 128) / 3
                    val v = stretched + streak
                    val tone = if (v < 42) {
                        seamTone
                    } else {
                        PixelNoise.quantise(v, grainPair, px, py)
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

    // Temporary bridge brushes for files updated in Units 3, 4, and 6; removed once those units complete.
    private val brushCache = ConcurrentHashMap<Int, ShaderBrush>()

    internal fun stoneBrush(seed: Int = 101, darkGround: Boolean = false): ShaderBrush {
        val key = (seed * 31) xor (if (darkGround) 0x11A1 else 0x22B2)
        return brushCache.getOrPut(key) {
            val c = PixelCanvas(64, 64).also {
                if (darkGround) drawStoneGround(it, seed) else drawPanelStone(it, seed)
            }
            createRepeatedBrush(c.px, c.w, c.h)
        }
    }

    internal fun oakBrush(seed: Int = 202): ShaderBrush {
        val key = (seed * 31) xor 0x33C3
        return brushCache.getOrPut(key) {
            val c = PixelCanvas(128, 64).also { drawOakBeam(it, seed, showPlankSeams = true) }
            createRepeatedBrush(c.px, c.w, c.h)
        }
    }

    internal fun parchmentBrush(seed: Int = 303): ShaderBrush {
        val key = (seed * 31) xor 0x44D4
        return brushCache.getOrPut(key) {
            val c = PixelCanvas(64, 64).also { drawParchment(it, seed, showStainVignette = false) }
            createRepeatedBrush(c.px, c.w, c.h)
        }
    }

    internal fun leatherBrush(seed: Int = 404): ShaderBrush {
        val key = (seed * 31) xor 0x55E5
        return brushCache.getOrPut(key) {
            val c = PixelCanvas(64, 64).also { drawLeather(it, seed, stitched = false) }
            createRepeatedBrush(c.px, c.w, c.h)
        }
    }

    internal fun ironBrush(seed: Int = 505): ShaderBrush {
        val key = (seed * 31) xor 0x66F6
        return brushCache.getOrPut(key) {
            val c = PixelCanvas(64, 64).also { drawHammeredIron(it, seed, withBevel = false) }
            createRepeatedBrush(c.px, c.w, c.h)
        }
    }

    private fun createRepeatedBrush(pixels: IntArray, width: Int, height: Int): ShaderBrush {
        val bitmap: ImageBitmap = Bitmap.createBitmap(
            pixels,
            width,
            height,
            Bitmap.Config.ARGB_8888
        ).asImageBitmap()
        return ShaderBrush(ImageShader(bitmap, TileMode.Repeated, TileMode.Repeated))
    }
}

/**
 * Full-screen or container ground in coursed stone blocks rendered at integer art-pixel resolution.
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
 * Aged dark oak beam texture with horizontal planks, stretched grain, knots, and optional seams.
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
