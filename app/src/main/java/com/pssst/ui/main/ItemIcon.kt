package com.pssst.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pssst.theme.OsrsHardShadow
import com.pssst.theme.OsrsSlotEmpty
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.art.PixelItemImage
import com.pssst.theme.art.pixelBevelBox

/** The pixel-art icon of an item, or its initials on a dark recessed stone tile while the app has no icon for it yet. */
@Composable
fun ItemIcon(id: Int, base64: String?, name: String, size: Dp = 32.dp, modifier: Modifier = Modifier) {
    val bitmap = remember(id, base64) { if (id > 0) ItemIconCache.getOrCreate(id, base64) else null }
    if (bitmap != null) {
        PixelItemImage(
            bitmap = bitmap,
            contentDescription = name,
            modifier = modifier.size(size)
        )
    } else {
        Box(
            modifier = modifier
                .size(size)
                .pixelBevelBox(fill = OsrsSlotEmpty, raised = false),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = itemInitials(name),
                color = OsrsTextMuted,
                fontSize = (size.value * 0.34f).sp,
                style = MaterialTheme.typography.labelSmall.copy(shadow = OsrsHardShadow)
            )
        }
    }
}

/** "Antidote++(4)" -> "A4", "Bandos chestplate" -> "BC": enough to tell tiles apart until the real icon arrives. */
fun itemInitials(name: String): String {
    val words = name.replace(Regex("[^A-Za-z0-9 ]"), " ").trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    return when {
        words.isEmpty() -> "?"
        words.size == 1 -> words[0].take(2).replaceFirstChar { it.uppercase() }
        else -> (words[0].take(1) + words.last().take(1)).uppercase()
    }
}
