package tech.pylons.wallet.core.engine

import tech.pylons.lib.types.Transaction
import tech.pylons.wallet.core.Core
import tech.pylons.lib.types.tx.msg.*
import tech.pylons.lib.types.tx.recipe.*
import tech.pylons.wallet.core.LowLevel
import tech.pylons.wallet.core.internal.HttpWire
import tech.pylons.lib.types.tx.Coin

class TxPylonsDevEngine(core : Core) : TxPylonsEngine (core) {

    override fun createCookbook(creator: String, ID : String, name: String, description: String, developer: String, version: String,
                                supportEmail: String, costPerBlock : Coin, enabled: Boolean) =
            handleTx {
                CreateCookbook(
                    creator = creator,
                    ID = ID,
                    name = name,
                    description = description,
                    developer = developer,
                    version = version,
                    supportEmail = supportEmail,
                    costPerBlock = costPerBlock,
                    enabled = enabled
                ).toSignedTx()
            }

    override fun createRecipe(
        creator: String,
        cookbookID: String,
        ID: String,
        name: String,
        description: String,
        version: String,
        coinInputs: List<CoinInput>,
        itemInputs: List<ItemInput>,
        entries: EntriesList,
        outputs: List<WeightedOutput>,
        blockInterval: Long,
        enabled: Boolean,
        extraInfo: String
    ): Transaction =
            handleTx {
                CreateRecipe(
                    creator = creator,
                    cookbookID = cookbookID,
                    ID = ID,
                    name = name,
                    description = description,
                    version = version,
                    coinInputs = coinInputs,
                    itemInputs = itemInputs,
                    entries = entries,
                    outputs = outputs,
                    blockInterval = blockInterval,
                    enabled = enabled,
                    extraInfo = extraInfo,
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

    override fun updateCookbook(
        Creator: String,
        ID: String,
        Name: String,
        Description: String,
        Developer: String,
        Version: String,
        SupportEmail : String,
        CostPerBlock: Coin,
        Enabled: Boolean
    ): Transaction =
            handleTx {
                UpdateCookbook(
                    Creator = Creator,
                    ID = ID,
                    Name = Name,
                    Description = Description,
                    Developer = Developer,
                    Version = Version,
                    SupportEmail = SupportEmail,
                    CostPerBlock = CostPerBlock,
                    Enabled = Enabled
                ).toSignedTx()
            }

    override fun updateRecipe(Creator: String, CookbookID : String, ID : String, Name : String, Description: String,
                              Version: String, CoinInputs : List<CoinInput>, ItemInputs : List<ItemInput>,
                              Entries : EntriesList, Outputs: List<WeightedOutput>, BlockInterval : Long, Enabled: Boolean, ExtraInfo: String) =
            handleTx {
                UpdateRecipe(
                    Creator = Creator,
                    CookbookID = CookbookID,
                    ID = ID,
                    Name = Name,
                    Description = Description,
                    Version = Version,
                    CoinInputs = CoinInputs,
                    ItemInputs = ItemInputs,
                    Entries = Entries,
                    Outputs = Outputs,
                    BlockInterval = BlockInterval,
                    Enabled = Enabled,
                    ExtraInfo = ExtraInfo
                ).toSignedTx()
            }

    fun queryTxBuilder(msgType : String) : String = HttpWire.get("${LowLevel.getUrlForQueries()}/pylons/$msgType/tx_build/0")
}