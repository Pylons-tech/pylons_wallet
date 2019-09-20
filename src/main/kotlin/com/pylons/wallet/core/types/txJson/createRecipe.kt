package com.pylons.wallet.core.types.txJson

import com.pylons.wallet.core.types.item.prototype.ItemPrototype
import com.pylons.wallet.core.types.SECP256K1

internal fun createRecipe (name: String, cookbook : String, desc : String, coinInputs : Map<String, Long>, coinOutputs : Map<String, Long>,
                           itemInputs: Array<ItemPrototype>, itemOutputs : Array<ItemPrototype>, time : Long, sender : String, pubkey: SECP256K1.PublicKey,
                           accountNumber: Long, sequence: Long) =
        baseJsonWeldFlow(createRecipeMsgTemplate(name, cookbook, desc, getCoinIOListForMessage(coinInputs),
                getCoinIOListForMessage(coinOutputs), getItemInputListForMessage(itemInputs), getItemOutputListForMessage(itemOutputs),
                time, sender),
                createRecipeSignTemplate(name, cookbook, desc, time,
                        getCoinIOListForSigning(coinInputs), getCoinIOListForSigning(coinOutputs),
                        getItemInputListForMessage(itemInputs), getItemOutputListForMessage(itemOutputs), sender),
                accountNumber, sequence, pubkey)

private fun createRecipeMsgTemplate (name : String, cookbook : String, desc : String, coinInputs : String, coinOutputs : String,
                                     itemInputs : String, itemOutputs: String, time : Long, sender : String) = """
        [
        {
            "type": "pylons/CreateRecipe",
            "value": {
                "RecipeName": "$name",
                "CookbookId": "$cookbook",
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

internal fun createRecipeSignTemplate (name : String, cookbook: String, desc: String, time: Long, coinInputs: String, coinOutputs: String,
                                       itemInputs: String, itemOutputs: String, sender: String) =
        """[{"BlockInterval":$time,"CoinInputs":$coinInputs,"CoinOutputs":$coinOutputs,"CookbookId":"$cookbook","Description":"$desc",""" +
            """"ItemInputs":$itemInputs,"ItemOutputs":$itemOutputs,"RecipeName":"$name","Sender":"$sender"}]"""
