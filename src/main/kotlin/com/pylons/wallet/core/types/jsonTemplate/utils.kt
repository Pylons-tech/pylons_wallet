package com.pylons.wallet.core.types.jsonTemplate

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.TxPylonsEngine
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.wallet.core.types.item.prototype.ItemPrototype
import com.pylons.wallet.core.types.SECP256K1
import java.util.*

private val base64 = Base64.getEncoder()

internal fun Double.s() : String {
    val s = this.toString()
    return when (s.substring(s.length - 2, s.length)) {
        ".0" -> s.replace(".0", "")
        else -> s
    }
}

internal fun baseJsonWeldFlow (msg : String, signComponent : String, accountNumber: Long, sequence: Long, pubkey: SECP256K1.PublicKey) : String {
    val cryptoHandler = (Core.engine as TxPylonsEngine).cryptoHandler
    val signable = baseSignTemplate(signComponent, sequence, accountNumber)
    println("Signable:")
    println(signable)
    val signBytes = signable.toByteArray(Charsets.UTF_8)
    val signatureBytes = cryptoHandler.signature(signBytes)
    val signature = base64.encodeToString(signatureBytes)
    return baseTemplate(msg, pubkeyToString(pubkey), signature)
}



private fun baseTemplate (msg : String, pubkey : String, signature : String) : String =
        """{
            "tx": {
                "msg": $msg,
    
                "fee": {
                "amount": null,
                "gas": "200000"
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

internal fun baseSignTemplate (msg : String, sequence: Long, accountNumber: Long) =
        """{"account_number":"$accountNumber","chain_id":"pylonschain","fee":{"amount":[],"gas":"200000"},"memo":"","msgs":$msg,"sequence":"$sequence"}"""

internal fun getCoinIOListForMessage(map : Map<String, Long>) : String {
    var sb = StringBuilder("[")
    map.forEach {
        sb.append("""{"Coin":"${it.key}","Count":"${it.value}"},""")
    }
    if (map.isNotEmpty()) sb.deleteCharAt(sb.length - 1)
    sb.append("]")
    return sb.toString()
}

internal fun getCoinIOListForSigning(map : Map<String, Long>) : String {
    var sb = StringBuilder("[")
    map.forEach {
        sb.append("""{"Coin":"${it.key}","Count":${it.value}},""")
    }
    if (map.isNotEmpty()) sb.deleteCharAt(sb.length - 1)
    sb.append("]")
    return sb.toString()
}

internal fun getItemInputListForMessage(array : Array<ItemPrototype>) : String {
    var sb = StringBuilder("[")
    array.forEach {
        sb.append("${it.exportItemInputJson(true)},")
    }
    if (array.isNotEmpty()) sb.deleteCharAt(sb.length - 1)
    sb.append("]")
    return when (sb.length) {
        2 -> "null"
        else -> sb.toString()
    }
}


internal fun getItemInputListForSigning(array : Array<ItemPrototype>) : String {
    var sb = StringBuilder("[")
    array.forEach {
        sb.append("${it.exportItemInputJson(false)},")
    }
    if (array.isNotEmpty()) sb.deleteCharAt(sb.length - 1)
    sb.append("]")
    return when (sb.length) {
        2 -> "null"
        else -> sb.toString()
    }
}

internal fun getItemOutputListForMessage(array : Array<ItemPrototype>) : String {
    var sb = StringBuilder("[")
    array.forEach {
        sb.append("${it.exportItemOutputJson(true)},")
    }
    if (array.isNotEmpty()) sb.deleteCharAt(sb.length - 1)
    sb.append("]")
    return when (sb.length) {
        2 -> "null"
        else -> sb.toString()
    }
}


internal fun getItemOutputListForSigning(array : Array<ItemPrototype>) : String {
    var sb = StringBuilder("[")
    array.forEach {
        sb.append("${it.exportItemOutputJson(false)},")
    }
    if (array.isNotEmpty()) sb.deleteCharAt(sb.length - 1)
    sb.append("]")
    return when (sb.length) {
        2 -> "null"
        else -> sb.toString()
    }
}

private fun pubkeyToString (pubkey: SECP256K1.PublicKey) = base64.encodeToString(CryptoCosmos.getCompressedPubkey(pubkey).toArray())