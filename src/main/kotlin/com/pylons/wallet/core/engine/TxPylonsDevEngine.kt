package com.pylons.wallet.core.engine

import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.item.prototype.ItemPrototype
import com.pylons.wallet.core.types.jsonTemplate.*

internal class TxPylonsDevEngine : TxPylonsEngine () {
    override val isDevEngine: Boolean = true
    override val backendType: Backend = Backend.LIVE_DEV

    override fun createCookbook(name: String, devel: String, desc: String, version: String, supportEmail: String, level: Long): Transaction =
            basicTxHandlerFlow { createCookbook(name, devel, desc, version, supportEmail, level, it.address,
                    cryptoHandler.keyPair!!.publicKey(), it.accountNumber, it.sequence) }

    override fun createRecipe(name : String, cookbookName: String, desc: String, coinInputs: Map<String, Long>,
                              itemInputs : Array<ItemPrototype>, entries : ParamSet, time: Long): Transaction =
            basicTxHandlerFlow { createRecipe(name, cookbookName, desc, coinInputs, itemInputs, entries, time,
                    it.address, cryptoHandler.keyPair!!.publicKey(), it.accountNumber, it.sequence) }

    override fun disableRecipe(id: String): Transaction =
            basicTxHandlerFlow { disableRecipe(id, it.address, cryptoHandler.keyPair!!.publicKey(),
                    it.accountNumber, it.sequence) }

    override fun enableRecipe(id: String): Transaction =
            basicTxHandlerFlow { enableRecipe(id, it.address, cryptoHandler.keyPair!!.publicKey(),
                    it.accountNumber, it.sequence) }

    override fun updateCookbook(id : String, devel: String, desc: String, version: String, supportEmail: String): Transaction =
            basicTxHandlerFlow { updateCookbook(id, devel, desc, version, supportEmail, it.address,
                    cryptoHandler.keyPair!!.publicKey(), it.accountNumber, it.sequence) }

    override fun updateRecipe(name: String, cookbookName: String, id: String, desc: String, coinInputs: Map<String, Long>, coinOutputs: Map<String, Long>,
                              itemInputs : Array<ItemPrototype>, itemOutputs : Array<ItemPrototype>, time: Long): Transaction =
            basicTxHandlerFlow { updateRecipe(name, cookbookName, id, desc, coinInputs, coinOutputs, itemInputs, itemOutputs, time,
                    it.address, cryptoHandler.keyPair!!.publicKey(), it.accountNumber, it.sequence) }

    fun queryTxBuilder(msgType : String) : String = HttpWire.get("$nodeUrl/pylons/$msgType/tx_build/")
}