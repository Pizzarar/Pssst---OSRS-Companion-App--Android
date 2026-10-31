package com.pssst.theme.art

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pssst.theme.OsrsBorderDark
import com.pssst.theme.OsrsBorderLight
import com.pssst.theme.OsrsBorderOutline
import com.pssst.theme.OsrsBrassMid
import com.pssst.theme.OsrsDarkStone
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsGoldDark
import com.pssst.theme.OsrsGoldHighlight
import com.pssst.theme.OsrsGoldShadow
import com.pssst.theme.OsrsOuterFrame
import com.pssst.theme.OsrsSlateCatchLight
import com.pssst.theme.OsrsSlateMid
import com.pssst.theme.OsrsSlateUndercut
import com.pssst.theme.OsrsSlotEmpty
import com.pssst.theme.OsrsTextPrimary

/**
 * Pixel-honest plaque, tab, and number-badge drawing routines on [PixelCanvas].
 */
object Plaques {

    /**
     * Draws a carved OSRS login-screen monument tablet: chiselled cool slate with a joint near each
     * end, a double gold border (gold outer bevel, brass channel, inner slate bevel), stepped
     * corners and brass side rivets.
     */
    fun drawPlaqueFrame(canvas: PixelCanvas, seed: Int = 208) {
        val w = canvas.w
        val h = canvas.h
        if (w <= 0 || h <= 0) return

        canvas.clear(OsrsSlateMid.toArgb())
        if (w >= 40 && h >= 12) {
            Textures.drawAshlar(
                canvas = canvas,
                style = Frames.BAR_ASHLAR,
                seed = seed,
                courseMin = h,
                courseRange = 1,
                blockMin = 12,
                blockRange = 1,
                top = 3,
                bottom = h - 3,
                jointsX = intArrayOf(14, w - 13, w)
            )
        }
        val outline = OsrsOuterFrame.toArgb()
        val brass = OsrsBrassMid.toArgb()
        val dark = OsrsBorderDark.toArgb()
        val ext = OsrsDarkStone.toArgb()

        canvas.bevelBox(0, 0, w, h, outline = outline, light = OsrsGoldHighlight.toArgb(), dark = OsrsGoldShadow.toArgb(), raised = true)
        if (w > 8 && h > 8) {
            canvas.rectOutline(2, 2, w - 4, h - 4, brass)
            canvas.hLine(3, 3, w - 7, OsrsSlateCatchLight.toArgb())
            canvas.vLine(3, 3, h - 7, OsrsSlateCatchLight.toArgb())
            canvas.hLine(3, h - 4, w - 6, OsrsSlateUndercut.toArgb())
            canvas.vLine(w - 4, 3, h - 6, OsrsSlateUndercut.toArgb())
        }
        if (w >= 14 && h >= 10) {
            canvas.stairCorner(0, 0, steps = 2, cornerIndex = 0, outline = outline, bevelTone = OsrsGoldHighlight.toArgb(), exteriorFill = ext)
            canvas.stairCorner(w - 1, 0, steps = 2, cornerIndex = 1, outline = outline, bevelTone = OsrsGoldHighlight.toArgb(), exteriorFill = ext)
            canvas.stairCorner(w - 1, h - 1, steps = 2, cornerIndex = 2, outline = outline, bevelTone = OsrsGoldShadow.toArgb(), exteriorFill = ext)
            canvas.stairCorner(0, h - 1, steps = 2, cornerIndex = 3, outline = outline, bevelTone = OsrsGoldShadow.toArgb(), exteriorFill = ext)
        }
        if (w >= 20 && h >= 8) {
            val ry = (h / 2 - 1).coerceAtLeast(2)
            Frames.brassRivet(canvas, 5, ry)
            Frames.brassRivet(canvas, w - 8, ry)
        }
    }

    /**
     * Draws a segmented bookmark tab:
     * - [selected] = true: raised stone tab with a 2-art-px top gold/brass bar, side bevels,
     *   top corner rivets, and an open bottom edge that joins the panel below.
     * - [selected] = false: recessed stone well one tone darker ([OsrsSlotEmpty]) closed off at the bottom.
     */
    fun drawBookmarkTab(canvas: PixelCanvas, selected: Boolean, seed: Int = 404) {
        val w = canvas.w
        val h = canvas.h
        if (w <= 0 || h <= 0) return

        val outline = OsrsOuterFrame.toArgb()
        val light = OsrsBorderLight.toArgb()
        val dark = OsrsBorderDark.toArgb()
        val brass = OsrsBrassMid.toArgb()
        val goldHi = OsrsGoldHighlight.toArgb()

        if (selected) {
            Textures.drawPanelStone(canvas, seed = seed)
            // Top and side outline (bottom is open into the panel below)
            canvas.hLine(0, 0, w, outline)
            canvas.vLine(0, 0, h, outline)
            canvas.vLine(w - 1, 0, h, outline)
            // Top 2-art-px brass/gold clip bar
            if (h >= 4 && w >= 4) {
                canvas.hLine(1, 1, w - 2, goldHi)
                canvas.hLine(1, 2, w - 2, brass)
                // Left highlight & right shadow down to bottom
                canvas.vLine(1, 3, h - 3, light)
                canvas.vLine(w - 2, 3, h - 3, dark)
            }
            if (w >= 16 && h >= 8) {
                canvas.rivet(2, 2, size = 3)
                canvas.rivet(w - 5, 2, size = 3)
            }
        } else {
            canvas.clear(OsrsSlotEmpty.toArgb())
            canvas.rectOutline(0, 0, w, h, outline)
            if (w >= 4 && h >= 4) {
                // Recessed top/left shadow
                canvas.hLine(1, 1, w - 2, dark)
                canvas.vLine(1, 1, h - 2, dark)
                // Caught light on right, dark groove along bottom
                canvas.vLine(w - 2, 1, h - 2, OsrsBorderOutline.toArgb())
                canvas.hLine(1, h - 2, w - 2, dark)
            }
        }
    }

    /**
     * Draws a small raised gold pixel plaque with 1-step corners for step numbers ("1", "2", "IP").
     */
    fun drawNumberPlaque(canvas: PixelCanvas) {
        val w = canvas.w
        val h = canvas.h
        if (w <= 0 || h <= 0) return

        val outline = OsrsOuterFrame.toArgb()
        val goldHi = OsrsGoldHighlight.toArgb()
        val goldBase = OsrsGold.toArgb()
        val goldDark = OsrsGoldDark.toArgb()
        val goldShadow = OsrsGoldShadow.toArgb()
        val goldPair = intArrayOf(goldDark, goldBase)

        for (y in 0 until h) {
            for (x in 0 until w) {
                val v = 110 + ((h - 1 - y) * 80) / h.coerceAtLeast(1)
                canvas.set(x, y, PixelNoise.quantise(v, goldPair, x, y))
            }
        }

        canvas.bevelBox(
            x = 0,
            y = 0,
            bw = w,
            bh = h,
            outline = outline,
            light = goldHi,
            dark = goldShadow,
            raised = true
        )
        if (w >= 6 && h >= 6) {
            canvas.stairCorner(0, 0, steps = 1, cornerIndex = 0, outline = outline, bevelTone = goldHi, exteriorFill = 0)
            canvas.stairCorner(w - 1, 0, steps = 1, cornerIndex = 1, outline = outline, bevelTone = goldHi, exteriorFill = 0)
            canvas.stairCorner(w - 1, h - 1, steps = 1, cornerIndex = 2, outline = outline, bevelTone = goldShadow, exteriorFill = 0)
            canvas.stairCorner(0, h - 1, steps = 1, cornerIndex = 3, outline = outline, bevelTone = goldShadow, exteriorFill = 0)
        }
    }
}

/**
 * Gold-leaf lettering composable: flat [OsrsGold] with a hard 1-art-px [OsrsOuterFrame] shadow
 * (no smooth gradient brush, no blur).
 */
@Composable
fun GoldLeafText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    fontWeight: FontWeight = FontWeight.Bold,
    textAlign: TextAlign? = null,
    maxLines: Int = 1
) {
    Text(
        text = text,
        modifier = modifier,
        color = OsrsGold,
        style = style.copy(
            fontWeight = fontWeight,
            shadow = Shadow(
                color = OsrsOuterFrame,
                offset = Offset(2f, 2f),
                blurRadius = 0f
            )
        ),
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis
    )
}

/**
 * Stepped-corner stone-and-brass title plaque for screen titles, hero headers, or onboarding banners.
 */
@Composable
fun TitlePlaque(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .plaqueFrame()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GoldLeafText(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = OsrsTextPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Section header plaque with flat gold lettering, 5x5 pixel diamond bullet, optional trailing slot,
 * and an engraved bottom pixel rule.
 */
@Composable
fun SectionHeaderPlaque(
    title: String,
    modifier: Modifier = Modifier,
    trailing: (@Composable RowScope.() -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                BulletGlyph(size = 6.dp, color = OsrsBrassMid)
                Spacer(modifier = Modifier.width(6.dp))
                GoldLeafText(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            if (trailing != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    content = trailing
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        EngravedRule(withEndCaps = true, withCenterFlourish = false)
    }
}

/**
 * Stepped-corner oak-and-brass pixel plaque frame modifier with seated side rivets.
 */
fun Modifier.plaqueFrame(
    seed: Int = 208
): Modifier = this.pixelArt(key = Pair("plaqueFrame", seed)) {
    Plaques.drawPlaqueFrame(this, seed)
}

/**
 * Physical pixel tab modifier for segmented tabs:
 * - Selected tab: raised panel stone with top brass clip and open bottom edge into content below.
 * - Unselected tab: recessed dark stone slot ([OsrsSlotEmpty]) closed at the bottom.
 */
fun Modifier.bookmarkTab(
    selected: Boolean,
    seed: Int = 404
): Modifier = this.pixelArt(key = Triple("bookmarkTab", selected, seed)) {
    Plaques.drawBookmarkTab(this, selected = selected, seed = seed)
}

/**
 * Raised gold pixel plaque with dark engraved numeral or short label ("1", "2", "IP").
 */
@Composable
fun NumberPlaque(
    label: String,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .pixelArt(key = "NumberPlaque") {
                Plaques.drawNumberPlaque(this)
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = OsrsOuterFrame,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
