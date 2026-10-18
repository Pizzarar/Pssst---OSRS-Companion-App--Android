package com.pssst.receiver

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.pssst.data.network.model.StateDataDto
import com.pssst.data.network.model.StateResponse
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException
import java.security.MessageDigest
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Receives snapshots sent by the Pssst Companion plugin: `POST /sync` with `Authorization: Bearer <sync token>` and the
 * same JSON body the Worker accepts. The plugin only sends, so the listening happens here, on the phone, and only while
 * the app is open or in a short background window ([ReceiverHost]).
 *
 * Deliberately small: one route, POST only, header-only token compared in constant time, home-network and Tailscale
 * peers only, bounded header and body size, a deadline per request, one connection at a time.
 */
class PhoneReceiver(
    private val port: Int = DEFAULT_PORT,
    private val token: suspend () -> String?,
    /** Saves the snapshot and returns the item ids the app still lacks icons for (put in the reply). */
    private val onSnapshot: suspend (StateResponse) -> List<Int>,
    /** The phone's own address the plugin reached it on (to spot a changed address later). */
    private val onDeliveredTo: (String) -> Unit = {},
    /** Called when a peer presents a Bearer token that does not match [token]. */
    private val onRejected: suspend () -> Unit = {},
    private val now: () -> Long = System::currentTimeMillis
) {
    private val gson = Gson()
    @Volatile private var socket: ServerSocket? = null

    val isRunning: Boolean get() = socket?.isClosed == false

    /** The bound port (tests bind port 0 to get a free one). */
    val localPort: Int get() = socket?.localPort ?: -1

    /** Binds the port. Returns false if it is taken. The accept loop runs on the caller's coroutine via [serve]. */
    fun bind(): Boolean {
        if (isRunning) return true
        return try {
            socket = ServerSocket(port, 4, InetAddress.getByName("0.0.0.0"))
            true
        } catch (e: IOException) {
            Log.w(TAG, "Could not listen on port $port: ${e.message}")
            false
        }
    }

    /** Accepts connections until [close]. Blocking: run it on Dispatchers.IO. */
    suspend fun serve() {
        val server = socket ?: return
        while (!server.isClosed) {
            try {
                server.accept().use { handle(it) }
            } catch (e: IOException) {
                if (!server.isClosed) Log.d(TAG, "Connection error: ${e.message}")
            }
        }
    }

    fun close() {
        try {
            socket?.close()
        } catch (_: IOException) {
        }
        socket = null
    }

    internal suspend fun handle(client: Socket) {
        val out = client.getOutputStream()
        if (!isPrivatePeer(client.inetAddress)) {
            return // closed without an answer: this listener is for your own PC only
        }
        client.soTimeout = READ_TIMEOUT_MS
        val deadline = System.nanoTime() + REQUEST_DEADLINE_NANOS
        val input = client.getInputStream()

        val head = readHead(input, deadline) ?: return respond(out, 431, "Request too large or too slow")
        val lines = head.split("\r\n")
        val requestLine = lines[0].split(" ")
        if (requestLine.size < 2) return respond(out, 400, "Bad request")

        val expected = token()?.trim().orEmpty()
        if (expected.isEmpty() || !isAuthorized(lines, expected)) {
            if (expected.isNotEmpty() && header(lines, "authorization") != null) {
                onRejected()
            }
            return respond(out, 401, "Unauthorized")
        }
        if (requestLine[0] != "POST") return respond(out, 405, "Method not allowed")
        if (requestLine[1].substringBefore('?') != "/sync") return respond(out, 404, "Not found")

        val length = header(lines, "content-length")?.toIntOrNull() ?: return respond(out, 411, "Length required")
        if (length < 0 || length > MAX_BODY_BYTES) return respond(out, 413, "Too large")
        val body = readBody(input, length, deadline) ?: return respond(out, 400, "Body incomplete")

        val data = parseState(body) ?: return respond(out, 400, "Body must be a JSON object")
        val needIcons = onSnapshot(StateResponse(data = data, updatedAt = now()))
        client.localAddress?.hostAddress?.let(onDeliveredTo)
        respond(out, 200, "{\"ok\":true,\"needIcons\":" + needIcons.joinToString(",", "[", "]") + "}", "application/json")
    }

    private fun parseState(body: String): StateDataDto? = try {
        val element = JsonParser.parseString(body)
        if (element.isJsonObject) gson.fromJson(element, StateDataDto::class.java) else null
    } catch (e: Exception) {
        null
    }

    private fun isAuthorized(lines: List<String>, expected: String): Boolean {
        val value = header(lines, "authorization") ?: return false
        if (!value.regionMatches(0, "Bearer ", 0, 7, ignoreCase = true)) return false
        val presented = value.substring(7).trim().lowercase(Locale.ROOT).toByteArray(Charsets.UTF_8)
        return MessageDigest.isEqual(expected.lowercase(Locale.ROOT).toByteArray(Charsets.UTF_8), presented)
    }

    private fun header(lines: List<String>, name: String): String? =
        lines.drop(1).firstNotNullOfOrNull { line ->
            val colon = line.indexOf(':')
            if (colon > 0 && line.substring(0, colon).trim().lowercase(Locale.ROOT) == name) line.substring(colon + 1).trim() else null
        }

    private fun readHead(input: InputStream, deadline: Long): String? {
        val buf = ByteArrayOutputStream()
        var matched = 0
        while (buf.size() < MAX_HEADER_BYTES && System.nanoTime() < deadline) {
            val b = try {
                input.read()
            } catch (_: SocketTimeoutException) {
                continue
            }
            if (b < 0) return null
            buf.write(b)
            matched = when {
                (matched == 0 || matched == 2) && b == '\r'.code -> matched + 1
                (matched == 1 || matched == 3) && b == '\n'.code -> matched + 1
                b == '\r'.code -> 1
                else -> 0
            }
            if (matched == 4) return buf.toString(Charsets.ISO_8859_1.name()).trimEnd()
        }
        return null
    }

    private fun readBody(input: InputStream, length: Int, deadline: Long): String? {
        val bytes = ByteArray(length)
        var read = 0
        while (read < length && System.nanoTime() < deadline) {
            val n = try {
                input.read(bytes, read, length - read)
            } catch (_: SocketTimeoutException) {
                continue
            }
            if (n < 0) return null
            read += n
        }
        return if (read == length) String(bytes, Charsets.UTF_8) else null
    }

    private fun respond(out: OutputStream, status: Int, body: String, type: String = "text/plain") {
        val bytes = body.toByteArray(Charsets.UTF_8)
        val head = "HTTP/1.1 $status ${reason(status)}\r\nContent-Type: $type; charset=utf-8\r\n" +
            "Content-Length: ${bytes.size}\r\nCache-Control: no-store\r\nConnection: close\r\n\r\n"
        try {
            out.write(head.toByteArray(Charsets.ISO_8859_1))
            out.write(bytes)
            out.flush()
        } catch (_: IOException) {
        }
    }

    private fun reason(status: Int) = when (status) {
        200 -> "OK"
        400 -> "Bad Request"
        401 -> "Unauthorized"
        404 -> "Not Found"
        405 -> "Method Not Allowed"
        411 -> "Length Required"
        413 -> "Payload Too Large"
        431 -> "Request Header Fields Too Large"
        else -> "Error"
    }

    companion object {
        const val DEFAULT_PORT = 8789
        // Room for a full snapshot plus a hundred icons; the body is only read after the token has been checked
        const val MAX_BODY_BYTES = 1024 * 1024
        private const val MAX_HEADER_BYTES = 8 * 1024
        private const val READ_TIMEOUT_MS = 1000
        private val REQUEST_DEADLINE_NANOS = TimeUnit.SECONDS.toNanos(5)
        private const val TAG = "PhoneReceiver"

        /** Home networks and Tailscale: loopback, link-local, RFC 1918, unique-local, and 100.64.0.0/10. */
        fun isPrivatePeer(peer: InetAddress?): Boolean {
            if (peer == null) return false
            if (peer.isLoopbackAddress || peer.isLinkLocalAddress || peer.isSiteLocalAddress) return true
            val b = peer.address
            return when (b.size) {
                4 -> (b[0].toInt() and 0xFF) == 100 && (b[1].toInt() and 0xC0) == 0x40
                16 -> (b[0].toInt() and 0xFE) == 0xFC
                else -> false
            }
        }
    }
}
