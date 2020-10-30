package com.pylons.wallet.core.engine

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.tx.msg.*
import com.pylons.wallet.core.types.tx.recipe.*

class TxPylonsDevEngine(core : Core) : TxPylonsEngine (core) {
    override val isDevEngine: Boolean = true
    override val backendType: Backend = Backend.LIVE_DEV

    override fun createCookbook(id : String, name: String, developer: String, description: String, version: String,
                                supportEmail: String, level: Long, costPerBlock : Long) =
            handleTx {
                CreateCookbook(
                        cookbookId = id,
                        name = name,
                        developer = developer,
                        description = description,
                        version = version,
                        supportEmail = supportEmail,
                        level = level,
                        costPerBlock = costPerBlock,
                        sender = it.address
                ).toSignedTx()
            }

    override fun createRecipe(name : String, cookbookId : String, description: String, blockInterval : Long,
                              coinInputs : List<CoinInput>, itemInputs : List<ItemInput>, entries : EntriesList,
                              outputs : List<WeightedOutput>) =
            handleTx {
                CreateRecipe(
                        cookbookId = cookbookId,
                        name = name,
                        description = description,
                        coinInputs = coinInputs,
                        itemInputs = itemInputs,
                        entries = entries,
                        outputs = outputs,
                        blockInterval = blockInterval,
                        sender = it.address
                ).toSignedTx()
            }

    override fun disableRecipe(id: String) =
            handleTx {
                DisableRecipe(
                        recipeId = id,
                        sender = it.address
                ).toSignedTx()
            }

    override fun enableRecipe(id: String) =
            handleTx {
                EnableRecipe(
                        recipeId = id,
                        sender = it.address
                ).toSignedTx()
            }

    override fun updateCookbook(id : String, developer: String, description: String, version: String,
                                supportEmail: String) =
            handleTx {
                UpdateCookbook(
                        id = id,
                        developer = developer,
                        description = description,
                        version = version,
                        supportEmail = supportEmail,
                        sender = it.address
                ).toSignedTx()
            }

    override fun updateRecipe(id : String, name : String, cookbookId : String, description: String,
                              blockInterval : Long, coinInputs : List<CoinInput>, itemInputs : List<ItemInput>,
                              entries : EntriesList, outputs: List<WeightedOutput>) =
            handleTx {
                UpdateRecipe(
                        id = id,
                        cookbookId = cookbookId,
                        name = name,
                        description = description,
                        itemInputs = itemInputs,
                        coinInputs = coinInputs,
                        entries = entries,
                        outputs = outputs,
                        blockInterval = blockInterval,
                        sender = it.address
                ).toSignedTx()
            }

    fun queryTxBuilder(msgType : String) : String = HttpWire.get("$nodeUrl/pylons/$msgType/tx_build/")
}