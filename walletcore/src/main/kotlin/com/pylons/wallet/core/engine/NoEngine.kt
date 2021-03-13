package com.pylons.wallet.core.engine

import com.pylons.lib.core.IEngine
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.crypto.CryptoHandler
import com.pylons.wallet.core.engine.crypto.CryptoNull
import com.pylons.lib.types.types.*
import com.pylons.lib.types.types.Execution
import com.pylons.lib.types.types.tx.Trade
import com.pylons.lib.types.types.tx.item.Item
import com.pylons.lib.types.types.tx.recipe.*
import com.pylons.lib.types.types.tx.trade.TradeItemInput

/**
 * Engine that throws NoEngineException on calling any function.
 * We don't want Core.Engine to be nullable, but it needs to be initialized to something
 * before Core.Start() is called, so it's initialized to this.
 */
internal class NoEngine(core : Core) : Engine(core), IEngine {
    override val prefix: String = "__NOENGINE__"
    override val backendType: Backend = Backend.NONE
    override var cryptoHandler: CryptoHandler = CryptoNull(core)
    override val usesMnemonic: Boolean = false
    override val isDevEngine: Boolean = false

    class NoEngineException : Exception("Core.engine is set to NoEngine. Initialize engine before calling engine methods.")

    override fun applyRecipe(id : String, itemIds : List<String>) : Transaction =
            throw NoEngineException()

    override fun checkExecution(id: String, payForCompletion : Boolean): Transaction =
            throw NoEngineException()

    override fun createCookbook(id : String, name: String, developer: String, description: String, version: String, supportEmail: String, level: Long, costPerBlock : Long): Transaction =
            throw NoEngineException()

    override fun createRecipe(name : String, cookbookId : String, description: String, blockInterval : Long,
                              coinInputs : List<CoinInput>, itemInputs : List<ItemInput>, entries : EntriesList,
                              outputs : List<WeightedOutput>) : Transaction =
            throw NoEngineException()

    override fun createTrade(coinInputs: List<CoinInput>, itemInputs: List<TradeItemInput>,
                             coinOutputs: List<Coin>, itemOutputs: List<Item>, ExtraInfo: String)   =
            throw NoEngineException()

    override fun disableRecipe(id: String): Transaction  =
            throw NoEngineException()

    override fun dumpCredentials(credentials: MyProfile.Credentials) =
            throw NoEngineException()

    override fun enableRecipe(id: String): Transaction  =
            throw NoEngineException()

    override fun fulfillTrade(tradeId: String, itemIds : List<String>): Transaction =
            throw NoEngineException()

    override fun cancelTrade(tradeId: String): Transaction =
            throw NoEngineException()

    override fun generateCredentialsFromKeys(): MyProfile.Credentials =
            throw NoEngineException()

    override fun generateCredentialsFromMnemonic(mnemonic: String, passphrase: String) =
            throw NoEngineException()

    override fun getNewCredentials(): MyProfile.Credentials =
            throw NoEngineException()

    override fun getProfileState(addr: String) = throw NoEngineException()

    override fun getInitialDataSets() = throw NoEngineException()

    override fun getNewCryptoHandler() = throw NoEngineException()

    override fun getMyProfileState() = throw NoEngineException()

    override fun getPendingExecutions(): List<Execution> =
            throw NoEngineException()

    override fun getPylons(q: Long): Transaction =
            throw NoEngineException()

    override fun googleIapGetPylons(productId: String, purchaseToken: String, receiptDataBase64: String, signature: String): Transaction =
            throw NoEngineException()

    override fun checkGoogleIapOrder(purchaseToken: String): Boolean =
            throw NoEngineException()

    override fun getStatusBlock(): StatusBlock  =
            throw NoEngineException()

    override fun getTransaction(id: String): Transaction =
            throw NoEngineException()

    override fun listRecipes(): List<Recipe> =
            throw NoEngineException()

    override fun listCookbooks(): List<Cookbook> =
            throw NoEngineException()

    override fun registerNewProfile(name : String, kp : PylonsSECP256K1.KeyPair?): Transaction =
            throw NoEngineException()

    override fun sendCoins(coins : List<Coin>, receiver: String) =
            throw NoEngineException()

    override fun createChainAccount(): Transaction =
            throw NoEngineException()

    override fun setItemFieldString(itemId : String, field : String, value : String): Transaction =
            throw NoEngineException()

    override fun updateCookbook(id: String, developer: String, description: String, version: String, supportEmail: String): Transaction =
            throw NoEngineException()

    override fun updateRecipe(id : String, name : String, cookbookId : String, description: String, blockInterval : Long,
                              coinInputs : List<CoinInput>, itemInputs : List<ItemInput>, entries : EntriesList, outputs: List<WeightedOutput>): Transaction =
            throw NoEngineException()

    override fun listTrades(): List<Trade> =
            throw NoEngineException()

    override fun sendItems(receiver: String, itemIds: List<String>): Transaction =
            throw NoEngineException()

    override fun getLockedCoins(): LockedCoin =
        throw NoEngineException()

    override fun getLockedCoinDetails(): LockedCoinDetails =
            throw NoEngineException()


}