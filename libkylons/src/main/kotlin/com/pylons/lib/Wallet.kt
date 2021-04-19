package com.pylons.lib

import com.pylons.ipc.DroidIpcWire
import com.pylons.ipc.HttpIpcWire
import com.pylons.ipc.Message
import com.pylons.ipc.Response
import com.pylons.lib.types.*
import com.pylons.lib.types.tx.Coin
import com.pylons.lib.types.tx.item.Item
import com.pylons.lib.types.tx.Trade
import com.pylons.lib.types.tx.recipe.*
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
    companion object {
        fun android() : AndroidWallet = AndroidWallet.instance

        fun devDevWallet() : DevDevWallet = DevDevWallet.instance
    }
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
     * @return: Profile
     */
    fun fetchProfile (address : String?, callback: (Profile?) -> Unit) {
        sendMessage(Profile::class, Message.GetProfile(address)) {
            val response = it as Response
            var profile:Profile? = null

            if(response.profilesOut.isNotEmpty()){
                profile = response.profilesOut.get(0)
            }
            callback(profile)
        }
    }

    /**
     * Get a list of all items owned by current profile.
     * @return: List<Item>
     */
    fun listItems(callback: (List<Item>) -> Unit) {
        sendMessage(Profile::class, Message.GetProfile()) {
            val response = it as Response
            var mItems = mutableListOf<Item>()
            if (response.profilesOut.isNotEmpty()) {
                response.profilesOut.get(0).items.forEach{
                    mItems.add(it)
                }
            }
            callback((mItems.toList()))
        }
    }

    /**
     * Register a new profile.
     * @return: Profile?
     */
    fun registerProfile (callback: (Profile?) -> Unit) {
        sendMessage(Profile::class, Message.RegisterProfile()) {
            val response = it as Response
            var profile:Profile? = null
            if(response.profilesOut.isNotEmpty()) {
                profile = response.profilesOut.get(0)
            }
            callback(profile)
        }
    }

    /**
     * CreateTrade
     * @return: Transaction?
     */
    fun placeForSale (item : Item, price : Long, callback: (Transaction?) -> Unit) {
        sendMessage(Transaction::class, Message.CreateTrade(listOf(
            klaxon.toJsonString(Coin("pylon", price))),
            listOf(), listOf(), listOf(item.id))) {
            val response = it as Response
            var tx:Transaction? = null
            if (response.txs.isNotEmpty()) {
                tx = response.txs.get(0)
            }

            callback(tx)
        }
    }

    /**
     * GetTrades
     * @return: List<Trade>
     */
    fun getTrades(callback: (List<Trade>) -> Unit) {
        sendMessage(List::class, Message.GetTrades()) {
            val response = it as Response
            val trades = response.tradesOut
            callback(trades)
        }
    }

    /**
     * FulfillTrade
     * @return: Transaction?
     */
    fun buyItem (trade : Trade, callback: (Transaction?) -> Unit) {
        sendMessage(Transaction::class, Message.FulfillTrade(trade.id)) {
            val response = it as Response
            var tx:Transaction? = null
            if (response.txs.isNotEmpty()) {
                tx = response.txs.get(0)
            }

            callback(tx)
        }
    }

    /**
     * CreateCookbooks
     * @return List<Cookbook>
     */
    fun createCookbooks(ids : List<String>,
                       names : List<String>,
                       developers : List<String>,
                       descriptions : List<String>,
                       versions : List<String>,
                       supportEmails : List<String>,
                       levels : List<Long>,
                       costsPerBlock : List<Long>,
                       callback: (List<Cookbook>)->Unit) {
        sendMessage(Transaction::class, Message.CreateCookbooks(
            ids = ids,
            names = names,
            developers = developers,
            descriptions = descriptions,
            versions = versions,
            supportEmails = supportEmails,
            levels = levels,
            costsPerBlock = costsPerBlock
        )){
            val response = it as Response
            callback(response.cookbooksOut)
        }
    }

    /**
     * CreateAutoCookbook
     * @return: Cookbook?
     */
    fun createAutoCookbook(profile: Profile, callback: (Cookbook?) -> Unit) {
        sendMessage(
            Transaction::class, Message.CreateCookbooks(
                listOf("autocookbook_${profile.address}_${Instant.now().toEpochMilli()}"),
                listOf("autocookbook_${profile.address}_${Instant.now().toEpochMilli()}"),
                listOf("autocookbook_${profile.address}"),
                listOf("autocookbook for use by managed appliations"),
                listOf("1.0.0"),
                listOf("support@pylons.tech"),
                listOf(1),
                //where this bug comes from? if no CostsForBlocks then, it return error transaction
                listOf(1)
            )
        ) {
            val response =  it as Response
            var cookbook:Cookbook? = null
            if (response.cookbooksOut.isNotEmpty()) {
                cookbook = response.cookbooksOut.get(0)
            }
            callback(cookbook)
        } // i don't exactly know what the correct way to handle level/costs is atm
    }

    /**
     * listCookbooks
     * @return: List<Cookbook>
     */
    fun listCookbooks(callback: (List<Cookbook>)->Unit) {
        sendMessage(Cookbook::class, Message.GetCookbooks()){
            val response = it as Response
            callback(response.cookbooksOut)
        }
    }

    /**
     * createRecipe
     * @return: Transaction?
     */
    fun createRecipe(name : String, cookbook : String, description : String,
                     blockInterval : Long, coinInputs : List<CoinInput>,
                     itemInputs: List<ItemInput>, outputTable : EntriesList,
                     outputs : List<WeightedOutput>, callback: (Transaction?) -> Unit) {
        sendMessage(Transaction::class, Message.CreateRecipes(listOf(name), listOf(cookbook), listOf(description),
            listOf(blockInterval), listOf(klaxon.toJsonString(coinInputs)), listOf(klaxon.toJsonString(itemInputs)),
            listOf(klaxon.toJsonString(outputTable)), listOf(klaxon.toJsonString(outputs)))) {
            val response = it as Response
            var tx:Transaction? = null
            if (response.txs.isNotEmpty()) {
                tx = response.txs.get(0)
            }
            callback(tx)
        }
    }

    /**
     * listRecipes
     *  @return: List<Recipe>
     */
    fun listRecipes(callback: (List<Recipe>)->Unit) {
        sendMessage(Recipe::class, Message.GetRecipes()) {
            val response = it as Response

            callback(response.recipesOut)
        }
    }


    fun android() : AndroidWallet = AndroidWallet.instance

    fun devDevWallet() : DevDevWallet = DevDevWallet.instance

    class AndroidWallet : Wallet(){
        companion object {
            val instance : AndroidWallet by lazy {AndroidWallet()}
        }

        override fun sendMessage(outType : KClass<*>, message: Message, callback: (Any?) -> Unit) {
            DroidIpcWire.writeMessage(DroidIpcWire.makeRequestMessage(message))

            val msg = DroidIpcWire.readMessage()
            if (msg != null){
                val messageType = message::class.java.simpleName
                val response = Response.deserialize(messageType, msg)
                callback(response)
            }
        }

        override fun exists(callback: (Boolean) -> Unit) {
            //what's this?
            callback(true)
        }
    }

    class DevDevWallet : Wallet() {
        companion object {
            val instance : DevDevWallet by lazy {DevDevWallet()}
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