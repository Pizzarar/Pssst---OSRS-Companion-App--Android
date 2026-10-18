package com.pssst.ui.setup

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pssst.theme.OsrsButton
import com.pssst.theme.OsrsButtonVariant
import com.pssst.theme.OsrsCheckbox
import com.pssst.theme.OsrsGold
import com.pssst.theme.OsrsHeaderDivider
import com.pssst.theme.OsrsRed
import com.pssst.theme.OsrsTextField
import com.pssst.theme.OsrsTextMuted
import com.pssst.theme.OsrsTextPrimary
import com.pssst.theme.art.FrameWeight
import com.pssst.theme.art.GoldLeafText
import com.pssst.theme.art.NumberPlaque
import com.pssst.theme.art.TitlePlaque
import com.pssst.theme.art.parchmentNotice
import com.pssst.theme.art.stoneGround
import com.pssst.theme.osrsPanel
import com.pssst.theme.osrsSlot
import com.pssst.ui.main.MainViewModel
import com.pssst.ui.main.rememberCopyText
import com.pssst.util.PhoneAddresses
import com.pssst.worker.SyncScheduler

/**
 * Pairing. Normal path: paste or type the Sync Token from RuneLite here, and type this phone's address (shown here)
 * into the plugin's Phone Address; the plugin then sends to this phone.
 */
@Composable
fun SetupScreen(
    viewModel: MainViewModel,
    onSetupComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val validRemembered = uiState.rememberedUuid.trim()

    LaunchedEffect(uiState.hasConfiguredUuid, uiState.hasSyncedData) {
        if (uiState.hasConfiguredUuid && uiState.hasSyncedData) {
            onSetupComplete()
        }
    }

    var tokenInput by remember(validRemembered) { mutableStateOf(validRemembered) }
    var rememberMe by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val copyText = rememberCopyText()
    var phoneAddresses by remember { mutableStateOf(PhoneAddresses.forDisplay(context)) }
    val isWaitingForHandshake = uiState.hasConfiguredUuid && !uiState.hasSyncedData
    val syncStatus = uiState.lastSyncStatus.orEmpty()
    val isRejectedByTokenMismatch = syncStatus.startsWith("Rejected")

    SetupContent(
        tokenInput = tokenInput,
        onTokenInputChange = {
            tokenInput = it
            errorMessage = null
            if (isWaitingForHandshake) {
                viewModel.clearUuid()
            }
        },
        rememberMe = rememberMe,
        onRememberMeChange = { rememberMe = it },
        errorMessage = errorMessage,
        phoneAddresses = phoneAddresses,
        onRefreshAddresses = { phoneAddresses = PhoneAddresses.forDisplay(context) },
        onCopyAddress = { copyText(it) },
        isWaitingForHandshake = isWaitingForHandshake,
        isRejectedByTokenMismatch = isRejectedByTokenMismatch,
        activeToken = uiState.uuid,
        onConnect = {
            val trimmedToken = tokenInput.trim()
            if (trimmedToken.isEmpty()) {
                errorMessage = "Enter a Sync Token (e.g. Goblin or goblin-whip)"
            } else {
                errorMessage = null
                viewModel.saveSetup("", trimmedToken, rememberUuid = rememberMe)
                SyncScheduler.schedulePeriodicSync(context)
                SyncScheduler.syncNow(context)
            }
        },
        onCancelHandshake = {
            SyncScheduler.cancelAll(context)
            viewModel.clearUuid()
        },
        modifier = modifier
    )
}

@Composable
fun SetupContent(
    tokenInput: String,
    onTokenInputChange: (String) -> Unit,
    rememberMe: Boolean,
    onRememberMeChange: (Boolean) -> Unit,
    errorMessage: String?,
    phoneAddresses: List<PhoneAddresses.Address>,
    onRefreshAddresses: () -> Unit,
    onCopyAddress: (String) -> Unit,
    isWaitingForHandshake: Boolean,
    isRejectedByTokenMismatch: Boolean,
    activeToken: String,
    onConnect: () -> Unit,
    onCancelHandshake: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .stoneGround()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TitlePlaque(
            title = "Welcome to Pssst",
            subtitle = "Pair with the Pssst Companion plugin in RuneLite. Your PC sends your bank, inventory and timers to this phone."
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .osrsPanel(weight = FrameWeight.HEAVY)
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NumberPlaque(label = "1", size = 24.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    GoldLeafText(
                        text = "1. Sync Token",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                OsrsTextField(
                    value = tokenInput,
                    onValueChange = onTokenInputChange,
                    label = "Sync Token",
                    placeholder = "e.g. Goblin or goblin-whip (from RuneLite)",
                    isError = errorMessage != null || isRejectedByTokenMismatch,
                    mono = true,
                    supportingText = errorMessage,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                OsrsCheckbox(
                    checked = rememberMe,
                    onCheckedChange = onRememberMeChange,
                    label = "Remember this Sync Token on this device"
                )

                Spacer(modifier = Modifier.height(14.dp))
                OsrsHeaderDivider()
                Spacer(modifier = Modifier.height(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    NumberPlaque(label = "2", size = 24.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    GoldLeafText(
                        text = "2. Phone Address",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
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
                    TextButton(onClick = onRefreshAddresses) {
                        Text("Check again", color = OsrsGold)
                    }
                }
                phoneAddresses.forEach { address ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .parchmentNotice()
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(address.host, style = MaterialTheme.typography.titleMedium, color = OsrsGold, fontWeight = FontWeight.Bold)
                            Text(address.label, style = MaterialTheme.typography.labelSmall, color = OsrsTextMuted)
                        }
                        TextButton(onClick = { onCopyAddress(address.host) }) {
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

                Spacer(modifier = Modifier.height(18.dp))

                if (isWaitingForHandshake) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .osrsSlot(
                                hasItem = false,
                                cornerRadius = 2.dp,
                                highlightBorderColor = if (isRejectedByTokenMismatch) OsrsRed else OsrsGold
                            )
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isRejectedByTokenMismatch) {
                                "Sync Token Mismatch"
                            } else {
                                "Listening for RuneLite ($activeToken)…"
                            },
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isRejectedByTokenMismatch) OsrsRed else OsrsGold
                        )
                        Text(
                            text = if (isRejectedByTokenMismatch) {
                                "Your PC reached this phone, but its Sync Token did not match '$activeToken'. Match the Sync Token in both places and click Save or Send Now in RuneLite."
                            } else {
                                "Waiting for your first snapshot from RuneLite. Make sure you are logged in (or have logged in once) and click Save or Send Now in the RuneLite Pssst panel."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = OsrsTextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OsrsButton(
                        text = "Cancel / Change Token",
                        onClick = onCancelHandshake,
                        variant = OsrsButtonVariant.STONE,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    OsrsButton(
                        text = "Connect",
                        onClick = onConnect,
                        variant = OsrsButtonVariant.GOLD,
                        modifier = Modifier.fillMaxWidth()
                    )
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

