package walletcore.internal

import walletcore.constants.*
import walletcore.ops.*
import walletcore.types.*

internal fun actionResolutionTable (action : String, msg : MessageData, extraArgs: MessageData? = null) : Response {
    return when (action) {
        "" -> noAction()
        Actions.walletServiceTest -> walletServiceTest()
        Actions.walletUiTest -> requiresArgs(extraArgs, ::walletUiTest)
        Actions.getUserDetails -> getUserDetails()
        Actions.getWalletCoreDetails -> getWalletCoreDetails()
        Actions.getOtherUserDetails -> getOtherUserDetails(msg)
        Actions.applyRecipe -> retryOnError { applyRecipe(msg) }
        Actions.performTransaction -> retryOnError { performTransaction(msg) }
        Actions.newProfile -> requiresArgs(extraArgs, ::newProfile)
        Actions.wipeUserData -> wipeUserData()
        else -> unrecognizedAction(action)
    }
}