package com.pylons.wallet.core.constants

class Actions {
    companion object {
        const val walletServiceTest = "WALLET_SERVICE_TEST"
        const val walletUiTest = "WALLET_UI_TEST"
        const val getUserDetails = "GET_USER_DETAILS"
        const val getWalletCoreDetails = "GET_WALLET_CORE_DETAILS"
        const val getOtherUserDetails = "GET_OTHER_USER_DETAILS"
        const val performTransaction = "PERFORM_TRANSACTION"
        const val applyRecipe = "APPLY_RECIPE"
        const val newProfile = "NEW_PROFILE"
        const val wipeUserData = "WIPE_USER_DATA"
        const val setUserProfileState = "SET_USER_PROFILE_STATE"
        const val setOtherUserProfileState = "SET_OTHER_USER_PROFILE_STATE"
        const val dumpUserProfileState = "DUMP_USER_PROFILE_STATE"
        const val getFriends = "GET_FRIENDS"
        const val setFriends = "SET_FRIENDS"
        const val getTransaction ="GET_TRANSACTION"
        const val getPylons = "GET_PYLONS"
        const val sendPylons = "SEND_PYLONS"
    }
}