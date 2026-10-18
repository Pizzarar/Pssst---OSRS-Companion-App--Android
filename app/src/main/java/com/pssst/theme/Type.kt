package com.pssst.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.pssst.R

val AlegreyaFontFamily = FontFamily(
    Font(R.font.alegreya_regular, FontWeight.Normal),
    Font(R.font.alegreya_medium, FontWeight.Medium),
    Font(R.font.alegreya_bold, FontWeight.SemiBold),
    Font(R.font.alegreya_bold, FontWeight.Bold),
    Font(R.font.alegreya_extrabold, FontWeight.ExtraBold)
)

val JetBrainsMonoFontFamily = FontFamily(
    Font(R.font.jetbrains_mono_regular, FontWeight.Normal),
    Font(R.font.jetbrains_mono_medium, FontWeight.Medium),
    Font(R.font.jetbrains_mono_bold, FontWeight.Bold)
)

val OsrsHardShadow = Shadow(
    color = Color.Black,
    offset = Offset(1.5f, 1.5f),
    blurRadius = 0f
)

val Typography = Typography(
    headlineMedium = TextStyle(
        fontFamily = AlegreyaFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        color = OsrsGold,
        shadow = OsrsHardShadow
    ),
    titleLarge = TextStyle(
        fontFamily = AlegreyaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        color = OsrsGold,
        shadow = OsrsHardShadow
    ),
    titleMedium = TextStyle(
        fontFamily = AlegreyaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = 22.sp,
        color = OsrsGold,
        shadow = OsrsHardShadow
    ),
    titleSmall = TextStyle(
        fontFamily = AlegreyaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        color = OsrsGold,
        shadow = OsrsHardShadow
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        color = OsrsTextPrimary
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        color = OsrsTextMuted
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        color = OsrsTextMuted
    ),
    labelLarge = TextStyle(
        fontFamily = AlegreyaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        color = OsrsGold
    ),
    labelMedium = TextStyle(
        fontFamily = AlegreyaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 16.sp,
        color = OsrsGold,
        shadow = OsrsHardShadow
    )
)
