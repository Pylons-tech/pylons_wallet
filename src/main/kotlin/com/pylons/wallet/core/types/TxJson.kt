package com.pylons.wallet.core.types

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.TxPylonsEngine
import com.pylons.wallet.core.engine.crypto.CryptoCosmos

import java.util.*
import kotlin.text.StringBuilder

object TxJson {
    val base64 = Base64.getEncoder()
    private fun removeWhitespace (input : String) = input.trimIndent().replace("\\s".toRegex(), "")

    private fun baseJsonWeldFlow (msg : String, signComponent : String, accountNumber: Int, sequence: Int, pubkey: SECP256K1.PublicKey) : String {
        val cryptoHandler = (Core.engine as TxPylonsEngine).cryptoHandler
        val signable = msgTemplate_Signable(signComponent, sequence, accountNumber)
        println("Signable:")
        println(signable)
        val signBytes = signable.toByteArray(Charsets.UTF_8)
        val signatureBytes = cryptoHandler.signature(signBytes)
        val signature = base64.encodeToString(signatureBytes)
        return baseTemplate(msg, pubkeyToString(pubkey), accountNumber.toString(), sequence.toString(), signature)
    }

    fun getPylons (amount : Int, address: String, pubkey: SECP256K1.PublicKey, accountNumber: Int, sequence: Int) =
            baseJsonWeldFlow(msgTemplate_GetPylons(amount.toString(), address), msgTemplate_SignComponent_GetPylons(amount),
                    accountNumber, sequence, pubkey)

    fun createRecipe (id : String, cookbookName : String, desc : String, inputs : Map<String, Int>, outputs : Map<String, Int>,
                      time : Int, sender : String,  pubkey: SECP256K1.PublicKey, accountNumber: Int, sequence: Int) =
            baseJsonWeldFlow(msgTemplate_CreateRecipe(id, cookbookName, desc, getInputOutputListForMessage(inputs),
                    getInputOutputListForMessage(outputs), time, sender),
                    msgTemplate_SignComponent_CreateRecipe(cookbookName, desc, time, id,
                    getInputOutputListForSigning(inputs), getInputOutputListForSigning(outputs), sender),
                    accountNumber, sequence, pubkey)

    fun sendPylons (amount : Int, sender: String, receiver: String, pubkey: SECP256K1.PublicKey, accountNumber: Int, sequence: Int) : String =
            baseJsonWeldFlow(msgTemplate_SendPylons(amount.toString(), sender, receiver), msgTemplate_SignComponent_SendPylons(amount, sender, receiver),
                    accountNumber, sequence, pubkey)

    fun createCookbook (name : String, devel : String, desc : String, version : String,
                        supportEmail : String, level : Int, sender : String,
                        pubkey: SECP256K1.PublicKey, accountNumber: Int, sequence: Int) : String =
            baseJsonWeldFlow(msgTemplate_CreateCookbook(name, devel, desc, version, supportEmail, level, sender),
                    msgTemplate_SignComponent_CreateCookbook(name, devel, desc, version, supportEmail, level, sender),
                    accountNumber, sequence, pubkey)

    fun updateCookbook (id : String, devel : String, desc : String, version : String,
                        supportEmail : String, sender : String,
                        pubkey: SECP256K1.PublicKey, accountNumber: Int, sequence: Int) : String =
            baseJsonWeldFlow(msgTemplate_UpdateCookbook(id, devel, desc, version, supportEmail, sender),
                    msgTemplate_SignComponent_UpdateCookbook(id, devel, desc, version, supportEmail, sender),
                    accountNumber, sequence, pubkey)

    private fun getInputOutputListForMessage(map : Map<String, Int>) : String {
        var sb = StringBuilder("[")
        map.forEach {
            sb.append("""{"Count":"${it.value}","Item":"${it.key}"},""")
        }
        sb.deleteCharAt(sb.length - 1)
        sb.append("]")
        return sb.toString()
    }

    private fun getInputOutputListForSigning(map : Map<String, Int>) : String {
        var sb = StringBuilder("[")
        map.forEach {
            sb.append("""{"Count":${it.value},"Item":"${it.key}"},""")
        }
        sb.deleteCharAt(sb.length - 1)
        sb.append("]")
        return sb.toString()
    }

    private fun msgTemplate_SignComponent_GetPylons (amount: Int) : String =
            """{"Amount":[{"amount":"$amount","denom":"pylon"}]}"""

    private fun msgTemplate_SignComponent_SendPylons (amount: Int, sender : String, receiver: String) : String =
            """[{"Amount":[{"amount":"$amount","denom":"pylon"}],"Receiver":"$receiver","Sender":"$sender"}]"""

    private fun msgTemplate_SignComponent_CreateCookbook (name : String, devel : String, desc : String, version : String,
                                                          supportEmail : String, level : Int, sender : String) : String =
            """[{"Description":"$desc","Developer":"$devel","Level":$level,"Name":"$name","Sender":"$sender","SupportEmail":"$supportEmail","Version":"$version"}]"""

    private fun msgTemplate_SignComponent_CreateRecipe (cookbookName: String, desc: String, time: Int, id: String, inputs: String, outputs: String, sender: String) =
            """[{"CookbookName":"$cookbookName","Description":"$desc","ExecutionTime":$time,"ID":"$id","Inputs":$inputs,"Outputs":$outputs,"Sender":$sender}]"""

    private fun msgTemplate_SignComponent_UpdateCookbook (id : String, devel : String, desc : String, version : String,
                                                          supportEmail : String, sender : String) : String =
            """[{"Description":"$desc","Developer":"$devel","ID":"$id","Sender":"$sender","SupportEmail":"$supportEmail","Version":"$version"}]"""

    private fun strFromBase64 (base64 : CharArray) : String {
        val sb = StringBuilder()
        base64.forEach { sb.append(it) }
        return sb.toString()
    }

    private fun pubkeyToString (pubkey: SECP256K1.PublicKey) = base64.encodeToString(CryptoCosmos.getCompressedPubkey(pubkey).toArray())

    private fun msgTemplate_Signable (msg : String, sequence: Int, accountNumber: Int) =
            """{"account_number":"$accountNumber","chain_id":"pylonschain","fee":{"amount":[],"gas":"200000"},"memo":"","msgs":$msg,"sequence":"$sequence"}"""

    private fun msgTemplate_CreateCookbook (name : String, devel : String, desc : String, version : String,
                                            supportEmail : String, level : Int, sender : String) = """
        [
        {
            "type": "pylons/CreateCookbook",
            "value": {
                "Name": "$name",
                "Description": "$desc",
                "Developer": "$devel",
                "Version": "$version",
                "SupportEmail": "$supportEmail",
                "Level": "$level",
                "Sender": "$sender"
            }
        }
        ]
    """

    private fun msgTemplate_CreateRecipe (id : String, cookbookName : String, desc : String, inputs : String, outputs : String,
                                          time : Int, sender : String) = """
        [
        {
            "type": "pylons/CreateRecipe",
            "value": {
                "ID": "$id",
                "CookbookName": "$cookbookName",
                "Description": "$desc",
                "Inputs": $inputs,
                "Outputs": $outputs,
                "ExecutionTime": "$time",
                "Sender": "$sender"
            }
        }
        ]     
    """

    private fun msgTemplate_UpdateCookbook (id : String, devel : String, desc : String, version : String,
                                            supportEmail : String, sender : String) = """
        [
        {
            "type": "pylons/UpdateCookbook",
            "value": {
                "ID": "$id",
                "Description": "$desc",
                "Developer": "$devel",
                "Version": "$version",
                "SupportEmail": "$supportEmail",
                "Sender": "$sender"
            }
        }
        ]
    """

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