package com.pylons.wallet.core.types.jsonTemplate

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.PylonsSECP256K1

@ExperimentalUnsignedTypes
fun getPylons (amount : Long, address: String, pubkey: PylonsSECP256K1.PublicKey, accountNumber: Long, sequence: Long) =
        baseJsonWeldFlow(Core.statusBlock.height, getPylonsMsgTemplate(amount.toString(), address), getPylonsSignTemplate(amount),
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