package com.pssst.theme.art

import android.graphics.Bitmap
import android.util.LruCache
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.pssst.theme.OsrsBorderDark
import com.pssst.theme.OsrsBorderLight
import com.pssst.theme.OsrsOuterFrame
import com.pssst.theme.OsrsPanelDark
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Computes the integer device-pixel scale for one art pixel (`artPx = max(2, round(density))`).
 *
 * At 1.0x–2.0x density this yields 2px; at 2.625x–3.0x (typical modern phones) 3px; at >=3.5x 4px.
 * Because `artPx` tracks `round(density)`, 1 art pixel stays approximately 1dp across devices while
 * always remaining at least 2x2 physical pixels so pixel steps are visibly crisp.
 */
fun computeArtPx(density: Float): Int = max(2, density.roundToInt())

val LocalArtPx = compositionLocalOf { 3 }

internal data class PixelCacheKey(
    val key: Any,
    val artWidth: Int,
    val artHeight: Int
)

internal val pixelBitmapCache = object : LruCache<PixelCacheKey, ImageBitmap>(16 * 1024 * 1024) {
    override fun sizeOf(key: PixelCacheKey, value: ImageBitmap): Int {
        return (value.width * value.height * 4).coerceAtLeast(4)
    }
}

/**
 * Converts this [PixelCanvas] into an Android [ImageBitmap] without antialiasing or filtering.
 */
fun PixelCanvas.toImageBitmap(): ImageBitmap {
    return Bitmap.createBitmap(px, w, h, Bitmap.Config.ARGB_8888).asImageBitmap()
}

/**
 * Renders low-resolution procedural pixel art into a cached [ImageBitmap] at `ceil(size / artPx)`
 * and scales it onto the composable with [FilterQuality.None] so every art pixel is a crisp square.
 */
fun Modifier.pixelArt(
    key: Any,
    draw: PixelCanvas.() -> Unit
): Modifier = this.drawWithCache {
    val artPx = computeArtPx(density)
    val aw = ceil(size.width / artPx.toFloat()).toInt().coerceAtLeast(1)
    val ah = ceil(size.height / artPx.toFloat()).toInt().coerceAtLeast(1)
    val cacheKey = PixelCacheKey(key, aw, ah)
    val bitmap = synchronized(pixelBitmapCache) {
        pixelBitmapCache.get(cacheKey) ?: PixelCanvas(aw, ah).apply(draw).toImageBitmap().also {
            pixelBitmapCache.put(cacheKey, it)
        }
    }
    val destW = aw * artPx
    val destH = ah * artPx
    val offsetX = -((destW - size.width.roundToInt()).coerceAtLeast(0) / 2)
    val offsetY = -((destH - size.height.roundToInt()).coerceAtLeast(0) / 2)
    onDrawBehind {
        if (size.width > 0f && size.height > 0f) {
            drawImage(
                image = bitmap,
                dstOffset = IntOffset(offsetX, offsetY),
                dstSize = IntSize(destW, destH),
                filterQuality = FilterQuality.None
            )
        }
    }
}

/**
 * Lightweight integer-aligned bevel box for dense lists and item slots (no bitmap allocation).
 * Every edge is a whole multiple of `artPx` device pixels.
 */
fun Modifier.pixelBevelBox(
    fill: Color = OsrsPanelDark,
    outline: Color = OsrsOuterFrame,
    light: Color = OsrsBorderLight,
    dark: Color = OsrsBorderDark,
    raised: Boolean = true
): Modifier = this.drawWithCache {
    val p = computeArtPx(density).toFloat()
    val w = size.width.roundToInt().toFloat()
    val h = size.height.roundToInt().toFloat()
    val topLeftTone = if (raised) light else dark
    val bottomRightTone = if (raised) dark else light
    onDrawBehind {
        if (w <= 0f || h <= 0f) return@onDrawBehind
        // Outer outline fill + interior fill
        drawRect(color = outline, topLeft = Offset.Zero, size = Size(w, h))
        if (w > 2 * p && h > 2 * p) {
            // Bevel ring
            drawRect(color = bottomRightTone, topLeft = Offset(p, p), size = Size(w - 2 * p, h - 2 * p))
            drawRect(color = topLeftTone, topLeft = Offset(p, p), size = Size(w - 3 * p, p))
            drawRect(color = topLeftTone, topLeft = Offset(p, p), size = Size(p, h - 3 * p))
        }
        if (w > 4 * p && h > 4 * p) {
            drawRect(color = fill, topLeft = Offset(2 * p, 2 * p), size = Size(w - 4 * p, h - 4 * p))
        }
    }
}

/**
 * Renders a 16x16 (or custom grid) [Sprite] at an exact integer multiple of `artPx` with [FilterQuality.None].
 */
@Composable
fun PixelIcon(
    sprite: Sprite,
    tones: SpriteTones = sprite.defaultNaturalTones ?: SpriteTones.GOLD,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val semanticsModifier = if (contentDescription != null) {
        Modifier.semantics {
            this.contentDescription = contentDescription
            this.role = Role.Image
        }
    } else {
        Modifier
    }
    Box(
        modifier = modifier
            .size(sprite.width.dp, sprite.height.dp)
            .then(semanticsModifier)
            .drawWithCache {
                val artPx = computeArtPx(density)
                val cacheKey = PixelCacheKey(
                    key = Triple("sprite", sprite.rows, tones),
                    artWidth = sprite.width,
                    artHeight = sprite.height
                )
                val bitmap = synchronized(pixelBitmapCache) {
                    pixelBitmapCache.get(cacheKey) ?: PixelCanvas(sprite.width, sprite.height).apply {
                        blit(sprite, tones, 0, 0)
                    }.toImageBitmap().also {
                        pixelBitmapCache.put(cacheKey, it)
                    }
                }
                val dstW = sprite.width * artPx
                val dstH = sprite.height * artPx
                val ox = ((size.width.roundToInt() - dstW) / 2)
                val oy = ((size.height.roundToInt() - dstH) / 2)
                onDrawBehind {
                    drawImage(
                        image = bitmap,
                        dstOffset = IntOffset(ox, oy),
                        dstSize = IntSize(dstW, dstH),
                        filterQuality = FilterQuality.None
                    )
                }
            }
    )
}

/**
 * Renders a low-resolution empty-state [Sprite] scene at an integer multiple of `artPx` with [FilterQuality.None].
 */
@Composable
fun PixelScene(
    scene: Sprite,
    tones: SpriteTones = scene.defaultNaturalTones ?: SpriteTones.STONE,
    scaleMultiplier: Int = 2,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val mult = scaleMultiplier.coerceAtLeast(1)
    val semanticsModifier = if (contentDescription != null) {
        Modifier.semantics {
            this.contentDescription = contentDescription
            this.role = Role.Image
        }
    } else {
        Modifier
    }
    Box(
        modifier = modifier
            .size((scene.width * mult).dp, (scene.height * mult).dp)
            .then(semanticsModifier)
            .drawWithCache {
                val artPx = computeArtPx(density) * mult
                val cacheKey = PixelCacheKey(
                    key = Triple("scene", scene.rows, tones),
                    artWidth = scene.width,
                    artHeight = scene.height
                )
                val bitmap = synchronized(pixelBitmapCache) {
                    pixelBitmapCache.get(cacheKey) ?: PixelCanvas(scene.width, scene.height).apply {
                        blit(scene, tones, 0, 0)
                    }.toImageBitmap().also {
                        pixelBitmapCache.put(cacheKey, it)
                    }
                }
                val dstW = scene.width * artPx
                val dstH = scene.height * artPx
                val ox = ((size.width.roundToInt() - dstW) / 2)
                val oy = ((size.height.roundToInt() - dstH) / 2)
                onDrawBehind {
                    drawImage(
                        image = bitmap,
                        dstOffset = IntOffset(ox, oy),
                        dstSize = IntSize(dstW, dstH),
                        filterQuality = FilterQuality.None
                    )
                }
            }
    )
}

/**
 * Computes the destination pixel size for an item sprite (`36x32` OSRS item PNG) inside a
 * target box of [boxWidthPx] x [boxHeightPx] device pixels.
 *
 * When the box is at least as large as the source sprite, returns an exact integer multiple
 * (`scale * srcWidth`, `scale * srcHeight`) so every pixel of the sprite maps to an identical
 * `scale x scale` block of device pixels without uneven row/column doubling.
 * When the box is smaller than the source sprite (e.g. a 18dp chip at 1.0x density), scales down
 * proportionally to fit within the box.
 */
fun computeItemSpriteDrawSize(
    srcWidth: Int,
    srcHeight: Int,
    boxWidthPx: Int,
    boxHeightPx: Int
): IntSize {
    if (srcWidth <= 0 || srcHeight <= 0 || boxWidthPx <= 0 || boxHeightPx <= 0) {
        return IntSize.Zero
    }
    val intScaleX = boxWidthPx / srcWidth
    val intScaleY = boxHeightPx / srcHeight
    val intScale = minOf(intScaleX, intScaleY)
    return if (intScale >= 1) {
        IntSize(srcWidth * intScale, srcHeight * intScale)
    } else {
        val ratio = minOf(
            boxWidthPx.toFloat() / srcWidth.toFloat(),
            boxHeightPx.toFloat() / srcHeight.toFloat()
        )
        IntSize(
            (srcWidth * ratio).roundToInt().coerceAtLeast(1),
            (srcHeight * ratio).roundToInt().coerceAtLeast(1)
        )
    }
}

/**
 * Draws an OSRS item [ImageBitmap] centered inside [modifier] at an integer device-pixel scale
 * (when the target box is >= `bitmap.width x bitmap.height`) with [FilterQuality.None].
 */
@Composable
fun PixelItemImage(
    bitmap: ImageBitmap,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val semanticsModifier = if (contentDescription != null) {
        Modifier.semantics {
            this.contentDescription = contentDescription
            this.role = Role.Image
        }
    } else {
        Modifier
    }
    Box(
        modifier = modifier
            .then(semanticsModifier)
            .drawWithCache {
                val boxW = size.width.roundToInt()
                val boxH = size.height.roundToInt()
                val dstSize = computeItemSpriteDrawSize(
                    srcWidth = bitmap.width,
                    srcHeight = bitmap.height,
                    boxWidthPx = boxW,
                    boxHeightPx = boxH
                )
                val ox = (boxW - dstSize.width) / 2
                val oy = (boxH - dstSize.height) / 2
                onDrawBehind {
                    if (dstSize.width > 0 && dstSize.height > 0) {
                        drawImage(
                            image = bitmap,
                            dstOffset = IntOffset(ox, oy),
                            dstSize = dstSize,
                            filterQuality = FilterQuality.None
                        )
                    }
                }
            }
    )
}

