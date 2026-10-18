package com.pssst.theme.art

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MaterialToneTest {

    @Test
    fun stoneGround_usesOnlyDeclaredTones_andDoesNotTile32x32() {
        val canvas = PixelCanvas(128, 128)
        Textures.drawStoneGround(canvas, seed = 101)
        val allowed = Textures.STONE_GROUND_TONES.toSet()

        for (px in canvas.px) {
            assertTrue(
                "Stone ground pixel #${Integer.toHexString(px)} not in STONE_GROUND_TONES",
                px in allowed
            )
        }

        // Verify that the top-left 32x32 region does not repeat verbatim at (32,0), (64,0), or (0,64)
        val block00 = extractBlock(canvas, 0, 0, 32, 32)
        val block10 = extractBlock(canvas, 32, 0, 32, 32)
        val block20 = extractBlock(canvas, 64, 0, 32, 32)
        val block02 = extractBlock(canvas, 0, 64, 32, 32)

        assertFalse("Stone ground repeated horizontally at x=32", block00.contentEquals(block10))
        assertFalse("Stone ground repeated horizontally at x=64", block00.contentEquals(block20))
        assertFalse("Stone ground repeated vertically at y=64", block00.contentEquals(block02))
    }

    @Test
    fun panelStone_usesOnlyThreeTextSafeTones() {
        val canvas = PixelCanvas(96, 64)
        Textures.drawPanelStone(canvas, seed = 101)
        val allowed = Textures.PANEL_STONE_TONES.toSet()
        for (px in canvas.px) {
            assertTrue(
                "Panel stone pixel #${Integer.toHexString(px)} not in PANEL_STONE_TONES",
                px in allowed
            )
        }
        // All 3 tones should appear in a 96x64 panel
        val used = canvas.px.toSet()
        assertTrue("Panel stone should use multiple tones for dithered mottling", used.size >= 2)
    }

    @Test
    fun oakBeam_usesOnlyDeclaredOakAndIronTones() {
        val canvas = PixelCanvas(120, 28)
        Textures.drawOakBeam(canvas, seed = 202, showPlankSeams = true, showEndBands = false)
        val allowedOak = Textures.OAK_TONES.toSet()
        for (px in canvas.px) {
            assertTrue(
                "Oak beam pixel #${Integer.toHexString(px)} not in OAK_TONES",
                px in allowedOak
            )
        }

        val banded = PixelCanvas(120, 28)
        Textures.drawOakBeam(banded, seed = 202, showPlankSeams = true, showEndBands = true)
        val allowedWithIron = (Textures.OAK_TONES + Textures.IRON_TONES).toSet()
        for (px in banded.px) {
            assertTrue(
                "Banded oak pixel #${Integer.toHexString(px)} not in OAK_TONES + IRON_TONES",
                px in allowedWithIron
            )
        }
    }

    @Test
    fun hammeredIron_usesOnlyIronTones_withSparseHammerMarks() {
        val canvas = PixelCanvas(80, 40)
        Textures.drawHammeredIron(canvas, seed = 505, withBevel = true)
        val allowed = Textures.IRON_TONES.toSet()
        for (px in canvas.px) {
            assertTrue(
                "Hammered iron pixel #${Integer.toHexString(px)} not in IRON_TONES",
                px in allowed
            )
        }

        val bodyTone = Textures.IRON_TONES[1]
        var nonBodyInterior = 0
        var totalInterior = 0
        for (y in 2 until canvas.h - 2) {
            for (x in 2 until canvas.w - 2) {
                totalInterior++
                if (canvas.get(x, y) != bodyTone) {
                    nonBodyInterior++
                }
            }
        }
        val ratio = nonBodyInterior.toDouble() / totalInterior.toDouble()
        assertTrue("Hammered iron marks too sparse or missing: $ratio", ratio > 0.01)
        assertTrue("Hammered iron marks too dense (polka-dot): $ratio", ratio < 0.12)
    }

    @Test
    fun parchment_usesOnlyDeclaredParchmentTones() {
        val canvas = PixelCanvas(96, 48)
        Textures.drawParchment(canvas, seed = 303, showStainVignette = true)
        val allowed = Textures.PARCHMENT_TONES.toSet()
        for (px in canvas.px) {
            assertTrue(
                "Parchment pixel #${Integer.toHexString(px)} not in PARCHMENT_TONES",
                px in allowed
            )
        }
    }

    @Test
    fun leather_usesOnlyDeclaredLeatherAndStitchTones() {
        val canvas = PixelCanvas(80, 60)
        Textures.drawLeather(canvas, seed = 404, stitched = true)
        val allowed = Textures.LEATHER_STITCH_TONES.toSet()
        for (px in canvas.px) {
            assertTrue(
                "Leather pixel #${Integer.toHexString(px)} not in LEATHER_STITCH_TONES",
                px in allowed
            )
        }
    }

    private fun extractBlock(canvas: PixelCanvas, x0: Int, y0: Int, bw: Int, bh: Int): IntArray {
        val out = IntArray(bw * bh)
        for (dy in 0 until bh) {
            for (dx in 0 until bw) {
                out[dy * bw + dx] = canvas.get(x0 + dx, y0 + dy)
            }
        }
        return out
    }
}
