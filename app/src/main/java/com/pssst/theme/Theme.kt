package com.pssst.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val OsrsColorScheme = darkColorScheme(
    primary = OsrsGold,
    onPrimary = Color.Black,
    primaryContainer = OsrsPanelLight,
    onPrimaryContainer = OsrsGold,
    secondary = OsrsYellow,
    onSecondary = Color.Black,
    secondaryContainer = OsrsSlotRecessed,
    onSecondaryContainer = OsrsYellow,
    tertiary = OsrsHerbGreen,
    onTertiary = Color.Black,
    background = OsrsDarkStone,
    onBackground = OsrsTextPrimary,
    surface = OsrsPanelDark,
    onSurface = OsrsTextPrimary,
    surfaceVariant = OsrsPanelLight,
    onSurfaceVariant = OsrsTextMuted,
    outline = OsrsBorderLight,
    outlineVariant = OsrsBorderOutline,
    error = OsrsRed,
    onError = Color.Black
)

@Composable
fun PssstTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = OsrsColorScheme,
        typography = Typography,
        content = content
    )
}

private fun DrawScope.drawStoneSpeckle() {
    val step = 14.dp.toPx().coerceAtLeast(10f)
    val dot = 1.25.dp.toPx().coerceAtLeast(1f)
    val cols = (size.width / step).toInt()
    val rows = (size.height / step).toInt()
    for (r in 0..rows) {
        for (c in 0..cols) {
            val hash = ((c * 73856093) xor (r * 19349663)) and 0x7fffffff
            if (hash % 3 == 0) continue
            val ox = (c + 0.2f + (hash % 7) * 0.09f) * step
            val oy = (r + 0.2f + ((hash shr 3) % 7) * 0.09f) * step
            if (ox < size.width - 3f && oy < size.height - 3f) {
                val isLight = (hash and 1) == 0
                drawRect(
                    color = if (isLight) OsrsBorderLight.copy(alpha = 0.16f) else OsrsOuterFrame.copy(alpha = 0.26f),
                    topLeft = Offset(ox, oy),
                    size = Size(dot, dot)
                )
            }
        }
    }
}

private fun DrawScope.drawCornerRivets(insetPx: Float, rivetSizePx: Float) {
    val pts = listOf(
        Offset(insetPx, insetPx),
        Offset(size.width - insetPx - rivetSizePx, insetPx),
        Offset(insetPx, size.height - insetPx - rivetSizePx),
        Offset(size.width - insetPx - rivetSizePx, size.height - insetPx - rivetSizePx)
    )
    for (pt in pts) {
        drawRect(color = OsrsOuterFrame, topLeft = pt - Offset(1f, 1f), size = Size(rivetSizePx + 2f, rivetSizePx + 2f))
        drawRect(color = OsrsRivetDark, topLeft = pt, size = Size(rivetSizePx, rivetSizePx))
        drawRect(color = OsrsRivetLight, topLeft = pt, size = Size(rivetSizePx * 0.6f, rivetSizePx * 0.6f))
    }
}

/**
 * Applies a Chiseled Stone Ledger OSRS panel background with subtle stone grain,
 * outer dark frame, inner 3D raised bevel, and optional corner hardware rivets.
 */
fun Modifier.osrsPanel(
    cornerRadius: Dp = 2.dp,
    borderWidth: Dp = 1.dp,
    showRivets: Boolean = false
): Modifier = composed {
    this
        .clip(RoundedCornerShape(cornerRadius))
        .background(
            Brush.verticalGradient(
                colors = listOf(OsrsPanelLight, OsrsPanelDark)
            )
        )
        .border(
            width = borderWidth,
            color = OsrsOuterFrame,
            shape = RoundedCornerShape(cornerRadius)
        )
        .drawBehind {
            drawStoneSpeckle()
            val outer = borderWidth.toPx()
            val bevel = 1.dp.toPx()
            val inset = outer + bevel / 2f
            // Inner raised top/left highlight
            drawLine(
                color = OsrsBorderLight.copy(alpha = 0.55f),
                start = Offset(outer, inset),
                end = Offset(size.width - outer, inset),
                strokeWidth = bevel
            )
            drawLine(
                color = OsrsBorderLight.copy(alpha = 0.55f),
                start = Offset(inset, outer),
                end = Offset(inset, size.height - outer),
                strokeWidth = bevel
            )
            // Inner bottom/right deep shadow
            drawLine(
                color = OsrsBorderDark.copy(alpha = 0.75f),
                start = Offset(outer, size.height - inset),
                end = Offset(size.width - outer, size.height - inset),
                strokeWidth = bevel
            )
            drawLine(
                color = OsrsBorderDark.copy(alpha = 0.75f),
                start = Offset(size.width - inset, outer),
                end = Offset(size.width - inset, size.height - outer),
                strokeWidth = bevel
            )
            if (showRivets && size.width > 48.dp.toPx() && size.height > 36.dp.toPx()) {
                drawCornerRivets(insetPx = 5.dp.toPx(), rivetSizePx = 2.5.dp.toPx())
            }
        }
}

/**
 * Applies a deep recessed OSRS slot well (top/left dark inset shadow, bottom/right rim highlight).
 */
fun Modifier.osrsSlot(
    hasItem: Boolean = true,
    cornerRadius: Dp = 2.dp,
    highlightBorderColor: Color? = null
): Modifier = composed {
    val fillColor = if (hasItem) OsrsSlotRecessed else OsrsSlotEmpty
    this
        .clip(RoundedCornerShape(cornerRadius))
        .background(fillColor)
        .border(1.dp, highlightBorderColor ?: OsrsOuterFrame, RoundedCornerShape(cornerRadius))
        .drawBehind {
            val outer = 1.dp.toPx()
            val shadowW = 1.25.dp.toPx()
            val hiW = 1.dp.toPx()
            // Deep top/left inset shadow
            drawLine(
                color = OsrsBorderDark,
                start = Offset(outer, outer + shadowW / 2f),
                end = Offset(size.width - outer, outer + shadowW / 2f),
                strokeWidth = shadowW
            )
            drawLine(
                color = OsrsBorderDark,
                start = Offset(outer + shadowW / 2f, outer),
                end = Offset(outer + shadowW / 2f, size.height - outer),
                strokeWidth = shadowW
            )
            // Bottom/right rim highlight
            drawLine(
                color = OsrsBorderLight.copy(alpha = 0.55f),
                start = Offset(outer, size.height - outer - hiW / 2f),
                end = Offset(size.width - outer, size.height - outer - hiW / 2f),
                strokeWidth = hiW
            )
            drawLine(
                color = OsrsBorderLight.copy(alpha = 0.55f),
                start = Offset(size.width - outer - hiW / 2f, outer),
                end = Offset(size.width - outer - hiW / 2f, size.height - outer),
                strokeWidth = hiW
            )
        }
}

/**
 * Engraved 2dp horizontal stone groove separating card headers from card bodies.
 */
@Composable
fun OsrsHeaderDivider(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp)
    ) {
        val half = size.height / 2f
        drawLine(
            color = OsrsBorderDark,
            start = Offset(0f, half / 2f),
            end = Offset(size.width, half / 2f),
            strokeWidth = half
        )
        drawLine(
            color = OsrsBorderLight.copy(alpha = 0.55f),
            start = Offset(0f, half + half / 2f),
            end = Offset(size.width, half + half / 2f),
            strokeWidth = half
        )
    }
}

/**
 * Framed rectangular OSRS progress bar with recessed dark-stone trough and 3-band shaded fill.
 */
@Composable
fun OsrsProgressBar(
    progress: Float,
    color: Color = OsrsHerbGreen,
    thin: Boolean = false,
    height: Dp? = null,
    modifier: Modifier = Modifier
) {
    val clamped = progress.coerceIn(0f, 1f)
    val barHeight = height ?: if (thin) 6.dp else 8.dp
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight)
            .clip(RoundedCornerShape(2.dp))
    ) {
        val w = size.width
        val h = size.height
        val stroke = 1.dp.toPx()

        // Trough fill
        drawRect(color = OsrsSlotEmpty, size = size)
        // Trough top inner shadow
        drawRect(
            color = OsrsBorderDark.copy(alpha = 0.85f),
            topLeft = Offset(stroke, stroke),
            size = Size((w - stroke * 2).coerceAtLeast(0f), stroke)
        )

        // 3-band shaded fill
        val innerW = (w - stroke * 2).coerceAtLeast(0f) * clamped
        val innerH = (h - stroke * 2).coerceAtLeast(0f)
        if (innerW > 0.5f && innerH > 0f) {
            // Midtone body
            drawRect(
                color = color,
                topLeft = Offset(stroke, stroke),
                size = Size(innerW, innerH)
            )
            // Top specular band
            val bandH = (1.25.dp.toPx()).coerceAtMost(innerH / 2.5f)
            drawRect(
                color = Color.White.copy(alpha = 0.28f),
                topLeft = Offset(stroke, stroke),
                size = Size(innerW, bandH)
            )
            // Bottom shade band
            drawRect(
                color = Color.Black.copy(alpha = 0.35f),
                topLeft = Offset(stroke, stroke + innerH - bandH),
                size = Size(innerW, bandH)
            )
            // Crisp vertical end-cap line when partially filled
            if (clamped < 0.99f) {
                drawLine(
                    color = OsrsOuterFrame,
                    start = Offset(stroke + innerW, stroke),
                    end = Offset(stroke + innerW, h - stroke),
                    strokeWidth = stroke
                )
            }
        }

        // Outer 1dp chiseled frame
        drawRect(
            color = OsrsOuterFrame,
            topLeft = Offset(stroke / 2f, stroke / 2f),
            size = Size(w - stroke, h - stroke),
            style = Stroke(width = stroke)
        )
    }
}

enum class OsrsButtonVariant {
    GOLD,
    STONE,
    DANGER
}

/**
 * Chiseled 3dp-chamfered OSRS plaque button with raised/pressed 3D bevels.
 */
@Composable
fun OsrsButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: OsrsButtonVariant = OsrsButtonVariant.GOLD,
    textColor: Color? = null,
    compact: Boolean = false,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val bgBrush = when (variant) {
        OsrsButtonVariant.GOLD -> Brush.verticalGradient(
            if (isPressed) listOf(OsrsGoldDark, OsrsGold) else listOf(OsrsGold, OsrsGoldDark)
        )
        OsrsButtonVariant.STONE -> Brush.verticalGradient(
            if (isPressed) listOf(OsrsSlotEmpty, OsrsSlotRecessed) else listOf(OsrsPanelLight, OsrsPanelDark)
        )
        OsrsButtonVariant.DANGER -> Brush.verticalGradient(
            if (isPressed) listOf(OsrsSlotEmpty, OsrsPanelDark) else listOf(OsrsPanelLight, OsrsSlotEmpty)
        )
    }
    val hiColor = when (variant) {
        OsrsButtonVariant.GOLD -> OsrsGoldHighlight
        OsrsButtonVariant.STONE -> OsrsBorderLight
        OsrsButtonVariant.DANGER -> OsrsRed.copy(alpha = 0.65f)
    }
    val loColor = when (variant) {
        OsrsButtonVariant.GOLD -> OsrsGoldShadow
        OsrsButtonVariant.STONE -> OsrsBorderDark
        OsrsButtonVariant.DANGER -> OsrsBorderDark
    }
    val topLeftBevel = if (isPressed) loColor else hiColor
    val bottomRightBevel = if (isPressed) hiColor else loColor
    val fg = textColor ?: when (variant) {
        OsrsButtonVariant.GOLD -> Color.Black
        OsrsButtonVariant.STONE -> OsrsYellow
        OsrsButtonVariant.DANGER -> OsrsRed
    }

    Box(
        modifier = modifier
            .height(if (compact) 30.dp else 38.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(bgBrush)
            .border(1.dp, OsrsOuterFrame, RoundedCornerShape(2.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .drawBehind {
                val outer = 1.dp.toPx()
                val bevel = 1.dp.toPx()
                val inset = outer + bevel / 2f
                drawLine(topLeftBevel, Offset(outer, inset), Offset(size.width - outer, inset), bevel)
                drawLine(topLeftBevel, Offset(inset, outer), Offset(inset, size.height - outer), bevel)
                drawLine(bottomRightBevel, Offset(outer, size.height - inset), Offset(size.width - outer, size.height - inset), bevel)
                drawLine(bottomRightBevel, Offset(size.width - inset, outer), Offset(size.width - inset, size.height - outer), bevel)
            }
            .padding(horizontal = if (compact) 10.dp else 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (enabled) fg else fg.copy(alpha = 0.45f),
            fontSize = if (compact) 12.sp else 14.sp,
            fontWeight = FontWeight.Bold,
            style = if (variant == OsrsButtonVariant.GOLD) {
                MaterialTheme.typography.labelLarge
            } else {
                MaterialTheme.typography.labelLarge.copy(shadow = OsrsHardShadow)
            },
            modifier = Modifier.offset {
                if (isPressed) IntOffset(0, 1.dp.roundToPx()) else IntOffset.Zero
            }
        )
    }
}

/**
 * Slim chiseled rectangular OSRS status badge.
 */
@Composable
fun OsrsStatusBadge(
    text: String,
    containerColor: Color,
    contentColor: Color,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isReadyFill = containerColor == OsrsHerbGreen
    val bgMod = if (isReadyFill) {
        Modifier.background(Brush.verticalGradient(listOf(Color(0xFF5CD660), Color(0xFF388E3C))))
    } else {
        Modifier.background(containerColor)
    }
    val baseMod = modifier
        .clip(RoundedCornerShape(2.dp))
        .then(bgMod)
        .border(1.dp, if (isReadyFill) OsrsOuterFrame else OsrsBorderOutline.copy(alpha = 0.7f), RoundedCornerShape(2.dp))
        .let { m -> if (onClick != null) m.clickable(onClick = onClick) else m }
        .padding(horizontal = 6.dp, vertical = 2.dp)

    Box(modifier = baseMod, contentAlignment = Alignment.Center) {
        Text(
            text = text,
            color = contentColor,
            fontSize = 10.sp,
            fontWeight = if (isReadyFill) FontWeight.ExtraBold else FontWeight.Bold,
            maxLines = 1,
            style = if (isReadyFill) {
                MaterialTheme.typography.labelSmall
            } else {
                MaterialTheme.typography.labelSmall.copy(shadow = OsrsHardShadow)
            }
        )
    }
}

/**
 * Slim selectable filter chip: flat subtle tab when idle, gold-underlined/bordered when selected.
 */
@Composable
fun OsrsFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chipMod = modifier
        .clip(RoundedCornerShape(2.dp))
        .background(if (selected) OsrsSlotEmpty else OsrsPanelDark.copy(alpha = 0.6f))
        .border(
            width = 1.dp,
            color = if (selected) OsrsGold else OsrsBorderOutline.copy(alpha = 0.6f),
            shape = RoundedCornerShape(2.dp)
        )
        .clickable(onClick = onClick)
        .padding(horizontal = 8.dp, vertical = 4.dp)

    Box(modifier = chipMod, contentAlignment = Alignment.Center) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) OsrsGold else OsrsTextMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelSmall.copy(shadow = OsrsHardShadow)
        )
    }
}

/**
 * Flat horizontal sub-tab strip with an active gold bottom bar indicator (no nested boxed tiles).
 */
@Composable
fun OsrsSegmentedTabs(
    tabs: List<Pair<String, Int>>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(OsrsSlotEmpty)
            .border(1.dp, OsrsOuterFrame, RoundedCornerShape(2.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, (title, count) ->
                val isSelected = index == selectedIndex
                val label = if (count > 0) "$title ($count)" else title
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(if (isSelected) OsrsPanelDark else Color.Transparent)
                        .clickable { onTabSelected(index) }
                        .drawBehind {
                            if (index > 0) {
                                drawLine(
                                    color = OsrsOuterFrame,
                                    start = Offset(0f, 4.dp.toPx()),
                                    end = Offset(0f, size.height - 4.dp.toPx()),
                                    strokeWidth = 1.dp.toPx()
                                )
                            }
                            if (isSelected) {
                                val indH = 2.dp.toPx()
                                drawRect(
                                    color = OsrsGold,
                                    topLeft = Offset(0f, size.height - indH),
                                    size = Size(size.width, indH)
                                )
                            }
                        }
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall.copy(shadow = OsrsHardShadow),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = when {
                            isSelected -> OsrsGold
                            count > 0 -> OsrsHerbGreen
                            else -> OsrsTextMuted
                        }
                    )
                }
            }
        }
    }
}

/**
 * Recessed stone toggle switch with a beveled rectangular slider thumb.
 */
@Composable
fun OsrsSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(width = 48.dp, height = 26.dp)
            .osrsSlot(hasItem = false, cornerRadius = 2.dp, highlightBorderColor = if (checked) OsrsHerbGreen else OsrsOuterFrame)
            .clickable { onCheckedChange(!checked) }
            .padding(3.dp),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        val thumbBg = if (checked) OsrsHerbGreen else OsrsPanelLight
        val thumbHi = if (checked) Color.White.copy(alpha = 0.4f) else OsrsBorderLight
        val thumbLo = if (checked) Color.Black.copy(alpha = 0.45f) else OsrsBorderDark
        Box(
            modifier = Modifier
                .size(width = 20.dp, height = 18.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(thumbBg)
                .border(1.dp, OsrsOuterFrame, RoundedCornerShape(2.dp))
                .drawBehind {
                    val b = 1.dp.toPx()
                    drawLine(thumbHi, Offset(b, b), Offset(size.width - b, b), b)
                    drawLine(thumbHi, Offset(b, b), Offset(b, size.height - b), b)
                    drawLine(thumbLo, Offset(b, size.height - b), Offset(size.width - b, size.height - b), b)
                    drawLine(thumbLo, Offset(size.width - b, b), Offset(size.width - b, size.height - b), b)
                }
        )
    }
}

/**
 * Recessed stone-slot checkbox with beveled gold fill when checked.
 */
@Composable
fun OsrsCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .osrsSlot(
                    hasItem = checked,
                    cornerRadius = 2.dp,
                    highlightBorderColor = if (checked) OsrsGold else OsrsBorderOutline
                ),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Canvas(modifier = Modifier.size(12.dp)) {
                    val p = Path().apply {
                        moveTo(size.width * 0.15f, size.height * 0.52f)
                        lineTo(size.width * 0.42f, size.height * 0.80f)
                        lineTo(size.width * 0.86f, size.height * 0.22f)
                    }
                    drawPath(p, color = OsrsGold, style = Stroke(width = 2.2.dp.toPx()))
                }
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (checked) OsrsGold else OsrsTextMuted
        )
    }
}

/**
 * Recessed stone-well text input replacing Material floating-cutout OutlinedTextField.
 */
@Composable
fun OsrsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    mono: Boolean = false,
    supportingText: String? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor = when {
        isError -> OsrsRed
        isFocused -> OsrsGold
        else -> OsrsBorderOutline
    }

    Column(modifier = modifier.fillMaxWidth()) {
        if (label.isNotBlank()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(shadow = OsrsHardShadow),
                fontWeight = FontWeight.Bold,
                color = when {
                    isError -> OsrsRed
                    isFocused -> OsrsGold
                    else -> OsrsTextMuted
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            cursorBrush = SolidColor(OsrsGold),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                capitalization = if (mono) androidx.compose.ui.text.input.KeyboardCapitalization.None else androidx.compose.ui.text.input.KeyboardCapitalization.Sentences,
                autoCorrectEnabled = !mono
            ),
            textStyle = TextStyle(
                color = OsrsTextPrimary,
                fontSize = 14.sp,
                fontFamily = if (mono) JetBrainsMonoFontFamily else FontFamily.Default
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .osrsSlot(hasItem = false, cornerRadius = 2.dp, highlightBorderColor = borderColor)
                .onFocusChanged { isFocused = it.isFocused }
                .padding(horizontal = 12.dp),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = OsrsTextMuted.copy(alpha = 0.45f),
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    innerTextField()
                }
            }
        )
        if (!supportingText.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodySmall,
                color = OsrsRed
            )
        }
    }
}

