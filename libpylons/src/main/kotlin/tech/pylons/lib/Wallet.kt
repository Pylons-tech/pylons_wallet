package tech.pylons.lib

import tech.pylons.ipc.DroidIpcWire
import tech.pylons.ipc.HttpIpcWire
import tech.pylons.ipc.Message
import tech.pylons.ipc.Response
import tech.pylons.lib.types.*
import tech.pylons.lib.types.tx.Coin
import tech.pylons.lib.types.tx.item.Item
import tech.pylons.lib.types.tx.Trade
import tech.pylons.lib.types.tx.recipe.*
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
     * fetchProfile (address : String?, callback: (Profile?) -> Unit)
     * retrieves wallet core profile for given address
     * if address is null, return current wallet core profile
     * if no wallet core exists, return null
     *
     * @param   address: String? - wallet core address to be fetched
     * @param   callback: (Profile?)
     *
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
     * listItems(callback: (List<Item>) -> Unit)
     * retrieves item list of current core
     *
     * @return List<Item> - list of Items
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
     * registerProfile (callback: (Profile?) -> Unit)
     * register new Core account.
     * return Profile of current registered Core if success, else return null
     *
     * @return Profile?
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
     * placeForSale (item : Item, price : Long, callback: (Transaction?) -> Unit)
     * Create Trade
     * Sell Item Only
     *
     * @param item:Item     item info to be in Trade
     * @param price:Long    wish price in pylons
     * @return Transaction?
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
     * getTrades(callback: (List<Trade>) -> Unit)
     * retrieve all Trade List
     *
     * @return List<Trade>
     */
    fun getTrades(callback: (List<Trade>) -> Unit) {
        sendMessage(List::class, Message.GetTrades()) {
            val response = it as Response
            val trades = response.tradesOut
            callback(trades)
        }
    }

    /**
     * Creates and resolves a Transaction to fulfill trades where the only required input is a cost in Pylons
     * without further user input. Calls callback with the transaction afterward. Transaction may fail,
     * and callback should handle that case.
     *
     * Underlying transaction message type is FulfillTrade.
     *
     * @param trade The ID of the trade to be fulfilled.
     * @param callback Callback to fire after completing handling of the transaction. Should accept a single parameter
     *  of type Transaction? and return Unit or void.
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
     * Creates and resolves a Transaction to create a cookbook with an auto-generated name and default parameters,
     * as follows:
     *
     * ```
     *  Auto-cookbook identifier convention: ${appName}_autocookbook_${profile.address}
     *  Cookbook ID, name, developer: auto-cookbook identifier
     *  Cookbook description: "$appName autocookbook for use by managed applications"
     *  Cookbook version: 1.0.0
     *  Cookbook support email: support@pylons.tech
     *  Cookbook level: 1
     *  Cost per block: 1
     *```
     *
     * Underlying transaction message type is CreateCookbook.
     *
     * @param profile Profile of current wallet user.
     * @param appName Name of application autocookbook is to be generated for.
     * @param callback Callback to fire after completing handling of the transaction. Should accept a single parameter
     *  of type Transaction? and return Unit or void.
     */
    fun createAutoCookbook(profile: Profile, appName:String, callback: (Transaction?) -> Unit) {
        sendMessage(
            Transaction::class, Message.CreateCookbooks(
                //listOf("${appName}_autocookbook_${profile.address}_${Instant.now().toEpochMilli()}"),
                //listOf("${appName}_autocookbook_${profile.address}_${Instant.now().toEpochMilli()}"),
                listOf("${appName}_autocookbook_${profile.address}"),
                listOf("${appName}_autocookbook_${profile.address}"),
                listOf("${appName}_autocookbook_${profile.address}"),
                listOf("$appName autocookbook for use by managed applications"),
                listOf("1.0.0"),
                listOf("support@pylons.tech"),
                listOf(1),
                //where this bug comes from? if no CostsForBlocks then, it return error transaction
                listOf(1)
            )
        ) {
            val response =  it as Response
            var tx:Transaction? = null
            if(response.txs.isNotEmpty()){
                tx = response.txs.get(0)

            }
            callback(tx)
        } // i don't exactly know what the correct way to handle level/costs is atm
    }

    /**
     * Creates and resolves a number of Transactions to create one or more cookbooks with manually-supplied parameters.
     * This is a batch operation to facilitate writing automated or semi-automated recipe management tools.
     * If only a single cookbook is being created, use lists of length 1 to supply parameters. All parameters must
     * be of the same length, or an exception will be thrown.
     *
     * Underlying transaction message type is CreateCookbook. This function creates multiple transactions.
     *
     * @param ids List of IDs to be used for the cookbooks being created.
     * @param names List of names to be used for the cookbooks being created.
     * @param developers List of developer names to be used for the cookbooks being created. Under most circumstances,
     *  all developer-name fields in a single operation should be identical.
     * @param descriptions List of human-readable descriptions to be used for the cookbooks being created.
     * @param versions List of versions to be used for the cookbooks being created.
     * @param supportEmails List of support email addressed to be used for the cookbooks being created. Under most
     *  circumstances, all support-email fields in a single operation should be identical.
     * @param levels List of levels to be used for the cookbooks being created.( Level is a deprecated field
     *  that no longer exists on the Pylons node, and will be removed from libpylons and all other projects in
     *  the pylons-wallet monorepo in a future refactor.)
     * @param costsPerBlock List of cost-per-block values to be used for the cookbooks being created.
     * @param callback Callback to fire after completing handling of all transactions. Should accept a single parameter
     *  of type List<Transaction> and return Unit or void.
     */
    fun createCookbooks(ids : List<String>,
                       names : List<String>,
                       developers : List<String>,
                       descriptions : List<String>,
                       versions : List<String>,
                       supportEmails : List<String>,
                       levels : List<Long>,
                       costsPerBlock : List<Long>,
                       callback: (List<Transaction>)->Unit) {

        // Verify params before doing anything to make sure we don't go ahead, do a few transactions, and then crash

        if (names.size != ids.size || developers.size != ids.size || descriptions.size != ids.size ||
            versions.size != ids.size || supportEmails.size != ids.size || levels.size != ids.size ||
            costsPerBlock.size != ids.size)
                throw IllegalArgumentException("All parameters of batch operation createCookbooks must be same size")

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
            callback(response.txs)
        }
    }

    /**
     * Creates and resolves a Transaction to create a recipe with the given parameters.
     *
     * Underlying transaction message type is CreateRecipe.
     *
     * TODO: CreateRecipes is actually a batch message. Either we're wrapping the batch operations here
     * (so this should be createRecipes) or we aren't (so we should have createCookbook.) There's no use case
     * for us supporting some but not all batch messages.
     *
     * @param name The name of the recipe to be created.
     * @param cookbook The ID of the cookbook to create a recipe in.
     * @param description The human-readable description to be used for the recipe.
     * @param blockInterval The number of blocks to wait after executing the recipe before completing that execution.
     * @param coinInputs Recipe's token inputs, as a list of CoinInput objects.
     * @param itemInputs Recipe's item input definitions, as a list of ItemInput objects.
     * @param outputTable An EntriesList. TODO: summarize this/outputs below better
     * @param outputs A list of WeightedOutput objects.
     * @param callback Callback to fire after completing handling of the transaction. Should accept a single parameter
     *  of type Transaction? and return Unit or void.
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
     * listCookbooks(callback: (List<Cookbook>)->Unit)
     * retrieve cookbooks for current account
     *
     * @return List<Cookbook>
     */
    fun listCookbooks(callback: (List<Cookbook>)->Unit) {
        sendMessage(Cookbook::class, Message.GetCookbooks()){
            val response = it as Response
            callback(response.cookbooksOut)
        }
    }

    /**
     * listRecipes
     * retrieve all recipe list
     *
     *  @return List<Recipe>
     */
    fun listRecipes(callback: (List<Recipe>)->Unit) {
        sendMessage(Recipe::class, Message.GetRecipes()) {

            val response = it as Response
            println("listRecipes ${response.recipesOut.count()}")

            callback(response.recipesOut)
        }
    }

    /**
     * executeRecipe
     *
     * @param recipe - recipe Name
     * @param cookbook - cookbook Id
     * @param itemInputs - list of item inputs names for recipe execution
     * @return return transaction of the recipe execution when success, else return null.
     *
     */
    fun executeRecipe(recipe: String, cookbook: String, itemInputs: List<String>, callback: (Transaction?)->Unit) {
        sendMessage(Transaction::class, Message.ExecuteRecipe(recipe, cookbook, itemInputs)){
            val response = it as Response
            var tx: Transaction? = null
            if(response.txs.isNotEmpty()) {
                tx = response.txs.get(0)
            }
            callback(tx)
        }
    }

    /**
     * enableRecipe
     *
     * @param recipeId recipe id to be enabled
     * @return Transaction?
     */
    fun enableRecipe(recipeId:String, callback: (Transaction?)->Unit) {
        sendMessage(Transaction::class, Message.EnableRecipes(listOf(recipeId))){
            val response = it as Response
            var tx: Transaction? = null
            if(response.txs.isNotEmpty()) {
                tx = response.txs.get(0)
            }
            callback(tx)
        }
    }

    /**
     * disableRecipe
     *
     * @param recipeId recipe id to be enabled
     * @return Transaction?
     */
    fun disableRecipe(recipeId:String, callback: (Transaction?)->Unit) {
        sendMessage(Transaction::class, Message.DisableRecipes(listOf(recipeId))){
            val response = it as Response
            var tx: Transaction? = null
            if(response.txs.isNotEmpty()) {
                tx = response.txs.get(0)
            }
            callback(tx)
        }
    }

    /**
     * BuyPylons
     * under construction
     *
     * @return Transaction?
     */
    fun buyPylons(callback: (Transaction?)->Unit) {
        sendMessage(Transaction::class, Message.BuyPylons()) {
            val response = it as Response
            var tx: Transaction? = null
            if(response.txs.isNotEmpty()) {
                tx = response.txs.get(0)
            }
            callback(tx)
        }
    }

    /**
     * getWebLinkForAndroid
     *
     * @return String?
     */
    fun getWebLinkForAndroid(recipeName:String, recipeId:String):String {
        return "http://tech.pylons/wallet?action=purchase_nft&recipe_id=$recipeId&nft_amount=1"
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