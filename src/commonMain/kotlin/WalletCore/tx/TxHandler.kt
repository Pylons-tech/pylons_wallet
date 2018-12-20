package walletcore.tx

import walletcore.types.Callback
import walletcore.types.Profile

/***
 * Generic interface for transaction-handling layers.
 * TxHandler provides a suite of basic functionality that WalletCore as a whole
 * can use to handle transactions at a high level; individual TXHandler implementations
 * will do the dirty work of binding that functionality to low-level blockchain
 * systems, in effect acting as "drivers."
 */
abstract class TxHandler {
    abstract fun commitTx(tx: Transaction)

    abstract fun getBalances(profile: Profile, callback: Callback<Wallet?>)
}