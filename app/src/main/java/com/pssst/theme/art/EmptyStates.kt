package com.pssst.theme.art

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsTextMuted
import kotlin.math.roundToInt

/**
 * 48x36 pixel-art scene subjects for empty states across the app.
 *
 * Names are kept for call-site stability; the art they draw:
 * - [CLOSED_CHEST]: a bank clerk dozing at the counter beside an open, cobwebbed, empty chest.
 * - [FALLOW_PATCH]: a grumpy tool leprechaun on a compost bucket, a rake in weedy fallow soil.
 * - [EMPTY_BIRDHOUSE]: a clockwork bird hanging off a redwood birdhouse, peering into the hole.
 * - [POTION_RACK]: an oak shelf with a mortar and pestle and a tipped, dripping vial.
 * - [QUEST_SCROLL]: a parchment notice pinned to a tavern post by a rune dagger, quest seal below.
 * - [SUNDIAL]: a stone sundial with a hanging "RESET" plank beside an emptied barrel.
 * - [LEDGER_BOOKMARK]: a velvet plinth under a chalk ghost of coin stacks, with a "?" price tag.
 */
enum class EmptyStateKind {
    CLOSED_CHEST,
    FALLOW_PATCH,
    EMPTY_BIRDHOUSE,
    POTION_RACK,
    QUEST_SCROLL,
    SUNDIAL,
    LEDGER_BOOKMARK
}

/**
 * Renders a 48x36 pixel-art scene directly over the stone ground above the screen's
 * existing empty-state copy (no octagon medallion frame).
 */
@Composable
fun OsrsEmptyState(
    kind: EmptyStateKind,
    message: String,
    modifier: Modifier = Modifier,
    title: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        EmptyStateVignette(kind = kind, size = 96.dp)
        Spacer(modifier = Modifier.height(10.dp))
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = OsrsGold,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
        }
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = OsrsTextMuted,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Standalone 48x36 empty-state scene.
 *
 * [size] picks a whole number of UI art pixels per scene texel (`96.dp` = 2, `48.dp` = 1), so the
 * scene shares the exact pixel grid of the surrounding sprites and frames instead of mixing sizes.
 */
@Composable
fun EmptyStateVignette(
    kind: EmptyStateKind,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp
) {
    val multiplier = (size.value / EmptyStates.SCENE_WIDTH).roundToInt().coerceAtLeast(1)
    PixelGridArt(
        artWidth = EmptyStates.SCENE_WIDTH,
        artHeight = EmptyStates.SCENE_HEIGHT,
        scaleMultiplier = multiplier,
        cacheKey = "empty_scene_${kind.name}",
        modifier = modifier
    ) {
        EmptyStates.drawScene(this, kind)
    }
}

/**
 * The Pssst crest (gold-trimmed heraldic shield with a gold "P"; the visible 24x24 zone of the
 * 36x36 launcher grid) for the Setup screen.
 */
@Composable
fun InformantCrest(
    modifier: Modifier = Modifier,
    scaleMultiplier: Int = 3
) {
    PixelGridArt(
        artWidth = EmptyStates.CREST_VIEW_SIZE,
        artHeight = EmptyStates.CREST_VIEW_SIZE,
        scaleMultiplier = scaleMultiplier,
        cacheKey = "informant_crest_view",
        modifier = modifier,
        contentDescription = "Pssst crest"
    ) {
        EmptyStates.drawInformantCrestView(this)
    }
}

/**
 * 32x24 Miscellania vignette: the castle of Miscellania. Healthy flies a gold-and-emerald banner
 * with lit torches and windows; warning shows a cracked turret, a tattered crimson banner and an
 * alarm glow in the gatehouse.
 * Decorative only: the card's approval and coffer text already carry the state.
 */
@Composable
fun KingdomStatusVignette(
    isWarning: Boolean,
    modifier: Modifier = Modifier,
    scaleMultiplier: Int = 2
) {
    PixelGridArt(
        artWidth = EmptyStates.VIGNETTE_WIDTH,
        artHeight = EmptyStates.VIGNETTE_HEIGHT,
        scaleMultiplier = scaleMultiplier,
        cacheKey = "kingdom_vignette_$isWarning",
        modifier = modifier
    ) {
        EmptyStates.drawKingdomVignette(this, isWarning)
    }
}

/**
 * 32x24 Slayer emblem: a horned skull run through by a broadsword, gilded once a task is finished.
 * Decorative only: the card's task text already carries the state.
 */
@Composable
fun SlayerEmblemVignette(
    isGilded: Boolean,
    modifier: Modifier = Modifier,
    scaleMultiplier: Int = 1
) {
    PixelGridArt(
        artWidth = EmptyStates.VIGNETTE_WIDTH,
        artHeight = EmptyStates.VIGNETTE_HEIGHT,
        scaleMultiplier = scaleMultiplier,
        cacheKey = "slayer_vignette_$isGilded",
        modifier = modifier
    ) {
        EmptyStates.drawSlayerVignette(this, isGilded)
    }
}

/**
 * Draws [artWidth] x [artHeight] pixel art with every texel exactly `artPx * scaleMultiplier` device
 * pixels, in a box sized to match, using [FilterQuality.None].
 */
@Composable
internal fun PixelGridArt(
    artWidth: Int,
    artHeight: Int,
    scaleMultiplier: Int,
    cacheKey: Any,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    draw: PixelCanvas.() -> Unit
) {
    val density = LocalDensity.current
    val texel = computeArtPx(density.density) * scaleMultiplier.coerceAtLeast(1)
    val widthDp = with(density) { (artWidth * texel).toDp() }
    val heightDp = with(density) { (artHeight * texel).toDp() }
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
            .size(widthDp, heightDp)
            .then(semanticsModifier)
            .drawWithCache {
                val key = PixelCacheKey(cacheKey, artWidth, artHeight)
                val bitmap = synchronized(pixelBitmapCache) {
                    pixelBitmapCache.get(key) ?: PixelCanvas(artWidth, artHeight).apply(draw)
                        .toImageBitmap()
                        .also { pixelBitmapCache.put(key, it) }
                }
                val dstW = artWidth * texel
                val dstH = artHeight * texel
                val ox = (size.width.roundToInt() - dstW) / 2
                val oy = (size.height.roundToInt() - dstH) / 2
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
 * Low-resolution scene art: 48x36 empty states, the 36x36 informant crest, and 32x24 Overview
 * vignettes. Grids are generated into [SceneArt] by `tools/art/build_art.py`.
 */
object EmptyStates {
    const val SCENE_WIDTH = 48
    const val SCENE_HEIGHT = 36

    const val VIGNETTE_WIDTH = 32
    const val VIGNETTE_HEIGHT = 24

    /** Full 36x36 crest grid; the launcher foreground maps it at 3x3 viewport units per pixel. */
    const val CREST_SIZE = 36

    /** Visible 24x24 zone of the crest (the adaptive-icon viewport) used on the Setup screen. */
    const val CREST_VIEW_X = 6
    const val CREST_VIEW_Y = 6
    const val CREST_VIEW_SIZE = 24

    val INFORMANT_CREST_GRID: SceneGrid get() = SceneArt.INFORMANT_CREST

    fun gridFor(kind: EmptyStateKind): SceneGrid = when (kind) {
        EmptyStateKind.CLOSED_CHEST -> SceneArt.CLOSED_CHEST
        EmptyStateKind.FALLOW_PATCH -> SceneArt.FALLOW_PATCH
        EmptyStateKind.EMPTY_BIRDHOUSE -> SceneArt.EMPTY_BIRDHOUSE
        EmptyStateKind.POTION_RACK -> SceneArt.POTION_RACK
        EmptyStateKind.QUEST_SCROLL -> SceneArt.QUEST_SCROLL
        EmptyStateKind.SUNDIAL -> SceneArt.SUNDIAL
        EmptyStateKind.LEDGER_BOOKMARK -> SceneArt.LEDGER_BOOKMARK
    }

    fun drawScene(canvas: PixelCanvas, kind: EmptyStateKind) {
        canvas.drawSceneGrid(gridFor(kind))
    }

    fun drawInformantCrest(canvas: PixelCanvas) {
        canvas.drawSceneGrid(SceneArt.INFORMANT_CREST)
    }

    fun drawInformantCrestView(canvas: PixelCanvas) {
        canvas.drawSceneGrid(SceneArt.INFORMANT_CREST, -CREST_VIEW_X, -CREST_VIEW_Y)
    }

    fun kingdomGrid(isWarning: Boolean): SceneGrid =
        if (isWarning) SceneArt.KINGDOM_WARNING else SceneArt.KINGDOM_HEALTHY

    fun slayerGrid(isGilded: Boolean): SceneGrid =
        if (isGilded) SceneArt.SLAYER_COMPLETE else SceneArt.SLAYER_ACTIVE

    fun drawKingdomVignette(canvas: PixelCanvas, isWarning: Boolean) {
        canvas.drawSceneGrid(kingdomGrid(isWarning))
    }

    fun drawSlayerVignette(canvas: PixelCanvas, isGilded: Boolean) {
        canvas.drawSceneGrid(slayerGrid(isGilded))
    }
}
