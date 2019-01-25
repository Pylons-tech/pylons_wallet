package walletcore.internal

import walletcore.constants.*
import walletcore.ops.*
import walletcore.types.*

internal fun actionResolutionTable (action : String, msg : MessageData, extraArgs: MessageData? = null) : Response {
    return when (action) {
        "" -> noAction()
        Actions.walletServiceTest -> devOnly(::walletServiceTest)
        Actions.walletUiTest -> devOnly{ requiresArgs(action, msg, extraArgs, ::walletUiTest) }
        Actions.getUserDetails -> getUserDetails()
        Actions.getWalletCoreDetails -> getWalletCoreDetails()
        Actions.getOtherUserDetails -> getOtherUserDetails(msg)
        Actions.applyRecipe -> retryOnError { applyRecipe(msg) }
        Actions.performTransaction -> retryOnError { performTransaction(msg) }
        Actions.newProfile -> requiresArgs(action, msg, extraArgs, ::newProfile)
        Actions.wipeUserData -> wipeUserData()
        Actions.setUserProfileState -> devOnly { setUserProfileState(msg) }
        else -> unrecognizedAction(action)
    }
}