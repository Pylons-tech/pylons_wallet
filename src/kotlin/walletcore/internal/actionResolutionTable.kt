package walletcore.internal

import walletcore.constants.*
import walletcore.ops.*
import walletcore.types.*

internal fun actionResolutionTable (action : String, msg : MessageData, args: MessageData? = null) : Response {
    return when (action) {
        "" -> noAction()
        Actions.walletServiceTest -> walletServiceTest()
        Actions.walletUiTest -> requiresArgs(args, ::walletUiTest)
        Actions.getUserDetails -> getUserDetails()
        Actions.getWalletCoreDetails -> getWalletCoreDetails()
        Actions.getOtherUserDetails -> TODO()
        Actions.applyRecipe -> applyRecipe(msg)
        Actions.performTransaction -> performTransaction(msg)
        Actions.newProfile -> requiresArgs(args, ::newProfile)
        Actions.wipeUserData -> wipeUserData()
        else -> unrecognizedAction(action)
    }
}