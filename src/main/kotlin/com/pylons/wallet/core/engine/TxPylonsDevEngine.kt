package com.pylons.wallet.core.engine

import com.pylons.wallet.core.types.Backend
import com.pylons.wallet.core.types.Transaction
import com.pylons.wallet.core.types.TxJson

internal class TxPylonsDevEngine : TxPylonsEngine () {
    override val isDevEngine: Boolean = true
    override val backendType: Backend = Backend.LIVE_DEV

    override fun createCookbook(name: String, devel: String, desc: String, version: String, supportEmail: String, level: Int): Transaction =
            basicTxHandlerFlow { TxJson.createCookbook(name, devel, desc, version, supportEmail, level, it.address,
                    cryptoHandler.keyPair!!.publicKey(), it.accountNumber, it.sequence) }

    override fun updateCookbook(id : String, devel: String, desc: String, version: String, supportEmail: String): Transaction =
            basicTxHandlerFlow { TxJson.updateCookbook(id, devel, desc, version, supportEmail, it.address,
                    cryptoHandler.keyPair!!.publicKey(), it.accountNumber, it.sequence) }

    override fun createRecipe(id: String, cookbookName: String, desc: String, inputs: String, outputs: String, time: Int): Transaction =
            basicTxHandlerFlow { TxJson.createRecipe(id, cookbookName, desc, inputs, outputs, time,
                    it.address, cryptoHandler.keyPair!!.publicKey(), it.accountNumber, it.sequence) }
}