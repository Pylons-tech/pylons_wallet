package com.pylons.wallet.core

import com.beust.klaxon.Klaxon
import kotlinx.coroutines.*

import com.pylons.wallet.core.constants.*
import com.pylons.wallet.core.internal.*
import com.pylons.wallet.core.engine.*
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.wallet.core.logging.LogEvent
import com.pylons.wallet.core.logging.LogTag
import com.pylons.wallet.core.logging.Logger
import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.tx.msg.Msg
import com.pylons.wallet.ipc.Message
import com.pylons.wallet.core.types.PylonsSECP256K1 as PylonsSECP256K1
import org.apache.tuweni.bytes.Bytes32
import org.spongycastle.util.encoders.Hex

/**
 * The number of times the core will retry valid-but-rejected transactions.
 * (For instance: if the remote profile doesn't have the resources to apply a recipe.)
 */
internal const val rejectedTxRetryTimes = 3
/**
 * The amount of time (in milliseconds) to wait before retrying such operations.
 */
internal const val retryDelay : Long = 500 // milliseconds

const val VERSION_STRING = "0.0.1a"

@ExperimentalUnsignedTypes
class Core(val config : Config) {
    companion object {
        var current : Core? = null
            private set
    }

    val userData = UserData(this)
    val lowLevel = LowLevel(this)
    var engine: Engine = NoEngine(this)
        private set
    var userProfile: MyProfile? = null
    internal var profilesBuffer : Set<Profile> = setOf()
    var sane : Boolean = false
        private set
    var started : Boolean = false
        private set
    var suspendedAction : String? = null
        internal set
    internal val klaxon = Klaxon()
    var statusBlock : StatusBlock = StatusBlock(-1, 0.0, VERSION_STRING)

    var onWipeUserData : (() -> Unit)? = null

    internal fun tearDown () {
        engine = NoEngine(this)
        userProfile = null
        sane = false
        started = false
        onCompletedOperation = null
    }

    /**
     * Serializes persistent user data as a JSON string. All wallet apps will need to take care of calling
     * backupUserData() and storing the results in local storage on their own.
     */
    fun backupUserData () : String? {
        return when (userProfile) {
            null -> null
            else -> {
                engine.dumpCredentials(userProfile!!.credentials)
                println(userData.dataSets["__CRYPTO_COSMOS__"]!!["key"])
                println(userData.exportAsJson())
                userData.exportAsJson()
            }
        }
    }

    fun setProfile (myProfile: MyProfile) {
        userProfile = myProfile
    }

    fun forceKeys (keyString : String, address : String) {
        val engine = engine as TxPylonsEngine
        engine.cryptoCosmos.keyPair =
                PylonsSECP256K1.KeyPair.fromSecretKey(
                        PylonsSECP256K1.SecretKey.fromBytes(Bytes32.wrap(
                                Hex.decode(keyString))))
        userProfile = MyProfile(
                this,
                credentials = TxPylonsEngine.Credentials(address),
                strings = mapOf("name" to "Jack"),
                items = listOf(),
                coins = listOf())
    }

    fun dumpKeys () : List<String> {
        val cc = engine.cryptoHandler as CryptoCosmos
        return listOf(Hex.toHexString(cc.keyPair!!.secretKey().bytesArray()),
                Hex.toHexString(cc.keyPair!!.publicKey().bytesArray()))
    }

    fun updateStatusBlock () {
        statusBlock = engine.getStatusBlock()
    }

    fun use() : Core {
        println("changing core")
        current = this
        Msg.useCore(this)
        Message.useCore(this)
        return this
    }

    fun start (userJson : String) {
        engine = when (config.backend) {
            Backend.LIVE -> TxPylonsEngine(this)
            Backend.LIVE_DEV -> TxPylonsDevEngine(this)
            Backend.NONE -> NoEngine(this)
        }
        runBlocking {
            try {
                userData.parseFromJson(userJson)
                if (userProfile == null) userProfile = when (userJson) {
                    "" -> null
                    else -> MyProfile.fromUserData(this@Core)
                }
            } catch (e : Exception) { // Eventually: we should recover properly from bad data
                Logger.implementation.log(LogEvent.USER_DATA_PARSE_FAIL,
                        """{"message":"${e.message.orEmpty()}","stackTrace":"${e.stackTrace!!.contentDeepToString()}","badUserData":$userJson}""",
                        LogTag.malformedData)
                userData.dataSets = engine.getInitialDataSets()
            }
            sane = true
            started = true
        }
    }

    var onCompletedOperation : (() -> Unit)? = null

    fun isReady () : Boolean {
        return sane && started
    }
}