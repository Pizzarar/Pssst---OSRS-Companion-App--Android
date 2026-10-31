package com.pssst.theme.art

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsHerbGreen
import com.pssst.theme.OsrsRed
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.OsrsTextPrimary
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class ArtContrastTest {

    @Test
    fun panelStone_meets45To1ForAllTextIncludingRedAndHerbGreen() {
        val allText = listOf(
            "OsrsTextPrimary" to OsrsTextPrimary,
            "OsrsTextMuted" to OsrsTextMuted,
            "OsrsGold" to OsrsGold,
            "OsrsHerbGreen" to OsrsHerbGreen,
            "OsrsRed" to OsrsRed
        )
        for (bg in Textures.PANEL_STONE_TONES) {
            for ((name, fg) in allText) {
                val ratio = contrastRatio(fg.toArgb(), bg)
                assertTrue(
                    "$name on panel #${Integer.toHexString(bg)} is ${"%.2f".format(ratio)}:1 (< 4.5:1)",
                    ratio >= 4.5
                )
            }
        }
    }

    /**
     * Copy that sits straight on the slate ground (empty states, setup notes, section labels) uses
     * primary, muted or gold text and must clear 4.5:1 on every block face. Red and herb green only
     * appear as large status text or inside panels and badges there, so they need 3:1.
     */
    @Test
    fun slateGroundFaces_meetTextContrast() {
        val bodyText = listOf(
            "OsrsTextPrimary" to OsrsTextPrimary,
            "OsrsTextMuted" to OsrsTextMuted,
            "OsrsGold" to OsrsGold
        )
        val statusText = listOf("OsrsHerbGreen" to OsrsHerbGreen, "OsrsRed" to OsrsRed)
        for (bg in Textures.STONE_GROUND_BASE_TONES) {
            for ((name, fg) in bodyText) {
                val ratio = contrastRatio(fg.toArgb(), bg)
                assertTrue("$name on slate #${Integer.toHexString(bg)} is ${"%.2f".format(ratio)}:1 (< 4.5:1)", ratio >= 4.5)
            }
            for ((name, fg) in statusText) {
                val ratio = contrastRatio(fg.toArgb(), bg)
                assertTrue("$name on slate #${Integer.toHexString(bg)} is ${"%.2f".format(ratio)}:1 (< 3:1)", ratio >= 3.0)
            }
        }
    }

    @Test
    fun oakBeam_meets45To1ForGoldPrimaryMutedAndHerbGreen() {
        val allowedText = listOf(
            "OsrsTextPrimary" to OsrsTextPrimary,
            "OsrsTextMuted" to OsrsTextMuted,
            "OsrsGold" to OsrsGold,
            "OsrsHerbGreen" to OsrsHerbGreen
        )
        for (bg in Textures.OAK_TONES) {
            for ((name, fg) in allowedText) {
                val ratio = contrastRatio(fg.toArgb(), bg)
                assertTrue(
                    "$name on oak #${Integer.toHexString(bg)} is ${"%.2f".format(ratio)}:1 (< 4.5:1)",
                    ratio >= 4.5
                )
            }
        }
    }

    @Test
    fun parchment_meets45To1ForPrimaryMutedAndGold() {
        val allowedText = listOf(
            "OsrsTextPrimary" to OsrsTextPrimary,
            "OsrsTextMuted" to OsrsTextMuted,
            "OsrsGold" to OsrsGold
        )
        for (bg in Textures.PARCHMENT_TONES) {
            for ((name, fg) in allowedText) {
                val ratio = contrastRatio(fg.toArgb(), bg)
                assertTrue(
                    "$name on parchment #${Integer.toHexString(bg)} is ${"%.2f".format(ratio)}:1 (< 4.5:1)",
                    ratio >= 4.5
                )
            }
        }
    }

    @Test
    fun leather_meets45To1ForPrimaryMutedAndGold() {
        val allowedText = listOf(
            "OsrsTextPrimary" to OsrsTextPrimary,
            "OsrsTextMuted" to OsrsTextMuted,
            "OsrsGold" to OsrsGold
        )
        for (bg in Textures.LEATHER_TONES) {
            for ((name, fg) in allowedText) {
                val ratio = contrastRatio(fg.toArgb(), bg)
                assertTrue(
                    "$name on leather #${Integer.toHexString(bg)} is ${"%.2f".format(ratio)}:1 (< 4.5:1)",
                    ratio >= 4.5
                )
            }
        }
    }

    companion object {
        fun contrastRatio(argbA: Int, argbB: Int): Double {
            val l1 = relativeLuminance(argbA)
            val l2 = relativeLuminance(argbB)
            val lighter = max(l1, l2)
            val darker = min(l1, l2)
            return (lighter + 0.05) / (darker + 0.05)
        }

        private fun relativeLuminance(argb: Int): Double {
            val r = srgbChannelToLinear((argb shr 16) and 0xFF)
            val g = srgbChannelToLinear((argb shr 8) and 0xFF)
            val b = srgbChannelToLinear(argb and 0xFF)
            return 0.2126 * r + 0.7152 * g + 0.0722 * b
        }

        private fun srgbChannelToLinear(c255: Int): Double {
            val c = c255 / 255.0
            return if (c <= 0.03928) c / 12.92 else ((c + 0.055) / 1.055).pow(2.4)
        }
    }
}
