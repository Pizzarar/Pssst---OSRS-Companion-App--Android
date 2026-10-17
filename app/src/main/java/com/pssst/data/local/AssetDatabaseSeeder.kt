package com.pssst.data.local

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

/**
 * The item manifest bundled with the app: every item id with its name, a GE price snapshot, and some icons.
 *
 * File format (version 1+): `{"version": N, "generatedAt": epochMs, "items": [{id, name, iconBase64?, gePrice}]}`.
 * A bare array (the original format) still loads, as version 0 with no price timestamp.
 *
 * Reseeding rules: the manifest is applied on first run and again whenever the bundled `version` is higher than the
 * one last applied. Names and icons always come from the bundle; a price only does if the bundle is newer than
 * what is stored, so a refreshed price is never replaced by an older baked one.
 */
object AssetDatabaseSeeder {
    private const val TAG = "AssetDatabaseSeeder"
    const val ASSET_FILE_NAME = "items-complete.json"
    private const val PREFS_NAME = "item_manifest_meta"
    private const val KEY_VERSION = "version"

    data class BundledManifest(val version: Int, val generatedAt: Long, val items: List<ItemManifestEntity>)

    fun loadManifest(context: Context, fileName: String = ASSET_FILE_NAME): BundledManifest {
        return runCatching {
            context.assets.open(fileName).use { inputStream ->
                InputStreamReader(inputStream, Charsets.UTF_8).use { reader ->
                    parseManifest(JsonParser.parseReader(reader))
                }
            }
        }.getOrElse { e ->
            Log.e(TAG, "Failed to load $fileName from assets", e)
            BundledManifest(0, 0L, emptyList())
        }
    }

    fun loadItems(context: Context, fileName: String = ASSET_FILE_NAME): List<ItemManifestEntity> =
        loadManifest(context, fileName).items

    internal fun parseManifest(root: com.google.gson.JsonElement): BundledManifest {
        val gson = Gson()
        val itemType = object : TypeToken<List<ItemManifestEntity>>() {}.type
        if (root.isJsonArray) {
            return BundledManifest(0, 0L, gson.fromJson(root, itemType) ?: emptyList())
        }
        val obj = root.asJsonObject
        val generatedAt = obj.get("generatedAt")?.asLong ?: 0L
        val items: List<ItemManifestEntity> = gson.fromJson(obj.get("items"), itemType) ?: emptyList()
        // Baked prices are only as old as the bundle; an item with no price has no timestamp either
        items.forEach { if (it.gePrice > 0 && it.priceUpdatedAt == 0L) it.priceUpdatedAt = generatedAt }
        return BundledManifest(obj.get("version")?.asInt ?: 0, generatedAt, items)
    }

    /** Applies the bundled manifest unconditionally (still never replacing a newer price). */
    suspend fun seed(
        context: Context,
        database: AppDatabase = AppDatabase.getDatabase(context),
        fileName: String = ASSET_FILE_NAME
    ) = withContext(Dispatchers.IO) {
        apply(context, database, loadManifest(context, fileName))
    }

    /**
     * Applies the bundled manifest if the table is (nearly) empty or the bundle is newer than the one last applied.
     *
     * @return true if it was applied
     */
    suspend fun seedIfNeeded(
        context: Context,
        database: AppDatabase = AppDatabase.getDatabase(context),
        fileName: String = ASSET_FILE_NAME
    ): Boolean = seedIfNeeded(context, database, loadManifest(context, fileName))

    internal suspend fun seedIfNeeded(
        context: Context,
        database: AppDatabase,
        manifest: BundledManifest
    ): Boolean = withContext(Dispatchers.IO) {
        if (manifest.items.isEmpty()) return@withContext false

        val count = database.itemManifestDao().count()
        val appliedVersion = prefs(context).getInt(KEY_VERSION, -1)
        if (count > 0 && manifest.version <= appliedVersion) {
            return@withContext false
        }
        Log.i(TAG, "Applying manifest v${manifest.version} (table has $count items, last applied v$appliedVersion)")
        apply(context, database, manifest)
        true
    }

    private fun apply(context: Context, database: AppDatabase, manifest: BundledManifest) {
        if (manifest.items.isEmpty()) return
        val dao = database.itemManifestDao()
        if (dao.count() == 0) {
            dao.insertAll(manifest.items)
        } else {
            dao.mergeBundled(manifest.items)
        }
        prefs(context).edit().putInt(KEY_VERSION, manifest.version).apply()
        Log.i(TAG, "Seeded ${manifest.items.size} item manifests into Room")
    }

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
