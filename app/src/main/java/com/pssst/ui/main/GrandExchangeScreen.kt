package com.pssst.ui.main

import androidx.compose.foundation.Canvas
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
import com.pssst.theme.art.BulletGlyph
import com.pssst.theme.art.FrameWeight
import com.pssst.theme.art.GoldLeafText
import com.pssst.theme.art.WatermarkScale
import com.pssst.theme.art.ledgerWellSlot
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
        // Summary Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .osrsPanel(weight = FrameWeight.HEAVY)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BulletGlyph(size = 7.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        GoldLeafText(
                            text = "Grand Exchange Offers",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp)
                        )
                    }
                    Text(
                        text = "$activeCount / 8 Slots Active • $completeCount Complete",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp,
                        color = OsrsTextMuted
                    )
                }

                Text(
                    text = formatGp(state.totalActiveGeValue),
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = OsrsGold
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
            .then(
                if (offer.isEmpty) {
                    Modifier.ledgerWellSlot(empty = true)
                } else {
                    Modifier.osrsPanel(weight = FrameWeight.MEDIUM)
                }
            )
            .padding(10.dp)
    ) {
        if (offer.isEmpty) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Slot ${offer.slot}",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = OsrsTextMuted
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        WatermarkScale()
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Empty",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 13.sp,
                            color = OsrsTextMuted
                        )
                    }
                }
            }
        } else {
            val isSell = offer.state == GeOfferState.SELLING ||
                offer.state == GeOfferState.SOLD ||
                offer.state == GeOfferState.CANCELLED_SELL
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top: Fixed-height header so 1-line and 2-line names align across columns
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
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
                                modifier = Modifier.size(38.dp)
                            )
                        } else {
                            Text(
                                text = itemInitials(offer.itemName),
                                color = OsrsTextPrimary,
                                fontSize = 11.sp,
                                style = MaterialTheme.typography.bodySmall.copy(shadow = OsrsHardShadow)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = offer.itemName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 13.5.sp,
                            lineHeight = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OsrsTextPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val (chipText, chipBg, chipFg) = when (offer.state) {
                                GeOfferState.BOUGHT -> Triple("BOUGHT", OsrsHerbGreen, Color.Black)
                                GeOfferState.SOLD -> Triple("SOLD", OsrsHerbGreen, Color.Black)
                                GeOfferState.BUYING -> Triple("BUY", OsrsSlotEmpty, OsrsGold)
                                GeOfferState.SELLING -> Triple("SELL", OsrsSlotEmpty, OsrsTextPrimary)
                                GeOfferState.CANCELLED_BUY, GeOfferState.CANCELLED_SELL -> Triple("CANCEL", OsrsSlotEmpty, OsrsRed)
                                else -> Triple("EMPTY", OsrsSlotEmpty, OsrsTextMuted)
                            }
                            OsrsStatusBadge(
                                text = chipText,
                                containerColor = chipBg,
                                contentColor = chipFg
                            )
                            Text(
                                text = "Slot ${offer.slot}",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 11.sp,
                                color = OsrsTextMuted
                            )
                        }
                    }
                }

                // Middle: Price & Bought/Sold label-value rows inside recessed ledger well
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .ledgerWellSlot(empty = true)
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    GeLabelValueRow(
                        label = "Price",
                        value = formatGp(offer.price.toLong()),
                        valueColor = OsrsGold
                    )
                    GeLabelValueRow(
                        label = if (isSell) "Sold" else "Bought",
                        value = "${offer.quantitySold} / ${offer.totalQuantity}"
                    )
                }

                // Bottom: Full-Height Progress Bar + Total Value
                Column(modifier = Modifier.fillMaxWidth()) {
                    OsrsProgressBar(
                        progress = offer.progress,
                        color = if (offer.isComplete) OsrsHerbGreen else OsrsGold,
                        thin = false
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatGp(offer.spent),
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 12.sp,
                            color = OsrsTextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = formatGp(offer.totalOfferValue),
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = OsrsGold,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GeLabelValueRow(
    label: String,
    value: String,
    valueColor: Color = OsrsTextPrimary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 11.sp,
            color = OsrsTextMuted
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = JetBrainsMonoFontFamily,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = valueColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
