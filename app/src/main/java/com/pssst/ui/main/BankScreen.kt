package com.pssst.ui.main

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pssst.theme.OsrsFilterChip
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsGreen
import com.pssst.theme.OsrsHardShadow
import com.pssst.theme.OsrsTextField
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.OsrsTextPrimary
import com.pssst.theme.OsrsWhite
import com.pssst.theme.OsrsYellow
import com.pssst.theme.osrsSlot

@Composable
fun BankScreen(
    state: MainDashboardUiState,
    onSearchQueryChanged: (String) -> Unit,
    onBankSortModeChanged: (BankSortMode) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Input
        OsrsTextField(
            value = state.searchQuery,
            onValueChange = onSearchQueryChanged,
            label = "Search Bank by Name or ID",
            placeholder = "e.g. Abyssal whip or 4151",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Sort mode chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            BankSortMode.values().forEach { mode ->
                val isSelected = state.bankSortMode == mode
                OsrsFilterChip(
                    text = mode.label,
                    selected = isSelected,
                    onClick = { onBankSortModeChanged(mode) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Bank Summary
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Items: ${state.bankItems.size}${if (state.searchQuery.isNotBlank()) " (${state.filteredBankItems.size})" else ""}",
                style = MaterialTheme.typography.bodyMedium,
                color = OsrsTextMuted
            )
            Text(
                text = "Value: ${state.formattedBankValue}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = OsrsGold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (state.bankItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No bank data synced yet.\nIt appears after you open your bank with Pssst Companion running.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = OsrsTextMuted
                )
            }
        } else if (state.filteredBankItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No items match '${state.searchQuery}'",
                    style = MaterialTheme.typography.bodyLarge,
                    color = OsrsTextMuted
                )
            }
        } else {
            // A new sort or search starts at the top: the grid otherwise keeps its scroll position and lands mid-list
            val gridState = rememberLazyGridState()
            LaunchedEffect(state.bankSortMode, state.searchQuery) { gridState.scrollToItem(0) }
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Adaptive(minSize = 72.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.filteredBankItems, key = { it.id }) { item ->
                    BankItemCard(item)
                }
            }
        }
    }
}

@Composable
fun BankItemCard(item: ItemUiModel, modifier: Modifier = Modifier) {
    val bitmap = remember(item.id, item.iconBase64) {
        ItemIconCache.getOrCreate(item.id, item.iconBase64)
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .osrsSlot(hasItem = true)
            .padding(3.dp)
    ) {
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

        // Quantity in top-left
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

        // Truncated item name at bottom if icon is showing
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
    }
}
