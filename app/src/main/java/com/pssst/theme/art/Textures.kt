package com.pssst.theme.art

import android.graphics.Bitmap
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.pssst.theme.OsrsBorderDark
import com.pssst.theme.OsrsBorderLight
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
import com.pssst.theme.OsrsPanelDark
import com.pssst.theme.OsrsPanelLight
import com.pssst.theme.OsrsParchmentDark
import com.pssst.theme.OsrsParchmentStain
import com.pssst.theme.OsrsParchmentWarm
import com.pssst.theme.OsrsStoneFlecks
import com.pssst.theme.OsrsStonePoreDark
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Deterministic, tileable, cached procedural material textures for the Pssst art packet.
 *
 * Every material tile is generated once per (kind, seed) into a small seamless [ImageBitmap]
 * (64x64 or 128x64) and wrapped in a repeating [ShaderBrush]. Compatible with minSdk 24 and
 * performs zero allocations during frame rendering or recomposition.
 */
object Textures {

    private val brushCache = ConcurrentHashMap<Int, ShaderBrush>()

    /**
     * Seamless carved-stone texture brush: mineral flecks, subtle pores, and weathered grain.
     */
    fun stoneBrush(seed: Int = 101, darkGround: Boolean = false): ShaderBrush {
        val key = (seed * 31) xor (if (darkGround) 0x11A1 else 0x22B2)
        return brushCache.getOrPut(key) {
            val size = 64
            val pixels = IntArray(size * size)
            val base = if (darkGround) OsrsDarkStone else OsrsPanelDark
            val pore = OsrsStonePoreDark
            val fleck = OsrsStoneFlecks

            for (y in 0 until size) {
                for (x in 0 until size) {
                    val n1 = tileableValueNoise(x, y, size, size, periodX = 8, periodY = 8, seed = seed)
                    val n2 = tileableValueNoise(x, y, size, size, periodX = 16, periodY = 16, seed = seed + 17)
                    val h = hash2D(x, y, seed + 53)
                    val baseMix = (n1 * 0.65f + n2 * 0.35f) // 0..1

                    var color = lerpColor(pore, base, 0.55f + baseMix * 0.45f)
                    if (h > 0.91f) {
                        // Warm mineral fleck
                        color = lerpColor(color, fleck, 0.32f)
                    } else if (h < 0.09f) {
                        // Tiny chisel pore
                        color = lerpColor(color, pore, 0.42f)
                    }
                    pixels[y * size + x] = color.toArgb()
                }
            }
            createRepeatedBrush(pixels, size, size)
        }
    }

    /**
     * Seamless aged dark oak wood-grain brush: horizontal cathedral heartwood grain and planed ridges.
     */
    fun oakBrush(seed: Int = 202): ShaderBrush {
        val key = (seed * 31) xor 0x33C3
        return brushCache.getOrPut(key) {
            val width = 128
            val height = 64
            val pixels = IntArray(width * height)

            for (y in 0 until height) {
                for (x in 0 until width) {
                    // Stretched horizontal noise + ring wave
                    val coarse = tileableValueNoise(x, y, width, height, periodX = 4, periodY = 16, seed = seed)
                    val fine = tileableValueNoise(x, y, width, height, periodX = 16, periodY = 32, seed = seed + 29)
                    val ringArg = (y.toFloat() / height.toFloat()) * 6f * Math.PI.toFloat() + coarse * 4.2f
                    val ring = (sin(ringArg) * 0.5f + 0.5f)
                    val fiber = hash2D(x / 2, y, seed + 71) * 0.14f
                    val t = (ring * 0.55f + fine * 0.31f + fiber).coerceIn(0f, 1f)

                    val color = if (t < 0.45f) {
                        lerpColor(OsrsOakGrainDark, OsrsOakBase, t / 0.45f)
                    } else {
                        lerpColor(OsrsOakBase, OsrsOakGrainLight, (t - 0.45f) / 0.55f * 0.65f)
                    }
                    pixels[y * width + x] = color.toArgb()
                }
            }
            createRepeatedBrush(pixels, width, height)
        }
    }

    /**
     * Seamless dark-mode weathered parchment brush: cross-woven linen fibers and subtle vellum mottling.
     * Keeps high contrast (>9:1 with OsrsTextPrimary, >5:1 with OsrsTextMuted).
     */
    fun parchmentBrush(seed: Int = 303): ShaderBrush {
        val key = (seed * 31) xor 0x44D4
        return brushCache.getOrPut(key) {
            val size = 64
            val pixels = IntArray(size * size)

            for (y in 0 until size) {
                for (x in 0 until size) {
                    val mottle = tileableValueNoise(x, y, size, size, periodX = 6, periodY = 6, seed = seed)
                    // Subtle linen cross-weave
                    val warp = sin((x.toFloat() / size) * 32f * Math.PI.toFloat()) * 0.5f + 0.5f
                    val weft = cos((y.toFloat() / size) * 32f * Math.PI.toFloat()) * 0.5f + 0.5f
                    val weave = (warp * 0.5f + weft * 0.5f) * 0.16f
                    val speck = if (hash2D(x, y, seed + 99) > 0.95f) -0.10f else 0f
                    val t = (mottle * 0.84f + weave + speck).coerceIn(0f, 1f)

                    val color = if (t < 0.5f) {
                        lerpColor(OsrsParchmentStain, OsrsParchmentDark, 0.45f + t * 1.1f)
                    } else {
                        lerpColor(OsrsParchmentDark, OsrsParchmentWarm, (t - 0.5f) * 0.9f)
                    }
                    pixels[y * size + x] = color.toArgb()
                }
            }
            createRepeatedBrush(pixels, size, size)
        }
    }

    /**
     * Seamless worn binding leather brush: cellular pebble-grain hide with warm worn crowns.
     */
    fun leatherBrush(seed: Int = 404): ShaderBrush {
        val key = (seed * 31) xor 0x55E5
        return brushCache.getOrPut(key) {
            val size = 64
            val pixels = IntArray(size * size)
            val grid = 8
            val cellSize = size / grid

            for (y in 0 until size) {
                for (x in 0 until size) {
                    // Tileable cellular Voronoi distance for pebble hide
                    val gx = x / cellSize
                    val gy = y / cellSize
                    var minDist = 999f
                    for (dy in -1..1) {
                        for (dx in -1..1) {
                            val nx = (gx + dx + grid) % grid
                            val ny = (gy + dy + grid) % grid
                            val px = (gx + dx) * cellSize + hash2D(nx, ny, seed) * cellSize * 0.8f
                            val py = (gy + dy) * cellSize + hash2D(nx, ny, seed + 13) * cellSize * 0.8f
                            val ddx = (x - px) / cellSize
                            val ddy = (y - py) / cellSize
                            val dist = sqrt(ddx * ddx + ddy * ddy)
                            if (dist < minDist) minDist = dist
                        }
                    }
                    // Pebble crown is low distance, crease is high distance
                    val crown = (1f - (minDist / 0.85f).coerceIn(0f, 1f))
                    val color = when {
                        crown < 0.25f -> lerpColor(OsrsLeatherCrease, OsrsLeatherBase, crown / 0.25f)
                        else -> lerpColor(OsrsLeatherBase, OsrsLeatherHighlight, (crown - 0.25f) * 0.55f)
                    }
                    pixels[y * size + x] = color.toArgb()
                }
            }
            createRepeatedBrush(pixels, size, size)
        }
    }

    /**
     * Seamless hammered wrought-iron brush: shallow forge-hammer dimples on dark iron.
     */
    fun ironBrush(seed: Int = 505): ShaderBrush {
        val key = (seed * 31) xor 0x66F6
        return brushCache.getOrPut(key) {
            val size = 64
            val pixels = IntArray(size * size)

            for (y in 0 until size) {
                for (x in 0 until size) {
                    val n = tileableValueNoise(x, y, size, size, periodX = 8, periodY = 8, seed = seed)
                    val micro = hash2D(x, y, seed + 37) * 0.12f
                    val t = (n * 0.88f + micro).coerceIn(0f, 1f)
                    val color = if (t < 0.5f) {
                        lerpColor(OsrsIronEdgeDark, OsrsIronBody, 0.5f + t)
                    } else {
                        lerpColor(OsrsIronBody, OsrsIronEdgeLight, (t - 0.5f) * 0.55f)
                    }
                    pixels[y * size + x] = color.toArgb()
                }
            }
            createRepeatedBrush(pixels, size, size)
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

    private fun hash2D(x: Int, y: Int, seed: Int): Float {
        var h = x * 374761393 + y * 668265263 + seed * 1442695041
        h = (h xor (h ushr 13)) * 1274126177
        h = h xor (h ushr 16)
        return (h and 0x7FFFFFFF).toFloat() / Int.MAX_VALUE.toFloat()
    }

    private fun tileableValueNoise(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        periodX: Int,
        periodY: Int,
        seed: Int
    ): Float {
        val fx = (x.toFloat() / width.toFloat()) * periodX
        val fy = (y.toFloat() / height.toFloat()) * periodY
        val ix0 = fx.toInt() % periodX
        val iy0 = fy.toInt() % periodY
        val ix1 = (ix0 + 1) % periodX
        val iy1 = (iy0 + 1) % periodY
        val tx = smoothStep(fx - fx.toInt())
        val ty = smoothStep(fy - fy.toInt())

        val v00 = hash2D(ix0, iy0, seed)
        val v10 = hash2D(ix1, iy0, seed)
        val v01 = hash2D(ix0, iy1, seed)
        val v11 = hash2D(ix1, iy1, seed)

        val nx0 = v00 + (v10 - v00) * tx
        val nx1 = v01 + (v11 - v01) * tx
        return nx0 + (nx1 - nx0) * ty
    }

    private fun smoothStep(t: Float): Float = t * t * (3f - 2f * t)

    internal fun lerpColor(a: Color, b: Color, t: Float): Color {
        val clamped = t.coerceIn(0f, 1f)
        return Color(
            red = a.red + (b.red - a.red) * clamped,
            green = a.green + (b.green - a.green) * clamped,
            blue = a.blue + (b.blue - a.blue) * clamped,
            alpha = a.alpha + (b.alpha - a.alpha) * clamped
        )
    }
}

/**
 * Full-screen or container ground in dark carved stone with subtle top-down lantern falloff.
 */
fun Modifier.stoneGround(seed: Int = 101): Modifier = this.drawWithCache {
    val brush = Textures.stoneBrush(seed = seed, darkGround = true)
    val vignette = Brush.verticalGradient(
        colors = listOf(
            OsrsPanelLight.copy(alpha = 0.08f),
            Color.Transparent,
            OsrsBorderDark.copy(alpha = 0.25f)
        )
    )
    onDrawBehind {
        drawRect(color = OsrsDarkStone)
        drawRect(brush = brush, alpha = 0.85f)
        drawRect(brush = vignette)
    }
}

/**
 * Carved stone panel interior texture with subtle top-lit sheen.
 */
fun Modifier.carvedStone(seed: Int = 101): Modifier = this.drawWithCache {
    val stone = Textures.stoneBrush(seed = seed, darkGround = false)
    val sheen = Brush.verticalGradient(
        colors = listOf(
            OsrsPanelLight.copy(alpha = 0.35f),
            Color.Transparent,
            OsrsBorderDark.copy(alpha = 0.28f)
        )
    )
    onDrawBehind {
        drawRect(color = OsrsPanelDark)
        drawRect(brush = stone, alpha = 0.75f)
        drawRect(brush = sheen)
    }
}

/**
 * Aged dark oak beam texture with optional horizontal plank seams.
 */
fun Modifier.oakBeam(seed: Int = 202, showPlankSeams: Boolean = true): Modifier = this.drawWithCache {
    val oak = Textures.oakBrush(seed = seed)
    val sheen = Brush.verticalGradient(
        colors = listOf(
            OsrsOakGrainLight.copy(alpha = 0.28f),
            Color.Transparent,
            OsrsOakGrainDark.copy(alpha = 0.45f)
        )
    )
    val strokePx = 1.dp.toPx()
    onDrawBehind {
        drawRect(color = OsrsOakBase)
        drawRect(brush = oak, alpha = 0.9f)
        drawRect(brush = sheen)
        if (showPlankSeams && size.height > 28.dp.toPx()) {
            val seamY = size.height * 0.5f
            drawLine(
                color = OsrsOakGrainDark.copy(alpha = 0.65f),
                start = Offset(0f, seamY),
                end = Offset(size.width, seamY),
                strokeWidth = strokePx
            )
            drawLine(
                color = OsrsOakGrainLight.copy(alpha = 0.35f),
                start = Offset(0f, seamY + strokePx),
                end = Offset(size.width, seamY + strokePx),
                strokeWidth = strokePx
            )
        }
    }
}

/**
 * Dark-mode weathered parchment surface with optional radial edge stain vignette.
 * Keeps contrast >= 9:1 for primary text and >= 5:1 for muted text.
 */
fun Modifier.parchment(seed: Int = 303, showStainVignette: Boolean = true): Modifier = this.drawWithCache {
    val vellum = Textures.parchmentBrush(seed = seed)
    val maxDim = maxOf(size.width, size.height, 1f)
    val edgeStain = Brush.radialGradient(
        colors = listOf(
            Color.Transparent,
            OsrsParchmentStain.copy(alpha = 0.45f)
        ),
        center = Offset(size.width * 0.5f, size.height * 0.5f),
        radius = maxDim * 0.72f
    )
    onDrawBehind {
        drawRect(color = OsrsParchmentDark)
        drawRect(brush = vellum, alpha = 0.8f)
        if (showStainVignette) {
            drawRect(brush = edgeStain)
        }
    }
}

/**
 * Worn oxblood-brown binding leather surface with optional inset waxed-thread saddle stitching.
 */
fun Modifier.leather(seed: Int = 404, stitched: Boolean = true): Modifier = this.drawWithCache {
    val hide = Textures.leatherBrush(seed = seed)
    val sheen = Brush.verticalGradient(
        colors = listOf(
            OsrsLeatherHighlight.copy(alpha = 0.25f),
            Color.Transparent,
            OsrsLeatherCrease.copy(alpha = 0.45f)
        )
    )
    val inset = 4.dp.toPx()
    val stitchStroke = Stroke(
        width = 1.dp.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 3.dp.toPx()), 0f)
    )
    onDrawBehind {
        drawRect(color = OsrsLeatherBase)
        drawRect(brush = hide, alpha = 0.85f)
        drawRect(brush = sheen)
        if (stitched && size.width > inset * 3 && size.height > inset * 3) {
            // Recessed stitch channel
            drawRect(
                color = OsrsLeatherCrease,
                topLeft = Offset(inset, inset),
                size = Size(size.width - inset * 2, size.height - inset * 2),
                style = Stroke(width = 1.5.dp.toPx())
            )
            // Waxed thread dashes
            drawRect(
                color = OsrsLeatherStitch.copy(alpha = 0.75f),
                topLeft = Offset(inset, inset),
                size = Size(size.width - inset * 2, size.height - inset * 2),
                style = stitchStroke
            )
        }
    }
}

/**
 * Hammered wrought-iron surface with top/left bevel glint.
 */
fun Modifier.hammeredIron(seed: Int = 505): Modifier = this.drawWithCache {
    val iron = Textures.ironBrush(seed = seed)
    val strokePx = 1.dp.toPx()
    onDrawBehind {
        drawRect(color = OsrsIronBody)
        drawRect(brush = iron, alpha = 0.9f)
        drawLine(OsrsIronEdgeLight, Offset(0f, 0f), Offset(size.width, 0f), strokePx)
        drawLine(OsrsIronEdgeLight, Offset(0f, 0f), Offset(0f, size.height), strokePx)
        drawLine(OsrsIronEdgeDark, Offset(0f, size.height - strokePx), Offset(size.width, size.height - strokePx), strokePx)
        drawLine(OsrsIronEdgeDark, Offset(size.width - strokePx, 0f), Offset(size.width - strokePx, size.height), strokePx)
    }
}
