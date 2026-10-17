package com.pssst.worker

import android.content.Context
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pssst.data.local.AppDatabase
import com.pssst.data.local.DataStoreUserPreferencesRepository
import com.pssst.data.local.UserPreferencesRepository
import com.pssst.data.network.WikiPrices
import com.pssst.data.network.WikiPricesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Refreshes item prices from the OSRS Wiki. Runs only if the user opted in, and does nothing if prices were
 * refreshed in the last 10 minutes, so neither the schedule nor the Refresh button can hammer the Wiki.
 */
class PriceRefreshWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    var database: AppDatabase = defaultDatabase ?: AppDatabase.getDatabase(appContext)
    var preferencesRepository: UserPreferencesRepository =
        defaultPreferences ?: DataStoreUserPreferencesRepository(appContext)
    var client: WikiPricesClient = defaultClient ?: createClient(appContext)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        if (preferencesRepository.getPriceRefreshEnabled() != true) {
            return@withContext Result.success()
        }

        val dao = database.itemManifestDao()
        val now = System.currentTimeMillis()
        if (now - dao.latestPriceTimestamp() < MIN_INTERVAL_MS) {
            Log.d(TAG, "Prices refreshed recently; skipping")
            return@withContext Result.success()
        }

        when (val result = client.fetchLatest()) {
            is WikiPricesClient.Result.Prices -> {
                if (result.prices.isEmpty()) {
                    Log.w(TAG, "Wiki returned no prices; will retry")
                    Result.retry()
                } else {
                    dao.applyPrices(result.prices, now)
                    Log.i(TAG, "Refreshed ${result.prices.size} prices")
                    Result.success()
                }
            }
            is WikiPricesClient.Result.HttpError -> {
                Log.w(TAG, "Wiki price request failed: HTTP ${result.code}")
                // 429 and 5xx are worth another try later (WorkManager backs off); other 4xx will not fix themselves
                if (result.code == 429 || result.code in 500..599) Result.retry() else Result.failure()
            }
            is WikiPricesClient.Result.NetworkError -> {
                Log.w(TAG, "Wiki price request failed: ${result.cause.message}")
                Result.retry()
            }
        }
    }

    companion object {
        const val TAG = "PriceRefreshWorker"
        private val MIN_INTERVAL_MS = TimeUnit.MINUTES.toMillis(10)

        @VisibleForTesting
        var defaultDatabase: AppDatabase? = null

        @VisibleForTesting
        var defaultPreferences: UserPreferencesRepository? = null

        @VisibleForTesting
        var defaultClient: WikiPricesClient? = null

        private fun createClient(context: Context): WikiPricesClient {
            val versionName = runCatching {
                context.packageManager.getPackageInfo(context.packageName, 0).versionName
            }.getOrNull() ?: "dev"
            // The disk cache is what turns an unchanged /latest into a conditional request (the Wiki sends an ETag)
            val http = OkHttpClient.Builder()
                .cache(Cache(File(context.cacheDir, "wiki_prices"), 5L * 1024 * 1024))
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
            return WikiPricesClient(http, WikiPrices.userAgent(versionName))
        }
    }
}
