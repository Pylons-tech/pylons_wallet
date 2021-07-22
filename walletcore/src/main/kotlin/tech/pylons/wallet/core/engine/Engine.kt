package tech.pylons.wallet.core.engine

import tech.pylons.lib.core.ICryptoHandler
import tech.pylons.lib.core.IEngine
import tech.pylons.lib.types.*
import tech.pylons.lib.types.credentials.ICredentials
import tech.pylons.lib.types.tx.Coin
import tech.pylons.lib.types.tx.Trade
import tech.pylons.lib.types.tx.item.Item
import tech.pylons.lib.types.tx.recipe.*
import tech.pylons.lib.types.tx.trade.TradeItemInput
import tech.pylons.wallet.core.Core

/***
 * Generic interface for transaction-handling layers.
 * Engine provides a suite of basic functionality that WalletCore as a whole
 * can use to handle transactions at a high level; individual TXHandler implementations
 * will do the dirty work of binding that functionality to low-level blockchain
 * systems, in effect acting as "drivers."
 */
abstract class Engine(val core : Core) : IEngine {
    /**
     * Identifier string, unique per Engine implementation.
     * Used to identify the engine type associated with a given dataset
     * when we dump the datastore to XML.
     */
    abstract override val prefix : String

    /** Identifies whether or not we're using BIP44 mnemonics when doing keygen. */
    abstract override val usesMnemonic : Boolean

    /** The current CryptoHandler instance associated with this engine */
    abstract override var cryptoHandler : ICryptoHandler

    /** Enable-recipe message */
    abstract override fun enableRecipe(id : String) : Transaction

    /** Batch enable-recipe message */
    override fun enableRecipes(recipes : List<String>) : List<Transaction> {
        val txs = mutableListOf<Transaction>()
        recipes.forEach { txs.add(enableRecipe(it)) }
        return txs
    }

    /** Disable-recipe message */
    abstract override fun disableRecipe(id : String) : Transaction

    /** Batch enable-recipe message */
    override fun disableRecipes(recipes : List<String>) : List<Transaction> {
        val txs = mutableListOf<Transaction>()
        recipes.forEach { txs.add(disableRecipe(it)) }
        return txs
    }

    /** Execute-recipe message */
    abstract override fun applyRecipe(id : String, itemIds : List<String>, paymentId: String) : Transaction

    /** Check-execution message */
    abstract override fun checkExecution(id : String, payForCompletion : Boolean) : Transaction

    /** Create-trade message */
    abstract override fun createTrade(coinInputs: List<CoinInput>, itemInputs: List<TradeItemInput>,
                                      coinOutputs : List<Coin>, itemOutputs : List<Item>,
                                      ExtraInfo : String) : Transaction

    /** Create-recipe message */
    abstract override fun createRecipe(name : String, cookbookId : String, description: String, blockInterval : Long,
                                       coinInputs : List<CoinInput>, itemInputs : List<ItemInput>, entries : EntriesList,
                                       outputs : List<WeightedOutput>, extraInfo: String) : Transaction

    /** Batch create-recipe message */
    override fun createRecipes(names : List<String>, cookbookIds : List<String>, descriptions: List<String>,
                      blockIntervals : List<Long>, coinInputs : List<List<CoinInput>>,
                      itemInputs : List<List<ItemInput>>, entries : List<EntriesList>,
                      outputs: List<List<WeightedOutput>>, extraInfos: List<String>) : List<Transaction> {
        val count = names.size
        val txs = mutableListOf<Transaction>()
        for (i in 0  until count) {
            txs.add(
                    createRecipe(
                            name = names[i],
                            cookbookId = cookbookIds[i],
                            description = descriptions[i],
                            blockInterval = blockIntervals[i],
                            coinInputs = coinInputs[i],
                            itemInputs = itemInputs[i],
                            entries = entries[i],
                            outputs = outputs[i],
                            extraInfo = extraInfos[i]
                    )
            )
        }
        return txs
    }

    /** Create-cookbook message */
    abstract override fun createCookbook (id : String, name : String, developer : String, description : String, version : String,
                                          supportEmail : String, level : Long, costPerBlock : Long) : Transaction

    /** Batch create-cookbook message */
    override fun createCookbooks(ids : List<String>, names : List<String>, developers: List<String>, descriptions: List<String>,
                        versions : List<String>, supportEmails: List<String>, levels : List<Long>,
                        costsPerBlock : List<Long>) : List<Transaction> {
        val count = names.size
        val txs = mutableListOf<Transaction>()
        for (i in 0 until count) {
            txs.add(
                    createCookbook(
                            id = ids[i],
                            name = names[i],
                            developer = developers[i],
                            description = descriptions[i],
                            version = versions[i],
                            supportEmail = supportEmails[i],
                            level = levels[i],
                            costPerBlock = costsPerBlock[i]
                    )
            )
        }
        return txs
    }

    /**
     * Copies some data from profile's credentials object to userdata
     * for serialization.
     *  TODO: why does this actually exist?
     */
    abstract override fun dumpCredentials (credentials: ICredentials)

    abstract override fun fulfillTrade (tradeId : String, itemIds : List<String>, paymentId: String) : Transaction

    abstract override fun cancelTrade (tradeId : String) : Transaction
    /**
     * Generates a new Credentials object appropriate for our engine
     * type from the given mnemonic.
     */
    abstract override fun generateCredentialsFromMnemonic (mnemonic : String, passphrase : String) : ICredentials

    /**
     * Generates a new Credentials object appropriate for our engine
     * type from keys in userdata.
     */
    abstract override fun generateCredentialsFromKeys () : ICredentials

    /**
     * Creates new, default Credentials object appropriate for engine
     * type.
     */
    abstract override fun getNewCredentials () : ICredentials

    abstract override fun getProfileState (addr : String) : Profile?

    /** Get the balances of the user account. */
    @ExperimentalUnsignedTypes
    abstract override fun getMyProfileState () : MyProfile?

    abstract override fun getPendingExecutions () : List<Execution>

    /** Get a new instance of a CryptoHandler object appropriate for engine type. */
    abstract override fun getNewCryptoHandler() : ICryptoHandler

    /** Get the current status block. (Status block is returned w/ all IPC calls) */
    abstract override fun getStatusBlock() : StatusBlock

    /**
     * Retrieves transaction w/ the given ID.
     * (In an engine built to implement Cosmos functionality, this is the txhash)
     */
    abstract override fun getTransaction (id : String) : Transaction

    /** Registers a new profile under given name. */
    abstract override fun registerNewProfile (name : String, kp : PylonsSECP256K1.KeyPair?) : Transaction

    abstract override fun createChainAccount () : Transaction

    /** Calls non-IAP get pylons endpoint. Shouldn't work against production nodes. */
    abstract override fun getPylons (q : Long) : Transaction

    /** Calls Google IAP get pylons endpoint. */
    abstract override fun googleIapGetPylons(productId: String, purchaseToken: String, receiptData: String, signature: String): Transaction

    abstract override fun checkGoogleIapOrder(purchaseToken: String) : Boolean

    /** Gets initial userdata tables for the engine type. */
    abstract override fun getInitialDataSets () : MutableMap<String, MutableMap<String, String>>

    /** Calls send pylons endpoint. */
    abstract override fun sendCoins (coins : List<Coin>, receiver : String) : Transaction

    /** Update-cookbook message */
    abstract override fun updateCookbook (id : String, developer : String, description : String, version : String,
                                          supportEmail : String) : Transaction

    /** Batch update-cookbook message */
    override fun updateCookbooks(ids : List<String>, names : List<String>, developers: List<String>, descriptions: List<String>,
                        versions : List<String>, supportEmails: List<String>) : List<Transaction> {
        val count = names.size
        val txs = mutableListOf<Transaction>()
        for (i in 0  until count) {
            txs.add(
                    updateCookbook(
                            id = ids[i],
                            developer = developers[i],
                            description = descriptions[i],
                            version = versions[i],
                            supportEmail = supportEmails[i]
                    )
            )
        }
        return txs
    }

    /** Update-recipe message */
    abstract override fun updateRecipe(id : String, name : String, cookbookId : String, description: String, blockInterval : Long,
                                       coinInputs : List<CoinInput>, itemInputs : List<ItemInput>, entries : EntriesList, outputs: List<WeightedOutput>, extraInfo: String) : Transaction

    /** Batch update-recipe message */
    override fun updateRecipes (ids: List<String>, names : List<String>, cookbookIds : List<String>, descriptions: List<String>,
                       blockIntervals : List<Long>, coinInputs : List<List<CoinInput>>, itemInputs : List<List<ItemInput>>,
                       entries : List<EntriesList>, outputs: List<List<WeightedOutput>>,extraInfos: List<String>) : List<Transaction> {
        val count = names.size
        val txs = mutableListOf<Transaction>()
        for (i in 0  until count) {
            txs.add(
                    updateRecipe(
                            id = ids[i],
                            name = names[i],
                            cookbookId = cookbookIds[i],
                            description = descriptions[i],
                            blockInterval = blockIntervals[i],
                            coinInputs = coinInputs[i],
                            itemInputs = itemInputs[i],
                            entries = entries[i],
                            outputs = outputs[i],
                            extraInfo = extraInfos[i]
                    )
            )
        }
        return txs
    }

    /** List recipes query */
    abstract override fun listRecipes () : List<Recipe>

    /** List cookbooks query */
    abstract override fun listCookbooks () : List<Cookbook>

    abstract override fun setItemFieldString (itemId : String, field : String, value : String) : Transaction

    abstract override fun listTrades () : List<Trade>

    abstract override fun sendItems(receiver: String, itemIds: List<String>) : Transaction

    abstract override fun getLockedCoins () : LockedCoin

    abstract override fun getLockedCoinDetails () : LockedCoinDetails

    abstract override fun getCompletedExecutions(): List<Execution>

    abstract override fun getItem(itemId: String): Item?

    abstract override fun listItems(): List<Item>

    abstract override fun listItemsByCookbookId(cookbookId: String?): List<Item>

    abstract override fun listItemsBySender(sender: String?): List<Item>

}