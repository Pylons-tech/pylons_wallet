package tech.pylons.lib.core

import tech.pylons.lib.types.*
import tech.pylons.lib.types.credentials.ICredentials
import tech.pylons.lib.types.tx.Coin
import tech.pylons.lib.types.tx.Trade
import tech.pylons.lib.types.tx.item.Item
import tech.pylons.lib.types.tx.recipe.*
import tech.pylons.lib.types.tx.trade.ItemRef
import tech.pylons.lib.types.tx.trade.TradeItemInput

/***
 * Generic interface for transaction-handling layers.
 * Engine provides a suite of basic functionality that WalletCore as a whole
 * can use to handle transactions at a high level; individual TXHandler implementations
 * will do the dirty work of binding that functionality to low-level blockchain
 * systems, in effect acting as "drivers."
 */
interface IEngine {
    /**
     * Identifier string, unique per Engine implementation.
     * Used to identify the engine type associated with a given dataset
     * when we dump the datastore to XML.
     */
    val prefix : String

    /** Identifies whether or not we're using BIP44 mnemonics when doing keygen. */
    val usesMnemonic : Boolean

    /** The current CryptoHandler instance associated with this engine */
    var cryptoHandler : ICryptoHandler

    /** Enable-recipe message */
    fun enableRecipe(id : String) : Transaction

    /** Batch enable-recipe message */
    fun enableRecipes(recipes : List<String>) : List<Transaction>

    /** Disable-recipe message */
    fun disableRecipe(id : String) : Transaction

    /** Batch enable-recipe message */
    fun disableRecipes(recipes : List<String>) : List<Transaction>

    /** Execute-recipe message */
    fun applyRecipe(creator: String , cookbookID: String, id: String, coinInputsIndex: Long, itemIds : List<String>) : Transaction

    /** Check-execution message */
    fun checkExecution(id : String, payForCompletion : Boolean) : Transaction

    /** Create-trade message */
    fun createTrade(creator: String, coinInputs: List<CoinInput>, itemInputs: List<ItemInput>,
                    coinOutputs : List<Coin>, itemOutputs : List<ItemRef>,
                    ExtraInfo : String) : Transaction

    /** Create-recipe message */
    fun createRecipe(creator : String, cookbookId : String, id : String, name : String, description: String, version: String,
                     coinInputs : List<CoinInput>, itemInputs : List<ItemInput>, entries : EntriesList,
                     outputs : List<WeightedOutput>, blockInterval : Long, enabled : Boolean, extraInfo: String) : Transaction

    /** Batch create-recipe message */
    fun createRecipes(creators : List<String>, cookbookIds : List<String>, ids : List<String>, names : List<String>, descriptions: List<String>, versions: List<String>,
                      coinInputs : List<List<CoinInput>>,
                      itemInputs : List<List<ItemInput>>, entries : List<EntriesList>,
                      outputs: List<List<WeightedOutput>>, blockIntervals : List<Long>, enableds : List<Boolean>, extraInfos: List<String>) : List<Transaction>

    /** Create-cookbook message */
    fun createCookbook (creator: String, id : String, name : String, description : String, developer : String, version : String,
                                 supportEmail : String, costPerBlock : Coin, enabled: Boolean) : Transaction

    /** Batch create-cookbook message */
    fun createCookbooks(creators: List<String>, ids : List<String>, names : List<String>, descriptions: List<String>, developers: List<String>,
                        versions : List<String>, supportEmails: List<String>,
                        costsPerBlocks : List<Coin>, enableds: List<Boolean>) : List<Transaction>

    /**
     * Copies some data from profile's credentials object to userdata
     * for serialization.
     *  TODO: why does this actually exist?
     */
    fun dumpCredentials (credentials: ICredentials)

    fun fulfillTrade (creator: String, ID : String, CoinInputsIndex: Long, itemIds : List<ItemRef>) : Transaction

    fun cancelTrade (tradeId : String) : Transaction
    /**
     * Generates a new Credentials object appropriate for our engine
     * type from the given mnemonic.
     */
    fun generateCredentialsFromMnemonic (mnemonic : String, passphrase : String) : ICredentials

    /**
     * Generates a new Credentials object appropriate for our engine
     * type from keys in userdata.
     */
    fun generateCredentialsFromKeys () : ICredentials

    /**
     * Creates new, default Credentials object appropriate for engine
     * type.
     */
    fun getNewCredentials () : ICredentials

    fun getProfileState (addr : String) : Profile?

    /** Get the balances of the user account. */
    fun getMyProfileState () : MyProfile?

    fun getCompletedExecutions() : List<Execution>

    fun getPendingExecutions () : List<Execution>

    /** Get a new instance of a CryptoHandler object appropriate for engine type. */
    fun getNewCryptoHandler() : ICryptoHandler

    /** Get the current status block. (Status block is returned w/ all IPC calls) */
    fun getStatusBlock() : StatusBlock

    /**
     * Retrieves transaction w/ the given ID.
     * (In an engine built to implement Cosmos functionality, this is the txhash)
     */
    fun getTransaction (id : String) : Transaction

    /** Registers a new profile under given name. */
    fun registerNewProfile (name : String, kp : PylonsSECP256K1.KeyPair?) : Transaction

    fun createChainAccount (name : String) : Transaction

    /** Calls Google IAP get pylons endpoint. */
    fun googleIapGetPylons(productId: String, purchaseToken: String, receiptData: String, signature: String): Transaction

    fun checkGoogleIapOrder(purchaseToken: String) : Boolean

    /** Gets initial userdata tables for the engine type. */
    fun getInitialDataSets () : MutableMap<String, MutableMap<String, String>>


    /** Update-cookbook message */
    fun updateCookbook (creator: String, id: String, name: String, description: String, developer: String, version: String, supportEmail: String, costPerBlock: Coin, enabled: Boolean) : Transaction

    /** Batch update-cookbook message */
    fun updateCookbooks(creators : List<String>, ids : List<String>, names : List<String>, descriptions: List<String>, developers: List<String>,
                        versions : List<String>, supportEmails: List<String>, costPerBlocks: List<Coin>, enableds: List<Boolean>) : List<Transaction>

    /** Update-recipe message */
    fun updateRecipe(Creator: String, CookbookID : String, ID : String, Name : String, Description: String,
                     Version: String, CoinInputs : List<CoinInput>, ItemInputs : List<ItemInput>,
                     Entries : EntriesList, Outputs: List<WeightedOutput>, BlockInterval : Long, Enabled: Boolean, ExtraInfo: String) : Transaction

    /** Batch update-recipe message */
    fun updateRecipes (creators: List<String>, cookbookIds: List<String>, ids: List<String>, names : List<String>, descriptions: List<String>,
                       versions: List<String>, coinInputs : List<List<CoinInput>>, itemInputs : List<List<ItemInput>>,
                       entries : List<EntriesList>, outputs: List<List<WeightedOutput>>,blockIntervals : List<Long>,  enableds: List<Boolean>, extraInfos: List<String>): List<Transaction>

    /** List recipes query */
    fun listRecipes () : List<Recipe>

    /** List cookbooks query */
    fun listCookbooks () : List<Cookbook>

    fun getPylons(amount : Long, creator: String) : Boolean

    fun setItemFieldString (itemId : String, field : String, value : String) : Transaction

    fun listTrades (creator: String) : List<Trade>

    fun sendItems(receiver: String, itemIds: List<String>) : Transaction

    fun listRecipesBySender() : List<Recipe>

    fun getRecipe(recipeId: String) : Recipe?

    fun listRecipesByCookbookId(cookbookId: String) : List<Recipe>

    fun getTrade(tradeId: String) : Trade?

    fun getItem(itemId: String): Item?

    fun listItems() : List<Item>

    fun listItemsBySender(sender: String?) : List<Item>

    fun listItemsByCookbookId(cookbookId: String?): List<Item>

    fun getCookbook(cookbookId: String): Cookbook?

    fun getExecution(executionId: String): Execution?

}