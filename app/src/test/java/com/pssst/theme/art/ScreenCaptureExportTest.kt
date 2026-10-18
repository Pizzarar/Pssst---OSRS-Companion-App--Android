package com.pssst.theme.art

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.io.FileOutputStream

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w380dp-h760dp-xxhdpi", sdk = [34])
class ScreenCaptureExportTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun exportAllPhoneScreensToPng() {
        val outDir = File("build/art-sheet/screens")
        outDir.mkdirs()

        val screens = listOf<Pair<String, @Composable () -> Unit>>(
            "01_setup" to { PreviewScreenSetup() },
            "02_overview" to { PreviewScreenOverview() },
            "03_bank" to { PreviewScreenBank() },
            "04_inventory" to { PreviewScreenInventory() },
            "05_potions" to { PreviewScreenPotions() },
            "06_grand_exchange" to { PreviewScreenGrandExchange() },
            "07_skills" to { PreviewScreenSkills() },
            "08_quests" to { PreviewScreenQuests() },
            "09_timers_dailies" to { PreviewScreenTimersDailies() },
            "10_timers_birdhouses" to { PreviewScreenTimersBirdhouses() },
            "11_timers_farming" to { PreviewScreenTimersFarming() },
            "12_timers_payments" to { PreviewScreenTimersPayments() },
            "13_drawer" to { PreviewScreenDrawer() },
            "14_empty_states" to { PreviewScreenEmptyStates() },
            "15_goal_dialog" to { PreviewScreenGoalDialog() }
        )

        val currentScreenIndex = androidx.compose.runtime.mutableIntStateOf(0)
        composeRule.setContent {
            Box(modifier = Modifier.size(380.dp, 760.dp)) {
                screens[currentScreenIndex.intValue].second.invoke()
            }
        }

        screens.forEachIndexed { idx, (name, _) ->
            composeRule.runOnUiThread {
                currentScreenIndex.intValue = idx
            }
            composeRule.waitForIdle()

            val outFile = File(outDir, "$name.png")
            composeRule.runOnUiThread {
                val decorView = composeRule.activity.window.decorView
                val w = decorView.width.coerceAtLeast(380)
                val h = decorView.height.coerceAtLeast(760)
                val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bmp)
                decorView.draw(canvas)
                FileOutputStream(outFile).use { fos ->
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, fos)
                }
            }
            assertTrue("Expected $name.png to be exported", outFile.exists() && outFile.length() > 1000)
        }
    }
}
