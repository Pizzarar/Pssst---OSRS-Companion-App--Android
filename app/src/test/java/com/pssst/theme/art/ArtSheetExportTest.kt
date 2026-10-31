package com.pssst.theme.art

import org.junit.Assert.assertTrue
import org.junit.Test
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class ArtSheetExportTest {

    @Test
    fun exportMaterialsSheetPng() {
        val outDir = File("build/art-sheet")
        outDir.mkdirs()

        val swatchW = 96
        val swatchH = 48
        val scale = 3
        val gap = 6

        val swatches = listOf<Pair<String, PixelCanvas.() -> Unit>>(
            "stoneGround" to { Textures.drawStoneGround(this, seed = 101) },
            "panelStone" to { Textures.drawPanelStone(this, seed = 101) },
            "oakBeam" to { Textures.drawOakBeam(this, seed = 202, showPlankSeams = true, showEndBands = true) },
            "hammeredIron" to { Textures.drawHammeredIron(this, seed = 505, withBevel = true) },
            "parchment" to { Textures.drawParchment(this, seed = 303, showStainVignette = true) },
            "leather" to { Textures.drawLeather(this, seed = 404, stitched = true) }
        )

        val cols = 2
        val rows = (swatches.size + cols - 1) / cols
        val sheetArtW = cols * swatchW + (cols + 1) * gap
        val sheetArtH = rows * swatchH + (rows + 1) * gap
        val sheet = PixelCanvas(sheetArtW, sheetArtH)
        sheet.clear(0xFF0C0906.toInt())

        swatches.forEachIndexed { idx, (_, drawFn) ->
            val col = idx % cols
            val row = idx / cols
            val ox = gap + col * (swatchW + gap)
            val oy = gap + row * (swatchH + gap)
            val sub = PixelCanvas(swatchW, swatchH).apply(drawFn)
            copySubCanvas(sheet, ox, oy, sub)
        }

        val outFile = File(outDir, "materials.png")
        writeScaledPng(sheet, scale, outFile)
        assertTrue("Expected materials.png to be written", outFile.exists() && outFile.length() > 0)
    }

    @Test
    fun exportFramesPlaquesOrnamentsSheetPng() {
        val outDir = File("build/art-sheet")
        outDir.mkdirs()

        val swatchW = 96
        val swatchH = 32
        val scale = 3
        val gap = 6

        val swatches = listOf<Pair<String, PixelCanvas.() -> Unit>>(
            "frameLight" to { Frames.drawLedgerPanel(this, FrameWeight.LIGHT) },
            "frameMedium" to { Frames.drawLedgerPanel(this, FrameWeight.MEDIUM) },
            "frameHeavy" to { Frames.drawLedgerPanel(this, FrameWeight.HEAVY) },
            "oakBeamFrame" to { Frames.drawOakBeamFrame(this) },
            "stoneSillFrame" to { Frames.drawStoneSillFrame(this) },
            "parchmentNotice" to { Frames.drawParchmentNotice(this) },
            "leatherBoundPanel" to { Frames.drawLeatherBoundPanel(this) },
            "plaqueFrame" to { Plaques.drawPlaqueFrame(this) },
            "bookmarkSelected" to { Plaques.drawBookmarkTab(this, selected = true) },
            "bookmarkUnselected" to { Plaques.drawBookmarkTab(this, selected = false) },
            "engravedRule" to {
                Textures.drawPanelStone(this)
                Ornaments.drawEngravedRule(this, withEndCaps = true, withCenterFlourish = true)
            },
            "ornamentsMix" to {
                Textures.drawPanelStone(this)
                val num = PixelCanvas(12, 12).also { Plaques.drawNumberPlaque(it) }
                copySubCanvas(this, 6, 10, num)
                val grip = PixelCanvas(18, 14).also { Ornaments.drawIronGrip(it) }
                copySubCanvas(this, 26, 9, grip)
                this.blit(Ornaments.BulletDiamondSprite, SpriteTones.GOLD, 52, 13)
                this.blit(Ornaments.WatermarkScaleSprite, Ornaments.WATERMARK_TONES, 68, 8)
            }
        )

        val cols = 2
        val rows = (swatches.size + cols - 1) / cols
        val sheetArtW = cols * swatchW + (cols + 1) * gap
        val sheetArtH = rows * swatchH + (rows + 1) * gap
        val sheet = PixelCanvas(sheetArtW, sheetArtH)
        sheet.clear(0xFF191612.toInt())

        swatches.forEachIndexed { idx, (_, drawFn) ->
            val col = idx % cols
            val row = idx / cols
            val ox = gap + col * (swatchW + gap)
            val oy = gap + row * (swatchH + gap)
            val sub = PixelCanvas(swatchW, swatchH).apply(drawFn)
            copySubCanvas(sheet, ox, oy, sub)
        }

        val outFile = File(outDir, "frames_plaques_ornaments.png")
        writeScaledPng(sheet, scale, outFile)
        assertTrue("Expected frames_plaques_ornaments.png to be written", outFile.exists() && outFile.length() > 0)
    }

    @Test
    fun exportSpritesSheetPng() {
        val outDir = File("build/art-sheet")
        outDir.mkdirs()

        val sprites = ArtIcons.ALL_SPRITES
        val cellSize = 22
        val gap = 4
        val cols = 8
        // 2 rows of 8 each for NATURAL, GOLD and STONE tones
        val totalRows = 6
        val sheetArtW = cols * cellSize + (cols + 1) * gap
        val sheetArtH = totalRows * cellSize + (totalRows + 1) * gap
        val sheet = PixelCanvas(sheetArtW, sheetArtH)
        Textures.drawPanelStone(sheet, seed = 101)

        // Rows 0..1: NATURAL default tones in recessed wells
        // Rows 2..3: GOLD tones in recessed wells
        // Rows 4..5: STONE tones in recessed wells
        val modes = listOf<Pair<Int, (Sprite) -> SpriteTones>>(
            0 to { s -> s.defaultNaturalTones ?: SpriteTones.GOLD },
            2 to { _ -> SpriteTones.GOLD },
            4 to { _ -> SpriteTones.STONE }
        )

        for ((baseRow, toneFn) in modes) {
            sprites.forEachIndexed { idx, (_, sprite) ->
                val col = idx % cols
                val row = baseRow + (idx / cols)
                val ox = gap + col * (cellSize + gap)
                val oy = gap + row * (cellSize + gap)
                sheet.bevelBox(
                    x = ox,
                    y = oy,
                    bw = cellSize,
                    bh = cellSize,
                    fill = 0xFF221D16.toInt(),
                    raised = false
                )
                sheet.blit(sprite, toneFn(sprite), ox + 3, oy + 3)
            }
        }

        val outFile = File(outDir, "sprites.png")
        writeScaledPng(sheet, scale = 4, file = outFile)
        assertTrue("Expected sprites.png to be written", outFile.exists() && outFile.length() > 0)
    }

    @Test
    fun exportScenesSheetPng() {
        val outDir = File("build/art-sheet")
        outDir.mkdirs()

        // Group 1: the 7 empty states (48x36). Group 2: crest (36x36) + 4 Overview vignettes (32x24).
        val emptyStates = EmptyStateKind.entries.map { kind ->
            SheetCell(EmptyStates.SCENE_WIDTH, EmptyStates.SCENE_HEIGHT) { EmptyStates.drawScene(this, kind) }
        }
        val extras = listOf(
            SheetCell(EmptyStates.CREST_SIZE, EmptyStates.CREST_SIZE) { EmptyStates.drawInformantCrest(this) },
            SheetCell(EmptyStates.VIGNETTE_WIDTH, EmptyStates.VIGNETTE_HEIGHT) { EmptyStates.drawKingdomVignette(this, false) },
            SheetCell(EmptyStates.VIGNETTE_WIDTH, EmptyStates.VIGNETTE_HEIGHT) { EmptyStates.drawKingdomVignette(this, true) },
            SheetCell(EmptyStates.VIGNETTE_WIDTH, EmptyStates.VIGNETTE_HEIGHT) { EmptyStates.drawSlayerVignette(this, false) },
            SheetCell(EmptyStates.VIGNETTE_WIDTH, EmptyStates.VIGNETTE_HEIGHT) { EmptyStates.drawSlayerVignette(this, true) }
        )
        val gap = 6
        val groups = listOf(
            Triple(emptyStates, 4, EmptyStates.SCENE_WIDTH + 8 to EmptyStates.SCENE_HEIGHT + 8),
            Triple(extras, 5, EmptyStates.CREST_SIZE + 8 to EmptyStates.CREST_SIZE + 8)
        )
        val sheetArtW = groups.maxOf { (_, cols, cell) -> cols * cell.first + (cols + 1) * gap }
        val sheetArtH = gap + groups.sumOf { (cells, cols, cell) ->
            ((cells.size + cols - 1) / cols) * (cell.second + gap)
        }
        val sheet = PixelCanvas(sheetArtW, sheetArtH)
        Textures.drawStoneGround(sheet, seed = 101)

        var top = gap
        for ((cells, cols, cell) in groups) {
            val (cellW, cellH) = cell
            cells.forEachIndexed { idx, sheetCell ->
                val ox = gap + (idx % cols) * (cellW + gap)
                val oy = top + (idx / cols) * (cellH + gap)
                val sub = PixelCanvas(sheetCell.w, sheetCell.h).apply(sheetCell.draw)
                copySubCanvas(sheet, ox + (cellW - sheetCell.w) / 2, oy + (cellH - sheetCell.h) / 2, sub)
            }
            top += ((cells.size + cols - 1) / cols) * (cellH + gap)
        }

        val outFile = File(outDir, "scenes.png")
        writeScaledPng(sheet, scale = 4, file = outFile)
        assertTrue("Expected scenes.png to be written", outFile.exists() && outFile.length() > 0)
    }

    private class SheetCell(val w: Int, val h: Int, val draw: PixelCanvas.() -> Unit)

    companion object {
        fun copySubCanvas(dest: PixelCanvas, ox: Int, oy: Int, src: PixelCanvas) {
            for (y in 0 until src.h) {
                for (x in 0 until src.w) {
                    val c = src.get(x, y)
                    if (c != 0) {
                        dest.set(ox + x, oy + y, c)
                    }
                }
            }
        }

        fun writeScaledPng(canvas: PixelCanvas, scale: Int, file: File) {
            val imgW = canvas.w * scale
            val imgH = canvas.h * scale
            val img = BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_ARGB)
            for (y in 0 until canvas.h) {
                for (x in 0 until canvas.w) {
                    val c = canvas.get(x, y)
                    for (sy in 0 until scale) {
                        for (sx in 0 until scale) {
                            img.setRGB(x * scale + sx, y * scale + sy, c)
                        }
                    }
                }
            }
            ImageIO.write(img, "PNG", file)
        }
    }
}
