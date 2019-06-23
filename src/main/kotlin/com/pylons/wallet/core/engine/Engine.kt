package com.pylons.wallet.core.engine

import com.pylons.wallet.core.engine.crypto.CryptoHandler
import com.pylons.wallet.core.types.*

/***
 * Generic interface for transaction-handling layers.
 * Engine provides a suite of basic functionality that WalletCore as a whole
 * can use to handle transactions at a high level; individual TXHandler implementations
 * will do the dirty work of binding that functionality to low-level blockchain
 * systems, in effect acting as "drivers."
 */
internal abstract class Engine {
    abstract val prefix : String
    abstract val usesCrypto : Boolean
    abstract val isDevEngine : Boolean
    abstract val isOffLineEngine : Boolean

    abstract fun applyRecipe(cookbook: String, recipe: String, preferredItemIds : List<String>) : Profile?

    abstract fun commitTx(tx: Transaction) : Profile?

    abstract fun dumpCredentials (credentials: Profile.Credentials)

    abstract fun generateCredentialsFromKeys () : Profile.Credentials

    abstract fun getNewCredentials () : Profile.Credentials

    abstract fun getForeignBalances(id : String) : ForeignProfile?

    abstract fun getOwnBalances () : Profile?

    abstract fun getNewCryptoHandler() : CryptoHandler

    abstract fun getStatusBlock() : StatusBlock

    abstract fun getTransaction (id : String) : Transaction?

    abstract fun registerNewProfile () : Profile?

    abstract fun getPylons (q : Int) : Profile?

    abstract fun getInitialDataSets () : MutableMap<String, MutableMap<String, String>>
}