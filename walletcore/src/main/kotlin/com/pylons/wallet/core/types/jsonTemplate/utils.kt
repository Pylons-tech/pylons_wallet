package com.pylons.wallet.core.types.jsonTemplate

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.TxPylonsEngine
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.wallet.core.logging.LogEvent
import com.pylons.wallet.core.logging.LogTag
import com.pylons.wallet.core.logging.Logger
import com.pylons.wallet.core.types.HttpWire
import com.pylons.wallet.core.types.PylonsSECP256K1
import java.util.*

private val base64 = Base64.getEncoder()

internal fun Double.s() : String {
    val s = this.toString()
    return when (s.substring(s.length - 2, s.length)) {
        ".0" -> s.replace(".0", "")
        else -> s
    }
}

@ExperimentalUnsignedTypes
internal fun baseJsonWeldFlow (msg : String, signComponent : String, accountNumber: Long, sequence: Long, pubkey: PylonsSECP256K1.PublicKey) : String {
    val cryptoHandler = (Core.engine as TxPylonsEngine).cryptoHandler
    val signable = baseSignTemplate(signComponent, sequence, accountNumber)
    Logger().log(LogEvent.BASE_JSON_WELD_FLOW, "signable: $signable", LogTag.info)
    val signBytes = signable.toByteArray(Charsets.UTF_8)
    val signatureBytes = cryptoHandler.signature(signBytes)
    val signature = base64.encodeToString(signatureBytes)
    return baseTxTemplate(msg, pubkeyToString(pubkey), signature)
}



internal fun baseTxTemplate (msg : String, pubkey : String, signature : String) : String =
        """{
            "tx": {
                "msg": $msg,
    
                "fee": {
                "amount": null,
                "gas": "400000"
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

fun baseSignTemplate (msg : String, sequence: Long, accountNumber: Long) =
        """{"account_number":"$accountNumber","chain_id":"pylonschain","fee":{"amount":[],"gas":"400000"},"memo":"","msgs":$msg,"sequence":"$sequence"}"""

private fun pubkeyToString (pubkey: PylonsSECP256K1.PublicKey) = base64.encodeToString(CryptoCosmos.getCompressedPubkey(pubkey).toArray())