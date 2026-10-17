package com.pssst.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pssst.theme.OsrsBorderOutline
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsHardShadow
import com.pssst.theme.OsrsPanelDark
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.OsrsTextPrimary
import com.pssst.theme.OsrsYellow
import com.pssst.theme.osrsPanel
import com.pssst.theme.osrsSlot

@Composable
fun PotionStorageScreen(
    state: MainDashboardUiState,
    onPotionSearchQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Input
        OutlinedTextField(
            value = state.potionSearchQuery,
            onValueChange = onPotionSearchQueryChanged,
            label = { Text("Search Potions", color = OsrsTextMuted) },
            placeholder = { Text("e.g. Prayer potion or Super restore", color = OsrsTextMuted.copy(alpha = 0.5f)) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = OsrsTextPrimary,
                unfocusedTextColor = OsrsTextPrimary,
                focusedBorderColor = OsrsGold,
                unfocusedBorderColor = OsrsBorderOutline,
                focusedContainerColor = OsrsPanelDark,
                unfocusedContainerColor = OsrsPanelDark,
                cursorColor = OsrsGold
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Potion Summary
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val totalDoses = state.potionStorageItems.sumOf { it.totalDoses }
            Text(
                text = "${state.potionStorageItems.size} Types • $totalDoses Total Doses",
                style = MaterialTheme.typography.bodyMedium,
                color = OsrsTextMuted
            )
            Text(
                text = "Value: ${formatGp(state.totalPotionStorageValue)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = OsrsGold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (state.potionStorageItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No potion storage data synced yet.\nIt appears after you open the potion store in your bank with Pssst Companion running.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = OsrsTextMuted
                )
            }
        } else if (state.filteredPotionStorageItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No potions match '${state.potionSearchQuery}'",
                    style = MaterialTheme.typography.bodyLarge,
                    color = OsrsTextMuted
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.filteredPotionStorageItems, key = { it.itemId }) { item ->
                    PotionStorageCard(item)
                }
            }
        }
    }
}

@Composable
fun PotionStorageCard(item: PotionStorageItemUiModel, modifier: Modifier = Modifier) {
    val bitmap = remember(item.itemId, item.iconBase64) {
        ItemIconCache.getOrCreate(item.itemId, item.iconBase64)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .osrsPanel()
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon slot
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .osrsSlot(hasItem = true)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = item.name,
                        filterQuality = FilterQuality.None,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(36.dp)
                    )
                } else {
                    Text(
                        text = "#${item.itemId}",
                        color = OsrsTextPrimary,
                        fontSize = 10.sp,
                        style = MaterialTheme.typography.bodySmall.copy(shadow = OsrsHardShadow)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = OsrsTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${item.totalDoses} doses (${item.fourDoseEquivalent} × (4)${if (item.remainingDoses > 0) " + ${item.remainingDoses}d" else ""})",
                    style = MaterialTheme.typography.bodySmall,
                    color = OsrsTextMuted
                )
            }

            // Value
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatGp(item.estimatedValue),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = OsrsGold
                )
                if (item.gePrice > 0L) {
                    Text(
                        text = "${formatGp(item.gePrice)} / (4)",
                        style = MaterialTheme.typography.labelSmall,
                        color = OsrsTextMuted
                    )
                }
            }
        }
    }
}
