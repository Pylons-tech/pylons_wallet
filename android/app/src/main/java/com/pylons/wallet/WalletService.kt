package com.pylons.wallet

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.util.Log
import walletCore.MessageData
import walletCore.Status

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
        val msg = messageDataFromIntent(intent)
        val result = walletCore.resolveMessage(msg)
        when (result!!.status) {
            Status.OK_TO_RETURN_TO_CLIENT -> passDataToShim(result.msg!!, intent)
            Status.REQUIRE_UI_ELEVATION -> requiresUI(intent)
            Status.INCOMING_MESSAGE_MALFORMED -> passDataToShim(msg, intent)
        }
    }

    private fun passDataToShim (msg : MessageData, intent : Intent) {
        val outgoingIntent = Intent(ACTION_RETURN_TO_CLIENT)
        copyMessageDataToIntent(msg, outgoingIntent)
        val innerIntent = Intent(ACTION_PASS_DATA_TO_SHIM)
        innerIntent.putExtra("_@PINNER", outgoingIntent)
        val pending = intent.getParcelableExtra<PendingIntent>("_@PPENDING");
        pending.send(applicationContext, 0, innerIntent)
    }

    private fun requiresUI (intent: Intent) {
        Log.i("info", "Asking client to transfer UI control...")
        Log.i("info", "Returning data to client (no UI transfer needed)")
        val successIntent = Intent(ACTION_REQUIRE_WALLET_UI)
        val pending = intent.getParcelableExtra<PendingIntent>("_@PPENDING");
        val innerIntent = Intent(ACTION_PASS_DATA_TO_SHIM)
        innerIntent.putExtra("_@PINNER", successIntent)
        pending.send(applicationContext, 0, innerIntent)
    }

    private fun copyMessageDataToIntent (msg : MessageData, intent : Intent) {
        /**
         * TODO: once the shim is redesigned to use the same strategy we use to do this w/o
         * declared extras between wallet/core this needs to stop making the putDeclaredExtra calls
         */
        for (k in msg.booleans.keys) intent.putDeclaredExtra(k, msg.booleans[k]!!)
        for (k in msg.booleanArrays.keys) intent.putDeclaredExtra(k, msg.booleanArrays[k]!!)
        for (k in msg.bytes.keys) intent.putDeclaredExtra(k, msg.bytes[k]!!)
        for (k in msg.byteArrays.keys) intent.putDeclaredExtra(k, msg.byteArrays[k]!!)
        for (k in msg.chars.keys) intent.putDeclaredExtra(k, msg.chars[k]!!)
        for (k in msg.charArrays.keys) intent.putDeclaredExtra(k, msg.charArrays[k]!!)
        for (k in msg.doubles.keys) intent.putDeclaredExtra(k, msg.doubles[k]!!)
        for (k in msg.doubleArrays.keys) intent.putDeclaredExtra(k, msg.doubleArrays[k]!!)
        for (k in msg.floats.keys) intent.putDeclaredExtra(k, msg.floats[k]!!)
        for (k in msg.floatArrays.keys) intent.putDeclaredExtra(k, msg.floatArrays[k]!!)
        for (k in msg.ints.keys) intent.putDeclaredExtra(k, msg.ints[k]!!)
        for (k in msg.intArrays.keys) intent.putDeclaredExtra(k, msg.intArrays[k]!!)
        for (k in msg.longs.keys) intent.putDeclaredExtra(k, msg.longs[k]!!)
        for (k in msg.longArrays.keys) intent.putDeclaredExtra(k, msg.longArrays[k]!!)
        for (k in msg.shorts.keys) intent.putDeclaredExtra(k, msg.doubles[k]!!)
        for (k in msg.shortArrays.keys) intent.putDeclaredExtra(k, msg.shortArrays[k]!!)
        for (k in msg.strings.keys) intent.putDeclaredExtra(k, msg.strings[k]!!)
        for (k in msg.stringArrays.keys) intent.putDeclaredExtra(k, msg.stringArrays[k]!!.toTypedArray())
        intent.commitDeclaredExtras()
    }

    companion object
}
