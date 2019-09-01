package com.pylons.wallet.core.types.txJson

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.TxPylonsEngine
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
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
    return baseTemplate(msg, pubkeyToString(pubkey), accountNumber.toString(), sequence.toString(), signature)
}

private fun baseTemplate (msg : String, pubkey : String, accountNumber : String, sequence: String, signature : String) : String =
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

internal fun getInputOutputListForMessage(map : Map<String, Long>) : String {
    var sb = StringBuilder("[")
    map.forEach {
        sb.append("""{"Count":"${it.value}","Item":"${it.key}"},""")
    }
    sb.deleteCharAt(sb.length - 1)
    sb.append("]")
    return sb.toString()
}

internal fun getInputOutputListForSigning(map : Map<String, Long>) : String {
    var sb = StringBuilder("[")
    map.forEach {
        sb.append("""{"Count":${it.value},"Item":"${it.key}"},""")
    }
    sb.deleteCharAt(sb.length - 1)
    sb.append("]")
    return sb.toString()
}

private fun pubkeyToString (pubkey: SECP256K1.PublicKey) = base64.encodeToString(CryptoCosmos.getCompressedPubkey(pubkey).toArray())