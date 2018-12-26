package walletcore.internal

import walletcore.constants.*
import walletcore.ops.*
import walletcore.types.*

internal fun actionResolutionTable (action : String, args: MessageData? = null) : Response {
    return when (action) {
        "" -> noAction()
        Actions.walletServiceTest -> walletServiceTest()
        Actions.walletUiTest -> requiresArgs(args) { m -> walletUiTest(m) }
        Actions.getUserDetails -> getUserDetails()
        Actions.getWalletCoreDetails -> getWalletCoreDetails()
        Actions.getOtherUserDetails -> TODO()
        Actions.submitTx -> TODO()
        else -> unrecognizedAction(action)
    }
}