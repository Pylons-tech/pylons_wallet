package com.pylons.wallet

import android.app.IntentService
import android.content.Intent

class WalletService : IntentService("WalletService") {
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
