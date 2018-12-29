package walletcore.tx

import walletcore.crypto.CryptoHandler
import walletcore.types.*

/***
 * Generic interface for transaction-handling layers.
 * TxHandler provides a suite of basic functionality that WalletCore as a whole
 * can use to handle transactions at a high level; individual TXHandler implementations
 * will do the dirty work of binding that functionality to low-level blockchain
 * systems, in effect acting as "drivers."
 */
abstract class TxHandler {
    abstract fun commitTx(tx: Transaction) : Profile?

    abstract fun getForeignBalances(id : String) : ForeignProfile?

    abstract fun getOwnBalances () : Profile?

    abstract fun getNewCryptoHandler(userData: UserData? = null) : CryptoHandler

    abstract fun getNewTransactionId() : String

    abstract fun getNewUserId() : String

    abstract fun loadCookbook(id : String) : Cookbook?

    abstract fun registerNewProfile () : Profile?
}