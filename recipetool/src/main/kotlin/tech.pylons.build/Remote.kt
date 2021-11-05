package tech.pylons.build

import tech.pylons.lib.types.Backend

data class Remote (val backend : Backend, val chainId : String, val txsUrl : String, val queryUrl : String) {
    companion object {
        val local = Remote(Backend.MANUAL,"pylons", "127.0.0.1:1317", "127.0.0.1:1317")
        val testnet = Remote(Backend.TESTNET,"pylons-testnet", "rpc.testnet.pylons.tech:26657", "api.testnet.pylons.tech:1317")
    }

    fun identifier () : String =
        when (backend) {
            Backend.MANUAL -> "local"
            Backend.TESTNET -> "testnet"
            else -> throw Exception("bad backend $backend")
        }
}