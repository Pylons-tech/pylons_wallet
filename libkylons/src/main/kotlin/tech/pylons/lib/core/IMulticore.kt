package tech.pylons.lib.core

import tech.pylons.lib.types.Config
import tech.pylons.lib.types.PylonsSECP256K1

interface IMulticore {
    companion object {
        val enabled get() = config != null
        var config : Config? = null
        var instance : IMulticore? = null
    }


    fun enable (cfg : Config)

    fun addCore (kp: PylonsSECP256K1.KeyPair?) : ICore

    fun switchCore (address : String) : ICore
}