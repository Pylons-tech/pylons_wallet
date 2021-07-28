package tech.pylons.build

data class DevConfig(
    val preferredName : String,
    val keys : String,
    val remote : String
) {
    fun remoteConfig () : Remote {
        return when (remote) {
            "local" -> Remote.local
            "testnet" -> Remote.testnet
            else -> throw Exception("Unrecognized remote $remote")
        }
    }
}