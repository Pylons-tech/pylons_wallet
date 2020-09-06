package com.pylons.wallet.core.types

import com.pylons.wallet.core.Core.engine
import com.pylons.wallet.core.constants.*
import com.pylons.wallet.core.engine.TxPylonsEngine
import com.pylons.wallet.core.types.tx.item.Item

/**
 * Internal state representation of the user's own userProfile.
 */
@ExperimentalUnsignedTypes
class MyProfile (var credentials: Credentials,
                 var lockedCoinDetails: LockedCoinDetails = LockedCoinDetails.default,
                 strings : Map<String, String>,
                 items : List<Item>,
                 coins : List<Coin>) :
        Profile(credentials.address, strings, coins, items) {
    abstract class Credentials (var address : String) { }

    companion object {
        val default get() = MyProfile(
                credentials = engine.generateCredentialsFromKeys(),
                strings = mapOf(),
                items = listOf(),
                coins = listOf()
        )

        fun fromUserData () : MyProfile? {
            val data = UserData.dataSets.getValue(engine.prefix)
            (engine as TxPylonsEngine).cryptoHandler.importKeysFromUserData()
            val credentials = engine.generateCredentialsFromKeys()
            return when (val name = data.getOrDefault("name", "")) {
                "" -> MyProfile(credentials = credentials, coins = listOf(), items = listOf(), strings = mutableMapOf())
                else -> MyProfile(credentials = credentials, strings = mutableMapOf(ReservedKeys.profileName to name),
                        coins = listOf(), items = listOf())
            }
        }
    }
}