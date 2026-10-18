package com.pssst.data

import com.pssst.data.local.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/** In-memory preferences for tests that need a paired phone. */
class TestPreferences(
    private val uuid: String? = null,
    private val notifications: Boolean = true
) : UserPreferencesRepository {
    override val uuidFlow: Flow<String?> = flowOf(uuid)
    override val rememberedUuidFlow: Flow<String?> = flowOf(uuid)
    override val serverUrlFlow: Flow<String?> = flowOf(null)
    override val lastSyncStatusFlow: Flow<String?> = flowOf(null)
    override val notificationsEnabledFlow: Flow<Boolean> = flowOf(notifications)
    override suspend fun getUuid(): String? = uuid
    override suspend fun saveUuid(uuid: String) {}
    override suspend fun clearUuid() {}
    override suspend fun getRememberedUuid(): String? = uuid
    override suspend fun saveRememberedUuid(uuid: String) {}
    override suspend fun clearRememberedUuid() {}
    override suspend fun getServerUrl(): String? = null
    override suspend fun saveServerUrl(serverUrl: String) {}
    override suspend fun clearServerUrl() {}
    override suspend fun saveSetup(serverUrl: String, uuid: String) {}
    var lastStatus: String? = null
    override suspend fun saveLastSyncStatus(status: String?) { lastStatus = status }
    var failures: Int = 0
    override suspend fun getSyncFailures(): Int = failures
    override suspend fun saveSyncFailures(count: Int) { failures = count }
    override suspend fun getNotificationsEnabled(): Boolean = notifications
    override suspend fun saveNotificationsEnabled(enabled: Boolean) {}
}
