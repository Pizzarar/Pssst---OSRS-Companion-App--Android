package com.pssst.theme.art

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PixelCanvasTest {

    @Test
    fun computeArtPx_clampsToAtLeastTwoAndRoundsDensity() {
        assertEquals(2, computeArtPx(1.0f))
        assertEquals(2, computeArtPx(1.5f))
        assertEquals(2, computeArtPx(2.0f))
        assertEquals(3, computeArtPx(2.625f))
        assertEquals(3, computeArtPx(3.0f))
        assertEquals(4, computeArtPx(3.5f))
    }

    @Test
    fun pixelNoise_isDeterministicForIdenticalSeedAndCoordinates() {
        for (y in -16..32) {
            for (x in -16..32) {
                val h1 = PixelNoise.hash(x, y, 101)
                val h2 = PixelNoise.hash(x, y, 101)
                assertEquals(h1, h2)
                assertTrue("Hash out of 0..255 range: $h1", h1 in 0..255)

                val v1 = PixelNoise.value(x, y, 6, 202)
                val v2 = PixelNoise.value(x, y, 6, 202)
                assertEquals(v1, v2)
                assertTrue("Value noise out of 0..255 range: $v1", v1 in 0..255)

                val c1 = PixelNoise.cellular(x, y, 4, 303)
                val c2 = PixelNoise.cellular(x, y, 4, 303)
                assertEquals(c1, c2)
                assertTrue("Cellular noise out of 0..255 range: $c1", c1 in 0..255)
            }
        }
    }

    @Test
    fun quantise_onlyOutputsGivenTonesAndOrderingHoldsAtExtremes() {
        val tones = intArrayOf(0xFF101010.toInt(), 0xFF202020.toInt(), 0xFF303030.toInt(), 0xFF404040.toInt())
        val toneSet = tones.toSet()

        for (v in 0..255) {
            for (y in 0 until 8) {
                for (x in 0 until 8) {
                    val q = PixelNoise.quantise(v, tones, x, y)
                    assertTrue("Quantised value $q not in allowed tones", q in toneSet)
                }
            }
        }

        // Extreme 0 always selects darkest tone; extreme 255 always selects lightest tone
        for (y in 0 until 4) {
            for (x in 0 until 4) {
                assertEquals(tones.first(), PixelNoise.quantise(0, tones, x, y))
                assertEquals(tones.last(), PixelNoise.quantise(255, tones, x, y))
            }
        }
    }

    @Test
    fun midpointCircle_isSymmetricAcrossAllEightOctants() {
        val canvas = PixelCanvas(21, 21)
        val cx = 10
        val cy = 10
        val color = 0xFFFF981F.toInt()
        canvas.circle(cx, cy, 7, color, filled = false)

        for (dy in -10..10) {
            for (dx in -10..10) {
                val p = canvas.get(cx + dx, cy + dy)
                assertEquals(p, canvas.get(cx - dx, cy + dy))
                assertEquals(p, canvas.get(cx + dx, cy - dy))
                assertEquals(p, canvas.get(cx - dx, cy - dy))
                assertEquals(p, canvas.get(cx + dy, cy + dx))
                assertEquals(p, canvas.get(cx - dy, cy + dx))
                assertEquals(p, canvas.get(cx + dy, cy - dx))
                assertEquals(p, canvas.get(cx - dy, cy - dx))
            }
        }
    }

    @Test
    fun bevelBox_andBlit_writeExactIntegerPixels() {
        val c1 = PixelCanvas(16, 16)
        val c2 = PixelCanvas(16, 16)
        c1.bevelBox(0, 0, 16, 16, outline = 1, light = 2, dark = 3, fill = 4, raised = true)
        c2.bevelBox(0, 0, 16, 16, outline = 1, light = 2, dark = 3, fill = 4, raised = true)
        assertArrayEquals(c1.px, c2.px)
        assertEquals(1, c1.get(0, 0))
        assertEquals(2, c1.get(1, 1))
        assertEquals(3, c1.get(14, 14))
        assertEquals(4, c1.get(8, 8))

        val sprite = Sprite(
            ".o.",
            "ohs",
            ".o."
        )
        val sc = PixelCanvas(3, 3)
        sc.blit(sprite, SpriteTones.GOLD, 0, 0)
        assertEquals(0, sc.get(0, 0))
        assertEquals(SpriteTones.GOLD.outline, sc.get(1, 0))
        assertEquals(SpriteTones.GOLD.highlight, sc.get(1, 1))
        assertEquals(SpriteTones.GOLD.shadow, sc.get(2, 1))
    }
}
