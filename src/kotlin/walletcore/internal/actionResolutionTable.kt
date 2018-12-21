package walletcore.internal

import walletcore.ops.*
import walletcore.types.*

internal fun actionResolutionTable (action : String, args: MessageData? = null) : Response {
    val out = when (action) {
        "" -> noAction()
        "WALLET_SERVICE_TEST" -> walletServiceTest()
        "WALLET_UI_TEST" -> requiresArgs(args) { m -> walletUiTest(m) }
        else -> unrecognizedAction(action)
    }
    return out
}