package com.pylons.wallet.core.internal

import com.pylons.wallet.core.constants.*
import com.pylons.wallet.core.ops.*
import com.pylons.wallet.core.types.*
import java.lang.Exception

internal fun actionResolutionTable (action : String, msg : MessageData, extraArgs: MessageData? = null) : Response {
    try {
        return when (action) {
            // Transactions
            Actions.APPLY_RECIPE -> retryOnError { applyRecipe(msg) }
            Actions.PERFORM_TRANSACTION -> retryOnError { performTransaction(msg) }
            Actions.NEW_PROFILE -> retryOnError { newProfile(msg) }
            Actions.GET_PYLONS -> retryOnError { getPylons(msg) }
            Actions.SEND_PYLONS -> retryOnError { sendPylons(msg) }

            // State queries
            Actions.GET_USER_DETAILS -> getUserDetails()
            Actions.GET_OTHER_USER_DETAILS -> getOtherUserDetails(msg)
            Actions.GET_TRANSACTION -> getTransaction(msg)

            // Wallet management
            Actions.GET_FRIENDS -> getFriends()
            Actions.SET_FRIENDS -> setFriends(msg)
            Actions.WIPE_USER_DATA -> wipeUserData()

            // Dev
            Actions.WALLET_SERVICE_TEST -> devOnly(::walletServiceTest)
            Actions.WALLET_UI_TEST -> devOnly{ requiresArgs(action, msg, extraArgs, ::walletUiTest) }
            Actions.SET_USER_PROFILE_STATE -> devOnly { setUserProfileState(msg) }
            Actions.SET_OTHER_USER_PROFILE_STATE -> devOnly { setOtherUserProfileState(msg) }
            Actions.DUMP_USER_PROFILE_STATE -> devOnly { dumpUserProfileState(msg) }
            Actions.CREATE_COOKBOOK -> devOnly { createCookbook(msg) }
            Actions.UPDATE_COOKBOOK -> devOnly { updateCookbook(msg) }

            // Invalid inputs
            "" -> noAction()
            else -> unrecognizedAction(action)
        }
    } catch (e : Exception) {
        var msg = MessageData()
        msg.strings[Keys.EXCEPTION] = e::class.qualifiedName.toString()
        msg.strings[Keys.MESSAGE] = e.message.orEmpty()
        msg.strings[Keys.STACK_TRACE] = e.stackTrace.toString()
        return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
    }

}