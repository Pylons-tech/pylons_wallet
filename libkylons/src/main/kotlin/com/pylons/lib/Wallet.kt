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
import java.time.Instant
import kotlin.reflect.KClass

/**
 * Generic high-level interface between JVM clients and a Pylons wallet.
 */
/**
 * tierre!!!: Here sets the callback parameter as String not the return object type
 * currently object type casting is the problem
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
    fun fetchProfile (address : String?, callback: (String?) -> Unit) {
        sendMessage(Profile::class, Message.GetProfile(address)) {callback(it as String?)}
    }

    /**
     * Get a list of all items owned by current profile.
     */
    fun listItems(callback: (String?) -> Unit) {
        sendMessage(Profile::class, Message.GetProfile()) {callback((it as String?))}
    }

    /**
     * Register a new profile.
     */
    fun registerProfile (callback: (String?) -> Unit) {
        sendMessage(Profile::class, Message.RegisterProfile()) {callback(it as String?)}
    }

    fun placeForSale (item : Item, price : Long, callback: (String?) -> Unit) {
        sendMessage(Transaction::class, Message.CreateTrade(listOf(
            klaxon.toJsonString(Coin("pylon", price))),
            listOf(), listOf(), listOf(item.id))) {callback(it as String?)}
    }

    fun getTrades(callback: (String?) -> Unit) {
        sendMessage(List::class, Message.GetTrades()) {callback(it as String?)}
    }

    fun buyItem (trade : Trade, callback: (String?) -> Unit) {
        sendMessage(Transaction::class, Message.FulfillTrade(trade.id)) {callback(it as String?)}
    }

    fun createCookbook(ids : List<String>,
                       names : List<String>,
                       developers : List<String>,
                       descriptions : List<String>,
                       versions : List<String>,
                       supportEmails : List<String>,
                       levels : List<Long>,
                       costsPerBlock : List<Long>,
                       callback: (String?)->Unit) {
        sendMessage(Transaction::class, Message.CreateCookbooks(
            ids = ids,
            names = names,
            developers = developers,
            descriptions = descriptions,
            versions = versions,
            supportEmails = supportEmails,
            levels = levels,
            costsPerBlock = costsPerBlock
        )){callback(it as String?)}
    }

    fun createAutoCookbook(profile: Profile, callback: (String?) -> Unit) {
        sendMessage(
            Transaction::class, Message.CreateCookbooks(
                listOf("autocookbook_${profile.address}_${Instant.now().toEpochMilli()}"),
                listOf("autocookbook_${profile.address}_${Instant.now().toEpochMilli()}"),
                listOf("autocookbook_${profile.address}"),
                listOf("autocookbook for use by managed appliations"),
                listOf("1.0.0"),
                listOf("support@pylons.tech"),
                listOf(1),
                listOf(0)
            )
        ) { callback(it as String?) } // i don't exactly know what the correct way to handle level/costs is atm
    }

    fun listCookbooks(callback: (String?)->Unit) {
        sendMessage(Cookbook::class, Message.GetCookbooks()){
            callback(it as String?)
        }
    }

    fun createRecipe(name : String, cookbook : String, description : String,
                     blockInterval : Long, coinInputs : List<CoinInput>,
                     itemInputs: List<ItemInput>, outputTable : EntriesList,
                     outputs : List<WeightedOutput>, callback: (String?) -> Unit) {
        sendMessage(Transaction::class, Message.CreateRecipes(listOf(name), listOf(cookbook), listOf(description),
            listOf(blockInterval), listOf(klaxon.toJsonString(coinInputs)), listOf(klaxon.toJsonString(itemInputs)),
            listOf(klaxon.toJsonString(outputTable)), listOf(klaxon.toJsonString(outputs)))) {callback(it as String?)}
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