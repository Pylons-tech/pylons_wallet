package tech.pylons.wallet.walletcore_test.engine

import tech.pylons.lib.PubKeyUtil
import tech.pylons.wallet.core.Core
import tech.pylons.wallet.core.engine.TxPylonsEngine
import tech.pylons.lib.types.Backend
import tech.pylons.lib.types.Config
import org.spongycastle.jce.provider.BouncyCastleProvider
import org.spongycastle.util.encoders.Hex
import org.junit.jupiter.api.Test
import java.security.Security

class KeysButNoStateFlow {
    val core = Core(Config(Backend.LIVE_DEV, listOf("http://127.0.0.1:1317"))).use()

    @ExperimentalUnsignedTypes
    @Test
    fun keysExistButNoState () {
        Security.addProvider(BouncyCastleProvider())
        val kp = PubKeyUtil.generateKeyPairFromMnemonic(PubKeyUtil.generateMnemonic())
        val addr = TxPylonsEngine.getAddressString(PubKeyUtil.getAddressFromKeyPair(kp).toArray())
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