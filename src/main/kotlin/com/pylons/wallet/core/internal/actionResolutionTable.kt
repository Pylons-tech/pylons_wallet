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
            Actions.NEW_PROFILE -> retryOnError { newProfile(msg) }
            Actions.GET_PYLONS -> retryOnError { getPylons(msg) }
            Actions.SEND_PYLONS -> retryOnError { sendPylons(msg) }
            //Actions.CREATE_TRADE -> retryOnError { createTrade(msg) }
            //Actions.FULFILL_TRADE -> retryOnError { fulfillTrade(msg) }

            // State queries
            Actions.GET_PROFILE -> getProfile(msg)
            Actions.GET_PENDING_EXECUTIONS -> getPendingExecutions(msg)
            Actions.GET_TRANSACTION -> getTransaction(msg)

            // Dev
            Actions.WALLET_SERVICE_TEST -> devOnly(::walletServiceTest)
            Actions.WALLET_UI_TEST -> devOnly{ requiresArgs(action, msg, extraArgs, ::walletUiTest) }
            Actions.CREATE_COOKBOOK -> devOnly { createCookbook(msg) }
            Actions.UPDATE_COOKBOOK -> devOnly { updateCookbook(msg) }
            //Actions.CREATE_RECIPE -> devOnly { createRecipe(msg) }
            //Actions.UPDATE_RECIPE -> devOnly { updateRecipe(msg) }
            //Actions.ENABLE_RECIPE -> devOnly { enableRecipe(msg) }
            //Actions.DISABLE_RECIPE -> devOnly { disableRecipe(msg) }

            // Invalid inputs
            "" -> noAction()
            else -> unrecognizedAction(action)
        }
    } catch (e : Exception) {
        var msg = MessageData()
        msg.strings[Keys.EXCEPTION] = e::class.qualifiedName.toString()
        msg.strings[Keys.MESSAGE] = e.message.orEmpty()
        msg.strings[Keys.STACK_TRACE] = e.stackTrace!!.contentToString()
        return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
    }

}