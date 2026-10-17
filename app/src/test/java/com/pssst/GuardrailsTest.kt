package com.pssst

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * GUIDELINES.md, rule 1 and 3: the app makes requests only to the server the user configured. No third-party host
 * may appear in code that ships in the app (the build-time manifest script is outside this tree, by design).
 */
class GuardrailsTest {

    private val forbidden = listOf(
        "runescape.wiki",          // the OSRS Wiki, prices API included
        "oldschool.runescape",     // official sites
        "static.runelite.net",     // RuneLite's static host (build-time only)
        "rosserlogic"              // the retired built-in server
    )

    private fun sources(): List<File> {
        val main = File("src/main")
        assertTrue("expected to run from the app module, cwd=${File(".").absolutePath}", main.isDirectory)
        return main.walkTopDown().filter { it.isFile && it.extension in setOf("kt", "java", "xml") }.toList()
    }

    @Test
    fun shippedCodeNamesNoThirdPartyHost() {
        val offenders = sources().flatMap { file ->
            file.readLines().withIndex()
                .filter { (_, line) -> forbidden.any { line.contains(it) } }
                .map { (i, line) -> "${file.path}:${i + 1}: ${line.trim()}" }
        }
        assertTrue("third-party hosts in shipped code:\n" + offenders.joinToString("\n"), offenders.isEmpty())
    }
}
