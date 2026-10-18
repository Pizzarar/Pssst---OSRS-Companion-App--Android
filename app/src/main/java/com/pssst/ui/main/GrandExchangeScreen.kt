package com.pssst.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
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
import com.pssst.theme.JetBrainsMonoFontFamily
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsHardShadow
import com.pssst.theme.OsrsHerbGreen
import com.pssst.theme.OsrsProgressBar
import com.pssst.theme.OsrsRed
import com.pssst.theme.OsrsSlotEmpty
import com.pssst.theme.OsrsStatusBadge
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
    val rows = remember(state.geOffers) {
        val padded = (1..8).map { slotNum ->
            state.geOffers.find { it.slot == slotNum } ?: GeOfferSlotUiModel(slot = slotNum)
        }
        padded.chunked(2)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        // Compact Summary Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .osrsPanel(showRivets = true)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Grand Exchange Offers",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = OsrsGold
                    )
                    Text(
                        text = "$activeCount / 8 Slots Active • $completeCount Complete",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
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

        Spacer(modifier = Modifier.height(8.dp))

        // 2 Across x 4 Down Grid filling the tab viewport
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            rows.forEach { rowOffers ->
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    rowOffers.forEach { offer ->
                        GeOfferCard(
                            offer = offer,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }
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
            .padding(8.dp)
    ) {
        if (offer.isEmpty) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Slot ${offer.slot}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = OsrsTextMuted.copy(alpha = 0.6f)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Empty",
                        style = MaterialTheme.typography.bodySmall,
                        color = OsrsTextMuted.copy(alpha = 0.38f)
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top: Icon + Name/Slot + Status Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .osrsSlot(hasItem = true)
                            .padding(3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap,
                                contentDescription = offer.itemName,
                                filterQuality = FilterQuality.None,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(26.dp)
                            )
                        } else {
                            Text(
                                text = itemInitials(offer.itemName),
                                color = OsrsTextPrimary,
                                fontSize = 9.sp,
                                style = MaterialTheme.typography.bodySmall.copy(shadow = OsrsHardShadow)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = offer.itemName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OsrsTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Slot ${offer.slot}",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = OsrsTextMuted
                            )
                            val (chipText, chipBg, chipFg) = when (offer.state) {
                                GeOfferState.BOUGHT -> Triple("BOUGHT", OsrsHerbGreen, Color.Black)
                                GeOfferState.SOLD -> Triple("SOLD", OsrsHerbGreen, Color.Black)
                                GeOfferState.BUYING -> Triple("BUY", OsrsSlotEmpty, OsrsGold)
                                GeOfferState.SELLING -> Triple("SELL", OsrsSlotEmpty, OsrsYellow)
                                GeOfferState.CANCELLED_BUY, GeOfferState.CANCELLED_SELL -> Triple("CANCEL", OsrsSlotEmpty, OsrsRed)
                                else -> Triple("EMPTY", OsrsSlotEmpty, OsrsTextMuted)
                            }
                            OsrsStatusBadge(
                                text = chipText,
                                containerColor = chipBg,
                                contentColor = chipFg
                            )
                        }
                    }
                }

                // Middle: Quantity & Unit Price
                Text(
                    text = "${offer.quantitySold} / ${offer.totalQuantity} @ ${formatGp(offer.price.toLong())}",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 10.sp,
                    color = OsrsTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Bottom: Progress Bar + Total Value
                Column(modifier = Modifier.fillMaxWidth()) {
                    OsrsProgressBar(
                        progress = offer.progress,
                        color = if (offer.isComplete) OsrsHerbGreen else OsrsGold,
                        thin = true
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatGp(offer.spent),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = OsrsTextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = formatGp(offer.totalOfferValue),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = OsrsYellow,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
