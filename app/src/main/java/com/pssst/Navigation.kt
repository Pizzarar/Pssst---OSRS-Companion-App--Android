package com.pssst

import com.pssst.worker.SyncScheduler
import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.pssst.ui.main.DashboardScreen
import com.pssst.ui.main.MainViewModel
import com.pssst.ui.setup.SetupScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun MainNavigation(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel(
        factory = MainViewModel.provideFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appContext = LocalContext.current.applicationContext
    val scope = rememberCoroutineScope()
    val isVerifiedConnection = uiState.hasConfiguredUuid && uiState.hasSyncedData
    val initialKey = if (isVerifiedConnection) DashboardNavKey else SetupNavKey
    val backStack = rememberNavBackStack(initialKey)

    LaunchedEffect(isVerifiedConnection) {
        if (isVerifiedConnection) {
            if (!backStack.contains(DashboardNavKey)) {
                backStack.clear()
                backStack.add(DashboardNavKey)
            }
        } else if (!backStack.contains(SetupNavKey)) {
            backStack.clear()
            backStack.add(SetupNavKey)
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<SetupNavKey> {
                SetupScreen(
                    viewModel = viewModel,
                    onSetupComplete = {
                        if (uiState.hasConfiguredUuid && uiState.hasSyncedData) {
                            backStack.clear()
                            backStack.add(DashboardNavKey)
                        }
                    }
                )
            }
            entry<DashboardNavKey> {
                DashboardScreen(
                    state = uiState,
                    onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
                    onBankSortModeChanged = { viewModel.onBankSortModeChanged(it) },
                    onPotionSearchQueryChanged = { viewModel.onPotionSearchQueryChanged(it) },
                    onQuestSearchQueryChanged = { viewModel.onQuestSearchQueryChanged(it) },
                    onQuestFilterChanged = { viewModel.onQuestFilterChanged(it) },
                    onToggleNotifications = { enabled ->
                        viewModel.setNotificationsEnabled(enabled)
                        // Queue (or cancel) the reminders from the saved snapshot right now, with or without a network
                        scope.launch(Dispatchers.IO) { SyncScheduler.replanFromCache(appContext, enabled) }
                    },
                    onSaveGoalConfig = { viewModel.saveGoalConfig(it) },
                    onSearchGoalItem = { viewModel.onGoalSearchQueryChanged(it) },
                    onDisconnect = {
                        SyncScheduler.cancelAll(appContext)
                        viewModel.clearUuid()
                        backStack.clear()
                        backStack.add(SetupNavKey)
                    }
                )
            }
        }
    )
}
