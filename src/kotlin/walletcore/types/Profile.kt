package walletcore.types

/**
 * Internal state representation of the user's own profile.
 */
data class Profile (
    val id : String = "",
    val strings : Map<String, String> = mapOf(),
    val coins : Set<Coin> = setOf(),
    val items : Set<Item> = setOf()
) {
    companion object {
        /**
         * Generates a new local profile.
         * args is a string:string map. Generally, it'll always at least
         * incorporate a profile name, stored under ReservedKeys.profileName;
         * other than that, this map might be used to encode optional user information
         * like demographics, contact info, a choice of avatar, etc.
         * TODO: Set up a formal spec outlining possible arguments
         */
        fun generate (_id : String, _s : Map<String, String>): Profile {
            return Profile(id = _id, strings = _s)
        }
    }
}