package walletcore.tx

import kotlinx.coroutines.*
import kotlinx.coroutines.runBlocking
import walletcore.Core
import walletcore.crypto.*
import walletcore.internal.newItemFromPrototype
import walletcore.types.*


/***
 * The dummy TxHandler implementation. This "fakes" all network/blockchain actions and just
 * lets all transactions succeed by default, so long as locally-verifiable conditions are met.
 * (For instance, you still can't use an item you don't actually have.)
 * Note that various operations performed by this TxHandler implementation will purposefully
 * block on a delay() call at some point. This doesn't serve any functional purpose, and the
 * logic should work the same way without the delay() calls; however, since TxDummy doesn't rely
 * on any kind of remote resources, calling delay() allows us to emulate the behavior of a real-world
 * system, which will have to wait on network operations.
 */
class TxDummy : TxHandler() {
    override val isDevTxLayer: Boolean = true
    override val isOfflineTxLayer: Boolean = true

    override fun applyRecipe(cookbook: Cookbook, recipe: Recipe, preferredItemIds : Set<String>): Profile? {
        // There really needs to be an apparatus for getting more detailed error data out of this than "nope"
        val boundItemInputs = bindItemInputsForRecipe(recipe, preferredItemIds) ?: return null
        bindItemCatalystsForRecipe(recipe) ?: return null
        if (!Core.userProfile!!.canPayCoins(recipe.coinsIn)) return null
        if (!Core.userProfile!!.canPayCoins(recipe.coinCatalysts)) return null
        recipe.coinsIn.forEach {
            Core.userProfile = Core.userProfile!!.removeCoins(setOf(it))
        }
        val coins = Core.userProfile!!.coins.addCoins(recipe.coinsOut, false)
        val outItems = mutableSetOf<Item>()
        recipe.itemsOut.forEach {
            outItems.add(newItemFromPrototype(it))
        }
        val items = Core.userProfile!!.items.exclude(boundItemInputs) + outItems
        Core.userProfile = Profile(id = Core.userProfile!!.id, strings = Core.userProfile!!.strings,
                coins = coins, items = items)
        return Core.userProfile
    }

    override fun commitTx(tx: Transaction) : Profile? {
        tx.submit()
        runBlocking { delay(500) }
        // Since there's no blockchain, we need to apply the transaction by hand
        Core.userProfile = Core.userProfile!!.addCoins(tx.coinsOut).removeCoins(tx.coinsIn).addItems(tx.itemsOut).removeItems(tx.itemsIn)
        tx.finish(Transaction.State.TX_ACCEPTED)
        return Core.userProfile
    }

    override fun getForeignBalances(id : String) : ForeignProfile?{
        runBlocking { delay(500) }
        System.out.println(OutsideWorldDummy.profiles.containsKey(id).toString() + " $id")
        return OutsideWorldDummy.profiles[id]
    }

    override fun getOwnBalances () : Profile? {
        runBlocking { delay(500) }
        return Core.userProfile
    }

    override fun getNewCryptoHandler(userData: UserData?): CryptoHandler {
        return CryptoDummy(userData)
    }

    override fun getNewTransactionId(): String {
        return "DUMMY"
    }

    override fun getNewUserId(): String {
        return "DUMMY"
    }

    override fun loadCookbook(id: String): Cookbook? {
        val cbk = OutsideWorldDummy.cookbooks[id]
        if (cbk != null) {
            val map = Core.loadedCookbooks.toMutableMap()
            map[id] = cbk
            Core.loadedCookbooks = map.toMap()
        }
        return cbk
    }

    override fun registerNewProfile() : Profile? {
        runBlocking { delay(500) }
        Core.userProfile = Profile(id = Core.userProfile!!.id, strings = Core.userProfile!!.strings, provisional = false)
        return Core.userProfile
    }
}