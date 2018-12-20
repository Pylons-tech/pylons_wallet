package walletcore.types

data class FriendList (
    val friends : Set<RemoteProfile> = setOf()
)