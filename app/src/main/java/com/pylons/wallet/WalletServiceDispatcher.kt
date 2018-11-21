package com.pylons.wallet

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log

class WalletServiceDispatcher : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("dispatcher get", "received a broadcast")
        var componentName = ComponentName(context!!.packageName, javaClass.name)
        context.startService(intent!!.setComponent(componentName))
    }
}

