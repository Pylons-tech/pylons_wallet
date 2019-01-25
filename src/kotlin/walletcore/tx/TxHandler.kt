package walletcore.tx

import walletcore.Core
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
    abstract val isDevTxLayer : Boolean

    abstract fun applyRecipe(cookbook: Cookbook, recipe: Recipe, preferredItemIds : Set<String>) : Profile?

    fun bindItemCatalystsForRecipe (recipe: Recipe) : Set<Item>? {
        val set = mutableSetOf<Item>()
        recipe.itemCatalysts.forEach {
            val item = Core.userProfile!!.findItemForPrototype(it, emptySet()) ?: return null
            set.add(item)
        }
        return set.toSet()
    }

    fun bindItemInputsForRecipe (recipe: Recipe, preferredItemIds : Set<String>) : Set<Item>? {
        val set = mutableSetOf<Item>()
        recipe.itemsIn.forEach {
            val item = Core.userProfile!!.findItemForPrototype(it, preferredItemIds) ?: return null
            set.add(item)
        }
        return set.toSet()
    }

    abstract fun commitTx(tx: Transaction) : Profile?

    abstract fun getForeignBalances(id : String) : ForeignProfile?

    abstract fun getOwnBalances () : Profile?

    abstract fun getNewCryptoHandler(userData: UserData? = null) : CryptoHandler

    abstract fun getNewTransactionId() : String

    abstract fun getNewUserId() : String

    abstract fun loadCookbook(id : String) : Cookbook?

    abstract fun registerNewProfile () : Profile?
}