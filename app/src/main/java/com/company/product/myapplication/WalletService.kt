package com.company.product.myapplication

import android.app.IntentService
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Intent
import android.content.Context
import java.lang.reflect.Type


// TODO: Rename parameters
private const val EXTRA_PARAM1 = "com.company.product.myapplication.extra.PARAM1"
private const val EXTRA_PARAM2 = "com.company.product.myapplication.extra.PARAM2"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class WalletService : IntentService("WalletService") {

    class Dispatcher : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            var componentName = ComponentName(context!!.packageName, WalletService.javaClass.name)
            context.startService(intent!!.setComponent(componentName))
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_INVOKE_WALLET_SERVICE -> resolvePylonsAction(intent)
        }
    }

    fun resolvePylonsAction(intent: Intent) {
        var pylonsAction = intent.getStringExtra("pylonsAction")
        when (pylonsAction) {
            "WALLET_SERVICE_TEST" -> {
                val successIntent = Intent(ACTION_RETURN_TO_CLIENT)
                successIntent.putExtra("info", "it works i guess")
                sendBroadcast(successIntent)
            }
            "WALLET_UI_TEST" -> requiresUI()
        }
    }

    fun requiresUI () {
        val needUIIntent = Intent(ACTION_REQUIRE_WALLET_UI)
        sendBroadcast(needUIIntent)
    }

    companion object
}
