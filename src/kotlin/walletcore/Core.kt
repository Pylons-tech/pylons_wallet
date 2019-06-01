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
    internal var txHandler: TxHandler? = null
        private set
    internal var userProfile: Profile? = null
    internal var friends: List<Friend> = listOf()
    internal var foreignProfilesBuffer : Set<ForeignProfile> = setOf()
    var uiInterrupts : UiInterrupts? = null
    var sane : Boolean = false
        private set
    var suspendedAction : String? = null
        internal set
    internal var suspendedMsg : MessageData? = null
    var statusBlock : StatusBlock = StatusBlock(-1, 0.0, "0.0.1a")
    internal var userData : UserData? = null


    internal fun tearDown () {
        txHandler = null
        userProfile = null
        uiInterrupts = null
        friends = listOf()
        sane = false
    }

    /**
     * Serializes persistent user data as a JSON string. All wallet apps will need to take care of calling
     * backupUserData() and storing the results in local storage on their own.
     */
    fun backupUserData () : String? = UserData.exportAsJson()

    fun setProfile (profile: Profile) {
        userProfile = profile
    }

    fun dumpUserProfile () : String = userProfile!!.dump()

    fun dumpForeignProfiles () : String = OutsideWorldDummy.dumpProfiles()

    fun dumpTx () : String = OutsideWorldDummy.dumpTransactions()

    fun initializeFakeWorldState (dbgStateProfileJson : String, dbgStateWorldJson : String, dbgStateTxJson : String) {
        OutsideWorldDummy.loadProfiles(dbgStateWorldJson)
        userProfile = Profile.load(dbgStateProfileJson)
        OutsideWorldDummy.loadTransactions(dbgStateTxJson)
    }

    fun start (backend: Backend, json : String) {
        txHandler = when (backend) {
            Backend.DUMMY -> TxDummy()
            Backend.ALPHA_REST -> TxDummy()
        }
        runBlocking {
            UserData.parseFromJson(json)
            userProfile = when (userData) {
                null -> Profile.fromUserData()
                else -> null
            }
            sane = true
        }
    }

    fun finishSuspendedActionWithArgs(args : MessageData) : Response? {
        val action = suspendedAction!!; suspendedAction = null
        val msg = suspendedMsg!!; suspendedMsg = null
        return actionResolutionTable(action, msg, args)
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
    fun resolveMessage(msg: MessageData): Response? {
        statusBlock = StatusBlock(txHandler!!.getHeight(), txHandler!!.getAverageBlockTime(), statusBlock.walletCoreVersion)
        if (!sane) {
            var msg = generateErrorMessageData(Error.CORE_IS_NOT_SANE, "Core state is not sane. Please call Core.start() before attempting to resolve messages.")
            throw Exception(msg.msg!!.strings[Keys.info])
        }
        val action = msg.strings[ReservedKeys.wcAction].orEmpty()
        return actionResolutionTable(action, msg)
    }

    /**
     * Wipe user data without going through action resolution table. Provided for wallet app UI wiring.
     */
    fun wipeUserData () = walletcore.ops.wipeUserData()

}