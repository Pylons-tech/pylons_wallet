package com.pylons.wallet.core.types.txJson

import com.pylons.wallet.core.types.item.prototype.ItemPrototype
import com.pylons.wallet.core.types.SECP256K1

internal fun updateRecipe (name: String, cookbookName : String, id: String, desc : String, coinInputs : Map<String, Long>,
                           coinOutputs : Map<String, Long>, itemInputs: Array<ItemPrototype>, itemOutputs : Array<ItemPrototype>,
                           time : Long, sender : String, pubkey: SECP256K1.PublicKey, accountNumber: Long, sequence: Long) =
        baseJsonWeldFlow(updateRecipeMsgTemplate(name, cookbookName, id, desc, getCoinIOListForMessage(coinInputs),
                getCoinIOListForMessage(coinOutputs), getItemInputListForMessage(itemInputs), getItemOutputListForMessage(itemOutputs), time, sender),
                updateRecipeSignTemplate(name, cookbookName, id, desc, time,
                        getCoinIOListForSigning(coinInputs), getCoinIOListForSigning(coinOutputs), getItemInputListForSigning(itemInputs), getItemOutputListForSigning(itemOutputs),
                        sender),
                accountNumber, sequence, pubkey)

private fun updateRecipeMsgTemplate (name : String, cookbookName : String, id : String, desc : String, coinInputs : String, coinOutputs : String,
                                     itemInputs : String, itemOutputs: String, time : Long, sender : String) = """
        [
        {
            "type": "pylons/UpdateRecipe",
            "value": {
                "RecipeName": "$name",
                "ID":"$id",
                "CookbookId": "$cookbookName",
                "Description": "$desc",
                "CoinInputs": $coinInputs,
                "CoinOutputs": $coinOutputs,
                "ItemInputs": $itemInputs,
                "ItemOutputs": $itemOutputs,
                "BlockInterval": "$time",
                "Sender": "$sender"
            }
        }
        ]     
    """

internal fun updateRecipeSignTemplate (name : String, cookbook: String, id : String, desc: String, time: Long, coinInputs : String, coinOutputs : String,
                                       itemInputs : String, itemOutputs: String, sender: String) =
        """[{"BlockInterval":$time,"CoinInputs":$coinInputs,"CoinOutputs":$coinOutputs,"CookbookId":"$cookbook","Description":"$desc",""" +
                """"ID":"$id","ItemInputs":$itemInputs,"ItemOutputs":$itemOutputs,"RecipeName":"$name","Sender":"$sender"}]"""

