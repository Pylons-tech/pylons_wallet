package com.pylons.lib

import com.beust.klaxon.Klaxon
import com.pylons.lib.types.tx.msg.Msg
import pylons.Tx
import java.io.ByteArrayOutputStream
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

enum class 	BroadcastMode {
    BroadcastMode_BROADCAST_MODE_UNSPECIFIED,
    // BROADCAST_MODE_BLOCK defines a tx broadcasting mode where the client waits for
    // the tx to be committed in a block.
    BroadcastMode_BROADCAST_MODE_BLOCK,
    // BROADCAST_MODE_SYNC defines a tx broadcasting mode where the client waits for
    // a CheckTx execution response only.
    BroadcastMode_BROADCAST_MODE_SYNC,
    // BROADCAST_MODE_ASYNC defines a tx broadcasting mode where the client returns
    // immediately.
    BroadcastMode_BROADCAST_MODE_ASYNC

}


private val base64 = Base64.getEncoder()
fun Double.s() : String {

    val s = this.toString()
    return when (s.substring(s.length - 2, s.length)) {
        ".0" -> s.replace(".0", "")
        else -> s
    }
}

//tierre: modified tx structure
fun baseJsonTemplateForTxPost (msg: String, pubkey: String, signature: String, gas: Long) : String =
    /*
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
        */
    """
    {
    "body":{
        "messages": $msg,
        "memo":"",
        "timeout_height":"0",
        "extension_options":[],
        "non_critical_extension_options":[]
       },
    "auth_info":{
        "signer_infos":[],
        "fee":{
            "amount":[],
            "gas_limit":"$gas",
            "payer":"",
            "granter":""
        }
    },
    "signatures":[
        "pub_key": {
            "type": "tendermint/PubKeySecp256k1",
            "value": "$pubkey"
        },
        "signature": "$signature"
    ]
   }
    """.trimIndent()

// tierre: /cosmos/tx/v1beta1/txs data type
fun baseTemplateForTxs(msg: String, mode: BroadcastMode):String{

    val proto_msg =  ProtoJsonUtil.fromJson(msg, type)
    val stream = ByteArrayOutputStream()

    if(proto_msg != null) {
        proto_msg.writeTo(stream)
    }

    return """
        {
            "tx_bytes": ${base64.encode(stream.toByteArray())},
            "mode": ${mode.name}
        }
    """.trimIndent()
}


fun baseJsonTemplateForTxSignature (msg: String, sequence: Long, accountNumber: Long, gas: Long) =
    """{"account_number":"$accountNumber","chain_id":"pylonschain","fee":{"amount":[],"gas":"$gas"},"memo":"","messages":$msg,"sequence":"$sequence"}"""