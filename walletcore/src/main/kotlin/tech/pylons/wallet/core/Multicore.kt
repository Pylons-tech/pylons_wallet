package tech.pylons.wallet.core

import tech.pylons.lib.PubKeyUtil
import tech.pylons.lib.core.IMulticore
import tech.pylons.lib.types.Config
import tech.pylons.lib.types.LockedCoinDetails
import tech.pylons.lib.types.MyProfile
import tech.pylons.lib.types.PylonsSECP256K1
import tech.pylons.lib.types.credentials.CosmosCredentials
import tech.pylons.wallet.core.engine.TxPylonsEngine
import tech.pylons.wallet.core.engine.crypto.CryptoCosmos

object Multicore : IMulticore {
    @ExperimentalUnsignedTypes
    private val loadedCores : MutableMap<String, Core> = mutableMapOf()

    override fun enable (cfg : Config) {
        if (IMulticore.config != null) throw Exception("Shouldn't be calling Multicore.enable() - multicore is already enabled")
        IMulticore.config = cfg
        IMulticore.instance = this
    }

    @ExperimentalUnsignedTypes
    override fun addCore (kp: PylonsSECP256K1.KeyPair?) : Core {
        var keyPair = kp ?: PylonsSECP256K1.KeyPair.random()
        val core = Core(IMulticore.config!!)
        val credentials = CosmosCredentials(TxPylonsEngine.getAddressString(PubKeyUtil.getAddressFromKeyPair(keyPair).toArray()))
        core.userProfile = MyProfile(
                core = core,
                credentials = credentials,
                lockedCoinDetails = LockedCoinDetails.default,
                strings = mapOf(),
                items = listOf(),
                coins = listOf()
        )
        loadedCores[credentials.address] = core
        core.use()
        core.start("")
        val cc = CryptoCosmos(Core.current!!)
        cc.keyPair = keyPair
        Core.current!!.engine.cryptoHandler = cc
        return core
    }

    @ExperimentalUnsignedTypes
    override fun switchCore (address : String) : Core {
        return when (loadedCores[address]) {
            null -> throw Exception("No loaded org.bitcoinj.core.core w/ address of $address")
            else -> loadedCores[address]!!.use()
        }
    }
}