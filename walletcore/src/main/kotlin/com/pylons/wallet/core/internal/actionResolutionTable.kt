package com.pylons.wallet.core.internal

import com.pylons.wallet.core.constants.*
import com.pylons.wallet.core.ops.*
import com.pylons.wallet.core.types.*
import java.lang.Exception

/**
 * Function that resolves incoming MessageData to engine methods, requests UI elevation where appropriate, etc.
 * Once processing has finished, actionResolutionTable returns a Response object encapsulating the data that should
 * be returned to the client application.
 */
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
            Actions.GET_PENDING_EXECUTIONS -> getPendingExecutions()
            Actions.GET_TRANSACTION -> getTransaction(msg)
            Actions.GET_RECIPES -> getRecipes()

            // Dev
            Actions.WALLET_SERVICE_TEST -> devOnly(::walletServiceTest)
            Actions.WALLET_UI_TEST -> devOnly{ requiresArgs(action, msg, extraArgs, ::walletUiTest) }
            Actions.BATCH_CREATE_COOKBOOK -> devOnly { batchCreateCookbook(msg) }
            Actions.BATCH_UPDATE_COOKBOOK -> devOnly { batchUpdateCookbook(msg) }
            Actions.BATCH_CREATE_RECIPE -> devOnly { batchCreateRecipe(msg) }
            Actions.BATCH_UPDATE_RECIPE -> devOnly { batchUpdateRecipe(msg) }
            Actions.BATCH_ENABLE_RECIPE -> devOnly { batchEnableRecipe(msg) }
            Actions.BATCH_DISABLE_RECIPE -> devOnly { batchDisableRecipe(msg) }

            // Invalid inputs
            "" -> noAction()
            else -> unrecognizedAction(action)
        }
    } catch (e : Exception) {
        val msg = MessageData()
        msg.strings[Keys.EXCEPTION] = e::class.qualifiedName.toString()
        msg.strings[Keys.MESSAGE] = e.message.orEmpty()
        msg.strings[Keys.STACK_TRACE] = e.stackTrace!!.contentToString()
        return Response(msg, Status.OK_TO_RETURN_TO_CLIENT)
    }

}