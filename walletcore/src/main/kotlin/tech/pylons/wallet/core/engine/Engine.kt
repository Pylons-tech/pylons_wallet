package tech.pylons.wallet.core.engine

import tech.pylons.lib.BigIntUtil
import tech.pylons.lib.core.ICryptoHandler
import tech.pylons.lib.core.IEngine
import tech.pylons.lib.types.*
import tech.pylons.lib.types.credentials.ICredentials
import tech.pylons.lib.types.tx.Coin
import tech.pylons.lib.types.tx.Trade
import tech.pylons.lib.types.tx.item.Item
import tech.pylons.lib.types.tx.recipe.*
import tech.pylons.lib.types.tx.trade.ItemRef
import tech.pylons.lib.types.tx.trade.TradeItemInput
import tech.pylons.wallet.core.Core

/***
 * Generic interface for transaction-handling layers.
 * Engine provides a suite of basic functionality that WalletCore as a whole
 * can use to handle transactions at a high level; individual TXHandler implementations
 * will do the dirty work of binding that functionality to low-level blockchain
 * systems, in effect acting as "drivers."
 */
abstract class Engine(val core : Core) : IEngine {
    /**
     * Identifier string, unique per Engine implementation.
     * Used to identify the engine type associated with a given dataset
     * when we dump the datastore to XML.
     */
    abstract override val prefix : String

    /** Identifies whether or not we're using BIP44 mnemonics when doing keygen. */
    abstract override val usesMnemonic : Boolean

    /** The current CryptoHandler instance associated with this engine */
    abstract override var cryptoHandler : ICryptoHandler

    /** Enable-recipe message */
    abstract override fun enableRecipe(id : String) : Transaction

    /** Batch enable-recipe message */
    override fun enableRecipes(recipes : List<String>) : List<Transaction> {
        val txs = mutableListOf<Transaction>()
        recipes.forEach { txs.add(enableRecipe(it)) }
        return txs
    }

    /** Disable-recipe message */
    abstract override fun disableRecipe(id : String) : Transaction

    /** Batch enable-recipe message */
    override fun disableRecipes(recipes : List<String>) : List<Transaction> {
        val txs = mutableListOf<Transaction>()
        recipes.forEach { txs.add(disableRecipe(it)) }
        return txs
    }

    /** Execute-recipe message */
    abstract override fun applyRecipe(creator: String , cookbookID: String, id: String, coinInputsIndex: Long, itemIds : List<String>, paymentInfos: PaymentInfo?) : Transaction

    /** Check-execution message */
    abstract override fun checkExecution(id : String, payForCompletion : Boolean) : Transaction

    /** Create-trade message */
    abstract override fun createTrade(creator: String, coinInputs: List<CoinInput>, itemInputs : List<ItemInput>,
                                      coinOutputs : List<Coin>, itemOutputs : List<ItemRef>,
                                      extraInfo : String) : Transaction

    /** Create-recipe message */
    abstract override fun createRecipe(creator : String, cookbookId : String, id : String, name : String, description: String, version: String,
                                       coinInputs : List<CoinInput>, itemInputs : List<ItemInput>, entries : EntriesList,
                                       outputs : List<WeightedOutput>, blockInterval : Long, enabled : Boolean, extraInfo: String) : Transaction

    /** Batch create-recipe message */
    override fun createRecipes(creators : List<String>, cookbookIds : List<String>, ids : List<String>, names : List<String>,
                               descriptions: List<String>, versions: List<String>, coinInputs : List<List<CoinInput>>,
                               itemInputs : List<List<ItemInput>>, entries : List<EntriesList>,
                               outputs: List<List<WeightedOutput>>, blockIntervals : List<Long>,
                               enableds : List<Boolean>, extraInfos: List<String>) : List<Transaction> {
        val count = names.size
        val txs = mutableListOf<Transaction>()
        for (i in 0  until count) {
            var biItemInputs = mutableListOf<ItemInput>()
            var biItemModifyOutputs = mutableListOf<ItemModifyOutput>()
            var biItemOutputs = mutableListOf<ItemOutput>()

            itemInputs[i].forEach {
                biItemInputs.add( BigIntUtil.toItemInput(it) )
            }
            entries[i].itemModifyOutputs?.forEach {
                biItemModifyOutputs.add(BigIntUtil.toItemModifyOutput(it))
            }
            entries[i].itemOutputs.forEach {
                biItemOutputs.add(BigIntUtil.toItemOutput(it))
            }

            var bi_outputTable = EntriesList(
                coinOutputs = entries[i].coinOutputs,
                itemModifyOutputs = biItemModifyOutputs.toList(),
                itemOutputs = biItemOutputs.toList()
            )

            txs.add(
                    createRecipe(
                        creator = creators[i],
                        cookbookId = cookbookIds[i],
                        id = ids[i],
                        name = names[i],
                        description = descriptions[i],
                        version = versions[i],
                        coinInputs = coinInputs[i],
                        itemInputs = biItemInputs,
                        entries = bi_outputTable,
                        outputs = outputs[i],
                        blockInterval = blockIntervals[i],
                        enabled = enableds[i],
                        extraInfo = extraInfos[i]
                    )
            )
        }
        return txs
    }

    /** Create-cookbook message */
    abstract override fun createCookbook (creator: String, id : String, name : String, description : String, developer : String, version : String,
                                          supportEmail : String, costPerBlock : Coin, enabled : Boolean) : Transaction

    /** Batch create-cookbook message */
    override fun createCookbooks(creators: List<String>, ids : List<String>, names : List<String>, descriptions: List<String>, developers: List<String>,
                        versions : List<String>, supportEmails: List<String>,
                        costsPerBlocks : List<Coin>, enableds: List<Boolean>) : List<Transaction> {
        val count = names.size
        val txs = mutableListOf<Transaction>()
        for (i in 0 until count) {
            txs.add(
                createCookbook(
                creator = creators[i],
                id = ids[i],
                name = names[i],
                description = descriptions[i],
                developer = developers[i],
                version = versions[i],
                supportEmail = supportEmails[i],
                costPerBlock = costsPerBlocks[i],
                enabled = enableds[i]
                )
            )
        }
        return txs
    }

    /**
     * Copies some data from profile's credentials object to userdata
     * for serialization.
     *  TODO: why does this actually exist?
     */
    abstract override fun dumpCredentials (credentials: ICredentials)

    abstract override fun fulfillTrade (creator: String, ID : Long, CoinInputsIndex: Long, itemIds : List<ItemRef>, paymentInfo: PaymentInfo?) : Transaction

    abstract override fun cancelTrade (creator : String, ID: Long) : Transaction
    /**
     * Generates a new Credentials object appropriate for our engine
     * type from the given mnemonic.
     */
    abstract override fun generateCredentialsFromMnemonic (mnemonic : String, passphrase : String) : ICredentials

    /**
     * Generates a new Credentials object appropriate for our engine
     * type from keys in userdata.
     */
    abstract override fun generateCredentialsFromKeys () : ICredentials

    /**
     * Creates new, default Credentials object appropriate for engine
     * type.
     */
    abstract override fun getNewCredentials () : ICredentials

    abstract override fun getProfileState (addr : String) : Profile?

    /** Get the balances of the user account. */
    @ExperimentalUnsignedTypes
    abstract override fun getMyProfileState () : MyProfile?

    abstract override fun getPendingExecutions () : List<Execution>

    /** Get a new instance of a CryptoHandler object appropriate for engine type. */
    abstract override fun getNewCryptoHandler() : ICryptoHandler

    /** Get the current status block. (Status block is returned w/ all IPC calls) */
    abstract override fun getStatusBlock() : StatusBlock

    /**
     * Retrieves transaction w/ the given ID.
     * (In an engine built to implement Cosmos functionality, this is the txhash)
     */
    abstract override fun getTransaction (id : String) : Transaction

    /** Registers a new profile under given name. */
    abstract override fun registerNewProfile (name : String, kp : PylonsSECP256K1.KeyPair?) : Transaction

    abstract override fun createChainAccount (name : String) : Transaction

    /** Calls Google IAP get pylons endpoint. */
    abstract override fun googleIapGetPylons(productId: String, purchaseToken: String, receiptData: String, signature: String): Transaction

    abstract override fun checkGoogleIapOrder(purchaseToken: String) : Boolean

    /** Gets initial userdata tables for the engine type. */
    abstract override fun getInitialDataSets () : MutableMap<String, MutableMap<String, String>>


    /** Update-cookbook message */
    abstract override fun updateCookbook (Creator: String,
                                          ID: String,
                                          Name: String,
                                          Description: String,
                                          Developer: String,
                                          Version: String,
                                          SupportEmail : String,
                                          CostPerBlock: Coin,
                                          Enabled: Boolean) : Transaction

    /** Batch update-cookbook message */
    override fun updateCookbooks(creators : List<String>, ids : List<String>, names : List<String>, descriptions: List<String>, developers: List<String>,
                                 versions : List<String>, supportEmails: List<String>, costPerBlocks: List<Coin>, enableds: List<Boolean>) : List<Transaction> {
        val count = names.size
        val txs = mutableListOf<Transaction>()
        for (i in 0  until count) {
            txs.add(
                    updateCookbook(
                        Creator = creators[i],
                        ID = ids[i],
                        Name = names[i],
                        Description = descriptions[i],
                        Developer = developers[i],
                        Version = versions[i],
                        SupportEmail = supportEmails[i],
                        CostPerBlock = costPerBlocks[i],
                        Enabled = enableds[i]
                    )
            )
        }
        return txs
    }

    /** Update-recipe message */
    abstract override fun updateRecipe(Creator: String, CookbookID : String, ID : String, Name : String, Description: String,
                                       Version: String, CoinInputs : List<CoinInput>, ItemInputs : List<ItemInput>,
                                       Entries : EntriesList, Outputs: List<WeightedOutput>, BlockInterval : Long, Enabled: Boolean, ExtraInfo: String) : Transaction

    /** Batch update-recipe message */
    override fun updateRecipes (creators: List<String>, cookbookIds: List<String>, ids: List<String>, names : List<String>, descriptions: List<String>,
                                Versions: List<String>, coinInputs : List<List<CoinInput>>, itemInputs : List<List<ItemInput>>,
                       entries : List<EntriesList>, outputs: List<List<WeightedOutput>>,blockIntervals : List<Long>,  enableds: List<Boolean>, extraInfos: List<String>) : List<Transaction> {
        val count = names.size
        val txs = mutableListOf<Transaction>()
        for (i in 0  until count) {
            txs.add(
                    updateRecipe(
                        Creator = creators[i],
                        CookbookID = cookbookIds[i],
                        ID = ids[i],
                        Name = names[i],
                        Description = descriptions[i],
                        Version = Versions[i],
                        CoinInputs = coinInputs[i],
                        ItemInputs = itemInputs[i],
                        Entries = entries[i],
                        Outputs = outputs[i],
                        BlockInterval = blockIntervals[i],
                        Enabled = enableds[i],
                        ExtraInfo = extraInfos[i]
                    )
            )
        }
        return txs
    }

    /** List recipes query */
    abstract override fun listRecipes () : List<Recipe>

    /** List cookbooks query */
    abstract override fun listCookbooks () : List<Cookbook>

    abstract override fun getPylons(amount : Long, creator: String) : Boolean

    abstract override fun setItemFieldString (itemId : String, field : String, value : String) : Transaction

    abstract override fun listTrades (creator: String) : List<Trade>

    abstract override fun sendItems(receiver: String, itemIds: List<String>) : Transaction

    abstract override fun getCompletedExecutions(): List<Execution>

    abstract override fun getItem(itemId: String): Item?

    abstract override fun getItem(itemId: String, cookbookId: String): Item?

    abstract override fun listItems(): List<Item>

    abstract override fun listItemsByCookbookId(cookbookId: String?): List<Item>

    abstract override fun listItemsBySender(sender: String?): List<Item>

}