package com.pylons.wallet.core.engine

import com.pylons.wallet.core.engine.crypto.CryptoHandler
import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.Execution
import com.pylons.wallet.core.types.tx.Trade
import com.pylons.wallet.core.types.tx.item.Item
import com.pylons.wallet.core.types.tx.recipe.*

/***
 * Generic interface for transaction-handling layers.
 * Engine provides a suite of basic functionality that WalletCore as a whole
 * can use to handle transactions at a high level; individual TXHandler implementations
 * will do the dirty work of binding that functionality to low-level blockchain
 * systems, in effect acting as "drivers."
 */
abstract class Engine {
    /**
     * Identifier string, unique per Engine implementation.
     * Used to identify the engine type associated with a given dataset
     * when we dump the datastore to XML.
     */
    abstract val prefix : String

    /**
     * Specifies the TX-handling backend associated w/ an Engine instance.
     */
    abstract val backendType : Backend

    /**
     * Identifies whether or not we're using BIP44 mnemonics when doing keygen.
     */
    abstract val usesMnemonic : Boolean

    /**
     * Should this engine have access to developer-use methods?
     */
    abstract val isDevEngine : Boolean

    /**
     * The current CryptoHandler instance associated with this engine
     */
    abstract var cryptoHandler : CryptoHandler

    /**
     * Enable-recipe message
     */
    abstract fun enableRecipe(id : String) : Transaction

    /**
     * Batch enable-recipe message
     */
    fun enableRecipes(recipes : List<String>) : List<Transaction> {
        val txs = mutableListOf<Transaction>()
        recipes.forEach { txs.add(enableRecipe(it)) }
        return txs
    }

    /**
     * Disable-recipe message
     */
    abstract fun disableRecipe(id : String) : Transaction

    /**
     * Batch enable-recipe message
     */
    fun disableRecipes(recipes : List<String>) : List<Transaction> {
        val txs = mutableListOf<Transaction>()
        recipes.forEach { txs.add(disableRecipe(it)) }
        return txs
    }

    /**
     * Execute-recipe message
     */
    abstract fun applyRecipe(id : String, itemIds : Array<String>) : Transaction

    abstract fun checkExecution(id : String, payForCompletion : Boolean) : Transaction

    abstract fun createTrade(coinInputs: List<CoinInput>, itemInputs: List<ItemInput>,
                             coinOutputs : List<CoinOutput>, itemOutputs : List<Item>,
                             ExtraInfo : String) : Transaction


                             /**
     * Create-recipe message
     */
    abstract fun createRecipe(sender : String, name : String, cookbookId : String, description: String, blockInterval : Long,
                              coinInputs : List<CoinInput>, itemInputs : List<ItemInput>, entries : EntriesList,
                              outputs : List<WeightedOutput>) : Transaction

    /**
     * Batch create-recipe message
     */
    fun createRecipes(sender : String, names : List<String>, cookbookIds : List<String>, descriptions: List<String>,
                      blockIntervals : List<Long>, coinInputs : List<List<CoinInput>>,
                      itemInputs : List<List<ItemInput>>, entries : List<EntriesList>,
                      outputs: List<List<WeightedOutput>>) : List<Transaction> {
        val count = names.size
        val txs = mutableListOf<Transaction>()
        for (i in 0  until count) {
            txs.add(
                    createRecipe(
                            sender = sender,
                            name = names[i],
                            cookbookId = cookbookIds[i],
                            description = descriptions[i],
                            blockInterval = blockIntervals[i],
                            coinInputs = coinInputs[i],
                            itemInputs = itemInputs[i],
                            entries = entries[i],
                            outputs = outputs[i]
                    )
            )
        }
        return txs
    }

    /**
     * Create-cookbook message
     */
    abstract fun createCookbook (id : String, name : String, developer : String, description : String, version : String,
                                 supportEmail : String, level : Long, costPerBlock : Long) : Transaction

    /**
     * Batch create-cookbook message
     */
    fun createCookbooks(ids : List<String>, names : List<String>, developers: List<String>, descriptions: List<String>,
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
    abstract fun dumpCredentials (credentials: MyProfile.Credentials)

    abstract fun fulfillTrade (tradeId : String) : Transaction

    abstract fun cancelTrade (tradeId : String) : Transaction
    /**
     * Generates a new Credentials object appropriate for our engine
     * type from the given mnemonic.
     */
    abstract fun generateCredentialsFromMnemonic (mnemonic : String, passphrase : String) : MyProfile.Credentials

    /***
     * Generates a new Credentials object appropriate for our engine
     * type from keys in userdata.
     */
    abstract fun generateCredentialsFromKeys () : MyProfile.Credentials

    /**
     * Creates new, default Credentials object appropriate for engine
     * type.
     */
    abstract fun getNewCredentials () : MyProfile.Credentials

    /**
     * Get the balances of a third-party account.
     */
    abstract fun getForeignBalances(id : String) : Profile?

    /**
     * Get the balances of the user account.
     */
    abstract fun getOwnBalances () : MyProfile?

    abstract fun getPendingExecutions () : List<Execution>

    /**
     * Get a new instance of a CryptoHandler object appropriate for
     * engine type.
     */
    abstract fun getNewCryptoHandler() : CryptoHandler

    /**
     * Get the current status block.
     * (Status block is returned w/ all IPC calls)
     */
    abstract fun getStatusBlock() : StatusBlock

    /**
     * Retrieves transaction w/ the given ID.
     * (In an engine built to implement Cosmos functionality, this is the txhash)
     */
    abstract fun getTransaction (id : String) : Transaction

    /**
     * Registers a new profile under given name.
     */
    abstract fun registerNewProfile (name : String, kp : PylonsSECP256K1.KeyPair?) : Transaction

    /**
     * Calls get pylons endpoint.
     * Takes an arbitrary number of pylons,
     * but backend is locked to 500 right now.
     * TODO: payment integration???
     */
    abstract fun getPylons (q : Long) : Transaction

    /**
     * Gets initial userdata tables for the engine type.
     */
    abstract fun getInitialDataSets () : MutableMap<String, MutableMap<String, String>>

    /***
     * Calls send pylons endpoint.
     */
    abstract fun sendCoins (denom : String, q : Long, receiver : String) : Transaction

    /**
     * Update-cookbook message
     */
    abstract fun updateCookbook (id : String, developer : String, description : String, version : String,
                                 supportEmail : String) : Transaction

    /**
     * Batch update-cookbook message
     */
    fun updateCookbooks(ids : List<String>, names : List<String>, developers: List<String>, descriptions: List<String>,
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

    /**
     * Update-recipe message
     */
    abstract fun updateRecipe(id : String, sender : String, name : String, cookbookId : String, description: String, blockInterval : Long,
                              coinInputs : List<CoinInput>, itemInputs : List<ItemInput>, entries : EntriesList, outputs: List<WeightedOutput>) : Transaction

    /**
     * Batch update-recipe message
     */
    fun updateRecipes (sender : String, ids: List<String>, names : List<String>, cookbookIds : List<String>, descriptions: List<String>,
                       blockIntervals : List<Long>, coinInputs : List<List<CoinInput>>, itemInputs : List<List<ItemInput>>,
                       entries : List<EntriesList>, outputs: List<List<WeightedOutput>>) : List<Transaction> {
        val count = names.size
        val txs = mutableListOf<Transaction>()
        for (i in 0  until count) {
            txs.add(
                    updateRecipe(
                            id = ids[i],
                            sender = sender,
                            name = names[i],
                            cookbookId = cookbookIds[i],
                            description = descriptions[i],
                            blockInterval = blockIntervals[i],
                            coinInputs = coinInputs[i],
                            itemInputs = itemInputs[i],
                            entries = entries[i],
                            outputs = outputs[i]
                    )
            )
        }
        return txs
    }

    /**
     * List recipes query
     */
    abstract fun listRecipes () : List<Recipe>

    /**
     * List cookbooks query
     */
    abstract fun listCookbooks () : List<Cookbook>

    abstract fun setItemFieldString (itemId : String, field : String, value : String) : Transaction

    abstract fun listTrades () : List<Trade>
}