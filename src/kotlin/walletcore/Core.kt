package walletcore

import walletcore.constants.*
import walletcore.crypto.*
import walletcore.internal.*
import walletcore.tx.*
import walletcore.types.*

object Core {
    val txHandler: TxHandler = TxDummy()
    var cryptoHandler: CryptoHandler? = null
    var userProfile: Profile? = null

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


    fun start (json : String? = null) {
        val userData = when (json) {
            null -> throw NotImplementedError()
            else -> UserData.parseFromJson(json)
        }
        userProfile = Profile.fromUserData(userData!!)
        cryptoHandler = txHandler.getNewCryptoHandler(userData)
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
        val action = msg.strings[ReservedKeys.wcAction].orEmpty()
        return actionResolutionTable(action, args)
    }
}