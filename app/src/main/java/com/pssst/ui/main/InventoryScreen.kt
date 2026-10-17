package com.pssst.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsGreen
import com.pssst.theme.OsrsHardShadow
import com.pssst.theme.OsrsRed
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.OsrsTextPrimary
import com.pssst.theme.OsrsWhite
import com.pssst.theme.OsrsYellow
import com.pssst.theme.osrsSlot

@Composable
fun InventoryScreen(
    state: MainDashboardUiState,
    modifier: Modifier = Modifier
) {
    val occupiedCount = state.inventorySlots.count { it.item != null }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Inventory Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Inventory (4 × 7)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OsrsGold
            )
            Text(
                text = "$occupiedCount / 28 Slots",
                style = MaterialTheme.typography.bodyMedium,
                color = if (occupiedCount == 28) OsrsRed else OsrsYellow,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4x7 Inventory Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(state.inventorySlots, key = { it.slotIndex }) { slot ->
                InventorySlotCard(slot)
            }
        }
    }
}

@Composable
fun InventorySlotCard(slot: InventorySlotUiModel, modifier: Modifier = Modifier) {
    val item = slot.item
    val hasItem = item != null
    val bitmap = remember(item?.id, item?.iconBase64) {
        if (item != null) ItemIconCache.getOrCreate(item.id, item.iconBase64) else null
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .osrsSlot(hasItem = hasItem)
            .padding(3.dp)
    ) {
        if (item != null) {
            // Pixel-art item icon or fallback ID/Name
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = item.name.ifBlank { "Item #${item.id}" },
                    filterQuality = FilterQuality.None,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.Center)
                )
            } else {
                Text(
                    text = item.name.ifBlank { "Unknown item (${item.id})" },
                    color = OsrsTextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall.copy(shadow = OsrsHardShadow),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Quantity in top-left (only show if > 1)
            if (item.quantity > 1) {
                val qtyColor = when {
                    item.quantity >= 10_000_000 -> OsrsGreen
                    item.quantity >= 100_000 -> OsrsWhite
                    else -> OsrsYellow
                }
                Text(
                    text = item.formattedQuantity,
                    color = qtyColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodySmall.copy(shadow = OsrsHardShadow),
                    modifier = Modifier.align(Alignment.TopStart)
                )
            }

            // Item name at bottom if icon is showing
            if (bitmap != null && item.name.isNotBlank()) {
                Text(
                    text = item.name,
                    color = OsrsTextMuted,
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall.copy(shadow = OsrsHardShadow),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                )
            }
        } else {
            // Slot index placeholder
            Text(
                text = "${slot.slotIndex + 1}",
                fontSize = 11.sp,
                color = OsrsTextMuted.copy(alpha = 0.25f),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
