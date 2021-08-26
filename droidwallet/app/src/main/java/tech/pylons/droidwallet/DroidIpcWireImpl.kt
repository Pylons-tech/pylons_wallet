package tech.pylons.droidwallet

import android.content.Context
import android.util.Log
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tech.pylons.ipc.DroidIpcWire

/**
 * @DoNotModify
 *
 * DroidIpcWire implementation (a bridge between libpylons and client app), referenced by the Pylons SDK.
 */
class DroidIpcWireImpl : DroidIpcWire() {

    companion object {
        private const val TAG = "DroidIpcWireImpl"

        init {
            implementation = DroidIpcWireImpl()
            Log.i(TAG, "DroidIpcWireImpl has just been instantiated.")
        }

        /**
         * initWallet
         * call when IpcService initiated. IpcService::onServiceConnected()
         * all ipc actions will be come after initWallet() succeed
         */
        fun initWallet(context: Context?) {
            runBlocking {
                launch {
                    establishConnection(WalletHandler.appName, WalletHandler.appPkgName) {
                        if (it) { // only if handshake is succeeded
                            println("Wallet Initiated")
                            WalletHandler.initUserInfo(context)
                        }
                    }
                }
            }
        }
    }

    override fun readString(): String? {
        return WalletHandler.getWalletConnection().getFromWallet()
    }

    override fun writeString(s: String) {
        WalletHandler.getWalletConnection().submitToWallet(s)
    }

}