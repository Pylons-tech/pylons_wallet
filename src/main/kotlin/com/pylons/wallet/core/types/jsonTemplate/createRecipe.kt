package com.pylons.wallet.core.types.jsonTemplate

import com.pylons.wallet.core.types.ParamSet
import com.pylons.wallet.core.types.item.prototype.ItemPrototype
import com.pylons.wallet.core.types.SECP256K1

internal fun createRecipe (name: String, cookbook : String, desc : String, coinInputs : Map<String, Long>, itemInputs: Array<ItemPrototype>,
                           entries : ParamSet, time : Long, sender : String, pubkey: SECP256K1.PublicKey,
                           accountNumber: Long, sequence: Long) =
        baseJsonWeldFlow(createRecipeMsgTemplate(name, cookbook, desc, getCoinIOListForMessage(coinInputs),
                getItemInputListForMessage(itemInputs), entries.toJson(false),
                time, sender),
                createRecipeSignTemplate(name, cookbook, desc, time,
                        getCoinIOListForSigning(coinInputs), getItemInputListForMessage(itemInputs),
                        entries.toJson(true), sender),
                accountNumber, sequence, pubkey)

private fun createRecipeMsgTemplate (name : String, cookbook : String, desc : String, coinInputs: String, itemInputs: String, entries : String, time : Long, sender : String) = """
        [
        {
            "type": "pylons/CreateRecipe",
            "value": {
                "RecipeName": "$name",
                "CookbookId": "$cookbook",
                "Description": "$desc",
                "CoinInputs": $coinInputs,
                "ItemInputs": $itemInputs,
                "Entries": $entries
                "BlockInterval": "$time",
                "Sender": "$sender"
            }
        }
        ]     
    """

internal fun createRecipeSignTemplate (name : String, cookbook: String, desc: String, time: Long, coinInputs: String,
                                       itemInputs: String, entries: String, sender: String) =
        """[{"BlockInterval":$time,"CoinInputs":$coinInputs,"CookbookId":"$cookbook","Description":"$desc",""" +
            """"Entries":$entries,"ItemInputs":$itemInputs,"RecipeName":"$name","Sender":"$sender"}]"""
