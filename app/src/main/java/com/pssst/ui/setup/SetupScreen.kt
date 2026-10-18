package com.pssst.ui.setup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pssst.data.network.DynamicStateApiService
import com.pssst.theme.OsrsBorderOutline
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsPanelDark
import com.pssst.theme.OsrsRed
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.OsrsTextPrimary
import com.pssst.theme.osrsPanel
import com.pssst.ui.main.MainViewModel
import com.pssst.util.PhoneAddresses
import com.pssst.worker.SyncScheduler

private const val LOOPBACK_WARNING = "127.0.0.1 is this phone. Use your server's address, such as https://pssst-worker.yourname.workers.dev"

/**
 * Pairing. Normal path: paste the Sync Token from RuneLite here, and type this phone's address (shown here) into the
 * plugin's Phone Address; the plugin then sends to this phone. Advanced: pull from your own server (a Pssst Worker).
 */
@Composable
fun SetupScreen(
    viewModel: MainViewModel,
    onSetupComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val initialServerUrl = viewModel.uiState.value.serverUrl
    val initialUuid = viewModel.uiState.value.uuid.ifBlank { viewModel.uiState.value.rememberedUuid }

    var tokenInput by remember { mutableStateOf(initialUuid) }
    var rememberMe by remember { mutableStateOf(viewModel.uiState.value.rememberedUuid.isNotBlank() || initialUuid.isNotBlank()) }
    var useOwnServer by remember { mutableStateOf(initialServerUrl.isNotBlank()) }
    var serverUrlInput by remember { mutableStateOf(initialServerUrl) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var urlWarningMessage by remember {
        mutableStateOf(if (DynamicStateApiService.isLoopbackUrl(initialServerUrl)) LOOPBACK_WARNING else null)
    }
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    var phoneAddresses by remember { mutableStateOf(PhoneAddresses.forDisplay(context)) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = OsrsTextPrimary,
        unfocusedTextColor = OsrsTextPrimary,
        focusedBorderColor = OsrsGold,
        unfocusedBorderColor = OsrsBorderOutline,
        focusedContainerColor = OsrsPanelDark,
        unfocusedContainerColor = OsrsPanelDark,
        cursorColor = OsrsGold
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to Pssst",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = OsrsGold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Pair with the Pssst Companion plugin in RuneLite. Your PC sends your bank, inventory and timers to this phone.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = OsrsTextMuted
        )

        Spacer(modifier = Modifier.height(28.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .osrsPanel()
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "1. Sync Token",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = OsrsGold
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = tokenInput,
                    onValueChange = {
                        tokenInput = it
                        errorMessage = null
                    },
                    label = { Text("Sync Token", color = OsrsTextMuted) },
                    placeholder = { Text("From the Pssst panel in RuneLite", color = OsrsTextMuted.copy(alpha = 0.5f)) },
                    singleLine = true,
                    isError = errorMessage != null,
                    supportingText = errorMessage?.let { { Text(it, color = OsrsRed) } },
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { rememberMe = !rememberMe },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = OsrsGold,
                            uncheckedColor = OsrsBorderOutline,
                            checkmarkColor = Color.Black
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Remember this Sync Token on this device",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (rememberMe) OsrsGold else OsrsTextMuted
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!useOwnServer) {
                    Text(
                        text = "2. Phone Address",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = OsrsGold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "In RuneLite → Pssst Companion → Phone, turn on Send to My Phone and type this into Phone Address:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OsrsTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    if (phoneAddresses.isEmpty()) {
                        Text(
                            text = "Connect this phone to the same Wi-Fi as your PC (or to Tailscale) to see its address.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OsrsRed
                        )
                        TextButton(onClick = { phoneAddresses = PhoneAddresses.forDisplay(context) }) {
                            Text("Check again", color = OsrsGold)
                        }
                    }
                    phoneAddresses.forEach { address ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(address.host, style = MaterialTheme.typography.titleMedium, color = OsrsGold, fontWeight = FontWeight.Bold)
                                Text(address.label, style = MaterialTheme.typography.labelSmall, color = OsrsTextMuted)
                            }
                            TextButton(onClick = { clipboard.setText(AnnotatedString(address.host)) }) {
                                Text("Copy", color = OsrsGold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "The Tailscale address never changes and also works at home.",
                        style = MaterialTheme.typography.labelSmall,
                        color = OsrsTextMuted
                    )
                } else {
                    Text(
                        text = "2. Your server",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = OsrsGold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = serverUrlInput,
                        onValueChange = {
                            serverUrlInput = it
                            urlWarningMessage = if (DynamicStateApiService.isLoopbackUrl(it)) LOOPBACK_WARNING else null
                            errorMessage = null
                        },
                        label = { Text("Server URL", color = OsrsTextMuted) },
                        placeholder = { Text("https://pssst-worker.yourname.workers.dev", color = OsrsTextMuted.copy(alpha = 0.5f)) },
                        supportingText = urlWarningMessage?.let { { Text(it, color = OsrsRed) } },
                        colors = fieldColors,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { useOwnServer = !useOwnServer }) {
                    Text(
                        if (useOwnServer) "Use the normal setup (PC sends to this phone)" else "Advanced: I run my own server",
                        color = OsrsTextMuted
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val trimmedToken = tokenInput.trim()
                        val trimmedServerUrl = if (useOwnServer) serverUrlInput.trim() else ""
                        if (trimmedToken.isEmpty()) {
                            errorMessage = "Enter the Sync Token from Pssst Companion"
                        } else if (useOwnServer && trimmedServerUrl.isEmpty()) {
                            urlWarningMessage = "Enter your server's address"
                        } else {
                            viewModel.saveSetup(trimmedServerUrl, trimmedToken, rememberUuid = rememberMe)
                            SyncScheduler.schedulePeriodicSync(context)
                            SyncScheduler.syncNow(context)
                            onSetupComplete()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OsrsGold,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Connect", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Your PC sends while this app is open, and every so often in the background, whenever this phone is on the same Wi-Fi (or Tailscale). Your last data and timers stay on the phone when you are away.",
            style = MaterialTheme.typography.bodySmall,
            color = OsrsTextMuted,
            textAlign = TextAlign.Center
        )
    }
}
