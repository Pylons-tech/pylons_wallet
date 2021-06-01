package tech.pylons.lib.types

import tech.pylons.lib.core.ICore
import tech.pylons.lib.types.credentials.ICredentials
import tech.pylons.lib.types.tx.Coin
import tech.pylons.wallet.libpylons.constants.*
import tech.pylons.lib.types.tx.item.Item

/**
 * Internal state representation of the user's own userProfile.
 */
@ExperimentalUnsignedTypes
class MyProfile (val core : ICore, var credentials: ICredentials,
                 var lockedCoinDetails: LockedCoinDetails = LockedCoinDetails.default,
                 strings : Map<String, String>,
                 items : List<Item>,
                 coins : List<Coin>) :
        Profile(credentials.address, strings, coins, items) {

    companion object {
        fun getDefault(core : ICore) = MyProfile(
                core = core,
                credentials = core.engine.generateCredentialsFromKeys(),
                strings = mapOf(),
                items = listOf(),
                coins = listOf()
        )

        fun fromUserData (core : ICore) : MyProfile? {
            val data = core.userData.dataSets.getValue(core.engine.prefix)
            core.engine.cryptoHandler.importKeysFromUserData()
            val credentials = core.engine.generateCredentialsFromKeys()
            return when (val name = data.getOrDefault("name", "")) {
                "" -> MyProfile(core, credentials = credentials, coins = listOf(), items = listOf(), strings = mutableMapOf())
                else -> MyProfile(core, credentials = credentials, strings = mutableMapOf(ReservedKeys.profileName to name),
                        coins = listOf(), items = listOf())
            }
        }
    }
}