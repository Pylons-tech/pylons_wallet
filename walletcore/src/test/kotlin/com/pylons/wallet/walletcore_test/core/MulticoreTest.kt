package com.pylons.wallet.walletcore_test.core

import com.pylons.lib.PubKeyUtil
import com.pylons.lib.core.IMulticore
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.Multicore
import com.pylons.wallet.core.engine.TxPylonsEngine
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.lib.types.*
import com.pylons.lib.types.credentials.CosmosCredentials
import com.pylons.wallet.core.internal.InternalPrivKeyStore
import org.apache.tuweni.bytes.Bytes32
import org.spongycastle.jce.provider.BouncyCastleProvider
import org.junit.jupiter.api.*
import java.security.Security

@ExperimentalUnsignedTypes
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class MulticoreTest {
    init {
        // Ensure the BouncyCastleProvider is registered before doing anything further
        Security.removeProvider("BC")
        Security.addProvider(BouncyCastleProvider())
    }

    private val config = Config(
            Backend.LIVE_DEV,
            listOf("http://127.0.0.1:1317")
    )

    private val testKeys =
            PylonsSECP256K1.KeyPair.fromSecretKey(PylonsSECP256K1.SecretKey.fromBytes(Bytes32.fromHexString(InternalPrivKeyStore.NODE_GENERATED_PRIVKEY)))
    private val altTestKeys =
            PylonsSECP256K1.KeyPair.fromSecretKey(PylonsSECP256K1.SecretKey.fromBytes(Bytes32.fromHexString(
                InternalPrivKeyStore.TUWENI_FIXTURES_SECRET)))
    // todo: this is really ugly; we need to make this much lower-friction
    private val testCredentials = CosmosCredentials(TxPylonsEngine.getAddressString(PubKeyUtil.getAddressFromKeyPair(testKeys).toArray()))
    private val altTestCredentials = CosmosCredentials(TxPylonsEngine.getAddressString(PubKeyUtil.getAddressFromKeyPair(altTestKeys).toArray()))

    /**
     * Can we enable multi-org.bitcoinj.core.core?
     */
    @Test
    @Order(0)
    fun enableMulticore() {
        Multicore.enable(config)
        assert(IMulticore.enabled)
    }

    /**
     * Can we add a org.bitcoinj.core.core and verify that it has the correct credentials?
     */
    @Test
    @Order(1)
    fun addCore() {
        // todo: this should actually take a keypair as an argument
        val c = Multicore.addCore(testKeys)
        assert(Core.current == c)
        assert(c.userProfile!!.credentials.address == testCredentials.address)
    }

    /**
     * Can we use the newly-added org.bitcoinj.core.core to resolve operations?
     */
    @Test
    @Order(2)
    fun useAddedCoreForOperation() {
        val r = Core.current!!.walletServiceTest("fooBar")
        assert(r == "Wallet service test OK input fooBar")
    }

    /**
     * Can we add another org.bitcoinj.core.core and start using it?
     */
    @Test
    @Order(3)
    fun addAnotherCore() {
        val c = Multicore.addCore(altTestKeys)
        assert(Core.current == c)
        assert(c.userProfile!!.credentials.address == altTestCredentials.address)
        val r = Core.current!!.walletServiceTest("fooBar")
        assert(r == "Wallet service test OK input fooBar")
    }

    /**
     * Can we grab the first org.bitcoinj.core.core again and use it?
     */
    @Test
    @Order(4)
    fun getFirstCore() {
        val c = Multicore.switchCore(testCredentials.address)
        assert(Core.current == c)
        assert(c.userProfile!!.credentials.address == testCredentials.address)
        val r = Core.current!!.walletServiceTest("fooBar")
        assert(r == "Wallet service test OK input fooBar")
    }

    /**
     * Make sure we didn't break crypto doing the reference-juggling
     */
    @Test
    @Order(5)
    fun doCryptoWithRetrievedCore() {
        val tx = Core.current!!.newProfile("foo", testKeys)
        // We don't care if it's accepted, just that we can make a signature
        assert(tx.state == Transaction.State.TX_ACCEPTED || tx.state == Transaction.State.TX_REFUSED)
    }


}