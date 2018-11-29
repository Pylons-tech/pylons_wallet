package com.pylons.wallet

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.util.Log

const val CHANNEL_ID = "PYLONS_WALLET"

class WalletService : IntentService("WalletService") {

    override fun onHandleIntent(intent: Intent?) {
        Log.i("onHandleIntent", intent!!.action)
        when (intent?.action) {
            ACTION_INVOKE_WALLET_SERVICE -> resolvePylonsAction(intent)
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("foo", "bar")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(NotificationChannel(CHANNEL_ID, "pylons_wallet",NotificationManager.IMPORTANCE_DEFAULT))
        }
        startForeground(9, NotificationCompat.Builder(this, CHANNEL_ID).setSmallIcon(R.drawable.ic_wallet_service).setAutoCancel(true).build())
        Log.i("foo", "bar-bar binks")
        return super.onStartCommand(intent, flags, startId)
    }

    fun resolvePylonsAction(intent: Intent) {
        var pylonsAction = intent.getStringExtra("pylonsAction")
        when (pylonsAction) {
            "WALLET_SERVICE_TEST" -> {
                Log.i("info", "Returning data to client (no UI transfer needed)")
                val successIntent = Intent(ACTION_RETURN_TO_CLIENT)
                successIntent.putDeclaredExtra("info", "it works i guess")
                successIntent.commitDeclaredExtras()
                val innerIntent = Intent(ACTION_PASS_DATA_TO_SHIM)
                innerIntent.putExtra("_@PINNER", successIntent)
                val pending = intent.getParcelableExtra<PendingIntent>("_@PPENDING");
                pending.send(applicationContext, 0, innerIntent)
            }
            "WALLET_UI_TEST" -> requiresUI(intent)
        }
    }

    fun requiresUI (intent: Intent) {
        Log.i("info", "Asking client to transfer UI control...")
        Log.i("info", "Returning data to client (no UI transfer needed)")
        val successIntent = Intent(ACTION_REQUIRE_WALLET_UI)
        val pending = intent.getParcelableExtra<PendingIntent>("_@PPENDING");
        val innerIntent = Intent(ACTION_PASS_DATA_TO_SHIM)
        innerIntent.putExtra("_@PINNER", successIntent)
        pending.send(applicationContext, 0, innerIntent)
    }

    companion object
}
