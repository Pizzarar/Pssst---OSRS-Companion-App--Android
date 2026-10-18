package com.pssst.theme.art

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.pssst.theme.OsrsBorderDark
import com.pssst.theme.OsrsBorderLight
import com.pssst.theme.OsrsBorderOutline
import com.pssst.theme.OsrsBrassMid
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsGoldDark
import com.pssst.theme.OsrsGoldHighlight
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
import com.pssst.theme.OsrsOuterFrame
import com.pssst.theme.OsrsPanelDark
import com.pssst.theme.OsrsPanelLight
import com.pssst.theme.OsrsParchmentDark
import com.pssst.theme.OsrsParchmentStain
import com.pssst.theme.OsrsSlotEmpty
import com.pssst.theme.OsrsSlotRecessed
import com.pssst.theme.art.Ornaments.drawDomedRivet
import com.pssst.theme.art.Ornaments.drawFacetedDiamond

/**
 * Corner hardware weight tiers for panel frames.
 */
enum class FrameWeight {
    /** Single forged dome rivet in each corner (small cards, chips, dialogs). */
    LIGHT,
    /** Chamfered wrought-iron L-brackets with dual rivets in each corner (standard cards). */
    MEDIUM,
    /** Forged horizontal spearhead hinge straps with triple rivets + inner brass fillet (hero cards, top bar). */
    HEAVY
}

/**
 * Upgraded carved-stone panel frame with procedural stone grain, chiseled bevel edges,
 * and wrought-iron corner hardware in [weight].
 */
fun Modifier.ledgerPanelFrame(
    weight: FrameWeight = FrameWeight.MEDIUM,
    seed: Int = 101,
    accentBorder: Color? = null
): Modifier = this.drawWithCache {
    val stoneBrush = Textures.stoneBrush(seed = seed, darkGround = false)
    val sheen = Brush.verticalGradient(
        colors = listOf(
            OsrsPanelLight.copy(alpha = 0.42f),
            Color.Transparent,
            OsrsBorderDark.copy(alpha = 0.35f)
        )
    )
    val outerPx = 1.dp.toPx()
    val bevelPx = 1.5.dp.toPx()

    onDrawBehind {
        val w = size.width
        val h = size.height

        // 1. Outermost carved shadow trench
        drawRect(color = OsrsOuterFrame)

        // 2. Carved stone fill + procedural grain + top-lit lantern gradient
        drawRect(
            color = OsrsPanelDark,
            topLeft = Offset(outerPx, outerPx),
            size = Size(w - outerPx * 2, h - outerPx * 2)
        )
        drawRect(
            brush = stoneBrush,
            topLeft = Offset(outerPx, outerPx),
            size = Size(w - outerPx * 2, h - outerPx * 2),
            alpha = 0.78f
        )
        drawRect(
            brush = sheen,
            topLeft = Offset(outerPx, outerPx),
            size = Size(w - outerPx * 2, h - outerPx * 2)
        )

        // 3. Chiseled stone bevels (top/left highlight, bottom/right shadow)
        val topBevelColor = accentBorder ?: OsrsBorderLight
        drawLine(topBevelColor, Offset(outerPx, outerPx), Offset(w - outerPx, outerPx), bevelPx)
        drawLine(topBevelColor, Offset(outerPx, outerPx), Offset(outerPx, h - outerPx), bevelPx)
        drawLine(OsrsBorderDark, Offset(outerPx, h - outerPx), Offset(w - outerPx, h - outerPx), bevelPx)
        drawLine(OsrsBorderDark, Offset(w - outerPx, outerPx), Offset(w - outerPx, h - outerPx), bevelPx)

        // 4. Optional inner brass fillet for HEAVY frames
        if (weight == FrameWeight.HEAVY && w > 36.dp.toPx() && h > 36.dp.toPx()) {
            val inset = 4.dp.toPx()
            drawRect(
                color = (accentBorder ?: OsrsBrassMid).copy(alpha = 0.38f),
                topLeft = Offset(inset, inset),
                size = Size(w - inset * 2, h - inset * 2),
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // 5. Corner hardware by weight
        when (weight) {
            FrameWeight.LIGHT -> drawCornerStuds()
            FrameWeight.MEDIUM -> drawCornerLBrackets()
            FrameWeight.HEAVY -> drawCornerHingeStraps()
        }
    }
}

/**
 * Top-bar frame: heavy aged dark oak beam bound with horizontal wrought-iron edge straps and rivets.
 */
fun Modifier.oakBeamFrame(seed: Int = 202): Modifier = this.drawWithCache {
    val oakBrush = Textures.oakBrush(seed = seed)
    val ironBrush = Textures.ironBrush(seed = seed + 11)
    val sheen = Brush.verticalGradient(
        colors = listOf(
            OsrsOakGrainLight.copy(alpha = 0.35f),
            Color.Transparent,
            OsrsOakGrainDark.copy(alpha = 0.55f)
        )
    )
    val strapH = 3.dp.toPx()
    val strokePx = 1.dp.toPx()

    onDrawBehind {
        val w = size.width
        val h = size.height

        // Base oak timber
        drawRect(color = OsrsOakBase)
        drawRect(brush = oakBrush, alpha = 0.92f)
        drawRect(brush = sheen)

        // Mid-beam horizontal timber join
        val seamY = h * 0.52f
        drawLine(OsrsOakGrainDark.copy(alpha = 0.7f), Offset(0f, seamY), Offset(w, seamY), strokePx)
        drawLine(OsrsOakGrainLight.copy(alpha = 0.3f), Offset(0f, seamY + strokePx), Offset(w, seamY + strokePx), strokePx)

        // Top & bottom forged iron binding bands
        drawRect(brush = ironBrush, topLeft = Offset(0f, 0f), size = Size(w, strapH))
        drawLine(OsrsIronEdgeLight, Offset(0f, strapH), Offset(w, strapH), strokePx)

        drawRect(brush = ironBrush, topLeft = Offset(0f, h - strapH), size = Size(w, strapH))
        drawLine(OsrsBrassMid.copy(alpha = 0.65f), Offset(0f, h - strapH - strokePx), Offset(w, h - strapH - strokePx), strokePx)
        drawLine(OsrsOuterFrame, Offset(0f, h - strokePx), Offset(w, h - strokePx), strokePx * 1.5f)

        // Corner hinge straps
        drawCornerHingeStraps()
    }
}

/**
 * Bottom navigation sill frame: carved stone sill with an engraved top brass/iron rail and corner brackets.
 */
fun Modifier.stoneSillFrame(seed: Int = 109): Modifier = this.drawWithCache {
    val stoneBrush = Textures.stoneBrush(seed = seed, darkGround = false)
    val railH = 3.dp.toPx()
    val strokePx = 1.dp.toPx()

    onDrawBehind {
        val w = size.width
        val h = size.height
        drawRect(color = OsrsPanelDark)
        drawRect(brush = stoneBrush, alpha = 0.85f)

        // Chiseled top iron & brass rail
        drawRect(color = OsrsOuterFrame, topLeft = Offset(0f, 0f), size = Size(w, railH))
        drawLine(OsrsBrassMid.copy(alpha = 0.7f), Offset(0f, railH), Offset(w, railH), strokePx * 1.2f)
        drawLine(OsrsBorderDark, Offset(0f, railH + strokePx), Offset(w, railH + strokePx), strokePx)

        // Corner L-brackets
        drawCornerLBrackets()
    }
}

/**
 * Recessed carved-stone well for item slots, skill cells, and top-bar status wells.
 * Features a 45-degree chamfered inner shadow rim and optional active gold-leaf glint.
 */
fun Modifier.ledgerWellSlot(
    empty: Boolean = false,
    highlighted: Boolean = false
): Modifier = this.drawWithCache {
    val fill = if (empty) OsrsSlotEmpty else OsrsSlotRecessed
    val strokePx = 1.2.dp.toPx()
    val innerPx = 2.2.dp.toPx()

    onDrawBehind {
        val w = size.width
        val h = size.height
        drawRect(color = OsrsOuterFrame)
        drawRect(
            color = fill,
            topLeft = Offset(strokePx, strokePx),
            size = Size(w - strokePx * 2, h - strokePx * 2)
        )
        // Recessed shadow on top & left
        drawLine(OsrsBorderDark, Offset(strokePx, strokePx), Offset(w - strokePx, strokePx), innerPx)
        drawLine(OsrsBorderDark, Offset(strokePx, strokePx), Offset(strokePx, h - strokePx), innerPx)
        // Caught light on bottom & right
        val catchColor = if (highlighted) OsrsGold.copy(alpha = 0.85f) else OsrsBorderLight
        drawLine(catchColor, Offset(strokePx, h - strokePx), Offset(w - strokePx, h - strokePx), strokePx)
        drawLine(catchColor, Offset(w - strokePx, strokePx), Offset(w - strokePx, h - strokePx), strokePx)

        if (highlighted) {
            drawRect(
                color = OsrsGoldHighlight.copy(alpha = 0.45f),
                topLeft = Offset(strokePx, strokePx),
                size = Size(w - strokePx * 2, h - strokePx * 2),
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }
}

/**
 * Weathered parchment notice strip pinned with forged iron corner tacks (used for QuickActionsBanner & URL slips).
 */
fun Modifier.parchmentNotice(
    accentBorder: Color = OsrsBrassMid,
    seed: Int = 303
): Modifier = this.drawWithCache {
    val vellum = Textures.parchmentBrush(seed = seed)
    val strokePx = 1.2.dp.toPx()
    val tackInset = 5.dp.toPx()
    val tackR = 2.dp.toPx()

    onDrawBehind {
        val w = size.width
        val h = size.height
        drawRect(color = OsrsOuterFrame)
        drawRect(
            color = OsrsParchmentDark,
            topLeft = Offset(strokePx, strokePx),
            size = Size(w - strokePx * 2, h - strokePx * 2)
        )
        drawRect(
            brush = vellum,
            topLeft = Offset(strokePx, strokePx),
            size = Size(w - strokePx * 2, h - strokePx * 2),
            alpha = 0.85f
        )
        // Aged edge vignette
        drawRect(
            color = OsrsParchmentStain.copy(alpha = 0.35f),
            topLeft = Offset(strokePx, strokePx),
            size = Size(w - strokePx * 2, h - strokePx * 2),
            style = Stroke(width = 3.dp.toPx())
        )
        // Crisp border rule
        drawRect(
            color = accentBorder.copy(alpha = 0.75f),
            topLeft = Offset(strokePx, strokePx),
            size = Size(w - strokePx * 2, h - strokePx * 2),
            style = Stroke(width = strokePx)
        )
        // 4 iron pin tacks
        if (w > 28.dp.toPx() && h > 22.dp.toPx()) {
            drawDomedRivet(Offset(tackInset, tackInset), tackR)
            drawDomedRivet(Offset(w - tackInset, tackInset), tackR)
            drawDomedRivet(Offset(tackInset, h - tackInset), tackR)
            drawDomedRivet(Offset(w - tackInset, h - tackInset), tackR)
        }
    }
}

/**
 * Bound worn-leather panel with saddle-stitched border and iron corner brackets (drawer, inventory pack tray).
 */
fun Modifier.leatherBoundPanel(seed: Int = 404): Modifier = this.drawWithCache {
    val hide = Textures.leatherBrush(seed = seed)
    val sheen = Brush.verticalGradient(
        colors = listOf(
            OsrsLeatherHighlight.copy(alpha = 0.28f),
            Color.Transparent,
            OsrsLeatherCrease.copy(alpha = 0.48f)
        )
    )
    val outerPx = 1.dp.toPx()
    val stitchInset = 5.dp.toPx()
    val dash = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
        floatArrayOf(4.dp.toPx(), 3.dp.toPx()),
        0f
    )

    onDrawBehind {
        val w = size.width
        val h = size.height
        drawRect(color = OsrsOuterFrame)
        drawRect(
            color = OsrsLeatherBase,
            topLeft = Offset(outerPx, outerPx),
            size = Size(w - outerPx * 2, h - outerPx * 2)
        )
        drawRect(
            brush = hide,
            topLeft = Offset(outerPx, outerPx),
            size = Size(w - outerPx * 2, h - outerPx * 2),
            alpha = 0.88f
        )
        drawRect(
            brush = sheen,
            topLeft = Offset(outerPx, outerPx),
            size = Size(w - outerPx * 2, h - outerPx * 2)
        )
        // Outer bevel
        drawLine(OsrsBorderLight, Offset(outerPx, outerPx), Offset(w - outerPx, outerPx), 1.5.dp.toPx())
        drawLine(OsrsBorderLight, Offset(outerPx, outerPx), Offset(outerPx, h - outerPx), 1.5.dp.toPx())
        drawLine(OsrsBorderDark, Offset(outerPx, h - outerPx), Offset(w - outerPx, h - outerPx), 1.5.dp.toPx())
        drawLine(OsrsBorderDark, Offset(w - outerPx, outerPx), Offset(w - outerPx, h - outerPx), 1.5.dp.toPx())

        if (w > stitchInset * 4 && h > stitchInset * 4) {
            drawRect(
                color = OsrsLeatherCrease,
                topLeft = Offset(stitchInset, stitchInset),
                size = Size(w - stitchInset * 2, h - stitchInset * 2),
                style = Stroke(width = 1.6.dp.toPx())
            )
            drawRect(
                color = OsrsLeatherStitch.copy(alpha = 0.72f),
                topLeft = Offset(stitchInset, stitchInset),
                size = Size(w - stitchInset * 2, h - stitchInset * 2),
                style = Stroke(width = 1.dp.toPx(), pathEffect = dash)
            )
        }
        drawCornerLBrackets()
    }
}

internal fun DrawScope.drawCornerStuds() {
    val w = size.width
    val h = size.height
    if (w < 24.dp.toPx() || h < 20.dp.toPx()) return
    val inset = 4.5.dp.toPx()
    val r = 2.dp.toPx()
    drawDomedRivet(Offset(inset, inset), r)
    drawDomedRivet(Offset(w - inset, inset), r)
    drawDomedRivet(Offset(inset, h - inset), r)
    drawDomedRivet(Offset(w - inset, h - inset), r)
}

internal fun DrawScope.drawCornerLBrackets() {
    val w = size.width
    val h = size.height
    if (w < 32.dp.toPx() || h < 28.dp.toPx()) {
        drawCornerStuds()
        return
    }
    val arm = 11.dp.toPx()
    val thick = 3.2.dp.toPx()
    val inset = 1.dp.toPx()
    val rivetR = 1.6.dp.toPx()

    // Helper for each corner (sx, sy in {-1, 1})
    val corners = arrayOf(
        intArrayOf(1, 1),
        intArrayOf(-1, 1),
        intArrayOf(1, -1),
        intArrayOf(-1, -1)
    )
    for (c in corners) {
        val sx = c[0]
        val sy = c[1]
        val cx = if (sx > 0) inset else w - inset
        val cy = if (sy > 0) inset else h - inset

        val bracket = Path().apply {
            moveTo(cx, cy)
            lineTo(cx + sx * arm, cy)
            lineTo(cx + sx * (arm - thick * 0.6f), cy + sy * thick)
            lineTo(cx + sx * thick, cy + sy * thick)
            lineTo(cx + sx * thick, cy + sy * (arm - thick * 0.6f))
            lineTo(cx, cy + sy * arm)
            close()
        }
        drawPath(bracket, color = OsrsIronBody)
        drawPath(
            bracket,
            color = OsrsIronEdgeLight,
            style = Stroke(width = 0.9.dp.toPx(), join = StrokeJoin.Bevel)
        )
        // Corner rivet
        drawDomedRivet(
            center = Offset(cx + sx * (thick + 1.8.dp.toPx()), cy + sy * (thick + 1.8.dp.toPx())),
            radius = rivetR
        )
    }
}

internal fun DrawScope.drawCornerHingeStraps() {
    val w = size.width
    val h = size.height
    if (w < 56.dp.toPx() || h < 34.dp.toPx()) {
        drawCornerLBrackets()
        return
    }
    val strapLen = 18.dp.toPx()
    val strapH = 5.dp.toPx()
    val insetY = 2.dp.toPx()
    val insetX = 1.5.dp.toPx()
    val rivetR = 1.6.dp.toPx()

    val corners = arrayOf(
        intArrayOf(1, 1),
        intArrayOf(-1, 1),
        intArrayOf(1, -1),
        intArrayOf(-1, -1)
    )
    for (c in corners) {
        val sx = c[0]
        val sy = c[1]
        val x0 = if (sx > 0) insetX else w - insetX
        val y0 = if (sy > 0) insetY else h - insetY
        val yInner = y0 + sy * strapH
        val yMid = (y0 + yInner) * 0.5f

        // Spearhead horizontal hinge strap
        val strap = Path().apply {
            moveTo(x0, y0)
            lineTo(x0 + sx * (strapLen - 3.dp.toPx()), y0)
            lineTo(x0 + sx * strapLen, yMid)
            lineTo(x0 + sx * (strapLen - 3.dp.toPx()), yInner)
            lineTo(x0, yInner)
            close()
        }
        drawPath(strap, color = OsrsIronBody)
        drawPath(
            strap,
            color = OsrsIronEdgeLight,
            style = Stroke(width = 1.dp.toPx(), join = StrokeJoin.Bevel)
        )
        // Dual rivets along the hinge strap
        drawDomedRivet(Offset(x0 + sx * 4.5.dp.toPx(), yMid), rivetR)
        drawDomedRivet(Offset(x0 + sx * 11.5.dp.toPx(), yMid), rivetR)
    }
}
