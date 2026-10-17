package com.pssst.data.local

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

class AssetDatabaseSeeder(
    private val context: Context,
    private val database: AppDatabase = AppDatabase.getDatabase(context)
) {

    suspend fun seed(fileName: String = ASSET_FILE_NAME): Unit = withContext(Dispatchers.IO) {
        val items = loadItems(context, fileName)
        if (items.isNotEmpty()) {
            database.itemManifestDao().insertAll(items)
            Log.i(TAG, "Seeded ${items.size} item manifests into Room")
        }
    }

    suspend fun seedIfEmpty(fileName: String = ASSET_FILE_NAME): Boolean = withContext(Dispatchers.IO) {
        val dao = database.itemManifestDao()
        val currentCount = dao.count()
        if (currentCount < 1000) {
            Log.i(TAG, "Database has $currentCount items (< 1000). Seeding full manifest...")
            seed(fileName)
            true
        } else {
            false
        }
    }

    companion object {
        private const val TAG = "AssetDatabaseSeeder"
        const val ASSET_FILE_NAME = "items-complete.json"

        fun loadItems(context: Context, fileName: String = ASSET_FILE_NAME): List<ItemManifestEntity> {
            return runCatching {
                context.assets.open(fileName).use { inputStream ->
                    InputStreamReader(inputStream, Charsets.UTF_8).use { reader ->
                        val itemType = object : TypeToken<List<ItemManifestEntity>>() {}.type
                        Gson().fromJson<List<ItemManifestEntity>>(reader, itemType) ?: emptyList()
                    }
                }
            }.getOrElse { e ->
                Log.e(TAG, "Failed to load $fileName from assets", e)
                emptyList()
            }
        }

        suspend fun seed(
            context: Context,
            database: AppDatabase = AppDatabase.getDatabase(context),
            fileName: String = ASSET_FILE_NAME
        ) = withContext(Dispatchers.IO) {
            val items = loadItems(context, fileName)
            if (items.isNotEmpty()) {
                database.itemManifestDao().insertAll(items)
                Log.i(TAG, "Seeded ${items.size} item manifests into Room")
            }
        }

        suspend fun seedIfEmpty(
            context: Context,
            database: AppDatabase = AppDatabase.getDatabase(context),
            fileName: String = ASSET_FILE_NAME
        ): Boolean = withContext(Dispatchers.IO) {
            val dao = database.itemManifestDao()
            val currentCount = dao.count()
            if (currentCount < 1000) {
                Log.i(TAG, "Database has $currentCount items (< 1000). Seeding full manifest...")
                seed(context, database, fileName)
                true
            } else {
                false
            }
        }
    }
}
