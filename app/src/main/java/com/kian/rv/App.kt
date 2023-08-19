package com.kian.rv

import android.app.Application
import android.content.IntentFilter
import android.provider.Telephony


class App : Application() {
    private var smsBroadcastReceiver: SmsBroadcastReceiver? = null
    override fun onCreate() {
        super.onCreate()
        smsBroadcastReceiver = SmsBroadcastReceiver()
        registerReceiver(
            smsBroadcastReceiver,
            IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
        )
    }

    override fun onTerminate() {
        unregisterReceiver(smsBroadcastReceiver)
        super.onTerminate()
    }


    public fun get_receiver():SmsBroadcastReceiver?{
        return smsBroadcastReceiver
    }


}







