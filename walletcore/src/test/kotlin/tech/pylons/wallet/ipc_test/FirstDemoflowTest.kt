package tech.pylons.wallet.ipc_test

import tech.pylons.wallet.core.Core
import tech.pylons.wallet.core.engine.TxPylonsDevEngine
import tech.pylons.wallet.core.engine.crypto.CryptoCosmos
import tech.pylons.lib.types.*
import tech.pylons.ipc.demoflow
import tech.pylons.wallet.core.internal.HttpWire
import org.junit.jupiter.api.Test

class FirstDemoflowTest {
    val core = Core(Config(Backend.LIVE_DEV, listOf("http://127.0.0.1"))).use()

    private fun engineSetup (key : String? = null) : TxPylonsDevEngine {
        HttpWire.verbose = true
        core.start("")
        val engine = core.engine as TxPylonsDevEngine
        engine.cryptoHandler = engine.getNewCryptoHandler() as CryptoCosmos
        if (key != null) {
            println("Key is not null")
            core.userData.dataSets["__CRYPTO_COSMOS__"] = mutableMapOf("key" to key)
            engine.cryptoHandler.importKeysFromUserData()
        }
        else engine.cryptoHandler.generateNewKeys()
        core.userProfile = MyProfile.getDefault(core)
        return engine
    }

    @ExperimentalUnsignedTypes
    @Test
    fun doDemoflow () {
        engineSetup()
        demoflow()
    }
}