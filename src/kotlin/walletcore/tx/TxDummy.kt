package walletcore.tx

import walletcore.constants.*
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
        tx.finish(Transaction.State.TX_ACCEPTED)
    }

    override fun getBalances(profile: Profile, callback: Callback<Profile?>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNewCryptoHandler(userData: UserData): CryptoHandler {
        return CryptoDummy(userData)
    }

    override fun getNewUserId(): String {
        return "DUMMY"
    }
}