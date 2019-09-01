package com.pylons.wallet.core.types.txJson

import com.pylons.wallet.core.types.SECP256K1

internal fun getPylons (amount : Long, address: String, pubkey: SECP256K1.PublicKey, accountNumber: Long, sequence: Long) =
        baseJsonWeldFlow(getPylonsMsgTemplate(amount.toString(), address), getPylonsSignTemplate(amount),
                accountNumber, sequence, pubkey)

fun getPylonsMsgTemplate (amount : String, address : String) = """
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

internal fun getPylonsSignTemplate (amount: Long) : String =
        """{"Amount":[{"amount":"$amount","denom":"pylon"}]}"""