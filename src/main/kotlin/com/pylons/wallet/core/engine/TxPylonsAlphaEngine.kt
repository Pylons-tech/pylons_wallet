package com.pylons.wallet.core.engine

import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.wallet.core.engine.crypto.CryptoHandler
import com.pylons.wallet.core.types.ForeignProfile
import com.pylons.wallet.core.types.Profile
import com.pylons.wallet.core.types.Transaction
import org.apache.tuweni.crypto.Hash
import org.apache.tuweni.crypto.sodium.SHA256Hash
import java.security.KeyPair
import org.wildfly.openssl.*

internal class TxPylonsAlphaEngine : Engine() {
    override val prefix : String = "__TXPYLONSALPHA__"
    override val usesCrypto: Boolean = true
    override val isDevEngine: Boolean = true
    override val isOffLineEngine: Boolean = false
    var cryptoHandler = CryptoCosmos()

    fun addressFromPubKey () {
        var compressedPubKey = cryptoHandler.keyPair!!.publicKey().asEcPoint()
        //val sha256 = Hash.sha2_256(compressedPubKey)
    }

    override fun applyRecipe(cookbook: String, recipe: String, preferredItemIds: List<String>): Profile? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun commitTx(tx: Transaction): Profile? {

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAverageBlockTime(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun dumpCredentials(credentials: Profile.Credentials) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNewCredentials(): Profile.Credentials {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getHeight(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getForeignBalances(id: String): ForeignProfile? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOwnBalances(): Profile? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNewCryptoHandler(): CryptoHandler {
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPylons(q: Int): Profile? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getInitialDataSets(): MutableMap<String, MutableMap<String, String>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}