package tech.pylons.lib.core

import tech.pylons.lib.types.*
import tech.pylons.lib.types.credentials.ICredentials
import tech.pylons.lib.types.tx.Coin
import tech.pylons.lib.types.tx.Trade
import tech.pylons.lib.types.tx.item.Item
import tech.pylons.lib.types.tx.recipe.*
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
    fun applyRecipe(id : String, itemIds : List<String>) : Transaction

    /** Check-execution message */
    fun checkExecution(id : String, payForCompletion : Boolean) : Transaction

    /** Create-trade message */
    fun createTrade(coinInputs: List<CoinInput>, itemInputs: List<TradeItemInput>,
                    coinOutputs : List<Coin>, itemOutputs : List<Item>,
                    ExtraInfo : String) : Transaction

    /** Create-recipe message */
    fun createRecipe(name : String, cookbookId : String, description: String, blockInterval : Long,
                     coinInputs : List<CoinInput>, itemInputs : List<ItemInput>, entries : EntriesList,
                     outputs : List<WeightedOutput>) : Transaction

    /** Batch create-recipe message */
    fun createRecipes(names : List<String>, cookbookIds : List<String>, descriptions: List<String>,
                      blockIntervals : List<Long>, coinInputs : List<List<CoinInput>>,
                      itemInputs : List<List<ItemInput>>, entries : List<EntriesList>,
                      outputs: List<List<WeightedOutput>>) : List<Transaction>

    /** Create-cookbook message */
    fun createCookbook (id : String, name : String, developer : String, description : String, version : String,
                                 supportEmail : String, costPerBlock : Long) : Transaction

    /** Batch create-cookbook message */
    fun createCookbooks(ids : List<String>, names : List<String>, developers: List<String>, descriptions: List<String>,
                        versions : List<String>, supportEmails: List<String>,
                        costsPerBlock : List<Long>) : List<Transaction>

    /**
     * Copies some data from profile's credentials object to userdata
     * for serialization.
     *  TODO: why does this actually exist?
     */
    fun dumpCredentials (credentials: ICredentials)

    fun fulfillTrade (tradeId : String, itemIds : List<String>) : Transaction

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

    fun createChainAccount () : Transaction

    /** Calls non-IAP get pylons endpoint. Shouldn't work against production nodes. */
    fun getPylons (q : Long) : Transaction

    /** Calls Google IAP get pylons endpoint. */
    fun googleIapGetPylons(productId: String, purchaseToken: String, receiptData: String, signature: String): Transaction

    fun checkGoogleIapOrder(purchaseToken: String) : Boolean

    /** Gets initial userdata tables for the engine type. */
    fun getInitialDataSets () : MutableMap<String, MutableMap<String, String>>

    /** Calls send pylons endpoint. */
    fun sendCoins (coins : List<Coin>, receiver : String) : Transaction

    /** Update-cookbook message */
    fun updateCookbook (id : String, developer : String, description : String, version : String,
                                 supportEmail : String) : Transaction

    /** Batch update-cookbook message */
    fun updateCookbooks(ids : List<String>, names : List<String>, developers: List<String>, descriptions: List<String>,
                        versions : List<String>, supportEmails: List<String>) : List<Transaction>

    /** Update-recipe message */
    fun updateRecipe(id : String, name : String, cookbookId : String, description: String, blockInterval : Long,
                              coinInputs : List<CoinInput>, itemInputs : List<ItemInput>, entries : EntriesList, outputs: List<WeightedOutput>) : Transaction

    /** Batch update-recipe message */
    fun updateRecipes (ids: List<String>, names : List<String>, cookbookIds : List<String>, descriptions: List<String>,
                       blockIntervals : List<Long>, coinInputs : List<List<CoinInput>>, itemInputs : List<List<ItemInput>>,
                       entries : List<EntriesList>, outputs: List<List<WeightedOutput>>) : List<Transaction>

    /** List recipes query */
    fun listRecipes () : List<Recipe>

    /** List cookbooks query */
    fun listCookbooks () : List<Cookbook>

    fun setItemFieldString (itemId : String, field : String, value : String) : Transaction

    fun listTrades () : List<Trade>

    fun sendItems(receiver: String, itemIds: List<String>) : Transaction

    fun getLockedCoins () : LockedCoin

    fun getLockedCoinDetails () : LockedCoinDetails

    fun listRecipesBySender() : List<Recipe>

    fun getRecipe(recipeId: String) : Recipe?

    fun listRecipesByCookbookId(cookbookId: String) : List<Recipe>

    fun getTrade(tradeId: String) : Trade?

}