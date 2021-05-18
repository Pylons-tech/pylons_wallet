package tech.pylons.lib

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

enum class 	BroadcastMode {
    BROADCAST_MODE_UNSPECIFIED,
    // BROADCAST_MODE_BLOCK defines a tx broadcasting mode where the client waits for
    // the tx to be committed in a block.
    BROADCAST_MODE_BLOCK,
    // BROADCAST_MODE_SYNC defines a tx broadcasting mode where the client waits for
    // a CheckTx execution response only.
    BROADCAST_MODE_SYNC,
    // BROADCAST_MODE_ASYNC defines a tx broadcasting mode where the client returns
    // immediately.
    BROADCAST_MODE_ASYNC

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

// tierre: should totally modify this part
// /cosmos/tx/v1beta1/txs data type
fun baseTemplateForTxs(tx_bytes: String, mode: BroadcastMode):String{

    return """
        {
            "tx_bytes": "${tx_bytes}",
            "mode": "${mode.name}"
        }
    """.trimIndent()
}

//tierre: should totally modify this part
fun baseJsonTemplateForTxSignature (msg: String, sequence: Long, accountNumber: Long, gas: Long) =
    """{"account_number":"$accountNumber","chain_id":"pylonschain","fee":{"amount":[],"gas":"$gas"},"memo":"","messages":$msg,"sequence":"$sequence"}"""