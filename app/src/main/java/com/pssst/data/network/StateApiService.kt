package com.pssst.data.network

import com.pssst.data.local.UserPreferencesRepository
import com.pssst.data.network.model.StateResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

interface StateApiService {
    suspend fun getState(
        @Header("Authorization") authHeader: String
    ): Response<StateResponse>

    companion object {
        // Retrofit insists on a base URL, but every request uses an absolute @Url taken from the user's own setting.
        // Nothing is ever sent here, and there is deliberately no default server.
        private const val UNUSED_BASE_URL = "https://localhost/"

        fun create(
            preferencesRepository: UserPreferencesRepository? = null,
            okHttpClient: OkHttpClient? = null
        ): StateApiService {
            val client = okHttpClient ?: OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(HttpLoggingInterceptor().apply {
                    // BODY dumped the full bank + bearer token to logcat in every build
                    level = HttpLoggingInterceptor.Level.BASIC
                    redactHeader("Authorization")
                })
                .build()

            val retrofitApi = Retrofit.Builder()
                .baseUrl(UNUSED_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(StateRetrofitApi::class.java)

            return DynamicStateApiService(preferencesRepository, retrofitApi)
        }
    }
}

internal interface StateRetrofitApi {
    @GET
    suspend fun getState(
        @Url url: String,
        @Header("Authorization") authHeader: String
    ): Response<StateResponse>
}

internal class DynamicStateApiService(
    private val preferencesRepository: UserPreferencesRepository?,
    private val retrofitApi: StateRetrofitApi
) : StateApiService {

    override suspend fun getState(authHeader: String): Response<StateResponse> {
        val rawBase = preferencesRepository?.getServerUrl()?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("No server URL is configured")
        val isRobolectric = runCatching { android.os.Build.FINGERPRINT == "robolectric" }.getOrDefault(false)
        val fullUrl = buildStateUrl(rawBase, rewriteLoopback = !isRobolectric)
        return retrofitApi.getState(fullUrl, authHeader)
    }

    companion object {
        private val LOOPBACK_HOSTS = setOf("0.0.0.0", "localhost", "127.0.0.1")

        fun normalizeBaseUrl(baseUrl: String): String {
            val trimmed = baseUrl.trim()
            val withScheme = if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
                "http://$trimmed"
            } else {
                trimmed
            }
            return try {
                val uri = java.net.URI(withScheme)
                val host = uri.host?.lowercase()
                if (host != null && host in LOOPBACK_HOSTS) {
                    val rewritten = java.net.URI(
                        uri.scheme,
                        uri.userInfo,
                        "10.0.2.2",
                        uri.port,
                        uri.path,
                        uri.query,
                        uri.fragment
                    ).toString()
                    runCatching {
                        android.util.Log.w("StateApi", "Rewrote $withScheme -> $rewritten (emulator loopback)")
                    }
                    rewritten
                } else {
                    withScheme
                }
            } catch (_: Exception) {
                withScheme
            }
        }

        fun isLoopbackUrl(url: String): Boolean {
            val trimmed = url.trim()
            if (trimmed.isEmpty()) return false
            val withScheme = if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
                "http://$trimmed"
            } else {
                trimmed
            }
            return try {
                val host = java.net.URI(withScheme).host?.lowercase()
                host != null && host in LOOPBACK_HOSTS
            } catch (_: Exception) {
                false
            }
        }

        fun buildStateUrl(baseUrl: String, rewriteLoopback: Boolean = true): String {
            val normalized = if (rewriteLoopback) {
                normalizeBaseUrl(baseUrl)
            } else {
                val trimmed = baseUrl.trim()
                if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
                    "http://$trimmed"
                } else {
                    trimmed
                }
            }
            return if (normalized.endsWith("/")) "${normalized}state" else "$normalized/state"
        }
    }
}
