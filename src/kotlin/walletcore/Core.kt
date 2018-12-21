package walletcore

import walletcore.constants.ReservedKeys
import walletcore.crypto.*
import walletcore.internal.actionResolutionTable
import walletcore.ops.*
import walletcore.tx.*
import walletcore.types.*

object Core {
    /**
     * Compartmentalizes the stateful parts of walletcore.
     * Supports backup and loading of persistent data - keys, friends, etc.
     * Must be set up before internal can perform useful work, either by calling
     * load() or by calling firstTimeSetup().
     */
    internal object Live {
        val txHandlerType = TxDummy::class
        var cryptoHandler : CryptoHandler? = null
        var profile : Profile? = null
        var txHandler : TxHandler? = null

        fun backup () : String {
            return  "?"

        }

        fun firstTimeSetup () {

        }

        fun load (serialized : String?) {

        }

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
     */
    fun resolveMessage(msg: MessageData, args: MessageData? = null): Response? {
        val action = msg.strings[ReservedKeys.wcAction].orEmpty()
        return actionResolutionTable(action, args)
    }
}