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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pssst.theme.OsrsBorderOutline
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsHardShadow
import com.pssst.theme.OsrsHerbGreen
import com.pssst.theme.OsrsRed
import com.pssst.theme.OsrsSlotEmpty
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.OsrsTextPrimary
import com.pssst.theme.OsrsYellow
import com.pssst.theme.osrsPanel
import com.pssst.theme.osrsSlot

@Composable
fun GrandExchangeScreen(
    state: MainDashboardUiState,
    modifier: Modifier = Modifier
) {
    val activeCount = state.geOffers.count { !it.isEmpty }
    val completeCount = state.geOffers.count { it.isComplete }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Summary Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .osrsPanel()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Grand Exchange Offers",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = OsrsGold
                    )
                    Text(
                        text = "$activeCount / 8 Slots Active • $completeCount Complete",
                        style = MaterialTheme.typography.bodySmall,
                        color = OsrsTextMuted
                    )
                }

                Text(
                    text = formatGp(state.totalActiveGeValue),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = OsrsYellow
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 8 GE Slots List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.geOffers, key = { it.slot }) { offer ->
                GeOfferCard(offer)
            }
        }
    }
}

@Composable
fun GeOfferCard(offer: GeOfferSlotUiModel, modifier: Modifier = Modifier) {
    val bitmap = remember(offer.itemId, offer.iconBase64) {
        if (offer.itemId > 0) ItemIconCache.getOrCreate(offer.itemId, offer.iconBase64) else null
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .osrsPanel()
            .padding(12.dp)
    ) {
        if (offer.isEmpty) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Slot ${offer.slot} — Empty",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OsrsTextMuted.copy(alpha = 0.6f)
                )
                Text(
                    text = "Empty",
                    style = MaterialTheme.typography.labelSmall,
                    color = OsrsTextMuted.copy(alpha = 0.4f)
                )
            }
        } else {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Item Icon
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
                                contentDescription = offer.itemName,
                                filterQuality = FilterQuality.None,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(36.dp)
                            )
                        } else {
                            Text(
                                text = "#${offer.itemId}",
                                color = OsrsTextPrimary,
                                fontSize = 10.sp,
                                style = MaterialTheme.typography.bodySmall.copy(shadow = OsrsHardShadow)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Name + Slot
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = offer.itemName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = OsrsTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Slot ${offer.slot} • ${offer.quantitySold} / ${offer.totalQuantity} @ ${formatGp(offer.price.toLong())}",
                            style = MaterialTheme.typography.bodySmall,
                            color = OsrsTextMuted
                        )
                    }

                    // Status Chip
                    val (chipText, chipBg, chipFg) = when (offer.state) {
                        GeOfferState.BOUGHT -> Triple("BOUGHT", OsrsHerbGreen, Color.Black)
                        GeOfferState.SOLD -> Triple("SOLD", OsrsHerbGreen, Color.Black)
                        GeOfferState.BUYING -> Triple("BUYING", OsrsSlotEmpty, OsrsGold)
                        GeOfferState.SELLING -> Triple("SELLING", OsrsSlotEmpty, OsrsYellow)
                        GeOfferState.CANCELLED_BUY, GeOfferState.CANCELLED_SELL -> Triple("CANCELLED", OsrsSlotEmpty, OsrsRed)
                        else -> Triple("EMPTY", OsrsSlotEmpty, OsrsTextMuted)
                    }

                    SuggestionChip(
                        onClick = {},
                        label = { Text(chipText, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = chipBg,
                            labelColor = chipFg
                        ),
                        border = if (chipBg == OsrsHerbGreen) null else SuggestionChipDefaults.suggestionChipBorder(enabled = true, borderColor = OsrsBorderOutline)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { offer.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = if (offer.isComplete) OsrsHerbGreen else OsrsGold,
                    trackColor = OsrsSlotEmpty
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Spent / Transferred row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Spent/Received: ${formatGp(offer.spent)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = OsrsTextMuted
                    )
                    Text(
                        text = "Total: ${formatGp(offer.totalOfferValue)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = OsrsYellow
                    )
                }
            }
        }
    }
}
