package com.pylons.wallet.core.types

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.TxPylonsAlphaEngine
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import org.bouncycastle.util.encoders.Hex

import java.lang.StringBuilder
import java.nio.charset.Charset
import java.util.*

object TxJson {
    val base64 = Base64.getEncoder()
    private fun removeWhitespace (input : String) = input.trimIndent().replace("\\s".toRegex(), "")

    private fun baseJsonWeldFlow (msg : String, signComponent : String, address: String, accountNumber: Int, sequence: Int,
                                  pubkey: SECP256K1.PublicKey) : String {
        val cryptoHandler = (Core.engine as TxPylonsAlphaEngine).cryptoHandler
        val signable = removeWhitespace(msgTemplate_Signable(signComponent, sequence, accountNumber, address))
        println("Signable:")
        println(signable)
        val signBytes = signable.toByteArray(Charsets.UTF_8)
        println(Hex.toHexString(signBytes))
        val signatureBytes = cryptoHandler.signature(signBytes)
        println("girish:")
        println(Hex.toHexString(cryptoHandler.signature("{}".toByteArray(Charsets.US_ASCII))))
        val signature = base64.encodeToString(signatureBytes)
        return baseTemplate(msg, pubkeyToString(pubkey), accountNumber.toString(), sequence.toString(), signature)
    }

    fun getPylons (amount : Int, address: String, pubkey: SECP256K1.PublicKey, accountNumber: Int, sequence: Int) =
            baseJsonWeldFlow(msgTemplate_GetPylons(amount.toString(), address), msgTemplate_SignComponent_GetPylons(amount),
                    address, accountNumber, sequence, pubkey)


    fun sendPylons (amount : Int, sender: String, receiver: String, pubkey: SECP256K1.PublicKey, accountNumber: Int, sequence: Int) : String =
            baseJsonWeldFlow(msgTemplate_SendPylons(amount.toString(), sender, receiver), msgTemplate_SignComponent_SendPylons(amount, sender, receiver),
                    sender, accountNumber, sequence, pubkey)

    private fun msgTemplate_SignComponent_GetPylons (amount: Int) : String =
            """{"Amount":[{"amount":"$amount","denom":"pylon"}]}"""

    private fun msgTemplate_SignComponent_SendPylons (amount: Int, sender : String, receiver: String) : String =
            """[{"Amount":[{"amount":"$amount","denom":"pylon"}],"Receiver":"$receiver","Sender":"$sender"}]"""

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
                "sequence": "6"
            }
            """)

    private fun msgTemplate_SendPylons (amount : String, sender : String, receiver : String) = """
            [
            {
                "type": "pylons/SendPylons",
                "value": {
                "Amount": [
                {
                    "denom": "pylon",
                    "amount": "$amount"
                }
                ],
                "Receiver": "$receiver",
                "Sender": "$sender"
                
               
            }
            }
            ]   
    """

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