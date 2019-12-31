package com.pylons.wallet.core.engine

import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.item.prototype.ItemPrototype
import com.pylons.wallet.core.types.jsonTemplate.*
import com.pylons.wallet.core.types.tx.msg.CreateRecipe
import com.pylons.wallet.core.types.tx.recipe.*

internal class TxPylonsDevEngine : TxPylonsEngine () {
    override val isDevEngine: Boolean = true
    override val backendType: Backend = Backend.LIVE_DEV

    override fun createCookbook(name: String, devel: String, desc: String, version: String, supportEmail: String, level: Long, costPerBlock : Long): Transaction =
            basicTxHandlerFlow { createCookbook(name, devel, desc, version, supportEmail, level, it.address,
                    cryptoHandler.keyPair!!.publicKey(), it.accountNumber, it.sequence, costPerBlock) }

    override fun createRecipe(sender : String, name : String, cookbookId : String, description: String, blockInterval : Long,
                              coinInputs : List<CoinInput>, itemInputs : List<ItemInput>, entries : WeightedParamList,
                              rType : Long, toUpgrade : ItemUpgradeParams) : Transaction =
    basicTxHandlerFlow { CreateRecipe(blockInterval, coinInputs, cookbookId, description, entries, itemInputs, name, sender, rType, toUpgrade).toSignedTx() }

    override fun disableRecipe(id: String): Transaction =
            basicTxHandlerFlow { disableRecipe(id, it.address, cryptoHandler.keyPair!!.publicKey(),
                    it.accountNumber, it.sequence) }

    override fun enableRecipe(id: String): Transaction =
            basicTxHandlerFlow { enableRecipe(id, it.address, cryptoHandler.keyPair!!.publicKey(),
                    it.accountNumber, it.sequence) }

    override fun updateCookbook(id : String, devel: String, desc: String, version: String, supportEmail: String): Transaction =
            basicTxHandlerFlow { updateCookbook(id, devel, desc, version, supportEmail, it.address,
                    cryptoHandler.keyPair!!.publicKey(), it.accountNumber, it.sequence) }

    override fun updateRecipe(id : String, sender : String, name : String, cookbookId : String, description: String, blockInterval : Long,
                              coinInputs : List<CoinInput>, itemInputs : List<ItemInput>, entries : WeightedParamList): Transaction =
            basicTxHandlerFlow { UpdateRecipe(blockInterval, coinInputs, cookbookId, description, entries, id, itemInputs, name, sender).toSignedTx() }

    fun queryTxBuilder(msgType : String) : String = HttpWire.get("$nodeUrl/pylons/$msgType/tx_build/")
}