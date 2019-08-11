package com.pylons.wallet.core.engine

import kotlinx.coroutines.*
import kotlinx.coroutines.runBlocking
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.crypto.*
import com.pylons.wallet.core.types.*
import com.squareup.moshi.*
import javafx.scene.input.Mnemonic
import kotlin.random.Random


/***
 * The dummy Engine implementation. This "fakes" all network/blockchain actions and just
 * lets all transactions succeed by default, so long as locally-verifiable conditions are met.
 * (For instance, you still can't use an item you don't actually have.)
 * Note that various operations performed by this Engine implementation will purposefully
 * block on a delay() call at some point. This doesn't serve any functional purpose, and the
 * logic should work the same way without the delay() calls; however, since TxDummyEngine doesn't rely
 * on any kind of remote resources, calling delay() allows us to emulate the behavior of a real-world
 * system, which will have to wait on network operations.
 */
internal class TxDummyEngine : Engine() {
    override val prefix : String = "__TXDUMMY__"
    override val backendType: Backend = Backend.DUMMY
    override val usesCrypto: Boolean = true // txdummy doesn't actually use crypto, but it thinkx it does
    override val usesMnemonic: Boolean = false
    override val isDevEngine: Boolean = true
    override val isOffLineEngine: Boolean = true

    class Credentials (id : String) : Profile.Credentials (id) {
        override fun dumpToMessageData(msg: MessageData) {
            msg.strings["address"] = address
        }
    }

    class CredentialsAdapter {
        @FromJson
        fun fromJson (json : String) : Profile.Credentials {
            return Moshi.Builder().build().adapter<Credentials>(Credentials::class.java).fromJson(json)!!
        }

        @ToJson
        fun toJson (credentials : Profile.Credentials) : String {
            return Moshi.Builder().build().adapter<Credentials>(Credentials::class.java).toJson(credentials as Credentials)!!
        }
    }

    override fun dumpCredentials(credentials: Profile.Credentials) {
        val c = credentials as Credentials
        UserData.dataSets.getValue(prefix)["address"] = c.address
    }

    override fun generateCredentialsFromMnemonic(mnemonic: String, passphrase: String): Profile.Credentials {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun generateCredentialsFromKeys(): Profile.Credentials {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNewCredentials(): Credentials {
        return Credentials(UserData.dataSets.get(prefix).orEmpty().getOrDefault("address", Random.nextLong().toString()))
    }

    override fun getStatusBlock(): StatusBlock {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTransaction(id: String): Transaction {
        return OutsideWorldDummy.transactions[id]!!
    }

    override fun applyRecipe(cookbook: String, recipe: String, preferredItemIds : List<String>): Transaction {
        // There really needs to be an apparatus for getting more detailed error data out of this than "nope"
        TODO("Rejigger this")
//        val gameRule = fetchGameRule(cookbook, recipe)
//        System.out.println("${gameRule.itemsOut?.size}")
//        System.out.println("Can apply rule $cookbook/$recipe? ${gameRule.canApply()}")
//        return when (gameRule.canApply()) {
//            true -> {
//                gameRule.applyOffline()
//                return Core.userProfile
//            }
//            false -> null
//        }
    }

    private fun fetchGameRule (cookbook: String, id: String) : GameRule {
        return when (OutsideWorldDummy.builtinGameRules.containsKey(cookbook) && OutsideWorldDummy.builtinGameRules[cookbook]!!.containsKey(id)) {
            true -> OutsideWorldDummy.builtinGameRules[cookbook]!![id]!!
            false -> OutsideWorldDummy.loadExternalGameRuleDef(cookbook, id)
        }
    }

    override fun commitTx(tx: Transaction) : Transaction {
        TODO("Rejigger this")
//        tx.submit()
//        runBlocking { delay(500) }
//        // Since there's no blockchain, we need to apply the transaction by hand
//        Core.userProfile!!.items.removeAll(tx.itemsIn)
//        tx.itemsOut.forEach { Core.userProfile!!.items.add(it) }
//        tx.coinsIn.forEach { Core.userProfile!!.coins[it.address] = Core.userProfile!!.coins[it.address]!! - it.count!! }
//        tx.coinsOut.forEach {
//            val base = when (Core.userProfile!!.coins[it.address]) {
//                null -> 0
//                else -> Core.userProfile!!.coins[it.address]!!
//            }
//            Core.userProfile!!.coins[it.address] = base + it.count!! }
//        tx.finish(Transaction.State.TX_ACCEPTED)
//        OutsideWorldDummy.addTx(tx)
        //return Core.userProfile
    }

    override fun getForeignBalances(id : String) : ForeignProfile?{
        runBlocking { delay(500) }
        println(OutsideWorldDummy.profiles.containsKey(id).toString() + " $id")
        return OutsideWorldDummy.profiles[id]
    }

    override fun getOwnBalances () : Profile? {
        runBlocking { delay(500) }
        return Core.userProfile
    }

    override fun getNewCryptoHandler(): CryptoHandler {
        return CryptoDummy()
    }

    override fun registerNewProfile(name : String) : Transaction {
        TODO("Rejigger this")
        //runBlocking { delay(500) }
        //Core.userProfile!!.provisional = false
        //return Core.userProfile
    }

    override fun getPylons(q: Int): Transaction {
        TODO("tx redesign")
        //runBlocking { delay(500) }
//        var tx = Transaction(getNewTransactionId(), "", (Core.userProfile!!.credentials as Credentials).address,
//                listOf(), listOf(Coin("pylons", q)))
//        return commitTx(tx)
    }

    override fun getInitialDataSets(): MutableMap<String, MutableMap<String, String>> {
        val cryptoTable = mutableMapOf<String, String>()
        val engineTable = mutableMapOf<String, String>()
        return mutableMapOf("__CRYPTO_DUMMY__" to cryptoTable, "__TXDUMMY__" to engineTable)
    }

    override fun sendPylons(q: Int, receiver: String): Transaction {
        TODO("tx redesign")
    }
}