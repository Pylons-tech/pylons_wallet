package com.pylons.lib

import com.beust.klaxon.Klaxon
import java.util.*

val klaxon = Klaxon()

annotation class QuotedJsonNumeral(val serializationMode : SerializationMode = SerializationMode.ALL)

annotation class NeverQuoteWrap

annotation class EmptyArray

enum class SerializationMode {
    FOR_BROADCAST,
    FOR_SIGNING,
    ALL
}

private val base64 = Base64.getEncoder()
fun Double.s() : String {
    val s = this.toString()
    return when (s.substring(s.length - 2, s.length)) {
        ".0" -> s.replace(".0", "")
        else -> s
    }
}

fun baseJsonTemplateForTxPost (msg: String, pubkey: String, signature: String, gas: Long) : String =
    """{
        "tx": {
            "msg": $msg,
    
                "fee": {
                "amount": null,
                "gas": "$gas"
            },
                "signatures": [
                {
                    "pub_key": {
                    "type": "tendermint/PubKeySecp256k1",
                    "value": "$pubkey"
                },
                    "signature": "$signature"
                }
                ],
                "memo": ""
            },
            "mode": "sync"
        }"""

fun baseJsonTemplateForTxSignature (msg: String, sequence: Long, accountNumber: Long, gas: Long) =
    """{"account_number":"$accountNumber","chain_id":"pylonschain","fee":{"amount":[],"gas":"$gas"},"memo":"","msgs":$msg,"sequence":"$sequence"}"""