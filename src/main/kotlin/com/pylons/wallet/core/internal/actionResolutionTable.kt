package com.pylons.wallet.core.internal

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.*
import com.pylons.wallet.core.ops.*
import com.pylons.wallet.core.types.*

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
        Actions.newProfile -> newProfile(msg) //requiresArgs(action, msg, extraArgs, ::newProfile)
        Actions.wipeUserData -> wipeUserData()
        Actions.setUserProfileState -> devOnly { setUserProfileState(msg) }
        Actions.setOtherUserProfileState -> devOnly { setOtherUserProfileState(msg) }
        Actions.dumpUserProfileState -> devOnly { dumpUserProfileState(msg) }
        Actions.getFriends -> getFriends()
        Actions.setFriends -> { setFriends(msg) }
        Actions.getTransaction -> getTransaction(msg)
        Actions.getPylons -> getPylons(msg)
        else -> unrecognizedAction(action)
    }
}