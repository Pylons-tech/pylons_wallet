package com.pylons.lib

import com.pylons.ipc.HttpIpcWire
import com.pylons.ipc.Message
import com.pylons.lib.types.*
import com.pylons.lib.types.tx.Coin
import com.pylons.lib.types.tx.item.Item
import com.pylons.lib.types.tx.recipe.CoinInput
import com.pylons.lib.types.tx.recipe.EntriesList
import com.pylons.lib.types.tx.recipe.ItemInput
import com.pylons.lib.types.tx.recipe.WeightedOutput
import com.pylons.lib.types.tx.Trade
import kotlin.reflect.KClass

/**
 * Generic high-level interface between JVM clients and a Pylons wallet.
 */
abstract class Wallet {
    /**
     * Signature for the method what we call to pass messages into the
     * wallet. IPC happens after this implementation.
     */
    protected abstract fun sendMessage(outType : KClass<*>, message: Message, callback : (Any?) -> Unit)

    /**
     * True if an IPC target exists; false otherwise.
     */
    abstract fun exists (callback : (Boolean) -> Unit)

    /**
     * Get current profile, or null if none exists.
     */
    fun fetchProfile (callback: (Profile?) -> Unit) {
        sendMessage(Profile::class, Message.GetProfile()) {callback(it as Profile?)}
    }

    /**
     * Get a list of all items owned by current profile.
     */
    fun listItems(callback: (List<Item>) -> Unit) {
        sendMessage(Profile::class, Message.GetProfile()) {callback((it as Profile?)?.items.orEmpty())}
    }

    /**
     * Register a new profile.
     */
    fun registerProfile (callback: (Profile?) -> Unit) {
        sendMessage(Profile::class, Message.RegisterProfile()) {callback(it as Profile?)}
    }

    fun placeForSale (item : Item, price : Long, callback: (Transaction?) -> Unit) {
        sendMessage(Transaction::class, Message.CreateTrade(listOf(
            klaxon.toJsonString(Coin("pylon", price))),
            listOf(), listOf(), listOf(item.id))) {callback(it as Transaction?)}
    }

    fun getTrades(callback: (List<Trade>?) -> Unit) {
        sendMessage(List::class, Message.GetTrades()) {callback(it as List<Trade>?)}
    }

    fun buyItem (trade : Trade, callback: (Transaction?) -> Unit) {
        sendMessage(Transaction::class, Message.FulfillTrade(trade.id)) {callback(it as Transaction?)}
    }

    fun createRecipe(name : String, cookbook : String, description : String,
                     blockInterval : Long, coinInputs : List<CoinInput>,
                     itemInputs: List<ItemInput>, outputTable : EntriesList,
                     outputs : List<WeightedOutput>, callback: (Transaction?) -> Unit) {
        sendMessage(Transaction::class, Message.CreateRecipes(listOf(name), listOf(cookbook), listOf(description),
        listOf(blockInterval), listOf(klaxon.toJsonString(coinInputs)), listOf(klaxon.toJsonString(itemInputs)),
        listOf(klaxon.toJsonString(outputTable)), listOf(klaxon.toJsonString(outputs)))) {callback(it as Transaction?)}
    }

    fun android() : AndroidWallet = AndroidWallet.instance

    fun devDevWallet() : DevDevWallet = DevDevWallet.instance

    class AndroidWallet : Wallet(){
        companion object {
            val instance : AndroidWallet = AndroidWallet()
        }

        override fun sendMessage(outType : KClass<*>, message: Message, callback: (Any?) -> Unit) {
            TODO("Not yet implemented")
        }

        override fun exists(callback: (Boolean) -> Unit) {
            TODO("Not yet implemented")
        }
    }

    class DevDevWallet : Wallet() {
        companion object {
            val instance : DevDevWallet = DevDevWallet()
        }

        override fun sendMessage(outType : KClass<*>, message: Message, callback: (Any?) -> Unit) {
            /// HttpIpcWire is dead simple; it just writes a string.
            HttpIpcWire.writeString(klaxon.toJsonString(message))
            // B/c HttpIpcWire is extremely simple, calling readMessage means we
            // just wait forever until we get something. That's fine - you just
            // want to use a worker thread to interact w/ your Wallet instances.
            callback(klaxon.parser(outType).parse(HttpIpcWire.readMessage().orEmpty()))
        }

        override fun exists(callback: (Boolean) -> Unit) {
            callback(true) // todo: devdevwallet doesn't handle connection breaking yet so we can't tell if it's connected.
        }
    }
}