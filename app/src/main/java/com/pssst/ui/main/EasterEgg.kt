package com.pssst.ui.main

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pssst.theme.osrsPanel
import com.pssst.util.IconWishlist
import kotlinx.coroutines.delay

/** One thing to show in the easter egg slot: a bundled picture, or an item icon. */
private sealed interface EggImage {
    data class Drawable(val resId: Int) : EggImage
    data class Item(val id: Int, val base64: String?) : EggImage
}

/**
 * The empty cell at the end of the skills grid. It quietly rotates through odd little pictures every few seconds; a tap
 * moves on. Pictures named easter_1.png, easter_2.png, ... in res/drawable-nodpi join it (gnome child and friends);
 * funny item icons fill in until then. No label: it is meant to be found.
 */
@Composable
fun EasterEggCell(state: MainDashboardUiState, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val images = remember(state.wishlistIcons) {
        val drawables = (1..40).mapNotNull { n ->
            context.resources.getIdentifier("easter_$n", "drawable", context.packageName).takeIf { it != 0 }
        }.map<Int, EggImage> { EggImage.Drawable(it) }
        val items = IconWishlist.EASTER_EGG.mapNotNull { id ->
            state.wishlistIcons[id]?.takeIf { it.isNotBlank() }?.let { EggImage.Item(id, it) }
        }
        (drawables + items).shuffled()
    }
    if (images.isEmpty()) return

    var index by remember { mutableIntStateOf(0) }
    LaunchedEffect(images, index) {
        delay(6_000)
        index = (index + 1) % images.size
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .osrsPanel()
            .clickable { index = (index + 1) % images.size }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Crossfade(targetState = images[index % images.size], label = "easter-egg") { image ->
            when (image) {
                is EggImage.Drawable -> Image(
                    painter = painterResource(image.resId),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize().padding(2.dp)
                )
                is EggImage.Item -> ItemIcon(image.id, image.base64, name = "", size = 44.dp)
            }
        }
    }
}
