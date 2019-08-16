package com.pylons.wallet.core.engine

import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.PathNotFoundException
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.Backend
import com.pylons.wallet.core.types.Transaction
import com.pylons.wallet.core.types.TxJson
import java.lang.Exception

internal class TxPylonsDevEngine : TxPylonsEngine () {
    override val isDevEngine: Boolean = true
    override val backendType: Backend = Backend.LIVE_DEV

    override fun createCookbook(name: String, devel: String, desc: String, version: String, supportEmail: String, level: Int): Transaction {
        val c = Core.userProfile!!.credentials as Credentials
        println(                    TxJson.createCookbook(name, devel, desc, version, supportEmail, level, c.address,
                cryptoHandler.keyPair!!.publicKey(), c.accountNumber, c.sequence))
        return Transaction(resolver =  {
            val response = postTxJson(
                    TxJson.createCookbook(name, devel, desc, version, supportEmail, level, c.address,
                            cryptoHandler.keyPair!!.publicKey(), c.accountNumber, c.sequence))
            try {
                val code = JsonPath.read<Int>(response, "$.code")
                if (code != null)
                    throw Exception("Node returned error code $code for message - ${JsonPath.read<String>(response, "$.raw_log.message")}")
            } catch (e : PathNotFoundException) {
                // swallow this - we only find an error code if there is in fact an error
            }
            it.id = JsonPath.read(response, "$.txhash")
        })
    }
}