package com.pssst

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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

@Composable
fun MainNavigation(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel(
        factory = MainViewModel.provideFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val initialKey = if (uiState.hasConfiguredUuid) DashboardNavKey else SetupNavKey
    val backStack = rememberNavBackStack(initialKey)

    LaunchedEffect(uiState.hasConfiguredUuid) {
        if (!uiState.hasConfiguredUuid && !backStack.contains(SetupNavKey)) {
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
                        backStack.clear()
                        backStack.add(DashboardNavKey)
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
                    onToggleNotifications = { viewModel.setNotificationsEnabled(it) },
                    onDisconnect = {
                        viewModel.clearUuid()
                        backStack.clear()
                        backStack.add(SetupNavKey)
                    }
                )
            }
        }
    )
}
