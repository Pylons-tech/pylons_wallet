package com.pylons.ipc

import com.pylons.lib.core.ICore
import com.pylons.lib.types.*
import com.pylons.lib.types.tx.Coin
import com.pylons.lib.types.tx.Trade
import com.pylons.lib.types.tx.item.Item
import com.pylons.lib.types.tx.msg.*
import com.pylons.lib.types.tx.recipe.Recipe

private const val MAX_TX_WAIT_RETRIES = 12

class Response (
    val message: Message,
    val accepted : Boolean,
    val messageId : Int,
    val clientId : Int,
    val walletId : Int,
    val statusBlock : StatusBlock,
    val coinsIn : List<Coin>,
    val coinsOut : List<Coin>,
    val cookbooksIn : List<Cookbook>,
    val cookbooksOut : List<Cookbook>,
    val executionsIn : List<Execution>,
    val executionsOut : List<Execution>,
    val itemsIn : List<Item>,
    val itemsOut : List<Item>,
    val profilesIn : List<Profile>,
    val profilesOut : List<Profile>,
    val recipesIn : List<Recipe>,
    val recipesOut : List<Recipe>,
    val tradesIn : List<Trade>,
    val tradesOut : List<Trade>,
    val txs : List<Transaction>,
    val unstructured : List<String> // this is used for exportkeys, mainly; we don't want actual named keys fields b/c
                                        // we don't really want end-users using exportkeys or anything else that uses unstructured data
) {
    companion object {
        protected var core : ICore? = null
        fun useCore(core : ICore) {
            println("ICore current ${Companion.core}")
            Companion.core = core
        }

        @ExperimentalUnsignedTypes
        fun emit(message: Message, accepted: Boolean, coinsIn: List<Coin> = listOf(), coinsOut: List<Coin> = listOf(),
                 cookbooksIn: List<String> = listOf(), cookbooksOut: List<Cookbook> = listOf(),
                 executionsIn: List<String> = listOf(), executionsOut: List<Execution> = listOf(),
                 itemsIn : List<String> = listOf(), itemsOut : List<Item> = listOf(),
                 profilesIn: List<String> = listOf(), profilesOut: List<Profile> = listOf(),
                 recipesIn: List<String> = listOf(), recipesOut: List<Recipe> = listOf(),
                 tradesIn : List<String> = listOf(), tradesOut: List<Trade> = listOf(),
                 unstructured: List<String> = listOf(), txs : List<Transaction> = listOf()) : Response {

            println("ICore current ${Companion.core}")

            val prf = core!!.getProfile(null)

            println("profile: ${prf}")

            // Before doing anything that changes state: retrieve all the inputs
            println("getCookbooks")

            val mCookbooksIn = mutableListOf<Cookbook>()
            if (cookbooksIn.isNotEmpty()) {
                /**
                 * We really have to retrieve _every_ cookbook rn. that's manageable in testnet but it's gonna
                 * be pretty fuckin' untenable in production. We absolutely need to have a way to retrieve single
                 * cookbooks by ID (or, better yet, sets of cookbooks by a list of ids) before it gets to that.
                 */
                val cbs = core!!.getCookbooks()
                cookbooksIn.forEach {
                    val id = it
                    cbs.forEach {
                        if (it.id == id) mCookbooksIn.add(it)
                    }
                }
            }

            println("getPendingExecutions")
            val mExecutionsIn = mutableListOf<Execution>()
            if (executionsIn.isNotEmpty()) {
                // we need better exec handling, getpendingexecutions isn't really enough
                val execs = core!!.getPendingExecutions()
                executionsIn.forEach {
                    val id = it
                    execs.forEach {
                        if (it.id == id) mExecutionsIn.add(it)
                    }
                }
            }
            println("items iter")
            val mItemsIn = mutableListOf<Item>()
            if (itemsIn.isNotEmpty()) {
                val items = prf!!.items
                itemsIn.forEach {
                    val id = it
                    items.forEach {
                        if (it.id == id) mItemsIn.add(it)
                    }
                }
            }
            println("getProfile")
            val mProfilesIn = mutableListOf<Profile>()
            if (profilesIn.isNotEmpty()) {
                profilesIn.forEach {
                    if (it == prf?.address) mProfilesIn.add(prf)
                    else {
                        val profile = core!!.getProfile(it)
                        if (profile != null) mProfilesIn.add(profile)
                    }
                }
            }
            println("getRecipes")
            val mRecipesIn = mutableListOf<Recipe>()
            if (recipesIn.isNotEmpty()) {
                val recipes = core!!.getRecipes()
                recipesIn.forEach {
                    val id = it
                    recipes.forEach {
                        if (it.id == id) mRecipesIn.add(it)
                    }
                }
            }
            println("listTrades")
            val mTradesIn = mutableListOf<Trade>()
            if (tradesIn.isNotEmpty()) {
                val trades = core!!.listTrades()
                tradesIn.forEach {
                    val id = it
                    trades.forEach {
                        if (it.id == id) mTradesIn.add(it)
                    }
                }
            }

            // That's done, so now we can actually deal w/ txs
            println("getTransaction")

            val mTxs = mutableListOf<Transaction>()
            txs.forEach {
                if (it.id != null) {
                    var retries = 0
                    var tx = core!!.getTransaction(it.id!!)
                    while (tx.code != Transaction.ResponseCode.OK && retries < MAX_TX_WAIT_RETRIES) {
                        tx = core!!.getTransaction(it.id!!)
                        retries++
                    }
                    mTxs.add(tx) // TODO: proper error handling (this doesn't have failed txs at all)
                }
            }

            val mCoinsOut = coinsOut.toMutableList()
            val mCookbooksOut = cookbooksOut.toMutableList()
            val mExecutionsOut = executionsOut.toMutableList()
            val mRecipesOut = recipesOut.toMutableList()
            val mTradesOut = tradesOut.toMutableList()
            val mItemsOut = itemsOut.toMutableList()
            // ...but now we have to inspect the emitted transaction and populate the various output fields
            println("tranaction switch")
            mTxs.forEach { transaction ->
                transaction.stdTx?.msg?.forEach { it ->
                    when (it::javaClass) {
                        //CancelTrade::javaClass -> {
                            // there's not any output here b/c the trade is just Not
                        //}
                        //CheckExecution::javaClass -> {
                            // we don't have any structured handling of checkexecution atm b/c it's an edge
                            // case that doesn't actually enable us to get anything useful out of the msg.
                            // either backend changes happen or we have to make another network hit to actually do
                            // anything useful w/ this.
                        //}
                        //CreateAccount::javaClass -> {
                            // same story, but this doesn't really matter b/c we always retrieve our profile regardless
                        //}
                        CreateCookbook::javaClass -> {
                            val msg = it as CreateCookbook
                            mCookbooksOut.add(Cookbook("", msg.cookbookId, msg.name, msg.description,
                            msg.version, msg.developer, msg.level, msg.sender, msg.supportEmail, msg.costPerBlock))
                        }
                        CreateRecipe::javaClass -> {
                            val msg = it as CreateRecipe
                            mRecipesOut.add(Recipe("", "", msg.sender, false, msg.name,
                            msg.cookbookId, msg.description, msg.blockInterval, msg.coinInputs, msg.itemInputs,
                            msg.entries, msg.outputs))
                        }
                        CreateTrade::javaClass -> {
                            val msg = it as CreateTrade
                            mTradesOut.add(Trade("", "", msg.coinInputs, msg.itemInputs, msg.coinOutputs, msg.itemOutputs,
                            msg.extraInfo, msg.sender, "", disabled = false, completed = false))
                        }
                        DisableRecipe::javaClass -> {
                            val r = mRecipesIn.first()
                            mRecipesOut.add(Recipe("", r.id, r.sender, true, r.name, r.cookbookId, r.description,
                            r.blockInterval, r.coinInputs, r.itemInputs, r.entries, r.outputs))
                        }
                        EnableRecipe::javaClass -> {
                            val r = mRecipesIn.first()
                            mRecipesOut.add(Recipe("", r.id, r.sender, false, r.name, r.cookbookId, r.description,
                                r.blockInterval, r.coinInputs, r.itemInputs, r.entries, r.outputs))
                        }
                        //ExecuteRecipe::javaClass -> {
                        // not enough data in the msg to reconstruct the outputs w/o an extra query, b/c recipes
                        // have randomness to contend with
                        //}
                        FulfillTrade::javaClass -> {
                            val trade = mTradesIn.first()
                            mCoinsOut.addAll(trade.coinOutputs)
                            mItemsOut.addAll(trade.itemOutputs)
                        }
                        GetPylons::javaClass -> {
                            mCoinsOut.addAll((it as GetPylons).amount)
                        }
                        SendCoins::javaClass -> {
                            mCoinsOut.addAll((it as SendCoins).amount)
                        }
                        UpdateCookbook::javaClass -> {
                            val msg = it as UpdateCookbook
                            // we can't get cookbook name/level from updatecookbook, but it'll be in mCookbooksIn
                            mCookbooksOut.add(
                                Cookbook("", msg.id, mCookbooksIn.first().name, msg.description, msg.version,
                            msg.developer, mCookbooksIn.first().level, msg.sender, msg.supportEmail,
                                    mCookbooksIn.first().costPerBlock)
                            )
                        }
                        UpdateItemString::javaClass -> {
                            // This is the fuckiest thing we actually have enough data to handle
                            val msg = it as UpdateItemString
                            var baseItem : Item? = null
                            mItemsIn.forEach {
                                if (it.id == msg.itemId)
                                    baseItem = it
                            }
                            if (baseItem != null) {
                                val mItemStrings = baseItem!!.strings.toMutableMap()
                                mItemStrings[msg.field] = msg.value
                                // lastupdate will always be wrong here tho, and there's no way to get that correct
                                // w/o querying the item again...
                                mItemsOut.add(Item(baseItem!!.nodeVersion, baseItem!!.id, baseItem!!.cookbookId,
                                baseItem!!.sender, baseItem!!.ownerRecipeID, baseItem!!.ownerTradeID, baseItem!!.tradable,
                                0, baseItem!!.doubles, baseItem!!.longs, mItemStrings,
                                baseItem!!.transferFee))
                            }
                        }
                        UpdateRecipe::javaClass -> {
                            val msg = it as UpdateRecipe
                            mRecipesOut.add(Recipe("", msg.id, msg.sender, false, msg.name, msg.cookbookId,
                            msg.description, msg.blockInterval, msg.coinInputs, msg.itemInputs, msg.entries, msg.outputs))
                        }
                        //SendItems::javaClass -> {
                            // like canceltrade - this has inputs but no real output
                        //}
                        //GoogleIapGetPylons::javaClass -> {
                            // this requires us to have the ability to go between product id and actual pylons counts
                        //}
                    }
                }
            }
            println("mProfilesOut")
            val mProfilesOut = profilesOut.toMutableList()
            if (txs.isNotEmpty()) mProfilesOut.add(core!!.getProfile(null)!!)
            println("make reponse")
            return Response(message, accepted, IPCLayer.implementation!!.messageId, IPCLayer.implementation!!.clientId,
            IPCLayer.implementation!!.walletId, core!!.statusBlock, coinsIn, mCoinsOut, mCookbooksIn, mCookbooksOut,
                mExecutionsIn, mExecutionsOut, mItemsIn, mItemsOut, mProfilesIn, mProfilesOut, mRecipesIn, mRecipesOut,
                mTradesIn, mTradesOut, mTxs, unstructured)
        }
    }
}