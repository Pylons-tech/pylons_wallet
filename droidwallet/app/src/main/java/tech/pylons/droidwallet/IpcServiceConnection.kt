package tech.pylons.droidwallet

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import tech.pylons.ipc.IIpcInterface

/**
 * @DoNotModify
 *
 * <IpcServiceConnection>
 * interface for monitoring Wallet's IPCService
 *
 * - iIpcService: IPC interface instance
 *
 * - bind() / unbind()
 *
 * - getFromWallet() / submitToWallet()
 */
class IpcServiceConnection(ctx: Context) : ServiceConnection {

    private var isServiceBinded: Boolean = false

    private var context: Context? = ctx
    private var iIpcService: IIpcInterface? = null

    /**
     * Returns a message sent from the Wallet-UI
     */
    fun getFromWallet(): String? {
        return if (isServiceBinded) {
            val msg = iIpcService!!.wallet2client()
            println("getFromWallet $msg")
            msg
        } else {
            null
        }
    }

    /**
     * Do send a message to the Wallet-UI
     */
    fun submitToWallet(json: String) {
        if (isServiceBinded)
            iIpcService!!.client2wallet(json)
    }

    override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
        isServiceBinded = true
        iIpcService = IIpcInterface.Stub.asInterface(service)
        DroidIpcWireImpl.initWallet(context) // handshake will be initiated immediately after ipc channel is established ...
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        Log.e(TAG, "Service has unexpectedly disconnected")
        iIpcService = null
        isServiceBinded = false
    }

    fun bind() {
        Log.d(TAG, "Bind")
        val serviceIntent = Intent("tech.pylons.wallet.ipc.BIND")
        serviceIntent.setPackage("tech.pylons.wallet")
        context!!.bindService(serviceIntent, this, Context.BIND_AUTO_CREATE)
    }

    fun unbind() {
        if (iIpcService != null) {
            Log.d(TAG, "Unbind")
            context!!.unbindService(this)
            iIpcService = null
        }
    }

    companion object {
        private const val TAG = "IpcServiceConnection"
    }
}