package com.pssst.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pssst.theme.art.EngravedRule
import com.pssst.theme.art.FrameWeight
import com.pssst.theme.art.Ornaments
import com.pssst.theme.art.PixelIcon
import com.pssst.theme.art.Sprite
import com.pssst.theme.art.SpriteTones
import com.pssst.theme.art.Textures
import com.pssst.theme.art.bookmarkTab
import com.pssst.theme.art.computeArtPx
import com.pssst.theme.art.ledgerPanelFrame
import com.pssst.theme.art.pixelArt
import com.pssst.theme.art.pixelBevelBox
import kotlin.math.roundToInt

private val OsrsColorScheme = darkColorScheme(
    primary = OsrsGold,
    onPrimary = Color.Black,
    primaryContainer = OsrsPanelLight,
    onPrimaryContainer = OsrsGold,
    secondary = OsrsGold,
    onSecondary = Color.Black,
    secondaryContainer = OsrsSlotRecessed,
    onSecondaryContainer = OsrsGold,
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

private val CheckboxCheckSprite = Sprite(
    "............",
    ".........oo.",
    "........ohbo",
    ".......ohbso",
    "..oo..ohbso.",
    ".ohboohbso..",
    "ohbbhhbso...",
    ".osbbbbso...",
    "..osbbso....",
    "...osso.....",
    "....oo......",
    "............"
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

/**
 * Applies a Carved Stone Ledger panel background with cached procedural pixel stone mottling,
 * outer 1-art-px dark frame, 1-art-px bevel, and wrought-iron corner hardware.
 */
fun Modifier.osrsPanel(
    cornerRadius: Dp = 0.dp,
    borderWidth: Dp = 1.dp,
    showRivets: Boolean = false,
    weight: FrameWeight = if (showRivets) FrameWeight.HEAVY else FrameWeight.MEDIUM,
    seed: Int = 101
): Modifier = this.ledgerPanelFrame(weight = weight, seed = seed)

/**
 * Applies a deep recessed carved-stone slot well as integer-aligned art-pixel rects (no bitmap allocation).
 */
fun Modifier.osrsSlot(
    hasItem: Boolean = true,
    cornerRadius: Dp = 0.dp,
    highlightBorderColor: Color? = null
): Modifier = this.pixelBevelBox(
    fill = if (hasItem) OsrsSlotRecessed else OsrsSlotEmpty,
    outline = OsrsOuterFrame,
    light = highlightBorderColor ?: OsrsBorderLight,
    dark = OsrsBorderDark,
    raised = false
)

/**
 * Engraved horizontal pixel groove with 3x3 stepped diamond end caps separating headers from bodies.
 */
@Composable
fun OsrsHeaderDivider(
    modifier: Modifier = Modifier,
    withEndCaps: Boolean = true,
    withCenterFlourish: Boolean = false
) {
    EngravedRule(
        modifier = modifier,
        withEndCaps = withEndCaps,
        withCenterFlourish = withCenterFlourish
    )
}

/**
 * Framed rectangular pixel progress bar with recessed trough, 1-art-px top highlight row,
 * 1-art-px bottom shadow row taken from the fill ramp, and 1-art-px 25/50/75% tick notches.
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
    val (hiRowColor, loRowColor) = when (color) {
        OsrsHerbGreen -> OsrsHerbLight to OsrsHerbDark
        OsrsGold -> OsrsGoldHighlight to OsrsGoldDark
        OsrsRed -> OsrsTextPrimary to OsrsRedDark
        else -> OsrsGoldHighlight to OsrsBorderDark
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight)
            .drawWithCache {
                val p = computeArtPx(density).toFloat()
                val w = size.width.roundToInt().toFloat()
                val h = size.height.roundToInt().toFloat()
                onDrawBehind {
                    if (w <= 0f || h <= 0f) return@onDrawBehind
                    // Outer 1-art-px dark frame
                    drawRect(color = OsrsOuterFrame, topLeft = Offset.Zero, size = Size(w, h))
                    val innerW = (w - 2f * p).coerceAtLeast(0f)
                    val innerH = (h - 2f * p).coerceAtLeast(0f)
                    if (innerW <= 0f || innerH <= 0f) return@onDrawBehind

                    // Recessed trough fill + 1-art-px top shadow
                    drawRect(color = OsrsSlotEmpty, topLeft = Offset(p, p), size = Size(innerW, innerH))
                    drawRect(color = OsrsBorderDark, topLeft = Offset(p, p), size = Size(innerW, p.coerceAtMost(innerH)))

                    // Integer-snapped progress fill
                    val fillSteps = ((innerW / p) * clamped).roundToInt()
                    val fillW = (fillSteps * p).coerceIn(0f, innerW)
                    if (fillW >= p) {
                        drawRect(color = color, topLeft = Offset(p, p), size = Size(fillW, innerH))
                        if (innerH >= 3f * p) {
                            drawRect(color = hiRowColor, topLeft = Offset(p, p), size = Size(fillW, p))
                            drawRect(color = loRowColor, topLeft = Offset(p, p + innerH - p), size = Size(fillW, p))
                        }
                        if (fillW < innerW) {
                            drawRect(color = OsrsOuterFrame, topLeft = Offset(p + fillW, p), size = Size(p, innerH))
                        }
                    }

                    // 1-art-px quarter gauge tick notches
                    if (!thin && innerW >= 24f * p) {
                        for (q in 1..3) {
                            val tickX = p + ((innerW * (q * 0.25f)) / p).roundToInt() * p
                            drawRect(color = OsrsOuterFrame, topLeft = Offset(tickX, p), size = Size(p, innerH))
                        }
                    }
                }
            }
    )
}

enum class OsrsButtonVariant {
    GOLD,
    STONE,
    DANGER
}

/**
 * Pixel plaque button with raised/pressed 1-art-px bevels and seated 3x3 pixel rivets.
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

    val fg = textColor ?: when (variant) {
        OsrsButtonVariant.GOLD -> Color.Black
        OsrsButtonVariant.STONE -> OsrsGold
        OsrsButtonVariant.DANGER -> OsrsRed
    }

    Box(
        modifier = modifier
            .height(if (compact) 30.dp else 38.dp)
            .pixelArt(key = Triple("OsrsButton", variant, isPressed to compact)) {
                val outline = OsrsOuterFrame.toArgb()
                when (variant) {
                    OsrsButtonVariant.GOLD -> {
                        val bodyPair = if (isPressed) {
                            intArrayOf(OsrsGoldShadow.toArgb(), OsrsGoldDark.toArgb())
                        } else {
                            intArrayOf(OsrsGoldDark.toArgb(), OsrsGold.toArgb())
                        }
                        for (y in 0 until h) {
                            for (x in 0 until w) {
                                val v = 96 + ((h - 1 - y) * 96) / h.coerceAtLeast(1)
                                set(x, y, com.pssst.theme.art.PixelNoise.quantise(v, bodyPair, x, y))
                            }
                        }
                        bevelBox(
                            x = 0,
                            y = 0,
                            bw = w,
                            bh = h,
                            outline = outline,
                            light = OsrsGoldHighlight.toArgb(),
                            dark = OsrsGoldShadow.toArgb(),
                            raised = !isPressed
                        )
                    }

                    OsrsButtonVariant.STONE -> {
                        if (isPressed) {
                            clear(OsrsSlotEmpty.toArgb())
                        } else {
                            Textures.drawPanelStone(this, seed = 115)
                        }
                        bevelBox(
                            x = 0,
                            y = 0,
                            bw = w,
                            bh = h,
                            outline = outline,
                            light = OsrsBorderLight.toArgb(),
                            dark = OsrsBorderDark.toArgb(),
                            raised = !isPressed
                        )
                    }

                    OsrsButtonVariant.DANGER -> {
                        if (isPressed) {
                            clear(OsrsSlotEmpty.toArgb())
                        } else {
                            Textures.drawPanelStone(this, seed = 119)
                        }
                        bevelBox(
                            x = 0,
                            y = 0,
                            bw = w,
                            bh = h,
                            outline = outline,
                            light = OsrsRed.toArgb(),
                            dark = OsrsRedDark.toArgb(),
                            raised = !isPressed
                        )
                    }
                }
                if (!compact && w >= 24 && h >= 8) {
                    val ry = (h / 2 - 1).coerceAtLeast(2)
                    rivet(3, ry, size = 3)
                    rivet(w - 6, ry, size = 3)
                }
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = if (compact) 10.dp else 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (enabled) fg else OsrsTextMuted,
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
 * Square pixel-bevel OSRS status badge.
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
    val lightEdge = if (isReadyFill) OsrsHerbLight else OsrsBrassMid
    val darkEdge = if (isReadyFill) OsrsHerbDark else OsrsBorderDark
    val baseMod = modifier
        .pixelBevelBox(
            fill = containerColor,
            outline = OsrsOuterFrame,
            light = lightEdge,
            dark = darkEdge,
            raised = true
        )
        .let { m -> if (onClick != null) m.clickable(onClick = onClick) else m }
        .padding(horizontal = 7.dp, vertical = 2.dp)

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
 * Square pixel filter chip: selected chip is recessed with gold text and a 3x3 pixel diamond
 * (or custom [leading] icon when provided); unselected chip is raised dark stone with muted text.
 */
@Composable
fun OsrsFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null
) {
    val hasLeading = leading != null
    val chipMod = modifier
        .pixelArt(key = Triple("OsrsFilterChip", selected, hasLeading)) {
            val outline = OsrsOuterFrame.toArgb()
            if (selected) {
                clear(OsrsSlotEmpty.toArgb())
                bevelBox(
                    x = 0,
                    y = 0,
                    bw = w,
                    bh = h,
                    outline = outline,
                    light = OsrsGold.toArgb(),
                    dark = OsrsBorderDark.toArgb(),
                    raised = false
                )
                if (!hasLeading && w >= 16 && h >= 7) {
                    Ornaments.drawPixelDiamond(
                        canvas = this,
                        cx = 4,
                        cy = h / 2,
                        radius = 1,
                        fill = OsrsGold.toArgb(),
                        highlight = OsrsGoldHighlight.toArgb(),
                        shadow = OsrsGoldDark.toArgb()
                    )
                }
            } else {
                clear(OsrsPanelDark.toArgb())
                bevelBox(
                    x = 0,
                    y = 0,
                    bw = w,
                    bh = h,
                    outline = outline,
                    light = OsrsBorderLight.toArgb(),
                    dark = OsrsBorderDark.toArgb(),
                    raised = true
                )
            }
        }
        .clickable(onClick = onClick)
        .padding(
            start = if (selected && !hasLeading) 11.dp else 7.dp,
            end = 8.dp,
            top = 4.dp,
            bottom = 4.dp
        )

    Box(modifier = chipMod, contentAlignment = Alignment.Center) {
        if (leading != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                leading()
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
        } else {
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
}

/**
 * Segmented pixel tabs sitting in a recessed stone-and-iron rail.
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
                        .bookmarkTab(selected = isSelected, seed = 404 + index * 7)
                        .clickable { onTabSelected(index) }
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
 * Recessed stone toggle switch with a riveted pixel slider thumb.
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
            .osrsSlot(hasItem = false, highlightBorderColor = if (checked) OsrsHerbGreen else OsrsBorderLight)
            .clickable { onCheckedChange(!checked) }
            .padding(3.dp),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .size(width = 20.dp, height = 18.dp)
                .pixelArt(key = Pair("OsrsSwitchThumb", checked)) {
                    val fill = if (checked) OsrsHerbGreen.toArgb() else OsrsBrassMid.toArgb()
                    val hi = if (checked) OsrsHerbLight.toArgb() else OsrsGoldHighlight.toArgb()
                    val lo = if (checked) OsrsHerbDark.toArgb() else OsrsBorderDark.toArgb()
                    clear(fill)
                    bevelBox(
                        x = 0,
                        y = 0,
                        bw = w,
                        bh = h,
                        outline = OsrsOuterFrame.toArgb(),
                        light = hi,
                        dark = lo,
                        raised = true
                    )
                    if (w >= 7 && h >= 7) {
                        rivet(w / 2 - 1, h / 2 - 1, size = 3)
                    }
                }
        )
    }
}

/**
 * Recessed stone-slot checkbox with a 12x12 pixel check mark sprite when checked.
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
                    highlightBorderColor = if (checked) OsrsGold else OsrsBorderOutline
                ),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                PixelIcon(
                    sprite = CheckboxCheckSprite,
                    tones = SpriteTones.GOLD,
                    modifier = Modifier.size(12.dp)
                )
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
 * Recessed dark-vellum parchment text input with pixel bevel and brass corner marks.
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
        else -> OsrsBrassMid
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
                .pixelArt(key = Pair("OsrsTextField", borderColor.toArgb())) {
                    Textures.drawParchment(this, seed = 309, showStainVignette = true)
                    bevelBox(
                        x = 0,
                        y = 0,
                        bw = w,
                        bh = h,
                        outline = OsrsOuterFrame.toArgb(),
                        light = borderColor.toArgb(),
                        dark = OsrsBorderDark.toArgb(),
                        raised = false
                    )
                    Ornaments.drawCornerMarks(
                        canvas = this,
                        inset = 2,
                        arm = 3,
                        baseColor = OsrsBrassMid.toArgb(),
                        highlightColor = OsrsGoldHighlight.toArgb()
                    )
                }
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
                            color = OsrsTextMuted,
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
