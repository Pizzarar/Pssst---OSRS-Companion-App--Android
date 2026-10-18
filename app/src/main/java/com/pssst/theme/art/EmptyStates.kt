package com.pssst.theme.art

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pssst.theme.OsrsBorderDark
import com.pssst.theme.OsrsBorderLight
import com.pssst.theme.OsrsBrassMid
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsGoldDark
import com.pssst.theme.OsrsIronBody
import com.pssst.theme.OsrsSlotEmpty
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.art.Ornaments.drawDomedRivet

/**
 * Code-drawn vignette subjects for empty states across the app.
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
 * Renders a code-drawn vignette inside a chamfered stone-and-brass medallion with the
 * screen's existing empty-state copy.
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
        EmptyStateVignette(kind = kind, size = 56.dp)
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
 * Standalone 56dp code-drawn vignette medallion for previewing or inline embedding.
 */
@Composable
fun EmptyStateVignette(
    kind: EmptyStateKind,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cut = w * 0.16f

        // Chamfered octagonal stone medallion frame
        val plate = Path().apply {
            moveTo(cut, 2f)
            lineTo(w - cut, 2f)
            lineTo(w - 2f, cut)
            lineTo(w - 2f, h - cut)
            lineTo(w - cut, h - 2f)
            lineTo(cut, h - 2f)
            lineTo(2f, h - cut)
            lineTo(2f, cut)
            close()
        }
        drawPath(plate, color = OsrsSlotEmpty)
        drawPath(
            plate,
            color = OsrsBorderLight,
            style = Stroke(width = 1.5.dp.toPx(), join = StrokeJoin.Bevel)
        )
        // Side rivets
        drawDomedRivet(Offset(w * 0.5f, 4.5.dp.toPx()), radius = 1.8.dp.toPx())
        drawDomedRivet(Offset(w * 0.5f, h - 4.5.dp.toPx()), radius = 1.8.dp.toPx())

        when (kind) {
            EmptyStateKind.CLOSED_CHEST -> drawClosedChestVignette()
            EmptyStateKind.FALLOW_PATCH -> drawFallowPatchVignette()
            EmptyStateKind.EMPTY_BIRDHOUSE -> drawEmptyBirdhouseVignette()
            EmptyStateKind.POTION_RACK -> drawPotionRackVignette()
            EmptyStateKind.QUEST_SCROLL -> drawQuestScrollVignette()
            EmptyStateKind.SUNDIAL -> drawSundialVignette()
            EmptyStateKind.LEDGER_BOOKMARK -> drawLedgerBookmarkVignette()
        }
    }
}

private fun DrawScope.drawClosedChestVignette() {
    val w = size.width
    val h = size.height
    val stroke = Stroke(width = 1.7.dp.toPx(), cap = StrokeCap.Square, join = StrokeJoin.Bevel)
    // Chest body fill
    val body = Path().apply {
        moveTo(w * 0.22f, h * 0.42f)
        lineTo(w * 0.30f, h * 0.28f)
        lineTo(w * 0.70f, h * 0.28f)
        lineTo(w * 0.78f, h * 0.42f)
        lineTo(w * 0.78f, h * 0.72f)
        lineTo(w * 0.22f, h * 0.72f)
        close()
    }
    drawPath(body, color = OsrsIronBody)
    drawPath(body, color = OsrsBrassMid, style = stroke)
    // Lid seam & iron straps
    drawLine(OsrsBrassMid, Offset(w * 0.22f, h * 0.44f), Offset(w * 0.78f, h * 0.44f), stroke.width)
    drawLine(OsrsBorderLight, Offset(w * 0.35f, h * 0.28f), Offset(w * 0.35f, h * 0.72f), stroke.width)
    drawLine(OsrsBorderLight, Offset(w * 0.65f, h * 0.28f), Offset(w * 0.65f, h * 0.72f), stroke.width)
    // Heavy padlock hasp
    drawRect(
        color = OsrsGoldDark,
        topLeft = Offset(w * 0.45f, h * 0.40f),
        size = Size(w * 0.10f, h * 0.14f)
    )
}

private fun DrawScope.drawFallowPatchVignette() {
    val w = size.width
    val h = size.height
    val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Square, join = StrokeJoin.Bevel)
    // Isometric stone-bordered allotment bed
    val bed = Path().apply {
        moveTo(w * 0.50f, h * 0.30f)
        lineTo(w * 0.80f, h * 0.50f)
        lineTo(w * 0.50f, h * 0.72f)
        lineTo(w * 0.20f, h * 0.50f)
        close()
    }
    drawPath(bed, color = OsrsIronBody)
    drawPath(bed, color = OsrsBrassMid, style = stroke)
    // Furrow lines across the fallow soil
    drawLine(OsrsBorderLight, Offset(w * 0.34f, h * 0.45f), Offset(w * 0.56f, h * 0.60f), 1.3.dp.toPx())
    drawLine(OsrsBorderLight, Offset(w * 0.44f, h * 0.38f), Offset(w * 0.66f, h * 0.53f), 1.3.dp.toPx())
}

private fun DrawScope.drawEmptyBirdhouseVignette() {
    val w = size.width
    val h = size.height
    val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Square, join = StrokeJoin.Bevel)
    // Pitched roof
    val roof = Path().apply {
        moveTo(w * 0.24f, h * 0.44f)
        lineTo(w * 0.50f, h * 0.24f)
        lineTo(w * 0.76f, h * 0.44f)
    }
    drawPath(roof, color = OsrsBrassMid, style = stroke)
    // Box walls
    drawRect(
        color = OsrsIronBody,
        topLeft = Offset(w * 0.32f, h * 0.42f),
        size = Size(w * 0.36f, h * 0.28f)
    )
    drawRect(
        color = OsrsBorderLight,
        topLeft = Offset(w * 0.32f, h * 0.42f),
        size = Size(w * 0.36f, h * 0.28f),
        style = stroke
    )
    // Dark empty hole & post
    drawCircle(color = OsrsBorderDark, radius = w * 0.065f, center = Offset(w * 0.50f, h * 0.53f))
    drawLine(OsrsBrassMid, Offset(w * 0.50f, h * 0.70f), Offset(w * 0.50f, h * 0.80f), stroke.width)
}

private fun DrawScope.drawPotionRackVignette() {
    val w = size.width
    val h = size.height
    val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Square, join = StrokeJoin.Bevel)
    // Wooden rack rails
    drawLine(OsrsBrassMid, Offset(w * 0.22f, h * 0.45f), Offset(w * 0.78f, h * 0.45f), stroke.width)
    drawLine(OsrsBrassMid, Offset(w * 0.22f, h * 0.68f), Offset(w * 0.78f, h * 0.68f), stroke.width)
    // Two empty glass vials
    for (cx in floatArrayOf(w * 0.38f, w * 0.62f)) {
        val vial = Path().apply {
            moveTo(cx - w * 0.04f, h * 0.30f)
            lineTo(cx + w * 0.04f, h * 0.30f)
            lineTo(cx + w * 0.04f, h * 0.40f)
            lineTo(cx + w * 0.09f, h * 0.50f)
            lineTo(cx + w * 0.09f, h * 0.66f)
            lineTo(cx - w * 0.09f, h * 0.66f)
            lineTo(cx - w * 0.09f, h * 0.50f)
            lineTo(cx - w * 0.04f, h * 0.40f)
            close()
        }
        drawPath(vial, color = OsrsIronBody)
        drawPath(vial, color = OsrsBorderLight, style = stroke)
    }
}

private fun DrawScope.drawQuestScrollVignette() {
    val w = size.width
    val h = size.height
    val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Square, join = StrokeJoin.Bevel)
    // Rolled scroll body
    drawRect(
        color = OsrsIronBody,
        topLeft = Offset(w * 0.30f, h * 0.28f),
        size = Size(w * 0.40f, h * 0.44f)
    )
    drawRect(
        color = OsrsBrassMid,
        topLeft = Offset(w * 0.30f, h * 0.28f),
        size = Size(w * 0.40f, h * 0.44f),
        style = stroke
    )
    // Top and bottom wooden scroll rods
    drawLine(OsrsGoldDark, Offset(w * 0.24f, h * 0.28f), Offset(w * 0.76f, h * 0.28f), stroke.width * 1.3f)
    drawLine(OsrsGoldDark, Offset(w * 0.24f, h * 0.72f), Offset(w * 0.76f, h * 0.72f), stroke.width * 1.3f)
    // Scribe lines inside scroll
    drawLine(OsrsBorderLight, Offset(w * 0.37f, h * 0.42f), Offset(w * 0.63f, h * 0.42f), 1.2.dp.toPx())
    drawLine(OsrsBorderLight, Offset(w * 0.37f, h * 0.52f), Offset(w * 0.57f, h * 0.52f), 1.2.dp.toPx())
}

private fun DrawScope.drawSundialVignette() {
    val w = size.width
    val h = size.height
    val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Square, join = StrokeJoin.Bevel)
    val center = Offset(w * 0.50f, h * 0.50f)
    val r = w * 0.23f
    drawCircle(color = OsrsIronBody, radius = r, center = center)
    drawCircle(color = OsrsBrassMid, radius = r, center = center, style = stroke)
    // Gnomon triangle shadow
    val gnomon = Path().apply {
        moveTo(center.x, center.y - r * 0.65f)
        lineTo(center.x + r * 0.55f, center.y + r * 0.25f)
        lineTo(center.x, center.y + r * 0.25f)
        close()
    }
    drawPath(gnomon, color = OsrsGoldDark.copy(alpha = 0.45f))
    drawPath(gnomon, color = OsrsGoldDark, style = stroke)
}

private fun DrawScope.drawLedgerBookmarkVignette() {
    val w = size.width
    val h = size.height
    val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Square, join = StrokeJoin.Bevel)
    val ribbon = Path().apply {
        moveTo(w * 0.34f, h * 0.24f)
        lineTo(w * 0.66f, h * 0.24f)
        lineTo(w * 0.66f, h * 0.74f)
        lineTo(w * 0.50f, h * 0.62f)
        lineTo(w * 0.34f, h * 0.74f)
        close()
    }
    drawPath(ribbon, color = OsrsIronBody)
    drawPath(ribbon, color = OsrsBrassMid, style = stroke)
    with(Ornaments) {
        drawFacetedDiamond(Offset(w * 0.50f, h * 0.44f), halfSize = 3.5.dp.toPx(), fill = OsrsGoldDark)
    }
}
