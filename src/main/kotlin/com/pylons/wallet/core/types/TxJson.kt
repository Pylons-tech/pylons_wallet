package com.pylons.wallet.core.types

import com.jayway.jsonpath.JsonPath
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.TxPylonsAlphaEngine
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.wallet.core.types.SECP256K1
import org.bouncycastle.jcajce.provider.digest.SHA256
import org.bouncycastle.util.encoders.Hex

import java.lang.StringBuilder
import java.nio.charset.Charset
import java.util.*

object TxJson {
    private val url = """http://35.224.155.76:80"""
    val base64 = Base64.getEncoder()
    private fun removeWhitespace (input : String) = input.trimIndent().replace("\\s".toRegex(), "")

    fun getPylons (amount : Int, address: String, pubkey: SECP256K1.PublicKey, accountNumber: Int, sequence: Int) : String {
        val cryptoHandler = (Core.engine as TxPylonsAlphaEngine).cryptoHandler
        val msg = msgTemplate_GetPylons(amount.toString(), address)
        val sComponent = msgTemplate_SignComponent_GetPylons(amount)
        val signable = removeWhitespace(msgTemplate_Signable(sComponent, sequence, accountNumber, address))
        println(signable)
        val signBytes = signable.toByteArray(Charsets.UTF_8)
        val signatureBytes = cryptoHandler.signature(signBytes)
        val signature = base64.encodeToString( signatureBytes)
        return baseTemplate(msg, pubkeyToString(pubkey), accountNumber.toString(), sequence.toString(), signature)
    }

    private fun msgTemplate_SignComponent_GetPylons (amount: Int) : String {
        return """[{"Amount":[{"amount":"$amount","denom":"pylon"}]"""
    }

    private fun strFromBase64 (base64 : CharArray) : String {
        val sb = StringBuilder()
        base64.forEach { sb.append(it) }
        return sb.toString()
    }

    private fun pubkeyToString (pubkey: SECP256K1.PublicKey) = base64.encodeToString(CryptoCosmos.getCompressedPubkey(pubkey).toArray())

    private fun msgTemplate_Signable (msg : String, sequence: Int, accountNumber: Int, address: String) = removeWhitespace("""
            {
                "account_number": "$accountNumber",
                "chain_id": "pylonschain",
                "fee": {
                    "amount": [],
                    "gas": "200000"
                },
                "memo": "",
                "msgs": $msg,
                    "Requester": "$address"
                }],
                "sequence": "$sequence"
            }
            """)

    private fun msgTemplate_GetPylons (amount : String, address : String) = """
            [
            {
                "type": "pylons/GetPylons",
                "value": {
                "Amount": [
                {
                    "denom": "pylon",
                    "amount": "$amount"
                }
                ],
                "Requester": "$address"
            }
            }
            ]
        """

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
}