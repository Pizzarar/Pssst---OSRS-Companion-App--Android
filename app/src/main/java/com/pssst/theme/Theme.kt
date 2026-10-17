package com.pssst.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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

/**
 * Applies a classic OSRS stone panel background with top-to-bottom gradient
 * and beveled highlighted top/left and shaded bottom/right borders.
 */
fun Modifier.osrsPanel(
    cornerRadius: Dp = 6.dp,
    borderWidth: Dp = 1.dp
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
            color = OsrsBorderOutline,
            shape = RoundedCornerShape(cornerRadius)
        )
        .drawBehind {
            val strokeWidth = borderWidth.toPx()
            // Top highlight
            drawLine(
                color = OsrsBorderLight,
                start = Offset(0f, strokeWidth / 2),
                end = Offset(size.width, strokeWidth / 2),
                strokeWidth = strokeWidth
            )
            // Left highlight
            drawLine(
                color = OsrsBorderLight,
                start = Offset(strokeWidth / 2, 0f),
                end = Offset(strokeWidth / 2, size.height),
                strokeWidth = strokeWidth
            )
            // Bottom shadow
            drawLine(
                color = OsrsBorderDark,
                start = Offset(0f, size.height - strokeWidth / 2),
                end = Offset(size.width, size.height - strokeWidth / 2),
                strokeWidth = strokeWidth
            )
            // Right shadow
            drawLine(
                color = OsrsBorderDark,
                start = Offset(size.width - strokeWidth / 2, 0f),
                end = Offset(size.width - strokeWidth / 2, size.height),
                strokeWidth = strokeWidth
            )
        }
}

/**
 * Applies a recessed OSRS bank or inventory slot background.
 */
fun Modifier.osrsSlot(
    hasItem: Boolean = true,
    cornerRadius: Dp = 4.dp
): Modifier = composed {
    val fillColor = if (hasItem) OsrsSlotRecessed else OsrsSlotEmpty
    this
        .clip(RoundedCornerShape(cornerRadius))
        .background(fillColor)
        .border(1.dp, OsrsBorderOutline, RoundedCornerShape(cornerRadius))
        .drawBehind {
            val stroke = 1.dp.toPx()
            // Inset top/left dark shadow (recessed depth)
            drawLine(
                color = OsrsBorderDark,
                start = Offset(0f, stroke / 2),
                end = Offset(size.width, stroke / 2),
                strokeWidth = stroke
            )
            drawLine(
                color = OsrsBorderDark,
                start = Offset(stroke / 2, 0f),
                end = Offset(stroke / 2, size.height),
                strokeWidth = stroke
            )
            // Bottom/right slight highlight
            drawLine(
                color = OsrsBorderLight.copy(alpha = 0.5f),
                start = Offset(0f, size.height - stroke / 2),
                end = Offset(size.width, size.height - stroke / 2),
                strokeWidth = stroke
            )
            drawLine(
                color = OsrsBorderLight.copy(alpha = 0.5f),
                start = Offset(size.width - stroke / 2, 0f),
                end = Offset(size.width - stroke / 2, size.height),
                strokeWidth = stroke
            )
        }
}
