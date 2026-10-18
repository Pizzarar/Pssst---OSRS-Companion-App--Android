package com.pssst.theme.art

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pssst.theme.OsrsBorderDark
import com.pssst.theme.OsrsBorderLight
import com.pssst.theme.OsrsBrassMid
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsGoldDark
import com.pssst.theme.OsrsGoldHighlight
import com.pssst.theme.OsrsIronBody
import com.pssst.theme.OsrsIronEdgeLight
import com.pssst.theme.OsrsLeatherBase
import com.pssst.theme.OsrsLeatherHighlight
import com.pssst.theme.OsrsLeatherStitch
import com.pssst.theme.OsrsOakBase
import com.pssst.theme.OsrsOuterFrame
import com.pssst.theme.OsrsSlotEmpty
import com.pssst.theme.OsrsTextPrimary
import com.pssst.theme.OsrsWaxCrimson
import com.pssst.theme.OsrsWaxHighlight
import com.pssst.theme.OsrsWaxShadow
import com.pssst.theme.art.Ornaments.drawCornerFlourishes
import com.pssst.theme.art.Ornaments.drawDomedRivet
import com.pssst.theme.art.Ornaments.drawFacetedDiamond
import kotlin.math.cos
import kotlin.math.sin

/**
 * Gold-leaf lettering composable: vertical 3-stop gold-leaf gradient fill with a crisp
 * 1px hard dark shadow (never a blur).
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
    val goldBrush = Brush.verticalGradient(
        colors = listOf(
            OsrsGoldHighlight,
            OsrsGold,
            OsrsGoldDark
        )
    )
    Text(
        text = text,
        modifier = modifier,
        style = style.copy(
            brush = goldBrush,
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
 * Chamfered stone-and-brass title plaque for screen titles, hero headers, or onboarding banners.
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
            .chamferedPlaqueFrame()
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
 * Section header plaque with gold-leaf lettering, chiseled diamond bullet, optional trailing slot,
 * and an engraved bottom rule.
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
                BulletGlyph(size = 7.dp, color = OsrsBrassMid)
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
 * Chamfered octagonal brass-and-oak plaque frame modifier with side rivets and inner corner flourishes.
 */
fun Modifier.chamferedPlaqueFrame(
    seed: Int = 208
): Modifier = this.drawWithCache {
    val oak = Textures.oakBrush(seed = seed)
    val cut = 6.dp.toPx()
    val strokePx = 1.4.dp.toPx()
    val rivetR = 2.dp.toPx()

    onDrawBehind {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(cut, 1f)
            lineTo(w - cut, 1f)
            lineTo(w - 1f, cut)
            lineTo(w - 1f, h - cut)
            lineTo(w - cut, h - 1f)
            lineTo(cut, h - 1f)
            lineTo(1f, h - cut)
            lineTo(1f, cut)
            close()
        }
        drawPath(path, color = OsrsOakBase)
        drawPath(path, brush = oak, alpha = 0.85f)
        drawPath(
            path,
            color = OsrsBrassMid,
            style = Stroke(width = strokePx, join = StrokeJoin.Bevel)
        )
        drawCornerFlourishes(inset = 4.dp.toPx(), armLength = 7.dp.toPx(), color = OsrsGoldDark.copy(alpha = 0.65f))
        if (h > 20.dp.toPx() && w > 48.dp.toPx()) {
            drawDomedRivet(Offset(8.dp.toPx(), h * 0.5f), rivetR)
            drawDomedRivet(Offset(w - 8.dp.toPx(), h * 0.5f), rivetR)
        }
    }
}

/**
 * Physical bookmark / index tab modifier for segmented tabs:
 * - Selected tab: warm worn-leather surface, raised brass top clip, side saddle-stitching,
 *   and gold-leaf bottom accent.
 * - Unselected tab: recessed dark stone slot with iron divider rim.
 */
fun Modifier.bookmarkTab(
    selected: Boolean,
    seed: Int = 404
): Modifier = this.drawWithCache {
    val leatherBrush = Textures.leatherBrush(seed = seed)
    val strokePx = 1.2.dp.toPx()
    val clipH = 2.5.dp.toPx()
    val rivetR = 1.5.dp.toPx()

    onDrawBehind {
        val w = size.width
        val h = size.height
        if (selected) {
            // Raised leather tab body
            drawRect(color = OsrsLeatherBase)
            drawRect(brush = leatherBrush, alpha = 0.88f)
            // Top brass bookmark clip
            drawRect(
                color = OsrsBrassMid,
                topLeft = Offset(0f, 0f),
                size = Size(w, clipH)
            )
            drawLine(OsrsGoldHighlight, Offset(0f, 0f), Offset(w, 0f), 1.dp.toPx())
            // Side bevels & bottom gold-leaf bar
            drawLine(OsrsLeatherHighlight, Offset(0f, clipH), Offset(0f, h), strokePx)
            drawLine(OsrsBorderDark, Offset(w - strokePx, clipH), Offset(w - strokePx, h), strokePx)
            drawRect(
                color = OsrsGold,
                topLeft = Offset(0f, h - 2.5.dp.toPx()),
                size = Size(w, 2.5.dp.toPx())
            )
            // Corner clip rivets
            if (w > 40.dp.toPx()) {
                drawDomedRivet(Offset(6.dp.toPx(), 5.dp.toPx()), rivetR)
                drawDomedRivet(Offset(w - 6.dp.toPx(), 5.dp.toPx()), rivetR)
            }
        } else {
            // Recessed stone tab well
            drawRect(color = OsrsSlotEmpty)
            drawLine(OsrsBorderDark, Offset(0f, 0f), Offset(w, 0f), strokePx * 1.3f)
            drawLine(OsrsBorderDark, Offset(0f, 0f), Offset(0f, h), strokePx)
            drawLine(OsrsBorderLight.copy(alpha = 0.55f), Offset(w - strokePx, 0f), Offset(w - strokePx, h), strokePx)
            drawLine(OsrsBorderDark, Offset(0f, h - strokePx), Offset(w, h - strokePx), strokePx)
        }
    }
}

/**
 * 12-lobe scalloped Crimson Sealing Wax medallion stamped with a gold-leaf numeral or label.
 */
@Composable
fun WaxSealMedallion(
    label: String,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val cx = this.size.width * 0.5f
            val cy = this.size.height * 0.5f
            val baseR = this.size.minDimension * 0.44f
            val lobes = 12
            val sealPath = Path()
            val steps = 60
            for (i in 0..steps) {
                val angle = (i.toFloat() / steps) * 2f * Math.PI.toFloat()
                val wave = sin(angle * lobes) * (baseR * 0.065f)
                val r = baseR + wave
                val x = cx + cos(angle) * r
                val y = cy + sin(angle) * r
                if (i == 0) sealPath.moveTo(x, y) else sealPath.lineTo(x, y)
            }
            sealPath.close()

            // Outer drop shadow & crimson wax body
            drawCircle(color = OsrsOuterFrame, radius = baseR * 1.04f, center = Offset(cx + 1f, cy + 1.5f))
            drawPath(sealPath, color = OsrsWaxCrimson)
            drawPath(
                sealPath,
                color = OsrsWaxHighlight,
                style = Stroke(width = 1.4.dp.toPx(), join = StrokeJoin.Round)
            )
            // Stamped inner recess ring
            drawCircle(
                color = OsrsWaxShadow,
                radius = baseR * 0.72f,
                center = Offset(cx, cy)
            )
            drawCircle(
                color = OsrsBrassMid.copy(alpha = 0.75f),
                radius = baseR * 0.72f,
                center = Offset(cx, cy),
                style = Stroke(width = 1.dp.toPx())
            )
        }
        GoldLeafText(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
    }
}
