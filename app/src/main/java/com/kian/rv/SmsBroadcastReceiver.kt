package com.kian.rv

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import androidx.appcompat.app.AppCompatActivity


class SmsBroadcastReceiver :
    BroadcastReceiver() {
    private var listener: SMSMessageListener? = null
    override fun onReceive(context: Context?, intent: Intent) {

        try {
            var sp = context!!.getSharedPreferences("init", AppCompatActivity.MODE_PRIVATE)
            var serviceProviderNumber = sp.getString("number","").toString()


            if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
                var smsSender = ""
                var smsBody = ""
                for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    smsSender = smsMessage.displayOriginatingAddress
                    smsBody += smsMessage.messageBody
                }
                smsSender = smsSender.replace("+98","0")
                if (smsSender == serviceProviderNumber ) {
                    if (listener != null) {
                        listener!!.onTextReceived(smsBody)
                    }
                }
            }
        }catch (e:Exception){}


    }

    fun setListener(listener: SMSMessageListener?) {
        this.listener = listener
    }

    interface SMSMessageListener {
        fun onTextReceived(text: String?)
    }

    companion object {
        private const val TAG = "SmsBroadcastReceiver"
    }
}














