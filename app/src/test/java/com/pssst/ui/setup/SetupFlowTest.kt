package com.pssst.ui.setup

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.pssst.data.local.AppDatabase
import com.pssst.data.local.PlayerStateDao
import com.pssst.data.local.UserPreferencesRepository
import com.pssst.ui.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SetupFlowTest {

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private lateinit var dao: PlayerStateDao
    private lateinit var fakePrefs: TestUserPreferencesRepository
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.playerStateDao()
        fakePrefs = TestUserPreferencesRepository()
        viewModel = MainViewModel(dao, fakePrefs, sharingStarted = SharingStarted.Eagerly)
    }

    @After
    fun tearDown() {
        database.close()
        Dispatchers.resetMain()
    }

    @Test
    fun setupFlow_savingUuidUpdatesPreferencesAndNavigatesToDashboard() = runTest(testDispatcher) {
        // 1. Initial boot into setup: no UUID saved
        val initial = viewModel.uiState.first { !it.isLoading }
        assertFalse(initial.hasConfiguredUuid)
        assertEquals("", initial.uuid)

        // 2. User pastes UUID and saves
        val token = "98765432-1111-2222-3333-444455556666"
        viewModel.saveUuid(token)

        // 3. Verify preferences and UI state updated
        val state = viewModel.uiState.first { it.hasConfiguredUuid }
        assertEquals(token, fakePrefs.savedUuid)
        assertTrue(state.hasConfiguredUuid)
        assertEquals(token, state.uuid)
    }

    @Test
    fun setupFlow_savingServerUrlAndUuidUpdatesPreferences() = runTest(testDispatcher) {
        val serverUrl = "http://192.168.1.100:8787"
        val token = "12345678-abcd-ef01-2345-6789abcdef01"

        viewModel.saveSetup(serverUrl, token)

        val state = viewModel.uiState.first { it.hasConfiguredUuid }
        assertEquals(serverUrl, fakePrefs.savedServerUrl)
        assertEquals(token, fakePrefs.savedUuid)
        assertEquals(token, fakePrefs.savedRememberedUuid)
        assertEquals(serverUrl, state.serverUrl)
        assertEquals(token, state.uuid)
        assertTrue(state.hasConfiguredUuid)
    }

    @Test
    fun setupFlow_rememberMeControlsSavedRememberedUuid() = runTest(testDispatcher) {
        val token = "token-to-forget"
        viewModel.saveSetup("http://10.0.2.2:8787", token, rememberUuid = false)
        testScheduler.advanceUntilIdle()
        assertEquals(token, fakePrefs.savedUuid)
        assertEquals(null, fakePrefs.savedRememberedUuid)

        viewModel.saveSetup("http://10.0.2.2:8787", token, rememberUuid = true)
        testScheduler.advanceUntilIdle()
        assertEquals(token, fakePrefs.savedRememberedUuid)

        viewModel.clearUuid()
        testScheduler.advanceUntilIdle()
        val clearedState = viewModel.uiState.first { !it.hasConfiguredUuid && it.rememberedUuid.isNotBlank() }
        assertEquals("", clearedState.uuid)
        assertEquals(token, clearedState.rememberedUuid)
    }
}

private class TestUserPreferencesRepository : UserPreferencesRepository {
    private val uuidStateFlow = MutableStateFlow<String?>(null)
    private val rememberedUuidStateFlow = MutableStateFlow<String?>(null)
    private val serverUrlStateFlow = MutableStateFlow<String?>(null)
    private val lastSyncStatusStateFlow = MutableStateFlow<String?>(null)

    override val uuidFlow: Flow<String?> = uuidStateFlow
    override val rememberedUuidFlow: Flow<String?> = rememberedUuidStateFlow
    override val serverUrlFlow: Flow<String?> = serverUrlStateFlow
    override val lastSyncStatusFlow: Flow<String?> = lastSyncStatusStateFlow

    var savedUuid: String? = null
    var savedRememberedUuid: String? = null
    var savedServerUrl: String? = null
    var savedLastSyncStatus: String? = null

    override suspend fun getUuid(): String? = savedUuid

    override suspend fun saveUuid(uuid: String) {
        savedUuid = uuid
        uuidStateFlow.value = uuid
    }

    override suspend fun clearUuid() {
        savedUuid = null
        uuidStateFlow.value = null
    }

    override suspend fun getRememberedUuid(): String? = savedRememberedUuid

    override suspend fun saveRememberedUuid(uuid: String) {
        savedRememberedUuid = uuid
        rememberedUuidStateFlow.value = uuid
    }

    override suspend fun clearRememberedUuid() {
        savedRememberedUuid = null
        rememberedUuidStateFlow.value = null
    }

    override suspend fun getServerUrl(): String? = savedServerUrl

    override suspend fun saveServerUrl(serverUrl: String) {
        savedServerUrl = serverUrl
        serverUrlStateFlow.value = serverUrl
    }

    override suspend fun clearServerUrl() {
        savedServerUrl = null
        serverUrlStateFlow.value = null
    }

    override suspend fun saveSetup(serverUrl: String, uuid: String) {
        savedServerUrl = serverUrl
        savedUuid = uuid
        serverUrlStateFlow.value = serverUrl
        uuidStateFlow.value = uuid
    }

    override suspend fun saveLastSyncStatus(status: String?) {
        savedLastSyncStatus = status
        lastSyncStatusStateFlow.value = status
    }

    private val notificationsEnabledStateFlow = MutableStateFlow(true)
    override val notificationsEnabledFlow: Flow<Boolean> = notificationsEnabledStateFlow
    override suspend fun getNotificationsEnabled(): Boolean = notificationsEnabledStateFlow.value
    override suspend fun saveNotificationsEnabled(enabled: Boolean) {
        notificationsEnabledStateFlow.value = enabled
    }
}
