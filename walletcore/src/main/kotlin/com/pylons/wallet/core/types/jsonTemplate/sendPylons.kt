package com.pylons.wallet.core.types.jsonTemplate

import com.pylons.wallet.core.types.PylonsSECP256K1

internal fun sendPylons (amount : Long, sender: String, receiver: String, pubkey: PylonsSECP256K1.PublicKey, accountNumber: Long, sequence: Long) : String =
        baseJsonWeldFlow(sendPylonsMsgTemplate(amount.toString(), sender, receiver), sendPylonsSignTemplate(amount, sender, receiver),
                accountNumber, sequence, pubkey)

private fun sendPylonsMsgTemplate (amount : String, sender : String, receiver : String) = """
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

fun sendPylonsSignTemplate (amount: Long, sender : String, receiver: String) : String =
        """[{"Amount":[{"amount":"$amount","denom":"pylon"}],"Receiver":"$receiver","Sender":"$sender"}]"""