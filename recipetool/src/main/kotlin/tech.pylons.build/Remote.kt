package tech.pylons.build

import tech.pylons.lib.types.Backend

data class Remote (val backend : Backend, val chainId : String, val txsUrl : String, val queryUrl : String) {
    companion object {
        val local = Remote(Backend.MANUAL,"pylonschain", "127.0.0.1:1317", "127.0.0.1:1317")
        val testnet = Remote(Backend.TESTNET,"pylons-testnet", "testnet.pylons.tech", "api.testnet.pylons.tech")
    }

    fun identifier () : String =
        when (backend) {
            Backend.MANUAL -> "local"
            Backend.TESTNET -> "testnet"
            else -> throw Exception("bad backend $backend")
        }
}