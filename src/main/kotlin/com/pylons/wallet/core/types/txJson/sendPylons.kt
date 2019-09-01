package com.pylons.wallet.core.types.txJson

import com.pylons.wallet.core.types.SECP256K1

internal fun sendPylons (amount : Long, sender: String, receiver: String, pubkey: SECP256K1.PublicKey, accountNumber: Long, sequence: Long) : String =
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

internal fun sendPylonsSignTemplate (amount: Long, sender : String, receiver: String) : String =
        """[{"Amount":[{"amount":"$amount","denom":"pylon"}],"Receiver":"$receiver","Sender":"$sender"}]"""