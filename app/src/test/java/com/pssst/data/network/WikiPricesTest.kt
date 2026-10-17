package com.pssst.data.network

import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.StringReader

class WikiPricesTest {

    private val day = 24L * 60 * 60
    private val now = 1_800_000_000L

    // --- the price rule ---------------------------------------------------------------------------------------

    @Test
    fun priceOf_isTheMidpointWhenBothSidesAreRecentRelativeToEachOther() {
        assertEquals(150L, WikiPrices.priceOf(high = 200, low = 100, highTime = now, lowTime = now - day))
        assertEquals(150L, WikiPrices.priceOf(high = 200, low = 100, highTime = now - 7 * day, lowTime = now))
    }

    @Test
    fun priceOf_usesTheNewerSideWhenTheOtherIsOverAWeekOlder() {
        // The low trade is a month stale: averaging it in would invent a price nobody traded at
        assertEquals(200L, WikiPrices.priceOf(high = 200, low = 100, highTime = now, lowTime = now - 30 * day))
        assertEquals(100L, WikiPrices.priceOf(high = 200, low = 100, highTime = now - 30 * day, lowTime = now))
    }

    @Test
    fun priceOf_usesTheOnlySideThatExistsAndNothingElse() {
        assertEquals(200L, WikiPrices.priceOf(200, null, now, null))
        assertEquals(100L, WikiPrices.priceOf(null, 100, null, now))
        assertNull(WikiPrices.priceOf(null, null, null, null))
        assertNull("a zero price is no price", WikiPrices.priceOf(0, 0, now, now))
    }

    // --- parsing ----------------------------------------------------------------------------------------------

    @Test
    fun parseLatest_readsPricesAndSkipsItemsWithoutOne() {
        val json = """
            {"data": {
              "2":    {"high": 200, "highTime": $now, "low": 100, "lowTime": $now},
              "6":    {"high": 180000, "highTime": $now, "low": null, "lowTime": null},
              "8":    {"high": null, "highTime": null, "low": null, "lowTime": null},
              "x":    {"high": 1, "low": 1},
              "10":   {"high": 50, "highTime": $now, "low": 30, "lowTime": ${now - 40 * day}, "somethingNew": [1, 2]}
            }, "unrelated": true}
        """.trimIndent()

        val prices = WikiPrices.parseLatest(StringReader(json))

        assertEquals(mapOf(2 to 150L, 6 to 180000L, 10 to 50L), prices)
    }

    @Test
    fun userAgent_namesTheAppAndHowToReachItsAuthor() {
        val ua = WikiPrices.userAgent("1.2.3")
        assertTrue(ua, ua.startsWith("Pssst/1.2.3 ("))
        assertTrue(ua, ua.contains("https://github.com/"))
    }

    // --- the client -------------------------------------------------------------------------------------------

    private lateinit var server: MockWebServer

    @Before
    fun startServer() {
        server = MockWebServer().also { it.start() }
    }

    @After
    fun stopServer() {
        server.shutdown()
    }

    private fun client() = WikiPricesClient(OkHttpClient(), "Pssst/test (contact)", server.url("/api/v1/osrs/latest").toString())

    @Test
    fun fetchLatest_sendsADescriptiveUserAgentAndParsesTheBody() = runBlocking {
        server.enqueue(MockResponse().setBody("""{"data": {"4151": {"high": 2000, "highTime": $now, "low": 1000, "lowTime": $now}}}"""))

        val result = client().fetchLatest()

        assertEquals(WikiPricesClient.Result.Prices(mapOf(4151 to 1500L)), result)
        assertEquals("Pssst/test (contact)", server.takeRequest().getHeader("User-Agent"))
    }

    @Test
    fun fetchLatest_reportsHttpErrorsAndNetworkFailuresWithoutThrowing() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(429))
        assertEquals(WikiPricesClient.Result.HttpError(429), client().fetchLatest())

        server.shutdown()
        assertTrue(client().fetchLatest() is WikiPricesClient.Result.NetworkError)
    }
}
