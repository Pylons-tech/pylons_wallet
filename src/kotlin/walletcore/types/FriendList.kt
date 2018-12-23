package walletcore.types

data class FriendList (
    val friends : Set<ForeignProfile> = setOf()
)