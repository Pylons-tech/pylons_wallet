package com.pylons.wallet.core.types.txJson

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.TxPylonsEngine
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.wallet.core.types.item.Item
import com.pylons.wallet.core.types.item.prototype.ItemPrototype
import com.pylons.wallet.core.types.SECP256K1
import java.util.*

private val base64 = Base64.getEncoder()

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
    return sb.toString()
}


internal fun getItemInputListForSigning(array : Array<ItemPrototype>) : String {
    var sb = StringBuilder("[")
    array.forEach {
        sb.append("${it.exportItemInputJson(false)},")
    }
    if (array.isNotEmpty()) sb.deleteCharAt(sb.length - 1)
    sb.append("]")
    return sb.toString()
}

internal fun getItemOutputListForMessage(array : Array<ItemPrototype>) : String {
    var sb = StringBuilder("[")
    array.forEach {
        sb.append("${it.exportItemOutputJson(true)},")
    }
    if (array.isNotEmpty()) sb.deleteCharAt(sb.length - 1)
    sb.append("]")
    return sb.toString()
}


internal fun getItemOutputListForSigning(array : Array<ItemPrototype>) : String {
    var sb = StringBuilder("[")
    array.forEach {
        sb.append("${it.exportItemOutputJson(false)},")
    }
    if (array.isNotEmpty()) sb.deleteCharAt(sb.length - 1)
    sb.append("]")
    return sb.toString()
}

private fun doublesArrayForMessage (item : Item) : String {
    var sb = StringBuilder("{")
    item.doubles.forEach {
        sb.append(""""${it.key}":"${it.value}",""")
    }
    if (item.doubles.isNotEmpty()) sb.deleteCharAt(sb.length - 1)
    sb.append("}")
    return sb.toString()
}



private fun doublesArrayForSigning (item : Item) : String {
    var sb = StringBuilder("{")
    item.doubles.forEach {
        sb.append(""""${it.key}":${it.value},""")
    }
    if (item.doubles.isNotEmpty()) sb.deleteCharAt(sb.length - 1)
    sb.append("}")
    return sb.toString()
}

private fun longsArrayForMessage (item : Item) : String {
    var sb = StringBuilder("{")
    item.longs.forEach {
        sb.append(""""${it.key}"}:"${it.value}",""")
    }
    if (item.longs.isNotEmpty()) sb.deleteCharAt(sb.length - 1)
    sb.append("}")
    return sb.toString()
}

private fun longsArrayForSigning (item : Item) : String {
    var sb = StringBuilder("{")
    item.longs.forEach {
        sb.append(""""${it.key}":${it.value},""")
    }
    if (item.longs.isNotEmpty()) sb.deleteCharAt(sb.length - 1)
    sb.append("}")
    return sb.toString()
}

private fun stringsArray (item : Item) : String {
    var sb = StringBuilder("{")
    item.strings.forEach {
        sb.append(""""${it.key}":"${it.value}",""")
    }
    if (item.strings.isNotEmpty()) sb.deleteCharAt(sb.length - 1)
    sb.append("}")
    return sb.toString()
}

private fun exportItemJsonForMessage (item : Item) : String {
    return """
        {
            "CookbookID": "${item.cookbook}",
            "Doubles": ${doublesArrayForMessage(item)},
            "ID": "${item.id}",
            "Longs": ${longsArrayForMessage(item)},
            "Sender": "${item.sender}",
            "Strings": ${stringsArray(item)}
        }"""
}

private fun exportItemJsonForSigning (item : Item) : String =
    """{"CookbookID":"${item.cookbook}","Doubles":${doublesArrayForSigning(item)},"ID":"${item.id}",""" +
            """"Longs":${longsArrayForSigning(item)},"Sender":"${item.sender}","Strings":${stringsArray(item)}}"""

private fun pubkeyToString (pubkey: SECP256K1.PublicKey) = base64.encodeToString(CryptoCosmos.getCompressedPubkey(pubkey).toArray())