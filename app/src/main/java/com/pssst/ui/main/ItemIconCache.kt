package com.pssst.ui.main

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

/**
 * In-memory LRU cache for decoded OSRS pixel art item icons.
 * Decodes base64 strings into Compose [ImageBitmap] once per item ID.
 */
object ItemIconCache {
    private const val MAX_ENTRIES = 1000

    private val cache = object : LinkedHashMap<Int, ImageBitmap>(128, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Int, ImageBitmap>?): Boolean {
            return size > MAX_ENTRIES
        }
    }

    @Synchronized
    fun get(id: Int): ImageBitmap? = cache[id]

    @Synchronized
    fun getOrCreate(id: Int, base64: String?): ImageBitmap? {
        cache[id]?.let { return it }
        if (base64.isNullOrBlank()) return null

        return try {
            val bytes = Base64.decode(base64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            bitmap?.asImageBitmap()?.also { imageBitmap ->
                cache[id] = imageBitmap
            }
        } catch (e: Throwable) {
            null
        }
    }

    @Synchronized
    fun clear() {
        cache.clear()
    }
}
