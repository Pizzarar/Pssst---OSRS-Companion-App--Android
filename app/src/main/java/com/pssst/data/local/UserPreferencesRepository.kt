package com.pssst.data.local

import kotlinx.coroutines.flow.flowOf
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

interface UserPreferencesRepository {
    val uuidFlow: Flow<String?>
    val rememberedUuidFlow: Flow<String?>
    val serverUrlFlow: Flow<String?>
    val lastSyncStatusFlow: Flow<String?>
    val notificationsEnabledFlow: Flow<Boolean>
    suspend fun getUuid(): String?
    suspend fun saveUuid(uuid: String)
    suspend fun clearUuid()
    suspend fun getRememberedUuid(): String?
    suspend fun saveRememberedUuid(uuid: String)
    suspend fun clearRememberedUuid()
    suspend fun getServerUrl(): String?
    suspend fun saveServerUrl(serverUrl: String)
    suspend fun clearServerUrl()
    suspend fun saveSetup(serverUrl: String, uuid: String)
    suspend fun saveLastSyncStatus(status: String?)
    suspend fun getNotificationsEnabled(): Boolean
    suspend fun saveNotificationsEnabled(enabled: Boolean)

    /**
     * Whether to refresh item prices from the OSRS Wiki. null means the user has not chosen yet: fetching from a
     * third party reveals their IP address, so it is never assumed either way. (Defaults keep other implementations,
     * such as test fakes, compiling.)
     */
    val priceRefreshEnabledFlow: Flow<Boolean?> get() = flowOf(null)
    suspend fun getPriceRefreshEnabled(): Boolean? = null
    suspend fun savePriceRefreshEnabled(enabled: Boolean) {}
}

class DataStoreUserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

    constructor(context: Context) : this(context.dataStore)

    private val uuidKey = stringPreferencesKey("user_uuid")
    private val rememberedUuidKey = stringPreferencesKey("remembered_uuid")
    private val serverUrlKey = stringPreferencesKey("server_url")
    private val lastSyncStatusKey = stringPreferencesKey("last_sync_status")
    private val notificationsEnabledKey = booleanPreferencesKey("notifications_enabled")
    private val priceRefreshEnabledKey = booleanPreferencesKey("price_refresh_enabled")

    override val uuidFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[uuidKey]
    }

    override val rememberedUuidFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[rememberedUuidKey]
    }

    override val serverUrlFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[serverUrlKey]
    }

    override val lastSyncStatusFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[lastSyncStatusKey]
    }

    override val notificationsEnabledFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[notificationsEnabledKey] ?: true
    }

    override val priceRefreshEnabledFlow: Flow<Boolean?> = dataStore.data.map { it[priceRefreshEnabledKey] }

    override suspend fun getPriceRefreshEnabled(): Boolean? = dataStore.data.map { it[priceRefreshEnabledKey] }.first()

    override suspend fun savePriceRefreshEnabled(enabled: Boolean) {
        dataStore.edit { it[priceRefreshEnabledKey] = enabled }
    }

    override suspend fun getUuid(): String? {
        return dataStore.data.map { it[uuidKey] }.first()
    }

    override suspend fun saveUuid(uuid: String) {
        dataStore.edit { preferences ->
            preferences[uuidKey] = uuid
        }
    }

    override suspend fun clearUuid() {
        dataStore.edit { preferences ->
            preferences.remove(uuidKey)
        }
    }

    override suspend fun getRememberedUuid(): String? {
        return dataStore.data.map { it[rememberedUuidKey] }.first()
    }

    override suspend fun saveRememberedUuid(uuid: String) {
        dataStore.edit { preferences ->
            preferences[rememberedUuidKey] = uuid
        }
    }

    override suspend fun clearRememberedUuid() {
        dataStore.edit { preferences ->
            preferences.remove(rememberedUuidKey)
        }
    }

    override suspend fun getServerUrl(): String? {
        return dataStore.data.map { it[serverUrlKey] }.first()
    }

    override suspend fun saveServerUrl(serverUrl: String) {
        dataStore.edit { preferences ->
            preferences[serverUrlKey] = serverUrl
        }
    }

    override suspend fun clearServerUrl() {
        dataStore.edit { preferences ->
            preferences.remove(serverUrlKey)
        }
    }

    override suspend fun saveSetup(serverUrl: String, uuid: String) {
        dataStore.edit { preferences ->
            preferences[serverUrlKey] = serverUrl
            preferences[uuidKey] = uuid
        }
    }

    override suspend fun saveLastSyncStatus(status: String?) {
        dataStore.edit { preferences ->
            if (status == null) {
                preferences.remove(lastSyncStatusKey)
            } else {
                preferences[lastSyncStatusKey] = status
            }
        }
    }

    override suspend fun getNotificationsEnabled(): Boolean {
        return dataStore.data.map { it[notificationsEnabledKey] ?: true }.first()
    }

    override suspend fun saveNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[notificationsEnabledKey] = enabled
        }
    }
}
