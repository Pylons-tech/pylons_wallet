package walletcore.tx

import com.squareup.moshi.Moshi
import kotlinx.coroutines.*
import kotlinx.coroutines.runBlocking
import walletcore.Core
import walletcore.crypto.*
import walletcore.gamerules.SimpleContract
import walletcore.internal.generateFailureMessageData
import walletcore.internal.newItemFromPrototype
import walletcore.types.*
import java.sql.Date
import java.sql.Time
import java.time.Instant
import java.util.*


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
    override fun getHeight(): Long {
        return OutsideWorldDummy.transactions.size.toLong()
    }

    override fun getAverageBlockTime(): Double {
        return 60.0
    }

    override fun getTransaction(id: String): Transaction? {
        return OutsideWorldDummy.transactions[id]
    }

    override val isDevTxLayer: Boolean = true
    override val isOfflineTxLayer: Boolean = true

    override fun applyRecipe(cookbook: String, recipe: String, preferredItemIds : List<String>): Profile? {
        // There really needs to be an apparatus for getting more detailed error data out of this than "nope"
        val gameRule = fetchGameRule(cookbook, recipe)
        System.out.println("${gameRule.itemsOut?.size}")
        System.out.println("Can apply rule $cookbook/$recipe? ${gameRule.canApply()}")
        return when (gameRule.canApply()) {
            true -> {
                gameRule.applyOffline()
                return Core.userProfile
            }
            false -> null
        }
    }

    private fun fetchGameRule (cookbook: String, id: String) : GameRule {
        return when (OutsideWorldDummy.builtinGameRules.containsKey(cookbook) && OutsideWorldDummy.builtinGameRules[cookbook]!!.containsKey(id)) {
            true -> OutsideWorldDummy.builtinGameRules[cookbook]!![id]!!
            false -> OutsideWorldDummy.loadExternalGameRuleDef(cookbook, id)
        }
    }

    override fun commitTx(tx: Transaction) : Profile? {
        tx.submit()
        runBlocking { delay(500) }
        // Since there's no blockchain, we need to apply the transaction by hand
        Core.userProfile!!.items.removeAll(tx.itemsIn)
        tx.itemsOut.forEach { Core.userProfile!!.items.add(it) }
        tx.coinsIn.forEach { Core.userProfile!!.coins[it.id] = Core.userProfile!!.coins[it.id]!! - it.count!! }
        tx.coinsOut.forEach {
            val base = when (Core.userProfile!!.coins[it.id]) {
                null -> 0
                else -> Core.userProfile!!.coins[it.id]!!
            }
            Core.userProfile!!.coins[it.id] = base + it.count!! }
        tx.finish(Transaction.State.TX_ACCEPTED)
        OutsideWorldDummy.addTx(tx)
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
        return "tx_${Date.from(Instant.now())}"
    }

    override fun getNewUserId(): String {
        return "usr_${Date.from(Instant.now())}"
    }

    override fun registerNewProfile() : Profile? {
        runBlocking { delay(500) }
        Core.setProfile(Profile(id = Core.userProfile!!.id, strings = Core.userProfile!!.strings, provisional = false))
        return Core.userProfile
    }

    override fun getPylons(q: Int): Profile? {
        //runBlocking { delay(500) }
        var tx = Transaction(getNewTransactionId(), "", Core.userProfile!!.id, listOf(), listOf(Coin("pylons", q)))
        return commitTx(tx)
    }
}