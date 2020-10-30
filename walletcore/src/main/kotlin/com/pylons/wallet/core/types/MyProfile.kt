package com.pylons.wallet.core.types

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.*
import com.pylons.wallet.core.engine.TxPylonsEngine
import com.pylons.wallet.core.types.tx.item.Item

/**
 * Internal state representation of the user's own userProfile.
 */
@ExperimentalUnsignedTypes
class MyProfile (val core : Core, var credentials: Credentials,
                 var lockedCoinDetails: LockedCoinDetails = LockedCoinDetails.default,
                 strings : Map<String, String>,
                 items : List<Item>,
                 coins : List<Coin>) :
        Profile(credentials.address, strings, coins, items) {
    abstract class Credentials (var address : String) { }

    companion object {
        fun getDefault(core : Core) = MyProfile(
                core = core,
                credentials = core.engine.generateCredentialsFromKeys(),
                strings = mapOf(),
                items = listOf(),
                coins = listOf()
        )

        fun fromUserData (core : Core) : MyProfile? {
            val data = core.userData.dataSets.getValue(core.engine.prefix)
            (core.engine as TxPylonsEngine).cryptoHandler.importKeysFromUserData()
            val credentials = core.engine.generateCredentialsFromKeys()
            return when (val name = data.getOrDefault("name", "")) {
                "" -> MyProfile(core, credentials = credentials, coins = listOf(), items = listOf(), strings = mutableMapOf())
                else -> MyProfile(core, credentials = credentials, strings = mutableMapOf(ReservedKeys.profileName to name),
                        coins = listOf(), items = listOf())
            }
        }
    }
}