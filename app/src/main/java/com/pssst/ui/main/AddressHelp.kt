package com.pssst.ui.main

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsPanelDark
import com.pssst.theme.OsrsRed
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.OsrsTextPrimary
import com.pssst.theme.osrsPanel

/** Shown on the Overview when RuneLite was sending to an address this phone no longer has. */
@Composable
fun AddressChangedBanner(newAddress: String, onHelp: () -> Unit, modifier: Modifier = Modifier) {
    val copyText = rememberCopyText()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .osrsPanel()
            .padding(16.dp)
    ) {
        Text(
            text = "Your phone's address changed",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = OsrsRed
        )
        Text(
            text = "RuneLite is still sending to the old one. In RuneLite, open the Pssst panel and set Phone Address to:",
            style = MaterialTheme.typography.bodyMedium,
            color = OsrsTextPrimary
        )
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = newAddress,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = OsrsGold,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = { copyText(newAddress) }) { Text("Copy", color = OsrsGold) }
        }
        Text(
            text = "If RuneLite uses a name for your phone instead, you can ignore this.",
            style = MaterialTheme.typography.labelSmall,
            color = OsrsTextMuted
        )
        TextButton(onClick = onHelp) { Text("Keep it from changing", color = OsrsGold) }
    }
}

/** How to stop the phone's address changing. Pssst can't change it itself: Android doesn't let apps do that. */
@Composable
fun KeepAddressDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = OsrsPanelDark,
        title = { Text("Keep this address from changing", color = OsrsGold, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Option(
                    "1. Reserve the address on your router",
                    "In your router's settings, look for \"DHCP reservation\", \"address reservation\" or \"static lease\", " +
                        "and reserve the address this phone has now. Every router is different; its manual or label says " +
                        "how to open the settings."
                )
                Option(
                    "2. Set a fixed address on this phone",
                    "In Wi-Fi settings, edit your home network and change IP settings from DHCP to Static. Pick an " +
                        "address outside your router's automatic range, or it can clash with another device."
                )
                TextButton(onClick = {
                    context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }) { Text("Open Wi-Fi settings", color = OsrsGold) }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Done", color = OsrsGold) } }
    )
}

@Composable
private fun Option(title: String, body: String) {
    Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = OsrsTextPrimary)
    Text(body, style = MaterialTheme.typography.bodyMedium, color = OsrsTextMuted)
    Spacer(modifier = Modifier.height(12.dp))
}
