package walletcore

import kotlinx.coroutines.*

import walletcore.constants.*
import walletcore.crypto.*
import walletcore.internal.*
import walletcore.tx.*
import walletcore.types.*

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
    internal val txHandler: TxHandler = TxDummy()
    internal var cryptoHandler: CryptoHandler? = null
    internal var userProfile: Profile? = null
    internal var foreignProfilesBuffer : Set<ForeignProfile> = setOf()
    internal var loadedCookbooks : Map<String, Cookbook> = mapOf()
    var uiInterrupts : UiInterrupts? = null
    var sane : Boolean = false
        private set

    internal fun tearDown () {
        cryptoHandler = null
        userProfile = null
        uiInterrupts = null
        sane = false
    }

    /**
     * Serializes persistent user data as a JSON string. All wallet apps will need to take care of calling
     * backupUserData() and storing the results in local storage on their own.
     */
    fun backupUserData () : String? {
        return when (userProfile) {
            null -> null
            else -> UserData(name = userProfile!!.getName(), id = userProfile!!.id).exportAsJson()
        }
    }

    fun config () {

    }

    fun start (json : String? = null) {
        runBlocking {
            val userData = when (json) {
                null -> null
                else -> UserData.parseFromJson(json)
            }
            userProfile = Profile.fromUserData(userData)
            if (userData != null) cryptoHandler = txHandler.getNewCryptoHandler(userData)
            sane = true
        }
    }

    fun newProfile () {
        
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
    fun resolveMessage(msg: MessageData, args: MessageData? = null): Response? {
        if (!sane) throw Exception("Core state is not sane. Call Core.start() before attempting to resolve messages.")
        val action = msg.strings[ReservedKeys.wcAction].orEmpty()
        return actionResolutionTable(action, msg, args)
    }

    /**
     * Wipe user data without going through action resolution table. Provided for wallet app UI wiring.
     */
    fun wipeUserData () {
        wipeUserData()
    }
}