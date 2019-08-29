package com.pylons.wallet.core.engine

import com.pylons.wallet.core.engine.crypto.CryptoHandler
import com.pylons.wallet.core.types.*

/**
 * Engine that throws NoEngineException on calling any function.
 * We don't want Core.Engine to be nullable, but it needs to be initialized to something
 * before Core.Start() is called, so it's initialized to this.
 */
internal class NoEngine : Engine() {
    override val prefix: String = "__NOENGINE__"
    override val backendType: Backend = Backend.NONE
    override val usesCrypto: Boolean = false
    override val usesMnemonic: Boolean = false
    override val isDevEngine: Boolean = false
    override val isOffLineEngine: Boolean = false

    class NoEngineException : Exception("Core.engine is set to NoEngine. Initialize engine before calling engine methods.")

    override fun getStatusBlock(): StatusBlock  =
            throw NoEngineException()

    override fun applyRecipe(id : String, coinsIn: Map<String, Int>) : Transaction =
            throw NoEngineException()

    override fun commitTx(tx: Transaction): Transaction =
            throw NoEngineException()

    override fun createCookbook(name: String, devel: String, desc: String, version: String, supportEmail: String, level: Int): Transaction =
            throw NoEngineException()

    override fun dumpCredentials(credentials: Profile.Credentials) =
            throw NoEngineException()

    override fun generateCredentialsFromMnemonic(mnemonic: String, passphrase: String) =
            throw NoEngineException()

    override fun generateCredentialsFromKeys(): Profile.Credentials =
            throw NoEngineException()

    override fun getNewCredentials(): Profile.Credentials =
            throw NoEngineException()

    override fun getForeignBalances(id: String): ForeignProfile? =
            throw NoEngineException()

    override fun getOwnBalances(): Profile? =
            throw NoEngineException()

    override fun getNewCryptoHandler(): CryptoHandler =
            throw NoEngineException()

    override fun getTransaction(id: String): Transaction =
            throw NoEngineException()

    override fun registerNewProfile(name : String): Transaction =
            throw NoEngineException()

    override fun getPylons(q: Int): Transaction =
            throw NoEngineException()

    override fun getInitialDataSets(): MutableMap<String, MutableMap<String, String>> =
            throw NoEngineException()

    override fun sendPylons(q: Int, receiver: String) =
            throw NoEngineException()

    override fun updateCookbook(id: String, devel: String, desc: String, version: String, supportEmail: String): Transaction =
            throw NoEngineException()

    override fun createRecipe(name : String, cookbookName: String, desc: String, inputs: Map<String, Int>, outputs: Map<String, Int>, time: Int): Transaction =
            throw NoEngineException()

    override fun updateRecipe(name: String, cookbookName: String, id: String, desc: String, inputs: Map<String, Int>, outputs: Map<String, Int>, time: Int): Transaction =
            throw NoEngineException()

    override fun listRecipes(cookbook: String): Array<Recipe> =
            throw NoEngineException()
}