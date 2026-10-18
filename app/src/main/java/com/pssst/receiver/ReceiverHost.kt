package com.pssst.receiver

import android.content.Context
import android.util.Log
import com.pssst.data.SnapshotIngest
import com.pssst.data.local.AppDatabase
import com.pssst.data.local.DataStoreUserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Owns the one [PhoneReceiver] in the process and keeps it listening while anyone needs it: the app in the foreground,
 * or a short background window. Reference counted, so the window ending while the app is open doesn't stop it.
 * Accepts snapshots only once a Sync Token is saved.
 */
object ReceiverHost {
    private const val TAG = "ReceiverHost"

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var appContext: Context? = null
    private var receiver: PhoneReceiver? = null
    private var serveJob: Job? = null
    private var holders = 0

    /** Replaceable in tests. */
    @Volatile var factory: ((Context) -> PhoneReceiver)? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    @Synchronized
    fun acquire() {
        holders++
        if (holders == 1) start()
    }

    @Synchronized
    fun release() {
        if (holders == 0) return
        holders--
        if (holders == 0) stop()
    }

    @Synchronized
    fun isListening(): Boolean = receiver?.isRunning == true

    private fun start() {
        val context = appContext ?: return
        val made = factory?.invoke(context) ?: defaultReceiver(context)
        if (!made.bind()) return
        receiver = made
        serveJob = scope.launch { made.serve() }
        Log.i(TAG, "Listening for RuneLite on port ${PhoneReceiver.DEFAULT_PORT}")
    }

    private fun stop() {
        receiver?.close()
        receiver = null
        serveJob?.cancel()
        serveJob = null
    }

    private fun defaultReceiver(context: Context): PhoneReceiver {
        val prefs = DataStoreUserPreferencesRepository(context)
        val ingest = SnapshotIngest(context, AppDatabase.getDatabase(context), prefs)
        return PhoneReceiver(
            token = { prefs.getUuid() },
            onSnapshot = { snapshot ->
                val uuid = prefs.getUuid()
                if (uuid.isNullOrBlank()) emptyList() else {
                    ingest.ingest(uuid, snapshot)
                    ingest.missingIcons(snapshot)
                }
            },
            onDeliveredTo = { address -> ReceiverPrefs.saveLastDeliveredTo(context, address) }
        )
    }
}
