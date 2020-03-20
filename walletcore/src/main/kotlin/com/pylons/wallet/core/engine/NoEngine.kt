package com.pylons.wallet.core.engine

import com.pylons.wallet.core.engine.crypto.CryptoHandler
import com.pylons.wallet.core.engine.crypto.CryptoNull
import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.Execution
import com.pylons.wallet.core.types.tx.recipe.*

/**
 * Engine that throws NoEngineException on calling any function.
 * We don't want Core.Engine to be nullable, but it needs to be initialized to something
 * before Core.Start() is called, so it's initialized to this.
 */
internal class NoEngine : Engine() {
    override val prefix: String = "__NOENGINE__"
    override val backendType: Backend = Backend.NONE
    override var cryptoHandler: CryptoHandler = CryptoNull()
    override val usesMnemonic: Boolean = false
    override val isDevEngine: Boolean = false
    override val isOffLineEngine: Boolean = false

    class NoEngineException : Exception("Core.engine is set to NoEngine. Initialize engine before calling engine methods.")

    override fun applyRecipe(id : String, itemIds : Array<String>) : Transaction =
            throw NoEngineException()

    override fun createCookbook(id : String, name: String, developer: String, description: String, version: String, supportEmail: String, level: Long, costPerBlock : Long): Transaction =
            throw NoEngineException()

    override fun createRecipe(sender : String, name : String, cookbookId : String, description: String, blockInterval : Long,
                              coinInputs : List<CoinInput>, itemInputs : List<ItemInput>, entries : WeightedParamList,
                              rType : Long, toUpgrade : ItemUpgradeParams) : Transaction =
            throw NoEngineException()

    override fun disableRecipe(id: String): Transaction  =
            throw NoEngineException()

    override fun dumpCredentials(credentials: Profile.Credentials) =
            throw NoEngineException()

    override fun enableRecipe(id: String): Transaction  =
            throw NoEngineException()

    override fun generateCredentialsFromKeys(): Profile.Credentials =
            throw NoEngineException()

    override fun generateCredentialsFromMnemonic(mnemonic: String, passphrase: String) =
            throw NoEngineException()

    override fun getForeignBalances(id: String): ForeignProfile? =
            throw NoEngineException()

    override fun getNewCredentials(): Profile.Credentials =
            throw NoEngineException()

    override fun getInitialDataSets(): MutableMap<String, MutableMap<String, String>> =
            throw NoEngineException()

    override fun getNewCryptoHandler(): CryptoHandler =
            throw NoEngineException()

    override fun getOwnBalances(): Profile? =
            throw NoEngineException()

    override fun getPendingExecutions(): List<Execution> =
            throw NoEngineException()

    override fun getPylons(q: Long): Transaction =
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

    override fun sendPylons(q: Long, receiver: String) =
            throw NoEngineException()

    override fun updateCookbook(id: String, developer: String, description: String, version: String, supportEmail: String): Transaction =
            throw NoEngineException()

    override fun updateRecipe(id : String, sender : String, name : String, cookbookId : String, description: String, blockInterval : Long,
                              coinInputs : List<CoinInput>, itemInputs : List<ItemInput>, entries : WeightedParamList): Transaction =
            throw NoEngineException()
}