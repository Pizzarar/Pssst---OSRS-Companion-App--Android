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
    var urlWarningMessage by remember {
        mutableStateOf(
            if (DynamicStateApiService.isLoopbackUrl(initialServerUrl)) {
                "127.0.0.1 is this phone, not your PC. Use your PC's address, such as http://192.168.1.20:8788"
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
            text = "Connect to the Pssst Companion plugin in RuneLite to see your bank, inventory and farming timers on your phone.",
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
                            "127.0.0.1 is this phone, not your PC. Use your PC's address, such as http://192.168.1.20:8788"
                        } else {
                            null
                        }
                        errorMessage = null
                    },
                    label = { Text("Server URL", color = OsrsTextMuted) },
                    placeholder = { Text("http://192.168.1.20:8788", color = OsrsTextMuted.copy(alpha = 0.5f)) },
                    supportingText = urlWarningMessage?.let {
                        { Text(it, color = OsrsRed) }
                    },
                    colors = fieldColors,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Sync Token field
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
                    placeholder = { Text("Paste your Sync Token", color = OsrsTextMuted.copy(alpha = 0.5f)) },
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
                        text = "Remember this Sync Token on this device",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (rememberMe) OsrsGold else OsrsTextMuted
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val trimmedToken = tokenInput.trim()
                        val trimmedServerUrl = serverUrlInput.trim()
                        urlWarningMessage = if (DynamicStateApiService.isLoopbackUrl(trimmedServerUrl)) {
                            "127.0.0.1 is this phone, not your PC. Use your PC's address, such as http://192.168.1.20:8788"
                        } else {
                            null
                        }
                        if (trimmedServerUrl.isEmpty()) {
                            urlWarningMessage = "Enter your server address, such as http://192.168.1.20:8788"
                        } else if (trimmedToken.isEmpty()) {
                            errorMessage = "Enter the Sync Token from Pssst Companion"
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
                    Text("Connect & Start Sync", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Find your Sync Token in RuneLite → Configuration → Pssst Companion → Master Sync.\nFor the server, use your PC's address on the same Wi-Fi, or its Tailscale (or other VPN) address.",
            style = MaterialTheme.typography.bodySmall,
            color = OsrsTextMuted,
            textAlign = TextAlign.Center
        )
    }
}
