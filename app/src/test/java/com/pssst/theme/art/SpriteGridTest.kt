package com.pssst.theme.art

import androidx.compose.ui.graphics.toArgb
import com.pssst.theme.OsrsGreen
import com.pssst.theme.OsrsWhite
import com.pssst.theme.OsrsYellow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SpriteGridTest {

    private val fillRoles = setOf('h', 'b', 's', 'a', 'd', 'l', 'm', 'k', 'g')
    private val allowedChars = fillRoles + setOf('.', 'o')

    @Test
    fun allArtIcons_are16x16_withValidRolesAnd1PxOutline() {
        assertEquals("Expected 16 sprites in ArtIcons.ALL_SPRITES", 16, ArtIcons.ALL_SPRITES.size)
        for ((name, sprite) in ArtIcons.ALL_SPRITES) {
            assert16x16(name, sprite)
            assertEnclosed(name, sprite)
        }
    }

    @Test
    fun ornamentSprites_areOutlinedAndEnclosed() {
        assert16x16("WatermarkScaleSprite", Ornaments.WatermarkScaleSprite)
        assertEnclosed("WatermarkScaleSprite", Ornaments.WatermarkScaleSprite)
        assertEnclosed("BulletDiamondSprite", Ornaments.BulletDiamondSprite)
    }

    @Test
    fun spriteToneSets_neverUseStackCountColours() {
        val forbidden = setOf(
            OsrsGreen.toArgb(),
            OsrsYellow.toArgb(),
            OsrsWhite.toArgb()
        )
        val toneSets = listOf(
            "GOLD" to SpriteTones.GOLD,
            "STONE" to SpriteTones.STONE,
            "HERB" to SpriteTones.HERB,
            "WOOD" to SpriteTones.WOOD,
            "PARCHMENT" to SpriteTones.PARCHMENT,
            "WARNING" to SpriteTones.WARNING,
            "LEATHER" to SpriteTones.LEATHER,
            "QUEST_BLUE" to SpriteTones.QUEST_BLUE,
            "TERRACOTTA_SPROUT" to SpriteTones.TERRACOTTA_SPROUT,
            "BLIGHT_WARNING" to SpriteTones.BLIGHT_WARNING,
            "WATERMARK_TONES" to Ornaments.WATERMARK_TONES
        )
        for ((name, tones) in toneSets) {
            for (role in fillRoles + 'o') {
                val c = tones.colorFor(role)
                assertFalse(
                    "SpriteTones.$name role '$role' uses stack-count colour #${Integer.toHexString(c)}",
                    c in forbidden
                )
                assertTrue("SpriteTones.$name role '$role' must be opaque", (c ushr 24) == 0xFF)
            }
        }
    }

    @Test
    fun secondaryRoles_fallBackToPrimaryRamp_soRetintsNeverLeaveHoles() {
        val gold = SpriteTones.GOLD
        assertEquals(gold.highlight, gold.colorFor('l'))
        assertEquals(gold.base, gold.colorFor('m'))
        assertEquals(gold.shadow, gold.colorFor('k'))
        assertEquals(gold.highlight, gold.colorFor('g'))
    }

    private fun assert16x16(name: String, sprite: Sprite) {
        assertEquals("$name width must be 16", 16, sprite.width)
        assertEquals("$name height must be 16", 16, sprite.height)
        val flat = sprite.rows.joinToString("")
        assertTrue("$name needs >= 20 transparent pixels", flat.count { it == '.' } >= 20)
        assertTrue("$name needs >= 12 outline pixels", flat.count { it == 'o' } >= 12)
    }

    private fun assertEnclosed(name: String, sprite: Sprite) {
        var hasOutline = false
        var hasBody = false
        for (y in 0 until sprite.height) {
            val row = sprite.rows[y]
            assertEquals("$name row $y length", sprite.width, row.length)
            for (x in 0 until sprite.width) {
                val ch = row[x]
                assertTrue("$name has invalid role '$ch' at ($x,$y)", ch in allowedChars)
                if (ch == 'o') hasOutline = true
                if (ch in fillRoles) {
                    hasBody = true
                    // Every fill pixel is enclosed by outline/other fill: never next to '.' or the canvas edge.
                    for ((nx, ny) in listOf(x - 1 to y, x + 1 to y, x to y - 1, x to y + 1)) {
                        if (nx in 0 until sprite.width && ny in 0 until sprite.height) {
                            assertFalse(
                                "$name has un-outlined fill '$ch' at ($x,$y) adjacent to '.' at ($nx,$ny)",
                                sprite.rows[ny][nx] == '.'
                            )
                        } else {
                            assertTrue("$name has un-outlined fill '$ch' touching canvas boundary at ($x,$y)", false)
                        }
                    }
                }
            }
        }
        assertTrue("$name must contain 'o' outline pixels", hasOutline)
        assertTrue("$name must contain interior body pixels", hasBody)
    }
}
