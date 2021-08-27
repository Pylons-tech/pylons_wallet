package tech.pylons.droidwallet

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tech.pylons.ipc.DroidIpcWire
import tech.pylons.lib.Wallet
import java.lang.ref.WeakReference

class WalletHandler {

    interface WalletCallback {
        fun onWalletConnected()
    }

    companion object {

        private const val TAG = "PylonsSDK"

        private lateinit var appId: String
        private lateinit var appName: String

        private var wallet: Wallet? = null

        private var walletConnection: WeakReference<IpcServiceConnection>? = null

        private var walletCallback: WalletCallback? = null


        private fun getWalletConnection(): IpcServiceConnection {
            return walletConnection!!.get()!!
        }

        private fun ifWalletExists(context: Context): Boolean = try {
            val pm: PackageManager = context.packageManager
            pm.getPackageInfo("tech.pylons.wallet", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

        /**
         * Getter for the wallet instance
         */
        fun getWallet(): Wallet {
            return wallet!!
        }

        /**
         * Set up connection to the Wallet
         *
         * - set appId & appName
         * - initialize AndroidWallet instance
         * - bind IPC service connection with the Wallet-UI
         */
        fun setup(context: Context, appId: String, appName: String, callback: WalletCallback?) {
            this.appName = appName
            this.appId = appId
            this.walletCallback = callback

            if (wallet == null) {
                wallet = Wallet.android()
            }

            walletConnection = WeakReference(IpcServiceConnection(context))
            if (ifWalletExists(context)) {
                walletConnection!!.get()!!.bind() // do bind here
            } else {
                Toast.makeText(context, "Wallet not installed.", Toast.LENGTH_LONG).show()
            }
        }

    }

    /**
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
            fun initWallet() {
                runBlocking {
                    launch {
                        establishConnection(appName, appId) {
                            if (it) { // only if handshake is succeeded
                                println("Wallet Initiated")
                                walletCallback?.onWalletConnected()
                            }
                        }
                    }
                }
            }
        }

        override fun readString(): String? {
            return getWalletConnection().getFromWallet()
        }

        override fun writeString(s: String) {
            getWalletConnection().submitToWallet(s)
        }

    }
}
