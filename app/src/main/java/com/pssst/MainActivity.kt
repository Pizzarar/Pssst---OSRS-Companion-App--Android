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
import com.pssst.theme.PssstTheme
import com.pssst.worker.SyncScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    lifecycleScope.launch(Dispatchers.IO) {
      val database = AppDatabase.getDatabase(applicationContext)
      AssetDatabaseSeeder.seedIfEmpty(applicationContext, database)
    }

    enableEdgeToEdge()
    setContent {
      PssstTheme { Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) { MainNavigation() } }
    }
  }

  override fun onResume() {
    super.onResume()
    SyncScheduler.syncNow(this)
  }
}

