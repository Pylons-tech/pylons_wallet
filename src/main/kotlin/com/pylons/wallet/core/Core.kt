package com.pylons.wallet.core

import kotlinx.coroutines.*

import com.pylons.wallet.core.constants.*
import com.pylons.wallet.core.internal.*
import com.pylons.wallet.core.engine.*
import com.pylons.wallet.core.types.*
import org.apache.tuweni.bytes.Bytes32
import org.bouncycastle.util.encoders.Hex
import java.util.concurrent.LinkedBlockingQueue

object Core {
    /**
     * The number of times the core will retry valid-but-rejected transactions.
     * (For instance: if the remote profile doesn't have the resources to apply a recipe.)
     */
    internal const val rejectedTxRetryTimes = 3
    /**
     * The amount of time (in milliseconds) to wait before retrying such operations.
     */
    internal const val retryDelay : Long = 500 // milliseconds
    internal var engine: Engine = NoEngine()
        private set
    internal var userProfile: Profile? = null
    internal var friends: List<Friend> = listOf()
    internal var foreignProfilesBuffer : Set<ForeignProfile> = setOf()
    var sane : Boolean = false
        private set
    var started : Boolean = false
        private set
    var suspendedAction : String? = null
        internal set
    internal var suspendedMsg : MessageData? = null
    const val VERSION_STRING = "0.0.1a"
    var statusBlock : StatusBlock = StatusBlock(-1, 0.0, VERSION_STRING)

    var onWipeUserData : (() -> Unit)? = null

    var config : Config? = null
        private set

    internal fun tearDown () {
        engine = NoEngine()
        userProfile = null
        friends = listOf()
        sane = false
        inDoResolveMessage = false
        started = false
        onCompletedOperation = null
    }

    /**
     * Serializes persistent user data as a JSON string. All wallet apps will need to take care of calling
     * backupUserData() and storing the results in local storage on their own.
     */
    fun backupUserData () : String? {
        engine.dumpCredentials(userProfile!!.credentials)
        println(UserData.dataSets["__CRYPTO_COSMOS__"]!!["key"])
        println( UserData.exportAsJson())
        return UserData.exportAsJson()
    }

    fun setProfile (profile: Profile) {
        userProfile = profile
    }

    fun forceKeys (keyString : String, address : String) {
        val engine = engine as TxPylonsEngine
        engine.cryptoHandler.keyPair = SECP256K1.KeyPair.fromSecretKey(SECP256K1.SecretKey.fromBytes(Bytes32.wrap(Hex.decode(keyString))))
        userProfile = Profile(TxPylonsEngine.Credentials(address), mutableMapOf("name" to "Jack"), mutableMapOf(), mutableListOf())
    }

    fun dumpUserProfile () : String = userProfile!!.dump()

    fun start (cfg: Config, userJson : String) {
        config = cfg
        engine = when (config!!.backend) {
            Backend.LIVE -> TxPylonsEngine()
            Backend.LIVE_DEV -> TxPylonsDevEngine()
            Backend.NONE -> NoEngine()
        }
        runBlocking {
            try {
                UserData.parseFromJson(userJson)
                userProfile = when (userJson) {
                    "" -> null
                    else -> Profile.fromUserData()
                }
            } catch (e : Exception) { // Eventually: we should recover properly from bad data
                Logger.implementation.log(LogTag.info, "Saved data was bad; generating new credentials." +
                        "(This behavior should not exist in production)")
                UserData.dataSets = engine.getInitialDataSets()
                userProfile = null
            }
            sane = true
            started = true
        }
    }

    fun finishSuspendedActionWithArgs(args : MessageData) : Response? {
        val action = suspendedAction!!; suspendedAction = null
        val msg = suspendedMsg!!; suspendedMsg = null
        return actionResolutionTable(action, msg, args)
    }

    private class MessageWithCallback (val msg : MessageData, val callback: ((Response?) -> Unit)?)

    private val messageResolutionQueue : LinkedBlockingQueue<MessageWithCallback> = LinkedBlockingQueue<MessageWithCallback>()
    private var inDoResolveMessage = false

    var onCompletedOperation : (() -> Unit)? = null

    fun isReady () : Boolean {
        return sane && !inDoResolveMessage && started
    }

    /**
     * resolveMessage is the main entry point which platform-specific wallet apps should use
     * in order to call into WalletCore. It takes two arguments:
     *
     * msg: MessageData object containing the data passed to us by the client. How, exactly,
     * the client does this will of course depend on the platform-specific IPC behavior, but
     * in general it should be packed in a relatively analogous form to this internal structure.
     * On Android it's the extras attached to the intent with which we invoke the service, on
     * Windows we just pass Message objects around directly, etc.
     *
     * args: A second MessageData object, provided for pylonsActions which require additional
     * data that cannot or should not be provided by the client in order to be resolved.
     * Null by default.
     *
     * Because walletCore will block on network operations - this is by design; restricting
     * the core to a single thread of execution ensures consistent state and deterministic
     * behavior - resolveMessage should not be called from the main thread of any wallet app.
     */
    fun resolveMessage(msg: MessageData, callback : ((Response?) -> Unit)?) {
        val dat = MessageWithCallback(msg, callback)
        try {
            inDoResolveMessage = true
            if (!sane) {
                throw IllegalStateException("Core state is not sane. Please call Core.start() before attempting to resolve messages.")
            }
            val action = dat.msg.strings[ReservedKeys.wcAction].orEmpty()
            val out = actionResolutionTable(action, dat.msg)
            out.msg!!.strings[ReservedKeys.statusBlock] = statusBlock.toJson()
            Logger.implementation.log("Resolution of message ${dat.msg.getAction()} complete}", LogTag.info)
            inDoResolveMessage = false
            println(out.msg.toString())
            dat.callback?.invoke(out)
            onCompletedOperation?.invoke()
        }
        // This isn't handled, so we're gonna throw it
        catch (e : Exception) {
            inDoResolveMessage = false // Cleanup before doing that
            throw e
        }
    }

    /**
     * Wipe user data without going through action resolution table. Provided for wallet app UI wiring.
     */
    fun wipeUserData () = com.pylons.wallet.core.ops.wipeUserData()

}