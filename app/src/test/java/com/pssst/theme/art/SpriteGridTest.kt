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

    private val allowedChars = setOf('.', 'o', 'h', 'b', 's', 'a', 'd')

    @Test
    fun allArtIcons_are16x16_withValidRolesAnd1PxOutline() {
        assertEquals("Expected 16 sprites in ArtIcons.ALL_SPRITES", 16, ArtIcons.ALL_SPRITES.size)

        for ((name, sprite) in ArtIcons.ALL_SPRITES) {
            assertEquals("$name width must be 16", 16, sprite.width)
            assertEquals("$name height must be 16", 16, sprite.height)

            var hasOutline = false
            var hasBody = false
            for (y in 0 until 16) {
                val row = sprite.rows[y]
                assertEquals("$name row $y length must be 16", 16, row.length)
                for (x in 0 until 16) {
                    val ch = row[x]
                    assertTrue("$name has invalid role '$ch' at ($x,$y)", ch in allowedChars)
                    if (ch == 'o') hasOutline = true
                    if (ch == 'h' || ch == 'b' || ch == 's' || ch == 'a' || ch == 'd') {
                        hasBody = true
                        // Every interior fill pixel must be enclosed by outline/other non-transparent pixels
                        // (i.e., never directly adjacent to a '.' cell without an 'o' outline between them)
                        val neighbors = listOf(
                            x - 1 to y,
                            x + 1 to y,
                            x to y - 1,
                            x to y + 1
                        )
                        for ((nx, ny) in neighbors) {
                            if (nx in 0..15 && ny in 0..15) {
                                val nCh = sprite.rows[ny][nx]
                                assertFalse(
                                    "$name has un-outlined fill '$ch' at ($x,$y) adjacent to '.' at ($nx,$ny)",
                                    nCh == '.'
                                )
                            } else {
                                // On the canvas boundary, non-outline pixels should not touch the outer edge
                                assertTrue(
                                    "$name has un-outlined fill '$ch' touching canvas boundary at ($x,$y)",
                                    false
                                )
                            }
                        }
                    }
                }
            }
            assertTrue("$name must contain 'o' outline pixels", hasOutline)
            assertTrue("$name must contain interior body pixels", hasBody)
        }
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
            "LEATHER" to SpriteTones.LEATHER
        )
        for ((name, tones) in toneSets) {
            for (role in listOf('o', 'h', 'b', 's', 'a', 'd')) {
                val c = tones.colorFor(role)
                assertFalse(
                    "SpriteTones.$name role '$role' uses stack-count colour #${Integer.toHexString(c)}",
                    c in forbidden
                )
            }
        }
    }
}
