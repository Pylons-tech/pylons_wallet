package com.pylons.wallet.core.types.jsonTemplate

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.PylonsSECP256K1

internal fun sendCoins (denom: String, amount : Long, sender: String, receiver: String, pubkey: PylonsSECP256K1.PublicKey, accountNumber: Long, sequence: Long) : String =
        baseJsonWeldFlow(Core.statusBlock.height, sendCoinsMsgTemplate(denom, amount.toString(), sender, receiver), sendCoinsSignTemplate(denom, amount, sender, receiver),
                accountNumber, sequence, pubkey)

private fun sendCoinsMsgTemplate (denom : String, amount : String, sender : String, receiver : String) = """
            [
            {
                "type": "pylons/SendCoins",
                "value": {
                "Amount": [
                {
                    "denom": "$denom",
                    "amount": "$amount"
                }
                ],
                "Receiver": "$receiver",
                "Sender": "$sender"
            }
            }
            ]   
    """

fun sendCoinsSignTemplate (denom: String, amount: Long, sender : String, receiver: String) : String =
        """[{"Amount":[{"amount":"$amount","denom":"$denom"}],"Receiver":"$receiver","Sender":"$sender"}]"""