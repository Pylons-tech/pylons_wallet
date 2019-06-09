package com.pylons.wallet.core.engine

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.wallet.core.engine.crypto.CryptoDummy
import com.pylons.wallet.core.types.*
import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.params.Networks
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

internal class TxPylonsAlphaTest {
    @Test
    fun frankenstein() {
        Security.addProvider(BouncyCastleProvider())
        Core.start(Backend.ALPHA_REST, "")
        val engine = Core.engine as TxPylonsAlphaEngine
        engine.cryptoHandler = engine.getNewCryptoHandler() as CryptoCosmos
        //engine.cryptoHandler.generateNewKeys()
        UserData.dataSets["__CRYPTO_COSMOS__"] = mutableMapOf("key" to "7e5c0ad3c8771ffe29cff8752da55859fe787f9677003bf8f78b78c6b87ea486")

        engine.cryptoHandler.importKeysFromUserData()
        Core.userProfile = Profile(engine.getNewCredentials(), mutableMapOf(), mutableMapOf(), mutableListOf())
        engine.getPylons(501)
        //assertEquals(Transaction.State.TX_ACCEPTED, tx.state)
    }
}