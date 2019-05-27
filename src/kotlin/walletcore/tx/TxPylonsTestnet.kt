package kotlin.walletcore.tx

import walletcore.Core
import walletcore.crypto.CryptoHandler
import walletcore.tx.TxDummy
import walletcore.tx.TxHandler
import walletcore.types.ForeignProfile
import walletcore.types.Profile
import walletcore.types.Transaction
import walletcore.types.UserData
import java.math.BigDecimal

class TxPylonsTestnet  : TxHandler() {
    private val addr = "http://35.224.155.76:80"
    override val isDevTxLayer: Boolean = true
    override val isOfflineTxLayer: Boolean = true

    class SdkCoin (
        val denom : String? = null
        val amount : Long? = null
    )

    class SdkDecCoin (
        val denom : String? = null
        val amount : BigDecimal? = null
    )

    class BaseReq (
        val from : String? = null
        val memo : String? = null
        val chain_id : String? = null
        val account_nuumber : Long? = null
        val sequence : Long? = null
        val fees : Array<SdkCoin>? = null
        val gasPrices : Array<SdkDecCoin>? = null
        val gas : String? = null
        val gasAdjustment : String? = null
        val simulate : Boolean? = null
    )

    override fun applyRecipe(cookbook: String, recipe: String, preferredItemIds: List<String>): Profile? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun commitTx(tx: Transaction): Profile? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAverageBlockTime(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getHeight(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getForeignBalances(id: String): ForeignProfile? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNewCryptoHandler(userData: UserData?): CryptoHandler {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNewTransactionId(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNewUserId(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTransaction(id: String): Transaction? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerNewProfile(): Profile? {

    }

    override fun getOwnBalances(): Profile? {
        val url = "$addr/bank/balances/${Core.userProfile!!.id}"
    }

    override fun getPylons(q: Int): Profile? {
        val url = "$addr/pylons/get_pylons"
        class jsonMessage (
            val base_req : BaseReq? = null
            val amount : String? = null
            val buyer : String? = null
        )
    }
}