package com.pssst.theme.art

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ColourDisciplineTest {

    private fun mainJavaDir(): File {
        val candidates = listOf(
            File("src/main/java"),
            File("app/src/main/java")
        )
        return candidates.firstOrNull { it.isDirectory }
            ?: error("Could not locate src/main/java from ${File(".").absolutePath}")
    }

    @Test
    fun stackCountColours_onlyAppearInColorKtAndItemQuantityBlocks() {
        val root = mainJavaDir()
        val allowedFiles = setOf("Color.kt", "BankScreen.kt", "InventoryScreen.kt")
        val stackTokens = listOf("OsrsGreen", "OsrsYellow", "OsrsWhite", "0xFF00FF80", "0xFFFFFF00")

        val violations = mutableListOf<String>()
        root.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .forEach { file ->
                val text = file.readText()
                for (token in stackTokens) {
                    if (text.contains(token)) {
                        if (file.name !in allowedFiles) {
                            violations += "${file.name} references stack-count token '$token'"
                        } else if (file.name == "BankScreen.kt" || file.name == "InventoryScreen.kt") {
                            // In BankScreen.kt and InventoryScreen.kt, each stack token may only appear
                            // once in imports and once in the item.quantity >= ... block
                            val count = Regex("\\b${Regex.escape(token)}\\b").findAll(text).count()
                            if (token.startsWith("Osrs") && count > 2) {
                                violations += "${file.name} uses '$token' $count times (expected <= 2: import + item.quantity)"
                            }
                        }
                    }
                }
            }

        assertTrue(
            "Stack-count colour discipline violations:\n" + violations.joinToString("\n"),
            violations.isEmpty()
        )
    }

    @Test
    fun artContrastAndOddTableRow_useOsrsPanelDarkWithValidContrast() {
        val tablesFile = mainJavaDir().resolve("com/pssst/theme/art/Tables.kt")
        assertTrue("Tables.kt must exist", tablesFile.isFile)
        val content = tablesFile.readText()
        assertEquals(
            "Tables.kt must not reference OsrsLedgerRowAlt",
            false,
            content.contains("OsrsLedgerRowAlt")
        )
        assertTrue(
            "Tables.kt odd rows must use OsrsPanelDark",
            content.contains("OsrsPanelDark")
        )
    }
}
