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

    override val usesCrypto: Boolean = false
    override val isDevEngine: Boolean = false
    override val isOffLineEngine: Boolean = false

    class NoEngineException : Exception("Core.engine is set to NoEngine. Initialize engine before calling engine methods.")

    override fun getStatusBlock(): StatusBlock  =
            throw NoEngineException()

    override fun applyRecipe(cookbook: String, recipe: String, preferredItemIds: List<String>): Profile? =
            throw NoEngineException()

    override fun commitTx(tx: Transaction): Profile? =
            throw NoEngineException()


    override fun dumpCredentials(credentials: Profile.Credentials) =
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

    override fun getTransaction(id: String): Transaction? =
            throw NoEngineException()

    override fun registerNewProfile(): Profile? =
            throw NoEngineException()

    override fun getPylons(q: Int): Profile? =
            throw NoEngineException()

    override fun getInitialDataSets(): MutableMap<String, MutableMap<String, String>> =
            throw NoEngineException()

}