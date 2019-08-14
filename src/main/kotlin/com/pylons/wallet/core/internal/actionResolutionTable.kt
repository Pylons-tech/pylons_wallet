package com.pylons.wallet.core.internal

import com.pylons.wallet.core.constants.*
import com.pylons.wallet.core.ops.*
import com.pylons.wallet.core.types.*
import java.lang.Exception

internal fun actionResolutionTable (action : String, msg : MessageData, extraArgs: MessageData? = null) : Response {
    try {
        return when (action) {
            // Transactions
            Actions.applyRecipe -> retryOnError { applyRecipe(msg) }
            Actions.performTransaction -> retryOnError { performTransaction(msg) }
            Actions.newProfile -> retryOnError { newProfile(msg) }
            Actions.getPylons -> retryOnError { getPylons(msg) }
            Actions.sendPylons -> retryOnError { sendPylons(msg) }

            // State queries
            Actions.getUserDetails -> getUserDetails()
            Actions.getOtherUserDetails -> getOtherUserDetails(msg)
            Actions.getTransaction -> getTransaction(msg)

            // Wallet management
            Actions.getWalletCoreDetails -> getWalletCoreDetails()
            Actions.getFriends -> getFriends()
            Actions.setFriends -> setFriends(msg)
            Actions.wipeUserData -> wipeUserData()

            // Dev
            Actions.walletServiceTest -> devOnly(::walletServiceTest)
            Actions.walletUiTest -> devOnly{ requiresArgs(action, msg, extraArgs, ::walletUiTest) }
            Actions.setUserProfileState -> devOnly { setUserProfileState(msg) }
            Actions.setOtherUserProfileState -> devOnly { setOtherUserProfileState(msg) }
            Actions.dumpUserProfileState -> devOnly { dumpUserProfileState(msg) }

            // Invalid inputs
            "" -> noAction()
            else -> unrecognizedAction(action)
        }
    } catch (e : Exception) {
        var msg = MessageData()
        msg.strings["exception"] = e::class.qualifiedName.toString()
        msg.strings["message"] = e.message.orEmpty()
        msg.strings["stackTrace"] = e.stackTrace.toString()
        return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
    }

}