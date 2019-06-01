//package com.pylons.wallet.core.engine
//
//import com.squareup.moshi.Moshi
//import com.pylons.wallet.core.Core
//import com.pylons.wallet.core.engine.crypto.CryptoCosmos
//import com.pylons.wallet.core.engine.crypto.CryptoHandler
//import com.pylons.wallet.core.engine.TxDummyEngine
//import com.pylons.wallet.core.engine.Engine
//import com.pylons.wallet.core.types.ForeignProfile
//import com.pylons.wallet.core.types.Profile
//import com.pylons.wallet.core.types.Transaction
//import com.pylons.wallet.core.types.UserData
//import java.io.OutputStreamWriter
//import java.math.BigDecimal
//import java.net.HttpURLConnection
//import java.net.URL
//import java.nio.charset.Charset
//import org.apache.commons.codec.binary.*
//import javax.xml.soap.Text
//
//class TxPylonsTestnet  : Engine() {
//    private val addr = "http://35.224.155.76:80"
//    override val isDevEngine: Boolean = true
//    override val isOffLineEngine: Boolean = true
//
//    class Signature (
//        val signature : String? = null,
//        val pub_key : PubKey? = null,
//        val account_number : Long? = 0,
//        val sequence : Long? = 0
//        )
//
//    class PubKey (
//        val type : String? = null,
//        val value : String? = null
//        )
//
//    class SdkCoin (
//        val denom : String? = null,
//        val amount : Long? = null
//    )
//
//    class SdkDecCoin (
//        val denom : String? = null,
//        val amount : BigDecimal? = null
//    )
//
//    class BaseReq (
//        var from : String? = null,
//        var memo : String? = null,
//        var chain_id : String? = null,
//        var account_nuumber : Long? = null,
//        var sequence : Long? = null,
//        var fees : Array<SdkCoin>? = null,
//        var gasPrices : Array<SdkDecCoin>? = null,
//        var gas : String? = null,
//        var gasAdjustment : String? = null,
//        var simulate : Boolean? = null
//    )
//
//    override fun applyRecipe(cookbook: String, recipe: String, preferredItemIds: List<String>): Profile? {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun commitTx(engine: Transaction): Profile? {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun getAverageBlockTime(): Double {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun getHeight(): Long {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun getForeignBalances(id: String): ForeignProfile? {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun getNewCryptoHandler(userData: UserData?): CryptoHandler = CryptoCosmos(userData)
//
//    override fun getNewTransactionId(): String {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun getNewUserId(): String {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun getTransaction(id: String): Transaction? {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun registerNewProfile(): Profile? {
//        return null
//    }
//
//    override fun getOwnBalances(): Profile? {
//        val url = "$addr/bank/balances/${Core.userProfile!!.id}"
//        return null
//    }
//
//    override fun getPylons(q: Int): Profile? {
//        val url = URL("$addr/pylons/get_pylons")
//        val pubKey = PubKey("tendermint/PubKeySecp256k1", Core.cryptoHandler!!.getEncodedPublicKey())
//        class Message {
//            var base_req : BaseReq = BaseReq()
//            var Requester : String = Core.userProfile!!.id
//        }
//        // We don't actually use q right now...
//        val msg = Message()
//        val sigBytes = Core.cryptoHandler!!.signature(
//                Moshi.Builder().build().adapter<Message>(Message::class.java).toJson(msg).toByteArray(Charset.defaultCharset())
//        )
//        val sigBlock = Signature(Base32().encodeToString(sigBytes), pubKey)
//        class FinalOutput {
//
//        }
//        with (url.openConnection() as HttpURLConnection) {
//            val writer = OutputStreamWriter(outputStream)
//            //writer.write()
//        }
//        return null
//    }
//}