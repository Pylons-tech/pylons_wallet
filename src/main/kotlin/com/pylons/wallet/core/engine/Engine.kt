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
    abstract val isDevTxLayer : Boolean
    abstract val isOfflineTxLayer : Boolean

    abstract fun applyRecipe(cookbook: String, recipe: String, preferredItemIds : List<String>) : Profile?

    abstract fun bootstrap ()

    abstract fun commitTx(tx: Transaction) : Profile?

    abstract fun getAverageBlockTime () : Double

    abstract fun dumpCredentials (credentials: Profile.Credentials)

    abstract fun getCredentials () : Profile.Credentials

    abstract fun getHeight () : Long

    abstract fun getForeignBalances(id : String) : ForeignProfile?

    abstract fun getOwnBalances () : Profile?

    abstract fun getNewCryptoHandler(userData: UserData? = null) : CryptoHandler

    abstract fun getNewTransactionId() : String

    abstract fun getNewUserId() : String

    abstract fun getTransaction (id : String) : Transaction?

    abstract fun registerNewProfile () : Profile?

    abstract fun getPylons (q : Int) : Profile?
}