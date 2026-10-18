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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.pssst.theme.OsrsBorderDark
import com.pssst.theme.OsrsBorderLight
import com.pssst.theme.OsrsBorderOutline
import com.pssst.theme.OsrsBrassMid
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsGoldHighlight
import com.pssst.theme.OsrsGoldShadow
import com.pssst.theme.OsrsIronBody
import com.pssst.theme.OsrsIronEdgeLight
import com.pssst.theme.OsrsLeatherBase
import com.pssst.theme.OsrsLeatherHighlight
import com.pssst.theme.OsrsOuterFrame
import com.pssst.theme.OsrsPanelDark
import com.pssst.theme.OsrsParchmentDark
import com.pssst.theme.OsrsParchmentStain
import com.pssst.theme.OsrsParchmentWarm
import com.pssst.theme.OsrsRed
import com.pssst.theme.OsrsRedDark
import com.pssst.theme.OsrsRivetLight
import com.pssst.theme.OsrsSlotEmpty
import com.pssst.theme.OsrsSlotRecessed
import com.pssst.theme.OsrsStonePoreDark
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.OsrsTextPrimary
import com.pssst.theme.OsrsWoodBase
import com.pssst.theme.OsrsWoodDark
import com.pssst.theme.OsrsWoodLight
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Code-drawn 32x24 pixel-art scene subjects for empty states across the app.
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
 * Renders a 32x24 pixel-art scene directly over the stone ground above the screen's
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
        EmptyStateVignette(kind = kind, size = 64.dp)
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
 * Standalone 32x24 art-pixel empty-state scene scaled with [FilterQuality.None].
 */
@Composable
fun EmptyStateVignette(
    kind: EmptyStateKind,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp
) {
    val heightDp = (size * 0.75f)
    Box(
        modifier = modifier
            .size(width = size, height = heightDp)
            .drawWithCache {
                val sceneW = EmptyStates.SCENE_WIDTH
                val sceneH = EmptyStates.SCENE_HEIGHT
                val cacheKey = PixelCacheKey(
                    key = "empty_scene_${kind.name}",
                    artWidth = sceneW,
                    artHeight = sceneH
                )
                val bitmap = synchronized(pixelBitmapCache) {
                    pixelBitmapCache.get(cacheKey) ?: PixelCanvas(sceneW, sceneH).apply {
                        EmptyStates.drawScene(this, kind)
                    }.toImageBitmap().also {
                        pixelBitmapCache.put(cacheKey, it)
                    }
                }
                val scale = min(
                    (this.size.width / sceneW).toInt(),
                    (this.size.height / sceneH).toInt()
                ).coerceAtLeast(1)
                val dstW = sceneW * scale
                val dstH = sceneH * scale
                val ox = ((this.size.width.roundToInt() - dstW) / 2)
                val oy = ((this.size.height.roundToInt() - dstH) / 2)
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
 * Low-resolution 32x24 pixel scenes for empty states.
 * Every scene uses a 1-art-px dark outline (`OsrsOuterFrame`) and top-left lighting.
 */
object EmptyStates {
    const val SCENE_WIDTH = 32
    const val SCENE_HEIGHT = 24

    fun drawScene(canvas: PixelCanvas, kind: EmptyStateKind) {
        when (kind) {
            EmptyStateKind.CLOSED_CHEST -> drawClosedChest(canvas)
            EmptyStateKind.FALLOW_PATCH -> drawFallowPatch(canvas)
            EmptyStateKind.EMPTY_BIRDHOUSE -> drawEmptyBirdhouse(canvas)
            EmptyStateKind.POTION_RACK -> drawPotionRack(canvas)
            EmptyStateKind.QUEST_SCROLL -> drawQuestScroll(canvas)
            EmptyStateKind.SUNDIAL -> drawSundial(canvas)
            EmptyStateKind.LEDGER_BOOKMARK -> drawLedgerBookmark(canvas)
        }
    }

    /**
     * Bank empty: closed oak chest with forged iron straps and a brass padlock plate.
     */
    private fun drawClosedChest(c: PixelCanvas) {
        val out = OsrsOuterFrame.toArgb()
        val wLight = OsrsWoodLight.toArgb()
        val wBase = OsrsWoodBase.toArgb()
        val wDark = OsrsWoodDark.toArgb()
        val iLight = OsrsIronEdgeLight.toArgb()
        val iBody = OsrsIronBody.toArgb()
        val gLight = OsrsGoldHighlight.toArgb()
        val gBase = OsrsGold.toArgb()
        val gDark = OsrsBrassMid.toArgb()
        val rivet = OsrsRivetLight.toArgb()

        // Arched lid top outline
        c.hLine(7, 5, 18, out)
        c.set(6, 6, out)
        c.hLine(7, 6, 18, wLight)
        c.set(25, 6, out)

        // Lid upper body (y=7..10)
        for (y in 7..10) {
            c.set(5, y, out)
            c.set(6, y, wLight)
            c.hLine(7, y, 18, if (y == 10) wDark else wBase)
            c.set(25, y, wDark)
            c.set(26, y, out)
        }

        // Lid seam (y=11)
        c.hLine(5, 11, 22, out)

        // Lower chest box (y=12..18)
        for (y in 12..18) {
            c.set(5, y, out)
            c.set(6, y, if (y == 18) wDark else wLight)
            val rowTone = when (y) {
                15, 18 -> wDark
                16 -> wLight
                else -> wBase
            }
            c.hLine(7, y, 18, rowTone)
            c.set(25, y, wDark)
            c.set(26, y, out)
        }
        // Bottom outline & ground shadow
        c.hLine(5, 19, 22, out)
        c.hLine(6, 20, 20, out)

        // Left & right vertical wrought-iron straps (x=9..11 and x=20..22)
        for (sx in intArrayOf(9, 20)) {
            for (y in 6..18) {
                if (y == 11) continue
                c.set(sx, y, iLight)
                c.set(sx + 1, y, iBody)
                c.set(sx + 2, y, out)
            }
            c.set(sx + 1, 8, rivet)
            c.set(sx + 1, 14, rivet)
            c.set(sx + 1, 17, rivet)
        }

        // Central brass padlock plate (x=13..18, y=9..15)
        c.rectOutline(13, 9, 6, 7, out)
        c.fill(14, 10, 4, 5, gBase)
        c.hLine(14, 10, 4, gLight)
        c.vLine(14, 10, 5, gLight)
        c.hLine(14, 14, 4, gDark)
        c.vLine(17, 10, 5, gDark)
        // Keyhole
        c.set(15, 11, out)
        c.set(16, 11, out)
        c.set(15, 12, out)
        c.set(16, 12, out)
        c.set(15, 13, out)
    }

    /**
     * Farming empty: rectangle of dark furrowed earth with a wooden timber border and stake.
     */
    private fun drawFallowPatch(c: PixelCanvas) {
        val out = OsrsOuterFrame.toArgb()
        val wLight = OsrsWoodLight.toArgb()
        val wBase = OsrsWoodBase.toArgb()
        val wDark = OsrsWoodDark.toArgb()
        val soilDark = OsrsStonePoreDark.toArgb()
        val soilMid = OsrsLeatherBase.toArgb()
        val soilLight = OsrsBorderOutline.toArgb()

        // Outer timber bed (x=3..28, y=9..20)
        c.rectOutline(3, 9, 26, 12, out)
        c.hLine(4, 10, 24, wLight)
        c.hLine(4, 19, 24, wDark)
        c.vLine(4, 10, 10, wLight)
        c.vLine(27, 10, 10, wDark)
        c.rectOutline(5, 11, 22, 8, out)

        // Fallow soil interior (x=6..25, y=12..17)
        for (y in 12..17) {
            val isRidge = (y % 2 == 0)
            for (x in 6..25) {
                val h = PixelNoise.hash(x, y, 710) and 7
                val col = when {
                    isRidge && h == 0 -> soilLight
                    isRidge -> soilMid
                    h == 1 -> wDark
                    else -> soilDark
                }
                c.set(x, y, col)
            }
        }

        // Four 3x3 corner timber posts
        for ((px, py) in listOf(3 to 9, 26 to 9, 3 to 18, 26 to 18)) {
            c.rectOutline(px, py, 3, 3, out)
            c.set(px + 1, py + 1, wLight)
        }

        // Wooden stake & sign board on right (x=17..26, y=2..13)
        c.vLine(20, 8, 6, out)
        c.vLine(21, 8, 6, wLight)
        c.vLine(22, 8, 6, wDark)
        c.vLine(23, 8, 6, out)

        c.rectOutline(17, 2, 10, 7, out)
        c.hLine(18, 3, 8, wLight)
        c.fill(18, 4, 8, 3, wBase)
        c.hLine(18, 7, 8, wDark)
        // Scribe marks on stake sign
        c.hLine(19, 4, 6, out)
        c.hLine(19, 6, 4, out)
    }

    /**
     * Birdhouses empty: wooden birdhouse on a post with an empty entrance hole.
     */
    private fun drawEmptyBirdhouse(c: PixelCanvas) {
        val out = OsrsOuterFrame.toArgb()
        val wLight = OsrsWoodLight.toArgb()
        val wBase = OsrsWoodBase.toArgb()
        val wDark = OsrsWoodDark.toArgb()
        val sLight = OsrsBorderLight.toArgb()
        val sDark = OsrsPanelDark.toArgb()
        val gLight = OsrsGoldHighlight.toArgb()
        val gDark = OsrsBrassMid.toArgb()

        // Stone base mound (x=10..21, y=20..22)
        c.rectOutline(10, 20, 12, 3, out)
        c.hLine(11, 21, 5, sLight)
        c.hLine(16, 21, 5, sDark)

        // Wooden post (x=14..17, y=15..20)
        for (y in 15..20) {
            c.set(14, y, out)
            c.set(15, y, wLight)
            c.set(16, y, wDark)
            c.set(17, y, out)
        }

        // Cabin walls (x=9..22, y=8..15)
        c.rectOutline(9, 8, 14, 8, out)
        for (y in 9..14) {
            c.set(10, y, wLight)
            c.hLine(11, y, 10, if (y == 14) wDark else wBase)
            c.set(21, y, wDark)
            // Vertical plank seams
            c.set(12, y, wDark)
            c.set(19, y, wDark)
        }

        // Dark empty entrance hole (x=14..17, y=10..12) + perch peg
        c.fill(14, 10, 4, 3, out)
        c.hLine(13, 13, 6, wLight)
        c.hLine(14, 14, 4, out)

        // Brass finial apex (x=14..17, y=1..2)
        c.hLine(15, 1, 2, out)
        c.set(14, 2, out)
        c.set(15, 2, gLight)
        c.set(16, 2, gDark)
        c.set(17, 2, out)

        // Stepped pitched roof (y=3..8)
        for (step in 0..5) {
            val y = 3 + step
            val left = 13 - step
            val right = 18 + step
            val span = right - left + 1
            if (step == 5) {
                c.hLine(left, y, span, out)
            } else {
                c.set(left, y, out)
                c.set(left + 1, y, out)
                val innerLeft = left + 2
                val innerRight = right - 2
                val mid = (innerLeft + innerRight) / 2
                for (x in innerLeft..innerRight) {
                    val col = when {
                        x < mid -> wLight
                        x == mid -> wBase
                        else -> wDark
                    }
                    c.set(x, y, col)
                }
                c.set(right - 1, y, out)
                c.set(right, y, out)
            }
        }
    }

    /**
     * Potion storage empty: wooden shelf with two empty glass vials (outline + highlight pixel).
     */
    private fun drawPotionRack(c: PixelCanvas) {
        val out = OsrsOuterFrame.toArgb()
        val wLight = OsrsWoodLight.toArgb()
        val wBase = OsrsWoodBase.toArgb()
        val wDark = OsrsWoodDark.toArgb()
        val iLight = OsrsIronEdgeLight.toArgb()
        val glassEdge = OsrsBorderLight.toArgb()
        val glassInterior = OsrsSlotEmpty.toArgb()
        val glassShadow = OsrsPanelDark.toArgb()
        val glint = OsrsTextPrimary.toArgb()

        // Wooden shelf plank (x=3..28, y=17..20)
        c.rectOutline(3, 17, 26, 4, out)
        c.hLine(4, 18, 24, wLight)
        c.hLine(4, 19, 24, wDark)

        // Iron wall brackets under shelf
        for (bx in intArrayOf(6, 23)) {
            c.rectOutline(bx, 20, 3, 3, out)
            c.set(bx + 1, 21, iLight)
        }

        // Two empty glass vials at vx = 7 and vx = 18 (each 7 art-px wide)
        for (vx in intArrayOf(7, 18)) {
            // Cork stopper (y=4..5)
            c.hLine(vx + 2, 4, 3, out)
            c.set(vx + 1, 5, out)
            c.set(vx + 2, 5, wLight)
            c.set(vx + 3, 5, wBase)
            c.set(vx + 4, 5, wDark)
            c.set(vx + 5, 5, out)

            // Vial lip (y=6)
            c.set(vx + 1, 6, out)
            c.hLine(vx + 2, 6, 3, glassEdge)
            c.set(vx + 5, 6, out)

            // Narrow neck (y=7..8)
            for (y in 7..8) {
                c.set(vx + 2, y, out)
                c.set(vx + 3, y, glassInterior)
                c.set(vx + 4, y, out)
            }

            // Sloped shoulder (y=9)
            c.set(vx + 1, 9, out)
            c.set(vx + 2, 9, glassEdge)
            c.set(vx + 3, 9, glassInterior)
            c.set(vx + 4, 9, glassShadow)
            c.set(vx + 5, 9, out)

            // Bulbous glass body (y=10..15)
            for (y in 10..15) {
                c.set(vx, y, out)
                c.set(vx + 1, y, if (y == 11) glint else glassEdge)
                c.set(vx + 2, y, if (y == 10) glint else glassInterior)
                c.set(vx + 3, y, glassInterior)
                c.set(vx + 4, y, glassInterior)
                c.set(vx + 5, y, glassShadow)
                c.set(vx + 6, y, out)
            }

            // Rounded vial base on shelf (y=16)
            c.set(vx + 1, 16, out)
            c.hLine(vx + 2, 16, 3, glassEdge)
            c.set(vx + 5, 16, out)
        }
    }

    /**
     * Quests empty: rolled parchment scroll tied with a gold-and-crimson band.
     */
    private fun drawQuestScroll(c: PixelCanvas) {
        val out = OsrsOuterFrame.toArgb()
        val pLight = OsrsTextMuted.toArgb()
        val pWarm = OsrsParchmentWarm.toArgb()
        val pDark = OsrsParchmentDark.toArgb()
        val pStain = OsrsParchmentStain.toArgb()
        val gLight = OsrsGoldHighlight.toArgb()
        val gBase = OsrsGold.toArgb()
        val gShadow = OsrsGoldShadow.toArgb()
        val rLight = OsrsRed.toArgb()
        val rDark = OsrsRedDark.toArgb()
        val ink = OsrsBorderOutline.toArgb()

        // Top & bottom scroll rods with brass finials (y=3..6 and y=17..20)
        for (ry in intArrayOf(3, 17)) {
            // Left finial knob (x=4..6)
            c.rectOutline(4, ry + 1, 3, 3, out)
            c.set(5, ry + 2, gLight)
            // Right finial knob (x=25..27)
            c.rectOutline(25, ry + 1, 3, 3, out)
            c.set(26, ry + 2, gBase)

            // Rolled parchment cylinder (x=6..25)
            c.rectOutline(6, ry, 20, 4, out)
            c.hLine(7, ry + 1, 18, pLight)
            c.hLine(7, ry + 2, 18, pWarm)
        }

        // Scroll body (x=8..23, y=7..16)
        for (y in 7..16) {
            c.set(8, y, out)
            c.set(9, y, pLight)
            c.hLine(10, y, 10, if (y % 2 == 0) pWarm else pDark)
            c.set(20, y, pDark)
            c.set(21, y, pStain)
            c.set(22, y, pStain)
            c.set(23, y, out)
        }

        // Scribe lines on upper and lower scroll face
        c.hLine(11, 8, 8, ink)
        c.hLine(11, 15, 7, ink)

        // Gold binding ribbon around waist (x=8..23, y=10..13)
        c.hLine(8, 10, 16, out)
        c.set(8, 11, out)
        c.hLine(9, 11, 14, gLight)
        c.set(23, 11, out)
        c.set(8, 12, out)
        c.hLine(9, 12, 14, gShadow)
        c.set(23, 12, out)
        c.hLine(8, 13, 16, out)

        // Central stamped red seal (x=13..18, y=9..14)
        c.rectOutline(13, 9, 6, 6, out)
        c.fill(14, 10, 4, 4, rDark)
        c.hLine(14, 10, 3, rLight)
        c.vLine(14, 10, 3, rLight)
    }

    /**
     * Dailies empty: carved stone sundial pedestal with bronze gnomon fin.
     */
    private fun drawSundial(c: PixelCanvas) {
        val out = OsrsOuterFrame.toArgb()
        val sLight = OsrsBorderLight.toArgb()
        val sMid = OsrsSlotRecessed.toArgb()
        val sDark = OsrsPanelDark.toArgb()
        val sShadow = OsrsBorderDark.toArgb()
        val gLight = OsrsGoldHighlight.toArgb()
        val gBase = OsrsGold.toArgb()
        val gDark = OsrsBrassMid.toArgb()
        val gShadow = OsrsGoldShadow.toArgb()

        // Lower stone base slab (x=4..27, y=19..22)
        c.rectOutline(4, 19, 24, 4, out)
        c.hLine(5, 20, 22, sLight)
        c.hLine(5, 21, 22, sDark)

        // Upper stone dial table (x=6..25, y=14..19)
        c.rectOutline(6, 14, 20, 6, out)
        c.hLine(7, 15, 18, sLight)
        // Engraved dial hour ticks along rim
        for (tx in intArrayOf(9, 12, 15, 18, 21)) {
            c.set(tx, 15, out)
        }
        c.hLine(7, 16, 18, sMid)
        c.hLine(7, 17, 18, sDark)
        c.hLine(7, 18, 18, sShadow)

        // Cast gnomon shadow across right side of dial table
        c.hLine(18, 16, 5, out)
        c.hLine(19, 17, 4, out)

        // Bronze triangular gnomon fin (x=11..19, y=4..14)
        for (step in 0..9) {
            val y = 4 + step
            val rightX = 12 + (step * 7) / 9
            c.set(11, y, out)
            c.set(12, y, gLight)
            for (x in 13 until rightX) {
                c.set(x, y, if (y == 13) gShadow else if (x == rightX - 1) gLight else gBase)
            }
            if (rightX >= 13) {
                c.set(rightX - 1, y, gDark)
            }
            c.set(rightX, y, out)
            if (rightX + 1 <= 20 && step < 9) {
                c.set(rightX + 1, y + 1, out)
            }
        }
        c.hLine(11, 3, 2, out)
    }

    /**
     * Goal empty: open leather-bound ledger page with forked gold bookmark and coin stack.
     */
    private fun drawLedgerBookmark(c: PixelCanvas) {
        val out = OsrsOuterFrame.toArgb()
        val lLight = OsrsLeatherHighlight.toArgb()
        val lBase = OsrsLeatherBase.toArgb()
        val pLight = OsrsTextMuted.toArgb()
        val pWarm = OsrsParchmentWarm.toArgb()
        val pDark = OsrsParchmentDark.toArgb()
        val rule = OsrsBorderOutline.toArgb()
        val gLight = OsrsGoldHighlight.toArgb()
        val gBase = OsrsGold.toArgb()
        val gDark = OsrsBrassMid.toArgb()
        val gShadow = OsrsGoldShadow.toArgb()

        // Ledger book board (x=4..22, y=3..18)
        c.rectOutline(4, 3, 19, 16, out)

        // Left leather spine (x=5..7, y=4..17)
        for (y in 4..17) {
            c.set(5, y, lLight)
            c.set(6, y, lBase)
            c.set(7, y, out)
        }

        // Blank parchment page (x=8..21, y=4..17)
        c.hLine(8, 4, 14, pLight)
        for (y in 5..17) {
            c.set(8, y, pLight)
            c.hLine(9, y, 11, if (y % 2 == 0) pWarm else pDark)
            c.set(20, y, pDark)
            c.set(21, y, pDark)
        }
        // Faint ruled ledger lines
        for (ry in intArrayOf(7, 10, 13, 15)) {
            c.hLine(10, ry, 9, rule)
        }

        // Gold bookmark ribbon hanging down (x=11..14, y=4..21)
        for (y in 4..20) {
            c.set(11, y, out)
            c.set(12, y, gLight)
            c.set(13, y, gBase)
            c.set(14, y, out)
        }
        // Forked ribbon tail at y=20..21
        c.set(12, 20, out)
        c.set(13, 20, out)
        c.set(11, 21, out)
        c.set(14, 21, out)

        // Stacked GP coin column in foreground right (x=19..27, y=11..20)
        c.rectOutline(19, 11, 9, 10, out)
        for (y in 12..19) {
            val isHighlightRow = (y % 2 == 0)
            c.set(20, y, if (isHighlightRow) gLight else gBase)
            c.hLine(21, y, 5, if (isHighlightRow) gBase else gDark)
            c.set(26, y, gShadow)
        }
    }
}
