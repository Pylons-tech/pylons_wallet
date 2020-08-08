package com.pylons.wallet.core.types

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.*
import com.pylons.wallet.core.engine.TxPylonsEngine
import com.pylons.wallet.core.types.tx.item.Item

/**
 * Internal state representation of the user's own userProfile.
 */
@ExperimentalUnsignedTypes
class MyProfile (var credentials: Credentials,
                 strings : Map<String, String>,
                 items : List<Item>,
                 coins : List<Coin>) :
        Profile(credentials.address, strings, coins, items) {
    abstract class Credentials (var address : String) { }

    companion object {
        fun fromUserData () : MyProfile? {
            val data = UserData.dataSets.getValue(Core.engine.prefix)
            (Core.engine as TxPylonsEngine).cryptoHandler.importKeysFromUserData()
            val credentials = Core.engine.generateCredentialsFromKeys()
            return when (val name = data.getOrDefault("name", "")) {
                "" -> MyProfile(credentials = credentials, coins = listOf(), items = listOf(), strings = mutableMapOf())
                else -> MyProfile(credentials = credentials, strings = mutableMapOf(ReservedKeys.profileName to name),
                        coins = listOf(), items = listOf())
            }
        }
    }
}