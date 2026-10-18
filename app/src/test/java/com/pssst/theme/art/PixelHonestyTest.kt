package com.pssst.theme.art

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class PixelHonestyTest {

    private val forbiddenThemeTokens = listOf(
        "Brush.linearGradient",
        "Brush.radialGradient",
        "Brush.verticalGradient",
        "Brush.horizontalGradient",
        "drawCircle",
        "drawOval",
        "drawRoundRect",
        "RoundedCornerShape",
        "StrokeCap.Round",
        "StrokeJoin.Round",
        "dashPathEffect",
        "ShaderBrush",
        "WaxSealMedallion",
        "OsrsWaxCrimson",
        "OsrsWaxHighlight",
        "OsrsWaxShadow",
        "OsrsLedgerRowAlt",
        "OsrsStoneFlecks"
    )

    @Test
    fun themeAndArtFilesContainZeroVectorGradientsCurvesOrRemovedTokens() {
        val themeRoot = listOf(
            File("src/main/java/com/pssst/theme"),
            File("app/src/main/java/com/pssst/theme")
        ).firstOrNull { it.exists() }
            ?: error("Could not locate src/main/java/com/pssst/theme directory")

        val ktFiles = themeRoot.walkTopDown().filter { it.isFile && it.extension == "kt" }.toList()
        assertTrue("Expected theme .kt files to exist", ktFiles.isNotEmpty())

        val violations = mutableListOf<String>()
        for (file in ktFiles) {
            val lines = file.readLines()
            lines.forEachIndexed { idx, rawLine ->
                val codePart = rawLine.substringBefore("//")
                for (token in forbiddenThemeTokens) {
                    if (codePart.contains(token)) {
                        violations.add("${file.name}:${idx + 1} contains forbidden token '$token'")
                    }
                }
            }
        }

        assertTrue(
            "Pixel-honesty guard failed:\n" + violations.joinToString("\n"),
            violations.isEmpty()
        )
    }
}
