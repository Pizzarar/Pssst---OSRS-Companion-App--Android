package com.pssst.data.network

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.io.Reader
import kotlin.math.abs

/**
 * Grand Exchange prices from the OSRS Wiki's real-time prices API (https://oldschool.runescape.wiki/w/RuneScape:Real-time_Prices).
 *
 * Etiquette the Wiki asks for, and what this does about it:
 * - a User-Agent that says what the app is and how to reach its author (never a browser UA, never spoofed);
 * - few requests: one `GET /latest` returns every price, the app makes it at most every 12 hours, and only if the user opted in;
 * - caching: the response carries an ETag, and the OkHttp cache turns an unchanged one into a conditional request.
 */
object WikiPrices {
    const val LATEST_URL = "https://prices.runescape.wiki/api/v1/osrs/latest"
    const val CONTACT_URL = "https://github.com/Pizzarar/Pssst---OSRS-Companion-App--Android"

    /** If the last high and last low trade are further apart than this, one of them is too old to average with. */
    private const val MAX_SIDE_SKEW_SECONDS = 7L * 24 * 60 * 60

    fun userAgent(versionName: String): String = "Pssst/$versionName (OSRS companion app; +$CONTACT_URL)"

    /**
     * One price per item: the midpoint of the last high and low trades, but only when both are recent relative to
     * each other. On rarely traded items one side can be weeks older; averaging it in would invent a price, so then
     * the newer side wins. With only one side known, that side is used. Null when there is no usable price.
     */
    fun priceOf(high: Long?, low: Long?, highTime: Long?, lowTime: Long?): Long? {
        val h = high?.takeIf { it > 0 }
        val l = low?.takeIf { it > 0 }
        return when {
            h != null && l != null -> {
                if (highTime != null && lowTime != null && abs(highTime - lowTime) > MAX_SIDE_SKEW_SECONDS) {
                    if (highTime >= lowTime) h else l
                } else {
                    (h + l) / 2
                }
            }
            else -> h ?: l
        }
    }

    /** Parses the `/latest` document (`{"data": {"<id>": {high, highTime, low, lowTime}, ...}}`) into id -> price. */
    fun parseLatest(source: Reader): Map<Int, Long> {
        val prices = HashMap<Int, Long>()
        JsonReader(source).use { reader ->
            reader.beginObject()
            while (reader.hasNext()) {
                if (reader.nextName() != "data") {
                    reader.skipValue()
                    continue
                }
                reader.beginObject()
                while (reader.hasNext()) {
                    val id = reader.nextName().toIntOrNull()
                    if (id == null) {
                        reader.skipValue()
                        continue
                    }
                    var high: Long? = null
                    var low: Long? = null
                    var highTime: Long? = null
                    var lowTime: Long? = null
                    reader.beginObject()
                    while (reader.hasNext()) {
                        val name = reader.nextName()
                        if (reader.peek() == JsonToken.NULL) {
                            reader.nextNull()
                            continue
                        }
                        when (name) {
                            "high" -> high = reader.nextLong()
                            "low" -> low = reader.nextLong()
                            "highTime" -> highTime = reader.nextLong()
                            "lowTime" -> lowTime = reader.nextLong()
                            else -> reader.skipValue()
                        }
                    }
                    reader.endObject()
                    priceOf(high, low, highTime, lowTime)?.let { prices[id] = it }
                }
                reader.endObject()
            }
            reader.endObject()
        }
        return prices
    }
}

class WikiPricesClient(
    private val http: OkHttpClient,
    private val userAgent: String,
    private val url: String = WikiPrices.LATEST_URL
) {
    sealed interface Result {
        data class Prices(val prices: Map<Int, Long>) : Result
        data class HttpError(val code: Int) : Result
        data class NetworkError(val cause: IOException) : Result
    }

    suspend fun fetchLatest(): Result = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(url).header("User-Agent", userAgent).build()
        try {
            http.newCall(request).execute().use { response ->
                val body = response.body
                if (!response.isSuccessful || body == null) {
                    Result.HttpError(response.code)
                } else {
                    Result.Prices(WikiPrices.parseLatest(body.charStream()))
                }
            }
        } catch (e: IOException) {
            Result.NetworkError(e)
        }
    }
}
