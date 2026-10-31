package com.pssst.theme.art

import androidx.compose.ui.graphics.toArgb
import com.pssst.theme.OsrsGreen
import com.pssst.theme.OsrsOuterFrame
import com.pssst.theme.OsrsWhite
import com.pssst.theme.OsrsYellow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class SceneGridTest {

    private val emptyStates = EmptyStateKind.entries.map { it.name to EmptyStates.gridFor(it) }
    private val vignettes = listOf(
        "KINGDOM_HEALTHY" to EmptyStates.kingdomGrid(isWarning = false),
        "KINGDOM_WARNING" to EmptyStates.kingdomGrid(isWarning = true),
        "SLAYER_ACTIVE" to EmptyStates.slayerGrid(isGilded = false),
        "SLAYER_COMPLETE" to EmptyStates.slayerGrid(isGilded = true)
    )
    private val allScenes = emptyStates + vignettes + ("INFORMANT_CREST" to EmptyStates.INFORMANT_CREST_GRID)

    @Test
    fun sceneGrids_haveExactDimensions() {
        for ((name, grid) in emptyStates) {
            assertEquals("$name width", EmptyStates.SCENE_WIDTH, grid.width)
            assertEquals("$name height", EmptyStates.SCENE_HEIGHT, grid.height)
        }
        for ((name, grid) in vignettes) {
            assertEquals("$name width", EmptyStates.VIGNETTE_WIDTH, grid.width)
            assertEquals("$name height", EmptyStates.VIGNETTE_HEIGHT, grid.height)
        }
        assertEquals(EmptyStates.CREST_SIZE, EmptyStates.INFORMANT_CREST_GRID.width)
        assertEquals(EmptyStates.CREST_SIZE, EmptyStates.INFORMANT_CREST_GRID.height)
    }

    @Test
    fun sceneGrids_outlineIsOuterFrame_andNoStackCountColours() {
        val forbidden = setOf(OsrsGreen.toArgb(), OsrsYellow.toArgb(), OsrsWhite.toArgb())
        assertEquals(OsrsOuterFrame.toArgb(), ScenePalettes.Outline)
        for ((name, grid) in allScenes) {
            grid.palette['o']?.let { assertEquals("$name 'o' must be OsrsOuterFrame", OsrsOuterFrame.toArgb(), it) }
            for ((ch, argb) in grid.palette) {
                assertFalse("$name '$ch' uses a stack-count colour", argb in forbidden)
                assertTrue("$name '$ch' must be opaque", (argb ushr 24) == 0xFF)
            }
        }
    }

    /**
     * Silhouettes must not dissolve into the dark stone they sit on: the dominant non-outline
     * colour clears 1.8:1 against the lightest panel stone, and so do at least 60% of body pixels.
     */
    @Test
    fun sceneGrids_clearContrastFloorAgainstPanelStone() {
        val stone = Textures.PANEL_STONE_TONES.maxByOrNull { luminanceOf(it) }!!
        for ((name, grid) in allScenes) {
            val counts = mutableMapOf<Int, Int>()
            for (row in grid.rows) {
                for (ch in row) {
                    if (ch == '.' || ch == ' ' || ch == 'o') continue
                    val argb = grid.palette.getValue(ch)
                    counts[argb] = (counts[argb] ?: 0) + 1
                }
            }
            val dominant = counts.maxByOrNull { it.value }!!.key
            val dominantRatio = ArtContrastTest.contrastRatio(dominant, stone)
            assertTrue(
                "$name dominant colour #${Integer.toHexString(dominant)} is ${"%.2f".format(dominantRatio)}:1",
                dominantRatio >= 1.8
            )
            val total = counts.values.sum()
            val lit = counts.filter { ArtContrastTest.contrastRatio(it.key, stone) >= 1.8 }.values.sum()
            assertTrue("$name only ${lit * 100 / total}% of body pixels clear 1.8:1", lit * 10 >= total * 6)
        }
    }

    @Test
    fun drawSceneGrid_paintsEveryOpaqueCell() {
        val grid = EmptyStates.gridFor(EmptyStateKind.QUEST_SCROLL)
        val canvas = PixelCanvas(grid.width, grid.height)
        canvas.drawSceneGrid(grid)
        for (y in 0 until grid.height) {
            for (x in 0 until grid.width) {
                val ch = grid.rows[y][x]
                val expected = if (ch == '.') 0 else grid.palette.getValue(ch)
                assertEquals("($x,$y)", expected, canvas.get(x, y))
            }
        }
    }

    @Test
    fun launcherIcons_useOnlyAxisAlignedWholePixelRects() {
        for (file in listOf("ic_launcher_foreground.xml", "ic_launcher_monochrome.xml")) {
            for ((_, data) in launcherPaths(file)) {
                assertTrue("$file has non-rect path commands: $data", PIXEL_PATH.matches(data))
                for (n in Regex("-?\\d+").findAll(data).map { it.value.toInt() }) {
                    assertEquals("$file coordinate $n is not on the 3-unit pixel grid", 0, n % 3)
                }
            }
        }
    }

    @Test
    fun launcherForeground_matchesInformantCrestGrid() {
        val drawn = mutableMapOf<Pair<Int, Int>, Int>()
        for ((colour, data) in launcherPaths("ic_launcher_foreground.xml")) {
            for (m in RECT.findAll(data)) {
                val (x, y, w, h) = m.destructured.toList().map { it.toInt() / 3 }
                for (yy in y until y + h) for (xx in x until x + w) drawn[xx to yy] = colour
            }
        }
        val grid = EmptyStates.INFORMANT_CREST_GRID
        for (y in 0 until grid.height) {
            for (x in 0 until grid.width) {
                val ch = grid.rows[y][x]
                val expected = if (ch == '.') null else grid.palette.getValue(ch) and 0xFFFFFF
                assertEquals("launcher pixel ($x,$y)", expected, drawn[x to y])
            }
        }
    }

    private fun launcherPaths(name: String): List<Pair<Int, String>> {
        val res = listOf(File("src/main/res/drawable"), File("app/src/main/res/drawable"))
            .first { it.isDirectory }
        val xml = File(res, name).readText()
        return PATH.findAll(xml).map { m ->
            m.groupValues[1].toInt(16) to m.groupValues[2]
        }.toList()
    }

    private fun luminanceOf(argb: Int): Double = ArtContrastTest.contrastRatio(argb, 0xFF000000.toInt())

    companion object {
        private val PATH = Regex("""fillColor="#([0-9A-Fa-f]{6})"\s+android:pathData="([^"]+)"""")
        private val RECT = Regex("""M(\d+),(\d+)h(\d+)v(\d+)h-\d+z""")
        private val PIXEL_PATH = Regex("""(M\d+,\d+h\d+v\d+h-\d+z)+""")
    }
}
