package com.pssst.theme.art

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class PreviewSheetSyncTest {

    private fun resolvePreviewHtml(): File {
        val candidates = listOf(
            File("design-packet/art/preview.html"),
            File("../design-packet/art/preview.html")
        )
        return candidates.firstOrNull { it.exists() }
            ?: error("Could not locate design-packet/art/preview.html from ${File(".").absolutePath}")
    }

    @Test
    fun previewHtml_usesPixelatedCanvasesAndNoSvgOrExternalUrls() {
        val html = resolvePreviewHtml().readText()
        assertTrue("preview.html must specify image-rendering: pixelated", html.contains("image-rendering: pixelated"))
        assertTrue("preview.html must specify image-rendering: crisp-edges", html.contains("image-rendering: crisp-edges"))
        assertFalse("preview.html must not contain <svg elements", html.lowercase().contains("<svg"))
        assertFalse("preview.html must not make external http/https requests", html.contains("https://") || html.contains("http://"))
    }

    @Test
    fun previewHtml_containsAllMaterialSeedsSpritesAndScenes() {
        val html = resolvePreviewHtml().readText()

        // Seeds
        for (seed in listOf(101, 202, 208, 303, 404, 505)) {
            assertTrue("preview.html missing seed $seed", html.contains(seed.toString()))
        }

        // All 16 sprites and every row string verbatim
        for ((name, sprite) in ArtIcons.ALL_SPRITES) {
            assertTrue("preview.html missing sprite $name", html.contains("\"$name\""))
            for (row in sprite.rows) {
                assertTrue("preview.html missing row '$row' for sprite $name", html.contains("\"$row\""))
            }
        }

        // All 7 empty-state scene kinds
        for (kind in EmptyStateKind.entries) {
            assertTrue("preview.html missing empty state scene ${kind.name}", html.contains(kind.name))
        }
    }
}
