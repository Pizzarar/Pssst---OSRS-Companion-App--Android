package com.pssst

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.pssst.data.local.AppDatabase
import com.pssst.data.local.AssetDatabaseSeeder
import com.pssst.data.local.DataStoreUserPreferencesRepository
import com.pssst.receiver.ReceiverHost
import com.pssst.theme.PssstTheme
import com.pssst.worker.SyncScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    SyncScheduler.cancelLegacyPriceWork(applicationContext)
    lifecycleScope.launch(Dispatchers.IO) {
      val database = AppDatabase.getDatabase(applicationContext)
      AssetDatabaseSeeder.seedIfNeeded(applicationContext, database)
    }

    enableEdgeToEdge()
    setContent {
      PssstTheme { Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) { MainNavigation() } }
    }
  }

  override fun onStart() {
    super.onStart()
    // While the app is on screen, RuneLite can send to it directly (phone mode)
    ReceiverHost.acquire()
  }

  override fun onStop() {
    ReceiverHost.release()
    super.onStop()
  }

  override fun onResume() {
    super.onResume()
    SyncScheduler.syncNow(this)
    // Reminders come from the saved snapshot, so they are in place even if this sync cannot reach the PC
    lifecycleScope.launch(Dispatchers.IO) {
      val enabled = DataStoreUserPreferencesRepository(applicationContext).getNotificationsEnabled()
      SyncScheduler.replanFromCache(applicationContext, enabled)
    }
  }
}

