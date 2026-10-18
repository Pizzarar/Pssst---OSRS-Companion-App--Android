package com.pssst

import android.app.Application
import com.pssst.worker.NetworkSyncTrigger

class PssstApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NetworkSyncTrigger.register(this)
    }
}
