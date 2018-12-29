package walletcore.tx

import kotlinx.coroutines.*
import kotlinx.coroutines.runBlocking
import walletcore.Core
import walletcore.crypto.*
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
        return ForeignProfile(id = id)
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerNewProfile() : Profile? {
        runBlocking { delay(500) }
        Core.userProfile = Profile(id = Core.userProfile!!.id, strings = Core.userProfile!!.strings, provisional = false)
        return Core.userProfile
    }
}