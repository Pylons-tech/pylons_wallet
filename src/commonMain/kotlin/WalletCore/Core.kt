package walletcore

import walletcore.ops.*
import walletcore.tx.*
import walletcore.types.*

object Core {
    private var profile : Profile? = null
    private var wallet : Wallet? = null

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
        var out: Response? = null
        val pylonsAction = msg.strings["pylonsAction"].orEmpty()
        when (pylonsAction) {
            "" -> out = noPylonsAction()
            "WALLET_SERVICE_TEST" -> out = walletServiceTest()
            "WALLET_UI_TEST" -> out = requiresArgs(args) { m -> walletUiTest(m)}
        }
        return out
    }

    /**
     * If an operation produces a (recoverable) error when we attempt to execute
     * it with the provided data, it generates a Response object containing
     * information on the error in question to be returned to the client.
     * Depending on the nature of the error, it may be possible for the client
     * to correct its own state and resubmit the original message, or it
     * may simply wind up giving an error message to end users.
     */
    private fun generateErrorMessageData(error: String, info: String): Response {
        val msg = MessageData()
        msg.strings["error"] = error
        msg.strings["info"] = info
        return Response(msg, Status.INCOMING_MESSAGE_MALFORMED)
    }

    /**
     * If an operation is called without additional arguments, and it requires
     * additional arguments, it generates a Response object which signals to
     * the wallet application that the operation in question requires additional
     * arguments. In general, this means that we need to bring up the wallet UI,
     * which tends to involve the application jumping through some sort of
     * platform-specific hoops. This response's Response.msg field is null,
     * and if you try to go back to the client with that it'll crash, so
     * don't do that.
     */
    private fun requiresArgs(args: MessageData?, func: (MessageData) -> Response): Response {
        return if (args != null) func(args)
        else Response(null, Status.REQUIRE_UI_ELEVATION)
    }

    /**
     * Generates the error message data to be given to clients in the
     * event that they attempt to call out to the wallet without
     * providing a pylonsAction.
     */
    private fun noPylonsAction(): Response {
        return generateErrorMessageData("NO_PYLONSACTION_FIELD", "The sent message didn't contain a pylonsAction field, so the wallet was unable to resolve it.")
    }
}