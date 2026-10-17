package com.pssst.ui.setup

import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.RadioButton
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pssst.data.network.DynamicStateApiService
import com.pssst.data.network.StateApiService
import com.pssst.theme.OsrsBorderOutline
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsPanelDark
import com.pssst.theme.OsrsRed
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.OsrsTextPrimary
import com.pssst.theme.osrsPanel
import com.pssst.ui.main.MainViewModel
import com.pssst.worker.SyncScheduler

@Composable
fun SetupScreen(
    viewModel: MainViewModel,
    onSetupComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val initialServerUrl = viewModel.uiState.value.serverUrl
    val initialUuid = viewModel.uiState.value.uuid.ifBlank { viewModel.uiState.value.rememberedUuid }

    var serverUrlInput by remember { mutableStateOf(initialServerUrl) }
    var tokenInput by remember { mutableStateOf(initialUuid) }
    var rememberMe by remember { mutableStateOf(viewModel.uiState.value.rememberedUuid.isNotBlank() || initialUuid.isNotBlank()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    // Live prices contact a third party (the OSRS Wiki sees the IP address), so nothing is preselected
    var priceChoice by remember { mutableStateOf(viewModel.uiState.value.priceRefreshEnabled) }
    var priceChoiceMissing by remember { mutableStateOf(false) }
    var urlWarningMessage by remember {
        mutableStateOf(
            if (DynamicStateApiService.isLoopbackUrl(initialServerUrl)) {
                "On the emulator use http://10.0.2.2:8787"
            } else {
                null
            }
        )
    }
    val context = LocalContext.current

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
            text = "Connect your RuneLite companion plugin to monitor your bank, inventory, and farming timers anywhere.",
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
                // Server URL Field
                Text(
                    text = "Server URL",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = OsrsGold
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = serverUrlInput,
                    onValueChange = {
                        serverUrlInput = it
                        urlWarningMessage = if (DynamicStateApiService.isLoopbackUrl(it)) {
                            "On the emulator use http://10.0.2.2:8787"
                        } else {
                            null
                        }
                        errorMessage = null
                    },
                    label = { Text("Server URL", color = OsrsTextMuted) },
                    placeholder = { Text("http://10.0.2.2:8787", color = OsrsTextMuted.copy(alpha = 0.5f)) },
                    supportingText = urlWarningMessage?.let {
                        { Text(it, color = OsrsRed) }
                    },
                    colors = fieldColors,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Sync Token (UUID) Field
                Text(
                    text = "Sync Token",
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
                    placeholder = { Text("Paste your UUID here", color = OsrsTextMuted.copy(alpha = 0.5f)) },
                    singleLine = true,
                    isError = errorMessage != null,
                    supportingText = errorMessage?.let { { Text(it, color = OsrsRed) } },
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Remember Me Row
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
                        text = "Remember this UUID on this device",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (rememberMe) OsrsGold else OsrsTextMuted
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Item prices",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = OsrsGold
                )
                PriceChoiceRow(
                    selected = priceChoice == true,
                    title = "Fetch live prices from the OSRS Wiki",
                    subtitle = "The Wiki's price server will see your IP address. Refreshes about twice a day.",
                    onSelect = { priceChoice = true; priceChoiceMissing = false }
                )
                PriceChoiceRow(
                    selected = priceChoice == false,
                    title = "Use the prices bundled with the app",
                    subtitle = "No extra network requests. Prices get older until the app is updated.",
                    onSelect = { priceChoice = false; priceChoiceMissing = false }
                )
                if (priceChoiceMissing) {
                    Text(
                        text = "Choose how to get item prices",
                        style = MaterialTheme.typography.bodySmall,
                        color = OsrsRed
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val trimmedToken = tokenInput.trim()
                        val trimmedServerUrl = serverUrlInput.trim()
                        urlWarningMessage = if (DynamicStateApiService.isLoopbackUrl(trimmedServerUrl)) {
                            "On the emulator use http://10.0.2.2:8787"
                        } else {
                            null
                        }
                        val chosenPrices = priceChoice
                        if (trimmedServerUrl.isEmpty()) {
                            urlWarningMessage = "Enter the address of your own server, such as http://192.168.1.20:8788"
                        } else if (trimmedToken.isEmpty()) {
                            errorMessage = "Please enter a valid Sync Token (UUID)"
                        } else if (chosenPrices == null) {
                            priceChoiceMissing = true
                        } else {
                            viewModel.setPriceRefreshEnabled(chosenPrices)
                            SyncScheduler.applyPriceRefreshChoice(context, chosenPrices)
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
                    Text("Connect & Start Sync", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Find your Sync Token (Auth Token) in RuneLite -> Configuration -> Companion Plugin.\nEnter a local IP or Nord Meshnet address for self-hosting.",
            style = MaterialTheme.typography.bodySmall,
            color = OsrsTextMuted,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PriceChoiceRow(
    selected: Boolean,
    title: String,
    subtitle: String,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelect,
            colors = RadioButtonDefaults.colors(selectedColor = OsrsGold, unselectedColor = OsrsBorderOutline)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column(modifier = Modifier.padding(top = 12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected) OsrsGold else OsrsTextPrimary
            )
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = OsrsTextMuted)
        }
    }
}
