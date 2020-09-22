package com.pylons.wallet.ipc

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.ops.*
import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.Transaction.Companion.submitAll
import com.pylons.wallet.core.types.tx.Trade
import com.pylons.wallet.core.types.tx.recipe.Recipe
import java.lang.StringBuilder
import java.util.*
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.functions
import kotlin.reflect.full.companionObjectInstance

sealed class Message {
    class ApplyRecipe(
            var recipe : String? = null,
            var cookbook : String? = null,
            var itemInputs : List<String>? = null
    ) : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<ApplyRecipe>(json)
        }

        override fun resolve() =
                TxResponse(Core.applyRecipe(recipe!!, cookbook!!, itemInputs!!)).pack()
    }

    class CancelTrade (
            var tradeId : String? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<CancelTrade>(json)
        }

        override fun resolve() = TxResponse(Core.cancelTrade(tradeId!!)).pack()
    }

    class CheckExecution(
            var id : String? = null,
            var payForCompletion : Boolean? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<CheckExecution>(json)
        }

        override fun resolve() = TxResponse(Core.checkExecution(id!!, payForCompletion!!)).pack()
    }

    class CreateCookbooks(
            var ids : List<String>? = null,
            var names : List<String>? = null,
            var developers : List<String>? = null,
            var descriptions : List<String>? = null,
            var versions : List<String>? = null,
            var supportEmails : List<String>? = null,
            var levels : List<Long>? = null,
            var costsPerBlock : List<Long>? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<CreateCookbooks>(json)
        }

        override fun resolve() = TxResponse(Core.batchCreateCookbook(ids!!, names!!, developers!!,
                descriptions!!, versions!!, supportEmails!!, levels!!, costsPerBlock!!)).pack()
    }

    class CreateRecipes(
            var names : List<String>? = null,
            var cookbooks : List<String>? = null,
            var descriptions : List<String>? = null,
            var blockIntervals : List<Long>? = null,
            var coinInputs : List<String>? = null,
            var itemInputs: List<String>? = null,
            var outputTables : List<String>? = null,
            var outputs : List<String>? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<CreateRecipes>(json)
        }

        override fun resolve() = TxResponse(Core.batchCreateRecipe(names!!, cookbooks!!,
                descriptions!!, blockIntervals!!, coinInputs!!, itemInputs!!, outputTables!!,
                outputs!!)).pack()
    }

    class CreateTrade (
            var coinInputs : List<String>? = null,
            var itemInputs: List<String>? = null,
            var coinOutputs: List<String>? = null,
            var itemOutputs : List<String>? = null,
            var extraInfo : String? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<CreateTrade>(json)
        }

        override fun resolve() = TxResponse(Core.createTrade(coinInputs!!, itemInputs!!,
                coinOutputs!!, itemOutputs!!, extraInfo!!)).pack()
    }

    class DisableRecipes (
            var recipes : List<String>? = null
    ) : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<DisableRecipes>(json)
        }

        override fun resolve() = TxResponse(Core.batchDisableRecipe(recipes!!)).pack()
    }

    class EnableRecipes (
            var recipes : List<String>? = null
    ) : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<EnableRecipes>(json)
        }

        override fun resolve() = TxResponse(Core.batchEnableRecipe(recipes!!)).pack()
    }

    class FulfillTrade(
            var tradeId : String? = null,
            var itemIds : List<String>? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<FulfillTrade>(json)
        }

        override fun resolve() = TxResponse(Core.fulfillTrade(tradeId!!, itemIds!!)).pack()
    }

    class GetCookbooks : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetCookbooks>(json)
        }

        override fun resolve() = CookbookResponse(Core.getCookbooks()).pack()
    }

    class GetPendingExecutions : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetPendingExecutions>(json)
        }

        override fun resolve() = ExecutionResponse(Core.getPendingExecutions()).pack()
    }

    class GetProfile(
            var address : String? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetProfile>(json)
        }

        override fun resolve() = ProfileResponse(listOf(Core.getProfile(address)!!)).pack()
    }

    class GetPylons(
            var count : Long? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetPylons>(json)
        }

        override fun resolve() = TxResponse(Core.getPylons(count!!)).pack()
    }

    class GetRecipes : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetRecipes>(json)
        }

        override fun resolve() = RecipeResponse(Core.getRecipes()).pack()
    }

    class GoogleIapGetPylons(
            var productId : String? = null,
            var purchaseToken : String? = null,
            var receiptData : String? = null,
            var signature : String? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GoogleIapGetPylons>(json)
        }

        override fun resolve() = TxResponse(
                Core.googleIapGetPylons(productId!!, purchaseToken!!, receiptData!!, signature!!)).pack()
    }

    class GetTransaction(
            var txHash : String? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetTransaction>(json)
        }

        override fun resolve() = TxResponse(Core.getTransaction(txHash!!)).pack()
    }

    class RegisterProfile(
            var name : String? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<RegisterProfile>(json)
        }

        override fun resolve() = TxResponse(Core.newProfile(name!!)).pack()
    }

    class SendCoins(
            var coins : String?,
            var receiver : String?
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<SendCoins>(json)
        }

        override fun resolve() = TxResponse(Core.sendCoins(coins!!, receiver!!)).pack()
    }

    class SetItemString(
            var itemId : String? = null,
            var field : String? = null,
            var value : String? = null
    ) : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<SetItemString>(json)
        }

        override fun resolve() = TxResponse(Core.setItemString(itemId!!, field!!, value!!)).pack()
    }

    class UpdateCookbooks(
            var ids : List<String>? = null,
            var names : List<String>? = null,
            var developers : List<String>? = null,
            var descriptions : List<String>? = null,
            var versions : List<String>? = null,
            var supportEmails : List<String>? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<UpdateCookbooks>(json)
        }

        override fun resolve() = TxResponse(Core.batchUpdateCookbook(names!!, developers!!, descriptions!!,
                    versions!!, supportEmails!!, ids!!)).pack()

    }

    class UpdateRecipes(
            var ids : List<String>? = null,
            var names : List<String>? = null,
            var cookbooks : List<String>? = null,
            var descriptions : List<String>? = null,
            var blockIntervals : List<Long>? = null,
            var coinInputs : List<String>? = null,
            var itemInputs: List<String>? = null,
            var outputTables : List<String>? = null,
            var outputs : List<String>? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<UpdateRecipes>(json)
        }

        override fun resolve() = TxResponse(Core.batchUpdateRecipe(ids!!, names!!, cookbooks!!,
                descriptions!!, blockIntervals!!, coinInputs!!, itemInputs!!, outputTables!!,
                outputs!!)).pack()
    }

    class WalletServiceTest(
            val input : String? = null
    ) : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<WalletServiceTest>(json)
        }

        override fun resolve() = TestResponse(Core.walletServiceTest(input!!)).pack()
    }

    class WalletUiTest : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<WalletUiTest>(json)
        }

        override fun ui(): UiHook {
            // WalletUiTest never returns until this hook is released
            return UILayer.addUiHook(UiHook(this))
        }

        override fun resolve() = TestResponse(Core.walletUiTest()).pack()
    }

    companion object {
        fun match(json: String) : Message? {
            println("Trying to match message")
            val jsonObject = Parser.default().parse(StringBuilder(json)) as JsonObject
            IPCLayer.implementation.messageId = jsonObject.int("messageId")!!
            println("Set messageId to ${IPCLayer.implementation.messageId}")
            val cid = jsonObject.int("clientId")
            val mid = jsonObject.int("walletId")
            if (cid != IPCLayer.implementation.clientId || mid != IPCLayer.implementation.walletId)
                throw Exception("Client/wallet ID mismatch - got ${jsonObject.int("clientId").toString()} ${jsonObject.int("walletId").toString()}, expected ${IPCLayer.implementation.clientId} ${IPCLayer.implementation.walletId}")
            val type = jsonObject.string("type")!!
            val msg =
                    Base64.getDecoder().decode(
                            jsonObject.string("msg")!!).toString(Charsets.US_ASCII)
            var ret : Message? = null
            Message::class.sealedSubclasses.forEach { kClass ->
                if (kClass.simpleName == type) {
                    println("attempting to match ${kClass.simpleName}")
                    val func = kClass.companionObject?.functions?.find { it.name == "deserialize" }
                    ret = func?.call(kClass.companionObjectInstance, msg) as Message?
                }
            }
            return ret
        }
    }

    open class UiHook(val msg : Message) {

        var live : Boolean = true
            private set

        fun release() : UiHook {
            live = false
            return this
        }
    }

    data class Response (
            val messageId : Int,
            val clientId : Int,
            val walletId : Int,
            val statusBlock : StatusBlock,
            val responseData: ResponseData
    ) {
        fun submit() {
            IPCLayer.handleResponse(this)
        }
    }

    abstract class ResponseData {
        fun pack () : Response = Response(IPCLayer.implementation.messageId,
                IPCLayer.implementation.clientId, IPCLayer.implementation.walletId,
                Core.statusBlock, this)
    }

    class CookbookResponse (val cookbooks: List<Cookbook>) : ResponseData()
    class ExecutionResponse (val executions: List<Execution>) : ResponseData()
    class RecipeResponse (val recipes : List<Recipe>) : ResponseData()
    class ProfileResponse (val profiles : List<Profile>) : ResponseData()
    class TradeResponse (val trades : List<Trade>) : ResponseData()
    class TestResponse(val output : String) : ResponseData()

    class TxResponse : ResponseData {
        val transactions : List<Transaction>
        constructor(txs : List<Transaction>) {
            transactions = txs
        }
        constructor(tx : Transaction) {
            transactions = listOf(tx)
        }

    }

    open fun ui () : UiHook {
        // Default ui() implementation just forces the ui hook through its entire lifecycle immediately.
        // This simplifies the way we need to deal w/ UI interactions - every message creates
        // a ui hook; it's just that some of them don't actually need to do any work before they're
        // done with it.
        return UILayer.releaseUiHook(UILayer.addUiHook(UiHook(this)))
    }

    abstract fun resolve() : Response
}