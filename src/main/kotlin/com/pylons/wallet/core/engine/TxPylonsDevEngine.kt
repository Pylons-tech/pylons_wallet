package com.pylons.wallet.core.engine

import com.pylons.wallet.core.types.Backend
import com.pylons.wallet.core.types.HttpWire
import com.pylons.wallet.core.types.Transaction
import com.pylons.wallet.core.types.TxJson

internal class TxPylonsDevEngine : TxPylonsEngine () {
    override val isDevEngine: Boolean = true
    override val backendType: Backend = Backend.LIVE_DEV

    override fun createCookbook(name: String, devel: String, desc: String, version: String, supportEmail: String, level: Long): Transaction =
            basicTxHandlerFlow { TxJson.createCookbook(name, devel, desc, version, supportEmail, level, it.address,
                    cryptoHandler.keyPair!!.publicKey(), it.accountNumber, it.sequence) }

    override fun updateCookbook(id : String, devel: String, desc: String, version: String, supportEmail: String): Transaction =
            basicTxHandlerFlow { TxJson.updateCookbook(id, devel, desc, version, supportEmail, it.address,
                    cryptoHandler.keyPair!!.publicKey(), it.accountNumber, it.sequence) }

    override fun createRecipe(name : String, cookbookName: String, desc: String, inputs: Map<String, Long>, outputs: Map<String, Long>, time: Long): Transaction =
            basicTxHandlerFlow { TxJson.createRecipe(name, cookbookName, desc, inputs, outputs, time,
                    it.address, cryptoHandler.keyPair!!.publicKey(), it.accountNumber, it.sequence) }

    override fun updateRecipe(name: String, cookbookName: String, id: String, desc: String, inputs: Map<String, Long>, outputs: Map<String, Long>, time: Long): Transaction =
            basicTxHandlerFlow { TxJson.updateRecipe(name, cookbookName, id, desc, inputs, outputs, time,
                    it.address, cryptoHandler.keyPair!!.publicKey(), it.accountNumber, it.sequence) }

    override fun disableRecipe(id: String): Transaction =
            basicTxHandlerFlow { TxJson.disableRecipe(id, it.address, cryptoHandler.keyPair!!.publicKey(),
                    it.accountNumber, it.sequence) }

    override fun enableRecipe(id: String): Transaction =
            basicTxHandlerFlow { TxJson.enableRecipe(id, it.address, cryptoHandler.keyPair!!.publicKey(),
                    it.accountNumber, it.sequence) }

    fun queryTxBuilder(msgType : String) : String = HttpWire.get("$url/pylons/$msgType/tx_build/")
}