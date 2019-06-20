package com.pylons.wallet.core.types

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.TxPylonsAlphaEngine
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import org.apache.tuweni.crypto.SECP256K1
import java.lang.StringBuilder
import java.nio.charset.Charset
import java.util.*

object TxJson {
    val base64 = Base64.getEncoder()
    private fun removeWhitespace (input : String) = input.trimIndent().replace("\\s".toRegex(), "")

    fun getPylons (amount : Int, address: String, pubkey: SECP256K1.PublicKey, accountNumber: Int, sequence: Int) : String {
        val cryptoHandler = (Core.engine as TxPylonsAlphaEngine).cryptoHandler
        val msg = msgTemplate_GetPylons(amount.toString(), address)
        val signable = msgTemplate_Signable(msg)

        val signature = base64.encodeToString(cryptoHandler.signature(signable.toByteArray(charset = Charset.defaultCharset())))
        return removeWhitespace(baseTemplate(msg, pubkeyToString(pubkey), accountNumber.toString(), sequence.toString(), signature))
    }

    private fun strFromBase64 (base64 : CharArray) : String {
        val sb = StringBuilder()
        base64.forEach { sb.append(it) }
        return sb.toString()
    }

    private fun pubkeyToString (pubkey: SECP256K1.PublicKey) = base64.encodeToString(CryptoCosmos.getCompressedPubkey(pubkey).toArray())

    private fun msgTemplate_Signable (msg : String) = removeWhitespace("""
            {"account_number":"4",
            "chain_id":"pylonschain",
            "fee":{"amount":null,"gas":"200000"},
            "memo":"",
            "msgs":$msg,
            "sequence":"0"}
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
                    "chain_id": "pylonschain",
                    "pub_key": {
                    "type": "tendermint/PubKeySecp256k1",
                    "value": "$pubkey"
                },
                      "account_number": "$accountNumber",
                    "sequence": "$sequence",
                    "signature": "$signature"
                }
                ],
                "memo": ""
            },
            "mode": "sync"
        }"""
}