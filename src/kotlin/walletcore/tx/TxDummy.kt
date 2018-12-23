package walletcore.tx

import walletcore.Core
import walletcore.crypto.*
import walletcore.types.*


/***
 * The dummy TxHandler implementation. This "fakes" all network/blockchain actions and just
 * lets all transactions succeed by default, so long as locally-verifiable conditions are met.
 * (For instance, you still can't use an item you don't actually have.)
 */
class TxDummy : TxHandler() {

    override fun commitTx(tx: Transaction) {
        tx.submit()
        // Since there's no blockchain, we need to apply the transaction by hand
        Core.userProfile = Core.userProfile!!.addCoins(tx.coinsOut).removeCoins(tx.coinsIn).addItems(tx.itemsOut).removeItems(tx.itemsIn)
        tx.finish(Transaction.State.TX_ACCEPTED)

    }

    override fun getForeignBalances(foreignProfile: ForeignProfile, callback: Callback<ForeignProfile>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOwnBalances(callback: Callback<Profile?>) {
        callback.onSuccess(Core.userProfile)
    }

    override fun getNewCryptoHandler(userData: UserData): CryptoHandler {
        return CryptoDummy(userData)
    }

    override fun getNewUserId(): String {
        return "DUMMY"
    }
}