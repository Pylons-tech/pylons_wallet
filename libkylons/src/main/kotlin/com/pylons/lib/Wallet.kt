package com.pylons.lib

import com.pylons.ipc.Message
import com.pylons.lib.types.*
import com.pylons.lib.types.tx.item.Item

/**
 * Generic high-level interface between JVM clients and a Pylons wallet.
 * TODO: port over the actual tx type so we can resolve messages that
 * return a tx (expect this to be messy)
 */
abstract class Wallet {
    /**
     * Signature for the method what we call to pass messages into the
     * wallet. IPC happens after this implementation.
     */
    protected abstract fun <T> sendMessage(message: Message, callback : (T) -> Unit)

    /**
     * True if an IPC target exists; false otherwise.
     */
    abstract fun exists (callback : (Boolean) -> Unit)

    /**
     * Get current profile, or null if none exists.
     */
    fun fetchProfile (callback: (Profile?) -> Unit) {
        sendMessage<Profile?>(Message.GetProfile()) {callback(it)}
    }

    /**
     * Get a list of all items owned by current profile.
     */
    fun listItems(callback: (List<Item>) -> Unit) {
        sendMessage<Profile?>(Message.GetProfile()) {callback(it?.items.orEmpty())}
    }

    /**
     * Register a new profile.
     */
    fun registerProfile (callback: (Profile?) -> Unit) {
        sendMessage<Profile?>(Message.RegisterProfile()) {callback(it)}
    }

    class Android : Wallet() {
        override fun <T> sendMessage(message: Message, callback: (T) -> Unit) {
            TODO("Not yet implemented")
        }

        override fun exists(callback: (Boolean) -> Unit) {
            TODO("Not yet implemented")
        }
    }

    // todo: idk when we'll have android wallet online so
    // once i like this library i should throw together this
    // binding and do a quick and nasty client for it as a sanity check
    class DevDevWallet : Wallet() {
        override fun <T> sendMessage(message: Message, callback: (T) -> Unit) {
            TODO("Not yet implemented")
        }

        override fun exists(callback: (Boolean) -> Unit) {
            TODO("Not yet implemented")
        }
    }
}