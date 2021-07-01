package tech.pylons.lib.core
import tech.pylons.lib.types.*
import tech.pylons.lib.types.tx.Trade
import tech.pylons.lib.types.tx.recipe.Recipe

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

    fun applyRecipe (recipe : String, cookbook : String, itemInputs : List<String>, paymentId: String = "") : Transaction

    fun batchCreateCookbook (ids : List<String>, names : List<String>, developers : List<String>, descriptions : List<String>, versions : List<String>,
                             supportEmails : List<String>, levels : List<Long>, costsPerBlock : List<Long>) : List<Transaction>

    fun batchCreateRecipe (names : List<String>, cookbooks : List<String>, descriptions : List<String>,
                                    blockIntervals : List<Long>, coinInputs: List<String>, itemInputs : List<String>,
                                    outputTables : List<String>, outputs : List<String>, extraInfos: List<String>) : List<Transaction>

    fun batchDisableRecipe (recipes : List<String>) : List<Transaction>

    fun batchEnableRecipe (recipes : List<String>) : List<Transaction>

    fun batchUpdateCookbook (names : List<String>, developers : List<String>, descriptions : List<String>, versions : List<String>,
                             supportEmails : List<String>, ids : List<String>) : List<Transaction>

    fun batchUpdateRecipe (ids : List<String>, names : List<String>, cookbooks : List<String>, descriptions : List<String>,
                           blockIntervals : List<Long>, coinInputs: List<String>, itemInputs : List<String>,
                           outputTables : List<String>, outputs : List<String>, extraInfos: List<String>) : List<Transaction>

    fun cancelTrade(tradeId : String) : Transaction

    fun checkExecution(id : String, payForCompletion : Boolean) : Transaction

    fun createTrade (coinInputs: List<String>, itemInputs : List<String>,
                     coinOutputs : List<String>, itemOutputs : List<String>,
                     extraInfo : String) : Transaction

    fun fulfillTrade(tradeId : String, itemIds : List<String>) : Transaction

    fun getCookbooks () : List<Cookbook>

    fun getPendingExecutions () : List<Execution>

    fun getPylons (q : Long) : Transaction

    fun getRecipes () : List<Recipe>

    fun getTransaction(txHash : String): Transaction

    fun googleIapGetPylons (productId: String, purchaseToken : String, receiptData : String,
                            signature : String) : Transaction

    fun newProfile (name : String, kp : PylonsSECP256K1.KeyPair? = null) : Transaction

    fun sendCoins (coins : String, receiver : String) : Transaction

    fun setItemString (itemId : String, field : String, value : String) : Transaction

    fun walletServiceTest(string: String): String

    fun walletUiTest() : String

    fun wipeUserData ()

    fun listCompletedExecutions () : List<Execution>

    fun listTrades () : List<Trade>

    fun buildJsonForTxPost(msg: String, signComponent: String, accountNumber: Long, sequence: Long, pubkey: PylonsSECP256K1.PublicKey, gas: Long) : String

    fun getRecipe(recipeId: String): Recipe?

    fun getRecipesByCookbook(cookbookId: String): List<Recipe>

    fun getRecipesBySender() : List<Recipe>

    fun getTrade(tradeId: String) : Trade?
}