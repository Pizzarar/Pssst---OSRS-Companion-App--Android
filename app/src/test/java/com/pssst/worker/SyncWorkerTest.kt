package com.pssst.worker

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.pssst.data.TestPreferences
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SyncWorkerTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @After
    fun tearDown() {
        SyncWorker.defaultPreferences = null
        SyncWorker.listenWindowMs = 75_000L
    }

    @Test
    fun notPairedMeansNothingToDo() = runBlocking {
        SyncWorker.defaultPreferences = TestPreferences(uuid = null)
        val result = TestListenableWorkerBuilder<SyncWorker>(context).build().doWork()
        assertEquals(ListenableWorker.Result.failure(), result)
    }

    @Test
    fun aRunIsAListeningWindowThatChangesNothingTheUserSees() = runBlocking {
        val prefs = TestPreferences(uuid = "0123456789abcdef0123456789abcdef")
        SyncWorker.defaultPreferences = prefs
        SyncWorker.listenWindowMs = 0

        val result = TestListenableWorkerBuilder<SyncWorker>(context).build().doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        assertEquals(null, prefs.lastStatus)
    }
}
