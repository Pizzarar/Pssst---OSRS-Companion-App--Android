package com.pssst.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsHeaderDivider
import com.pssst.theme.OsrsRed
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.OsrsTextPrimary
import com.pssst.theme.art.GoldLeafText
import com.pssst.theme.art.WaxSealMedallion
import com.pssst.theme.art.parchmentNotice
import com.pssst.theme.osrsPanel
import com.pssst.util.PhoneAddresses

/** The address to type into RuneLite → Pssst Companion → Phone Address, with Copy. Shown in phone mode. */
@Composable
fun PhoneAddressCard(state: MainDashboardUiState, modifier: Modifier = Modifier, onHelp: () -> Unit = {}) {
    val context = LocalContext.current
    val copyText = rememberCopyText()
    var addresses by remember { mutableStateOf(PhoneAddresses.forDisplay(context)) }
    val waiting = state.lastSyncedAt <= 0L

    Column(
        modifier = modifier
            .fillMaxWidth()
            .osrsPanel()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WaxSealMedallion(label = "IP", size = 26.dp)
            GoldLeafText(
                text = if (waiting) "Waiting for your PC" else "Phone Address for RuneLite",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        OsrsHeaderDivider()
        Spacer(modifier = Modifier.height(8.dp))
        if (waiting) {
            Text(
                text = "In RuneLite → Pssst Companion → Phone, turn on Send to My Phone and type this into Phone Address:",
                style = MaterialTheme.typography.bodyMedium,
                color = OsrsTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
        }
        if (addresses.isEmpty()) {
            Text(
                text = "Connect this phone to the same Wi-Fi as your PC (or to Tailscale) to see its address.",
                style = MaterialTheme.typography.bodyMedium,
                color = OsrsRed
            )
            TextButton(onClick = { addresses = PhoneAddresses.forDisplay(context) }) {
                Text("Check again", color = OsrsGold)
            }
        }
        addresses.forEach { address ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .parchmentNotice()
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(address.host, style = MaterialTheme.typography.titleLarge, color = OsrsGold, fontWeight = FontWeight.Bold)
                    Text(address.label, style = MaterialTheme.typography.labelSmall, color = OsrsTextMuted)
                }
                TextButton(onClick = { copyText(address.host) }) {
                    Text("Copy", color = OsrsGold)
                }
            }
        }
        TextButton(onClick = onHelp) {
            Text("Keep it from changing", color = OsrsGold)
        }
    }
}

