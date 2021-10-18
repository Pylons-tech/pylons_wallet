package tech.pylons.lib.core
import tech.pylons.lib.types.*
import tech.pylons.lib.types.tx.Coin
import tech.pylons.lib.types.tx.Trade
import tech.pylons.lib.types.tx.item.Item
import tech.pylons.lib.types.tx.recipe.*
import tech.pylons.lib.types.tx.trade.ItemRef

@ExperimentalUnsignedTypes
interface ICore {
    companion object {
        var current : ICore? = null
            private set
    }

    val userData : UserData
    val lowLevel : ILowLevel
    var engine: IEngine
    var userProfile: MyProfile?
    var sane : Boolean
    var started : Boolean
    var suspendedAction : String?
    var statusBlock : StatusBlock
    var onWipeUserData : (() -> Unit)?

    /**
     * Serializes persistent user data as a JSON string. All wallet apps will need to take care of calling
     * backupUserData() and storing the results in local storage on their own.
     */
    fun backupUserData () : String?

    fun setProfile (myProfile: MyProfile)

    fun forceKeys (keyString : String, address : String)

    fun dumpKeys () : List<String>

    fun updateStatusBlock ()

    fun use() : ICore

    fun start (userJson : String)

    var onCompletedOperation : (() -> Unit)?

    fun isReady () : Boolean {
        return sane && started
    }

    fun getProfile (addr : String?) : Profile?

    fun applyRecipe (creator: String , cookbookID: String, id: String, coinInputsIndex: Long, itemIds : List<String>) : Transaction

    fun batchCreateCookbook (creators: List<String>, ids : List<String>, names : List<String>, descriptions : List<String>, developers : List<String>, versions : List<String>,
                             supportEmails : List<String>, costsPerBlocks : List<Coin>, enableds : List<Boolean>) : List<Transaction>

    fun batchCreateRecipe (creators: List<String>, cookbooks : List<String>, ids : List<String>, names : List<String>, descriptions : List<String>, versions : List<String>,
                           coinInputs: List<List<CoinInput>>, itemInputs : List<List<ItemInput>>, outputTables : List<EntriesList>, outputs : List<List<WeightedOutput>>,
                           blockIntervals : List<Long>, enableds : List<Boolean>, extraInfos: List<String>) : List<Transaction>

    fun batchDisableRecipe (recipes : List<String>) : List<Transaction>

    fun batchEnableRecipe (recipes : List<String>) : List<Transaction>

    fun batchUpdateCookbook (creators : List<String>, ids : List<String>, names : List<String>, descriptions: List<String>, developers: List<String>,
                             versions : List<String>, supportEmails: List<String>, costPerBlocks: List<Coin>, enableds: List<Boolean>) : List<Transaction>

    fun batchUpdateRecipe (creators: List<String>, cookbookIds: List<String>, ids: List<String>, names : List<String>, descriptions: List<String>,
                           versions: List<String>, coinInputs : List<String>, itemInputs : List<String>,
                           entries : List<String>, outputs: List<String>, blockIntervals : List<Long>,
                           enableds: List<Boolean>, extraInfos: List<String>) : List<Transaction>

    fun cancelTrade(tradeId : String) : Transaction

    fun checkExecution(id : String, payForCompletion : Boolean) : Transaction

    fun createTrade (creator: String, coinInputs: List<CoinInput>, itemInputs : List<ItemInput>,
                     coinOutputs : List<Coin>, itemOutputs : List<ItemRef>,
                     extraInfo : String) : Transaction

    fun fulfillTrade(creator: String, ID : String, CoinInputsIndex: Long, Items : List<ItemRef>) : Transaction

    fun getCookbooks () : List<Cookbook>

    fun getPylons(amount : Long, creator: String) : Boolean

    fun getPendingExecutions () : List<Execution>

    fun getRecipes () : List<Recipe>

    fun getTransaction(txHash : String): Transaction

    fun googleIapGetPylons (productId: String, purchaseToken : String, receiptData : String,
                            signature : String) : Transaction

    fun newProfile (name : String, kp : PylonsSECP256K1.KeyPair? = null) : Transaction

    fun setItemString (itemId : ItemRef, field : String, value : String) : Transaction

    fun walletServiceTest(string: String): String

    fun walletUiTest() : String

    fun wipeUserData ()

    fun listCompletedExecutions () : List<Execution>

    fun listTrades (creator: String) : List<Trade>

    fun buildJsonForTxPost(msg: String, signComponent: String, accountNumber: Long, sequence: Long, pubkey: PylonsSECP256K1.PublicKey, gas: Long) : String

    fun getRecipe(recipeId: String): Recipe?

    fun getRecipesByCookbook(cookbookId: String): List<Recipe>

    fun getRecipesBySender() : List<Recipe>

    fun getTrade(tradeId: String) : Trade?

    fun getItem(itemId: String): Item?

    fun listItems() : List<Item>

    fun listItemsBySender(sender: String?) : List<Item>

    fun listItemsByCookbookId(cookbookId: String?): List<Item>

    fun getCookbook(cookbookId: String): Cookbook?

    fun getExecution(executionId: String): Execution?
    /**
     * Returns the on-chain ID of the recipe with the cookbook and name provided
     */
    fun getRecipeIdFromCookbookAndName(cookbook: String, name: String) : String?
}