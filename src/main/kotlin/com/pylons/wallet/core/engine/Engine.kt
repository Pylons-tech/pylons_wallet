package com.pylons.wallet.core.engine

import com.pylons.wallet.core.engine.crypto.CryptoHandler
import com.pylons.wallet.core.types.*
import com.sun.org.apache.xpath.internal.operations.Bool
import com.sun.xml.internal.fastinfoset.util.StringArray

/***
 * Generic interface for transaction-handling layers.
 * Engine provides a suite of basic functionality that WalletCore as a whole
 * can use to handle transactions at a high level; individual TXHandler implementations
 * will do the dirty work of binding that functionality to low-level blockchain
 * systems, in effect acting as "drivers."
 */
internal abstract class Engine {
    abstract val prefix : String
    abstract val backendType : Backend
    abstract val usesCrypto : Boolean
    abstract val usesMnemonic : Boolean
    abstract val isDevEngine : Boolean
    abstract val isOffLineEngine : Boolean

    /**
     * Enable-recipe message
     */
    abstract fun enableRecipe(id : String) : Transaction

    /**
     * Disable-recipe message
     */
    abstract fun disableRecipe(id : String) : Transaction

    /**
     * Execute-recipe message
     */
    abstract fun applyRecipe(id : String) : Transaction

    /**
     * Create-recipe message
     */
    abstract fun createRecipe(name : String, cookbookName : String, desc : String, coinInputs : Map<String, Long>,
                              coinOutputs : Map<String, Long>, itemInputs: Array<Item>, itemOutputs : Array<Item>, time : Long) : Transaction

    /**
     * Low-level commit TX function.
     * TODO: determine if this should even exist; sendPylons suggests 'no'
     */
    abstract fun commitTx(tx: Transaction) : Transaction

    /**
     * Create-cookbook message
     */
    abstract fun createCookbook (name : String, devel : String, desc : String, version : String,
                                 supportEmail : String, level : Long) : Transaction

    /**
     * Copies some data from profile's credentials object to userdata
     * for serialization.
     *  TODO: why does this actually exist?
     */
    abstract fun dumpCredentials (credentials: Profile.Credentials)

    /**
     * Generates a new Credentials object appropriate for our engine
     * type from the given mnemonic.
     */
    abstract fun generateCredentialsFromMnemonic (mnemonic : String, passphrase : String) : Profile.Credentials

    /***
     * Generates a new Credentials object appropriate for our engine
     * type from keys in userdata.
     */
    abstract fun generateCredentialsFromKeys () : Profile.Credentials

    /**
     * Creates new, default Credentials object appropriate for engine
     * type.
     */
    abstract fun getNewCredentials () : Profile.Credentials

    /**
     * Get the balances of a third-party account.
     */
    abstract fun getForeignBalances(id : String) : ForeignProfile?

    /**
     * Get the balances of the user account.
     */
    abstract fun getOwnBalances () : Profile?

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
     * Does not generate new keys!
     * TODO: actually i think this does generate keys, but it Oughtn't
     */
    abstract fun registerNewProfile (name : String) : Transaction

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
    abstract fun sendPylons (q : Long, receiver : String) : Transaction

    /**
     * Update-cookbook message
     */
    abstract fun updateCookbook (id : String, devel : String, desc : String, version : String,
                                 supportEmail : String) : Transaction

    /**
     * Update-recipe message
     */
    abstract fun updateRecipe(name : String, cookbookName : String, id : String, desc : String, coinInputs : Map<String, Long>,
                              coinOutputs : Map<String, Long>, itemInputs: Array<Item>, itemOutputs : Array<Item>, time : Long) : Transaction

    /**
     * List recipes query
     */
    abstract fun listRecipes (cookbook : String) : Array<Recipe>
}