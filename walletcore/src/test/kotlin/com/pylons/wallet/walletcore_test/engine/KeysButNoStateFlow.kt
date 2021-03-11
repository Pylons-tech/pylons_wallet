package com.pylons.wallet.walletcore_test.engine

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.TxPylonsEngine
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.lib.types.types.Backend
import com.pylons.lib.types.types.Config
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Hex
import org.junit.jupiter.api.Test
import java.security.Security

class KeysButNoStateFlow {
    val core = Core(Config(Backend.LIVE_DEV, listOf("http://127.0.0.1:1317"))).use()

    @ExperimentalUnsignedTypes
    @Test
    fun keysExistButNoState () {
        Security.addProvider(BouncyCastleProvider())
        val kp = CryptoCosmos.generateKeyPairFromMnemonic(CryptoCosmos.generateMnemonic())
        val addr = TxPylonsEngine.getAddressString(CryptoCosmos.getAddressFromKeyPair(kp).toArray())
        // get startup json for this keypair
        val data = """
            {"dataSets" : {
            "__CRYPTO_COSMOS__": {"key": "${Hex.toHexString(kp.secretKey().bytesArray())}"}, 
            "__TXPYLONSALPHA__": {"address": "$addr"}}, "version" : null}
        """.trimIndent()
        core.start(data)
        assert(core.engine.getMyProfileState() == null)
    }
}