package com.pssst

import android.app.Application
import com.pssst.receiver.ReceiverHost
import com.pssst.worker.NetworkSyncTrigger

class PssstApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ReceiverHost.init(this)
        NetworkSyncTrigger.register(this)
    }
}
