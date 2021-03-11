package com.pylons.wallet.core

import com.pylons.wallet.core.engine.TxPylonsEngine
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.lib.types.types.Config
import com.pylons.lib.types.types.LockedCoinDetails
import com.pylons.lib.types.types.MyProfile
import com.pylons.lib.types.types.PylonsSECP256K1

object Multicore {
    private var config : Config? = null
    private val loadedCores : MutableMap<String, Core> = mutableMapOf()

    val enabled get() = config != null

    fun enable (cfg : Config) {
        if (config != null) throw Exception("Shouldn't be calling Multicore.enable() - multicore is already enabled")
        config = cfg
    }

    fun addCore (kp: PylonsSECP256K1.KeyPair?) : Core {
        var keyPair = kp ?: PylonsSECP256K1.KeyPair.random()
        val core = Core(config!!)
        val credentials = TxPylonsEngine.Credentials(TxPylonsEngine.getAddressString(CryptoCosmos.getAddressFromKeyPair(keyPair).toArray()))
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
        keyPair.use()
        return core
    }

    fun switchCore (address : String) : Core {
        return when (loadedCores[address]) {
            null -> throw Exception("No loaded core w/ address of $address")
            else -> loadedCores[address]!!.use()
        }
    }
}