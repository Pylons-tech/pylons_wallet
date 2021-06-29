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
     * Generate a web link for the given recipe.
     *
     * returns web link string.
     */
    abstract fun generateWebLink (recipeName:String, recipeId:String):String

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
     * buyItem (trade : Trade, callback: (Transaction?) -> Unit)
     * FulfillTrade
     *
     * @param trade
     * @return Transaction?
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
     *
     * Cookbook Id Naming Convention: ${main_cookbook_code}_${appName}_${identifier}_${identifier}
     * Cookbook Id should contains Cookbook Creator App's Name

     * @param ids
     * @param names
     * @param developers
     * @param descriptions
     * @param versions
     * @param supportEmails
     * @param levels
     * @param costsPerBlock
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
                       callback: (List<Transaction>)->Unit) {
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
     * CreateAutoCookbook
     * Cookbook is unique per app/ per pylons account
     * Cookbook Id Naming Convention: ${main_cookbook_code}_${appName}_${identifier}_${identifier}
     * Cookbook Id should contains Cookbook Creator App's Name
     *
     * @return Transaction?
     */
    fun createAutoCookbook(profile: Profile, appName:String, callback: (Transaction?) -> Unit) {
        sendMessage(
            Transaction::class, Message.CreateCookbooks(
                //listOf("${appName}_autocookbook_${profile.address}_${Instant.now().toEpochMilli()}"),
                //listOf("${appName}_autocookbook_${profile.address}_${Instant.now().toEpochMilli()}"),
                listOf("${appName}_autocookbook_${profile.address}"),
                listOf("${appName}_autocookbook_${profile.address}"),
                listOf("${appName}_autocookbook_${profile.address}"),
                listOf("${appName} autocookbook for use by managed appliations"),
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
     * createRecipe
     *
     * @return Transaction?
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
     * listRecipesBySender
     * retrieve all recipe list
     *
     *  @return List<Recipe>
     */
    fun listRecipesBySender(callback: (List<Recipe>)->Unit) {
        sendMessage(Recipe::class, Message.GetRecipesBySender()) {

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
     * @return Transaction?f
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

        override fun generateWebLink(recipeName: String, recipeId: String): String {
            return "http://wallet.pylons.tech/?action=purchase_nft&recipe_id=$recipeId&nft_amount=1"
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

        override fun generateWebLink(recipeName: String, recipeId: String): String {
            return ""
        }
    }
}