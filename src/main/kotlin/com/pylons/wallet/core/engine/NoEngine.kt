package com.pylons.wallet.core.engine

import com.pylons.wallet.core.engine.crypto.CryptoHandler
import com.pylons.wallet.core.types.ForeignProfile
import com.pylons.wallet.core.types.Profile
import com.pylons.wallet.core.types.Transaction
import com.pylons.wallet.core.types.UserData

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

    override fun applyRecipe(cookbook: String, recipe: String, preferredItemIds: List<String>): Profile? =
            throw NoEngineException()

    override fun bootstrap() =
            throw NoEngineException()

    override fun commitTx(tx: Transaction): Profile? =
            throw NoEngineException()

    override fun getAverageBlockTime(): Double =
            throw NoEngineException()

    override fun dumpCredentials(credentials: Profile.Credentials) =
            throw NoEngineException()

    override fun getNewCredentials(): Profile.Credentials =
            throw NoEngineException()

    override fun getHeight(): Long =
            throw NoEngineException()

    override fun getForeignBalances(id: String): ForeignProfile? =
            throw NoEngineException()

    override fun getOwnBalances(): Profile? =
            throw NoEngineException()

    override fun getNewCryptoHandler(): CryptoHandler =
            throw NoEngineException()

    override fun getNewTransactionId(): String =
            throw NoEngineException()

    override fun getNewUserId(): String =
            throw NoEngineException()

    override fun getTransaction(id: String): Transaction? =
            throw NoEngineException()

    override fun registerNewProfile(): Profile? =
            throw NoEngineException()

    override fun getPylons(q: Int): Profile? =
            throw NoEngineException()
}