package com.pssst.theme.art

import androidx.compose.ui.graphics.toArgb
import com.pssst.theme.OsrsBorderLight
import com.pssst.theme.OsrsBorderOutline
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsGoldDark
import com.pssst.theme.OsrsGoldHighlight
import com.pssst.theme.OsrsHerbDark
import com.pssst.theme.OsrsHerbGreen
import com.pssst.theme.OsrsHerbLight
import com.pssst.theme.OsrsIronBody
import com.pssst.theme.OsrsIronEdgeDark
import com.pssst.theme.OsrsIronEdgeLight
import com.pssst.theme.OsrsLeatherBase
import com.pssst.theme.OsrsLeatherCrease
import com.pssst.theme.OsrsLeatherHighlight
import com.pssst.theme.OsrsOuterFrame
import com.pssst.theme.OsrsRed
import com.pssst.theme.OsrsRedDark
import com.pssst.theme.OsrsRivetDark
import com.pssst.theme.OsrsRivetLight
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.OsrsTextPrimary
import com.pssst.theme.OsrsWoodBase
import com.pssst.theme.OsrsWoodDark
import com.pssst.theme.OsrsWoodLight

/**
 * Tone palette mapping single-character sprite grid codes to integer ARGB colours.
 *
 * Grid role legend:
 * - `'o'` = 1-art-px dark outline ([outline])
 * - `'h'` = top-left lit highlight ([highlight])
 * - `'b'` = main body fill ([base])
 * - `'s'` = bottom-right shaded fill ([shadow])
 * - `'a'` = secondary accent highlight/fill ([accent])
 * - `'d'` = secondary accent shadow ([accentDark])
 * - `'.'` = transparent (0)
 */
data class SpriteTones(
    val outline: Int = OsrsOuterFrame.toArgb(),
    val highlight: Int,
    val base: Int,
    val shadow: Int,
    val accent: Int = OsrsIronEdgeLight.toArgb(),
    val accentDark: Int = OsrsIronBody.toArgb()
) {
    fun colorFor(role: Char): Int = when (role) {
        'o' -> outline
        'h' -> highlight
        'b' -> base
        's' -> shadow
        'a' -> accent
        'd' -> accentDark
        '.', ' ' -> 0
        else -> throw IllegalArgumentException("Unknown sprite role character: '$role'")
    }

    companion object {
        val GOLD = SpriteTones(
            outline = OsrsOuterFrame.toArgb(),
            highlight = OsrsGoldHighlight.toArgb(),
            base = OsrsGold.toArgb(),
            shadow = OsrsGoldDark.toArgb(),
            accent = OsrsIronEdgeLight.toArgb(),
            accentDark = OsrsIronBody.toArgb()
        )

        val STONE = SpriteTones(
            outline = OsrsOuterFrame.toArgb(),
            highlight = OsrsRivetLight.toArgb(),
            base = OsrsBorderLight.toArgb(),
            shadow = OsrsBorderOutline.toArgb(),
            accent = OsrsIronEdgeLight.toArgb(),
            accentDark = OsrsIronBody.toArgb()
        )

        val HERB = SpriteTones(
            outline = OsrsOuterFrame.toArgb(),
            highlight = OsrsHerbLight.toArgb(),
            base = OsrsHerbGreen.toArgb(),
            shadow = OsrsHerbDark.toArgb(),
            accent = OsrsWoodLight.toArgb(),
            accentDark = OsrsWoodBase.toArgb()
        )

        val WOOD = SpriteTones(
            outline = OsrsOuterFrame.toArgb(),
            highlight = OsrsWoodLight.toArgb(),
            base = OsrsWoodBase.toArgb(),
            shadow = OsrsWoodDark.toArgb(),
            accent = OsrsIronEdgeLight.toArgb(),
            accentDark = OsrsIronBody.toArgb()
        )

        val PARCHMENT = SpriteTones(
            outline = OsrsOuterFrame.toArgb(),
            highlight = OsrsTextPrimary.toArgb(),
            base = OsrsTextMuted.toArgb(),
            shadow = OsrsBorderLight.toArgb(),
            accent = OsrsGold.toArgb(),
            accentDark = OsrsGoldDark.toArgb()
        )

        val WARNING = SpriteTones(
            outline = OsrsOuterFrame.toArgb(),
            highlight = OsrsRed.toArgb(),
            base = OsrsRed.toArgb(),
            shadow = OsrsRedDark.toArgb(),
            accent = OsrsTextPrimary.toArgb(),
            accentDark = OsrsGold.toArgb()
        )

        val LEATHER = SpriteTones(
            outline = OsrsOuterFrame.toArgb(),
            highlight = OsrsLeatherHighlight.toArgb(),
            base = OsrsLeatherBase.toArgb(),
            shadow = OsrsLeatherCrease.toArgb(),
            accent = OsrsGold.toArgb(),
            accentDark = OsrsGoldDark.toArgb()
        )
    }
}

/**
 * Low-resolution integer pixel sprite defined as rows of character role codes.
 */
class Sprite(
    val rows: List<String>,
    val defaultNaturalTones: SpriteTones? = null
) {
    val height: Int = rows.size
    val width: Int = rows.firstOrNull()?.length ?: 0

    constructor(
        vararg rows: String,
        naturalTones: SpriteTones? = null
    ) : this(rows.toList(), naturalTones)

    init {
        require(rows.isNotEmpty()) { "Sprite must have at least one row" }
        require(width > 0) { "Sprite rows must not be empty" }
        rows.forEachIndexed { idx, row ->
            require(row.length == width) {
                "Sprite row $idx has length ${row.length}, expected $width"
            }
        }
    }
}

/**
 * Pure Kotlin integer pixel buffer for low-resolution OSRS-style interface art.
 *
 * Every coordinate is in whole art pixels. Antialiasing does not exist in [PixelCanvas]:
 * lines are integer spans, curves are midpoint pixel circles, and 45-degree corners are
 * stepped pixel staircases.
 */
class PixelCanvas(val w: Int, val h: Int) {
    val px: IntArray = IntArray(w * h)

    fun get(x: Int, y: Int): Int {
        if (x < 0 || x >= w || y < 0 || y >= h) return 0
        return px[y * w + x]
    }

    fun set(x: Int, y: Int, argb: Int) {
        if (x < 0 || x >= w || y < 0 || y >= h) return
        px[y * w + x] = argb
    }

    fun clear(argb: Int) {
        px.fill(argb)
    }

    fun fill(x: Int, y: Int, fw: Int, fh: Int, argb: Int) {
        if (fw <= 0 || fh <= 0) return
        val x0 = x.coerceIn(0, w)
        val y0 = y.coerceIn(0, h)
        val x1 = (x + fw).coerceIn(0, w)
        val y1 = (y + fh).coerceIn(0, h)
        for (py in y0 until y1) {
            val rowOffset = py * w
            for (pxIdx in x0 until x1) {
                px[rowOffset + pxIdx] = argb
            }
        }
    }

    fun hLine(x: Int, y: Int, len: Int, argb: Int) {
        if (y < 0 || y >= h || len <= 0) return
        val x0 = x.coerceIn(0, w)
        val x1 = (x + len).coerceIn(0, w)
        val rowOffset = y * w
        for (pxIdx in x0 until x1) {
            px[rowOffset + pxIdx] = argb
        }
    }

    fun vLine(x: Int, y: Int, len: Int, argb: Int) {
        if (x < 0 || x >= w || len <= 0) return
        val y0 = y.coerceIn(0, h)
        val y1 = (y + len).coerceIn(0, h)
        for (py in y0 until y1) {
            px[py * w + x] = argb
        }
    }

    fun rectOutline(x: Int, y: Int, rw: Int, rh: Int, argb: Int) {
        if (rw <= 0 || rh <= 0) return
        hLine(x, y, rw, argb)
        hLine(x, y + rh - 1, rw, argb)
        vLine(x, y, rh, argb)
        vLine(x + rw - 1, y, rh, argb)
    }

    /**
     * Draws a 1-art-px outlined and 1-art-px bevelled box lit from the top-left (when [raised] is true)
     * or recessed into the surface (when [raised] is false).
     */
    fun bevelBox(
        x: Int,
        y: Int,
        bw: Int,
        bh: Int,
        outline: Int = OsrsOuterFrame.toArgb(),
        light: Int = OsrsBorderLight.toArgb(),
        dark: Int = OsrsBorderOutline.toArgb(),
        fill: Int? = null,
        raised: Boolean = true
    ) {
        if (bw <= 0 || bh <= 0) return
        if (fill != null && bw > 4 && bh > 4) {
            fill(x + 2, y + 2, bw - 4, bh - 4, fill)
        }
        rectOutline(x, y, bw, bh, outline)
        if (bw >= 3 && bh >= 3) {
            val topLeftTone = if (raised) light else dark
            val bottomRightTone = if (raised) dark else light
            // Bottom and right bevel lines first
            hLine(x + 1, y + bh - 2, bw - 2, bottomRightTone)
            vLine(x + bw - 2, y + 1, bh - 2, bottomRightTone)
            // Top and left bevel lines win at top-left corner
            hLine(x + 1, y + 1, bw - 3, topLeftTone)
            vLine(x + 1, y + 1, bh - 3, topLeftTone)
        }
    }

    /**
     * Cuts a 45-degree corner as an integer pixel staircase of [steps] art pixels.
     *
     * @param cornerIndex 0 = top-left, 1 = top-right, 2 = bottom-right, 3 = bottom-left
     */
    fun stairCorner(
        x: Int,
        y: Int,
        steps: Int,
        cornerIndex: Int,
        outline: Int = OsrsOuterFrame.toArgb(),
        bevelTone: Int = OsrsBorderLight.toArgb(),
        exteriorFill: Int = 0
    ) {
        if (steps <= 0) return
        for (i in 0 until steps) {
            val span = steps - i
            for (j in 0 until span) {
                val (cx, cy) = when (cornerIndex) {
                    0 -> (x + j) to (y + i)
                    1 -> (x - j) to (y + i)
                    2 -> (x - j) to (y - i)
                    else -> (x + j) to (y - i)
                }
                set(cx, cy, exteriorFill)
            }
            val (ox, oy) = when (cornerIndex) {
                0 -> (x + span) to (y + i)
                1 -> (x - span) to (y + i)
                2 -> (x - span) to (y - i)
                else -> (x + span) to (y - i)
            }
            set(ox, oy, outline)
            val (bx, by) = when (cornerIndex) {
                0 -> (ox + 1) to oy
                1 -> (ox - 1) to oy
                2 -> (ox - 1) to oy
                else -> (ox + 1) to oy
            }
            set(bx, by, bevelTone)
        }
    }

    /**
     * Midpoint integer circle algorithm with 8-way octant symmetry.
     */
    fun circle(cx: Int, cy: Int, r: Int, argb: Int, filled: Boolean = false) {
        if (r < 0) return
        if (r == 0) {
            set(cx, cy, argb)
            return
        }
        var x = r
        var y = 0
        var err = 1 - r
        while (x >= y) {
            if (filled) {
                hLine(cx - x, cy + y, 2 * x + 1, argb)
                hLine(cx - x, cy - y, 2 * x + 1, argb)
                hLine(cx - y, cy + x, 2 * y + 1, argb)
                hLine(cx - y, cy - x, 2 * y + 1, argb)
            } else {
                set(cx + x, cy + y, argb)
                set(cx + y, cy + x, argb)
                set(cx - y, cy + x, argb)
                set(cx - x, cy + y, argb)
                set(cx - x, cy - y, argb)
                set(cx - y, cy - x, argb)
                set(cx + y, cy - x, argb)
                set(cx + x, cy - y, argb)
            }
            y++
            if (err < 0) {
                err += 2 * y + 1
            } else {
                x--
                err += 2 * (y - x) + 1
            }
        }
    }

    /**
     * Draws a seated pixel rivet (3x3 or 4x4 art pixels) with 1-px top-left highlight and
     * 1-px bottom-right shadow.
     */
    fun rivet(x: Int, y: Int, size: Int = 3) {
        val outline = OsrsIronEdgeDark.toArgb()
        val body = OsrsIronBody.toArgb()
        val light = OsrsRivetLight.toArgb()
        val dark = OsrsRivetDark.toArgb()
        if (size <= 3) {
            // 3x3 pixel stud
            fill(x, y, 3, 3, outline)
            set(x + 1, y, body)
            set(x, y + 1, body)
            set(x + 1, y + 1, light)
            set(x + 2, y + 1, dark)
            set(x + 1, y + 2, dark)
        } else {
            // 4x4 heavy iron rivet
            fill(x, y, 4, 4, outline)
            fill(x + 1, y + 1, 2, 2, body)
            set(x + 1, y + 1, light)
            set(x + 2, y + 1, body)
            set(x + 1, y + 2, body)
            set(x + 2, y + 2, dark)
        }
    }

    /**
     * Blits a [Sprite] onto this canvas at ([x], [y]) using [tones]. Transparent (`'.'`) cells are skipped.
     */
    fun blit(
        sprite: Sprite,
        tones: SpriteTones = sprite.defaultNaturalTones ?: SpriteTones.GOLD,
        x: Int = 0,
        y: Int = 0
    ) {
        for (sy in 0 until sprite.height) {
            val row = sprite.rows[sy]
            for (sx in 0 until sprite.width) {
                val c = tones.colorFor(row[sx])
                if (c != 0) {
                    set(x + sx, y + sy, c)
                }
            }
        }
    }
}

/**
 * Deterministic fixed-point integer noise and ordered Bayer dithering.
 *
 * Uses 32-bit integer arithmetic compatible with JavaScript's `Math.imul` so the HTML preview sheet
 * produces bit-identical textures.
 */
object PixelNoise {
    private val BAYER_4X4 = intArrayOf(
        0, 8, 2, 10,
        12, 4, 14, 6,
        3, 11, 1, 9,
        15, 7, 13, 5
    )

    /**
     * Returns a deterministic integer in `0..255` for coordinates ([x], [y]) and [seed].
     */
    fun hash(x: Int, y: Int, seed: Int): Int {
        var h = seed xor (x * 0x27D4EB2D) xor (y * 0x165667B1)
        h = (h xor (h ushr 15)) * -0x7A143595 // 0x85EBCA6B in 32-bit two's complement
        h = (h xor (h ushr 13)) * -0x3D4D51CB // 0xC2B2AE35 in 32-bit two's complement
        return (h xor (h ushr 16)) and 0xFF
    }

    /**
     * Bilinearly interpolated integer value noise in `0..255` with grid spacing [cell] art pixels.
     */
    fun value(x: Int, y: Int, cell: Int, seed: Int): Int {
        if (cell <= 1) return hash(x, y, seed)
        val gx = Math.floorDiv(x, cell)
        val gy = Math.floorDiv(y, cell)
        val fx = Math.floorMod(x, cell)
        val fy = Math.floorMod(y, cell)

        val v00 = hash(gx, gy, seed)
        val v10 = hash(gx + 1, gy, seed)
        val v01 = hash(gx, gy + 1, seed)
        val v11 = hash(gx + 1, gy + 1, seed)

        val top = (v00 * (cell - fx) + v10 * fx) / cell
        val bot = (v01 * (cell - fx) + v11 * fx) / cell
        return ((top * (cell - fy) + bot * fy) / cell).coerceIn(0, 255)
    }

    /**
     * Cellular (Worley) pebble-grain noise in `0..255`.
     * Returns higher values near pebble crowns and lower values in the crease between cells.
     */
    fun cellular(x: Int, y: Int, cell: Int, seed: Int): Int {
        val c = cell.coerceAtLeast(2)
        val gx = Math.floorDiv(x, c)
        val gy = Math.floorDiv(y, c)
        var minDistSq = c * c * 2
        for (dy in -1..1) {
            for (dx in -1..1) {
                val nx = gx + dx
                val ny = gy + dy
                val jx = (hash(nx, ny, seed) * c) shr 8
                val jy = (hash(nx, ny, seed xor 0x5A5A) * c) shr 8
                val px = nx * c + jx
                val py = ny * c + jy
                val ddx = x - px
                val ddy = y - py
                val distSq = ddx * ddx + ddy * ddy
                if (distSq < minDistSq) {
                    minDistSq = distSq
                }
            }
        }
        val maxDistSq = (c * c).coerceAtLeast(1)
        val inv = 255 - ((minDistSq * 255) / maxDistSq).coerceIn(0, 255)
        return inv.coerceIn(0, 255)
    }

    /**
     * Returns the 4x4 Bayer threshold in `0..15` for art pixel ([x], [y]).
     */
    fun bayer4(x: Int, y: Int): Int {
        val bx = x and 3
        val by = y and 3
        return BAYER_4X4[(by shl 2) or bx]
    }

    /**
     * Quantises [v255] (`0..255`) into the ordered [tones] ramp (darkest to lightest) using
     * 4x4 Bayer ordered dithering between adjacent tones.
     *
     * Every returned integer is guaranteed to be a member of [tones].
     */
    fun quantise(v255: Int, tones: IntArray, x: Int, y: Int): Int {
        require(tones.isNotEmpty()) { "tones must not be empty" }
        if (tones.size == 1) return tones[0]
        val clamped = v255.coerceIn(0, 255)
        val segments = tones.size - 1
        val scaled = clamped * segments
        val idx = (scaled / 255).coerceAtMost(segments - 1)
        val rem = scaled - idx * 255
        val threshold = bayer4(x, y) * 16 + 8
        val pick = if (rem > threshold) idx + 1 else idx
        return tones[pick.coerceIn(0, segments)]
    }
}
