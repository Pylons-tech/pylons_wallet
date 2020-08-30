package com.pylons.wallet.ipc

import com.beust.klaxon.JsonObject
import com.beust.klaxon.KlaxonException
import com.beust.klaxon.Parser
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.ops.*
import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.tx.recipe.CoinInput
import com.pylons.wallet.core.types.tx.recipe.ItemInput
import com.pylons.wallet.core.types.tx.recipe.Recipe
import java.lang.StringBuilder
import java.util.*
import kotlin.math.cos
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.functions
import kotlin.reflect.full.companionObjectInstance

sealed class Message {
    class ApplyRecipe(
            var recipe : String? = null,
            var cookbook : String? = null,
            var itemInputs : List<String>? = null
    ) : Message() {
        class Response(tx : Transaction): Message.Response() {
            val txhash = tx.id?: ""
            val state = tx.state
            val error = tx.txError?: listOf()
        }

        companion object {
            fun deserialize(json: String) = klaxon.parse<ApplyRecipe>(json)
        }

        override fun resolve() =
                listOf(Response(Core.applyRecipe(recipe!!, cookbook!!, itemInputs!!).submit()))
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
        class Response(tx : Transaction): Message.Response() {
            val txhash = tx.id?: ""
            val state = tx.state
            val error = tx.txError?: listOf()
        }

        companion object {
            fun deserialize(json : String) = klaxon.parse<CreateCookbooks>(json)
        }

        override fun resolve() : List<Response> {
            val l = mutableListOf<Response>()
            val r = Core.batchCreateCookbook(ids!!, names!!, developers!!, descriptions!!,
            versions!!, supportEmails!!, levels!!, costsPerBlock!!)
            r.forEach { l.add(Response(it)) }
            return l
        }
    }

    class UpdateCookbooks(
            var ids : List<String>? = null,
            var names : List<String>? = null,
            var developers : List<String>? = null,
            var descriptions : List<String>? = null,
            var versions : List<String>? = null,
            var supportEmails : List<String>? = null
    ) : Message() {
        class Response(tx : Transaction): Message.Response() {
            val txhash = tx.id?: ""
            val state = tx.state
            val error = tx.txError?: listOf()
        }

        companion object {
            fun deserialize(json : String) = klaxon.parse<UpdateCookbooks>(json)
        }

        override fun resolve() : List<Response> {
            val l = mutableListOf<Response>()
            val r = Core.batchUpdateCookbook(names!!, developers!!, descriptions!!,
                    versions!!, supportEmails!!, ids!!)
            r.forEach { l.add(Response(it)) }
            return l
        }
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
        class Response(tx : Transaction): Message.Response() {
            val txhash = tx.id?: ""
            val state = tx.state
            val error = tx.txError?: listOf()
        }

        companion object {
            fun deserialize(json : String) = klaxon.parse<CreateRecipes>(json)
        }

        override fun resolve() : List<Response> {
            val l = mutableListOf<Response>()
            val r = Core.batchCreateRecipe(names!!, cookbooks!!, descriptions!!,
                    blockIntervals!!, coinInputs!!, itemInputs!!,
                    outputTables!!, outputs!!)
            r.forEach { l.add(Response(it)) }
            return l
        }
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
        class Response(tx : Transaction): Message.Response() {
            val txhash = tx.id?: ""
            val state = tx.state
            val error = tx.txError?: listOf()
        }

        companion object {
            fun deserialize(json : String) = klaxon.parse<UpdateRecipes>(json)
        }

        override fun resolve() : List<Response> {
            val l = mutableListOf<Response>()
            val r = Core.batchUpdateRecipe(ids!!, names!!, cookbooks!!, descriptions!!,
                    blockIntervals!!, coinInputs!!, itemInputs!!, outputTables!!, outputs!!)
            r.forEach { l.add(Response(it)) }
            return l
        }
    }

    class CheckExecution(
            var id : String? = null,
            var payForCompletion : Boolean? = null
    ) : Message() {
        class Response(tx : Transaction): Message.Response() {
            val txhash = tx.id?: ""
            val state = tx.state
            val error = tx.txError?: listOf()
        }

        companion object {
            fun deserialize(json : String) = klaxon.parse<CheckExecution>(json)
        }

        override fun resolve() =
                listOf(Response(Core.checkExecution(id!!, payForCompletion!!)))
    }

    class CreateTrade (
            var coinInputs : List<String>? = null,
            var itemInputs: List<String>? = null,
            var coinOutputs: List<String>? = null,
            var itemOutputs : List<String>? = null,
            var extraInfo : String? = null
    ) : Message() {
        class Response(tx : Transaction): Message.Response() {
            val txhash = tx.id?: ""
            val state = tx.state
            val error = tx.txError?: listOf()
        }

        companion object {
            fun deserialize(json : String) = klaxon.parse<CreateTrade>(json)
        }

        override fun resolve() =
                listOf(Response(Core.createTrade(coinInputs!!, itemInputs!!,
                        coinOutputs!!, itemOutputs!!, extraInfo!!)))
    }

    class CancelTrade(
            var tradeId : String? = null
    ) : Message() {
        class Response(tx : Transaction): Message.Response() {
            val txhash = tx.id?: ""
            val state = tx.state
            val error = tx.txError?: listOf()
        }

        companion object {
            fun deserialize(json : String) = klaxon.parse<CancelTrade>(json)
        }

        override fun resolve() =
                listOf(Response(Core.cancelTrade(tradeId!!)))
    }

    class EnableRecipes (var recipes : List<String>? = null) : Message() {
        class Response(tx : Transaction): Message.Response() {
            val txhash = tx.id?: ""
            val state = tx.state
            val error = tx.txError?: listOf()
        }

        companion object {
            fun deserialize(json: String) = klaxon.parse<EnableRecipes>(json)
        }

        override fun resolve(): List<Response> {
            val l = mutableListOf<Response>()
            val r = Core.batchEnableRecipe(recipes!!)
            r.forEach { l.add(Response(it)) }
            return l
        }
    }

    class DisableRecipes (var recipes : List<String>? = null) : Message() {
        class Response(tx : Transaction): Message.Response() {
            val txhash = tx.id?: ""
            val state = tx.state
            val error = tx.txError?: listOf()
        }

        companion object {
            fun deserialize(json: String) = klaxon.parse<DisableRecipes>(json)
        }

        override fun resolve(): List<Response> {
            val l = mutableListOf<Response>()
            val r = Core.batchDisableRecipe(recipes!!)
            r.forEach { l.add(Response(it)) }
            return l
        }
    }

    class FulfillTrade(
            var tradeId : String? = null
    ) : Message() {
        class Response(tx : Transaction): Message.Response() {
            val txhash = tx.id?: ""
            val state = tx.state
            val error = tx.txError?: listOf()
        }

        companion object {
            fun deserialize(json : String) = klaxon.parse<FulfillTrade>(json)
        }

        override fun resolve() =
                listOf(Response(Core.fulfillTrade(tradeId!!)))
    }

    class GetCookbooks : Message() {
        class Response(val cookbooks : List<Cookbook>): Message.Response()

        companion object {
            fun deserialize(json : String) = klaxon.parse<GetCookbooks>(json)
        }

        override fun resolve() = listOf(Response(Core.getCookbooks()))
    }

    class GetPendingExecutions : Message() {
        class Response(val executions : List<Execution>): Message.Response()

        companion object {
            fun deserialize(json : String) = klaxon.parse<GetPendingExecutions>(json)
        }

        override fun resolve() = listOf(Response(Core.getPendingExecutions()))
    }

    class GetProfile(private val addr : String? = null) : Message() {
        class Response(val profile : Profile?): Message.Response()

        companion object {
            fun deserialize(json : String) = klaxon.parse<GetProfile>(json)
        }

        override fun resolve() = listOf(Response(Core.getProfile(addr)))
    }

    class GetPylons(private val count : Long? = null) : Message() {
        class Response(tx : Transaction): Message.Response() {
            val txhash = tx.id?: ""
            val state = tx.state
            val error = tx.txError?: listOf()
        }

        companion object {
            fun deserialize(json : String) = klaxon.parse<GetPylons>(json)
        }

        override fun resolve() = listOf(Response(Core.getPylons(count!!)))
    }

    class GetRecipes : Message() {
        class Response(val recipes : List<Recipe>) : Message.Response()

        companion object {
            fun deserialize(json : String) = klaxon.parse<GetRecipes>(json)
        }

        override fun resolve() = listOf(Response(Core.getRecipes()))
    }

    class GetTransaction(val txhash : String? = null) : Message() {
        class Response(val tx : Transaction) : Message.Response()

        companion object {
            fun deserialize(json : String) = klaxon.parse<GetTransaction>(json)
        }

        override fun resolve() = listOf(Response(Core.getTransaction(txhash!!)))
    }

    class RegisterProfile(val name : String? = null) : Message() {
        class Response(tx : Transaction): Message.Response() {
            val txhash = tx.id?: ""
            val state = tx.state
            val error = tx.txError?: listOf()
        }

        companion object {
            fun deserialize(json : String) = klaxon.parse<RegisterProfile>(json)
        }

        override fun resolve() = listOf(Response(Core.newProfile(name!!)))
    }

    class SendPylons(val addr : String? = null, val q : Long? = null) : Message() {
        class Response(tx : Transaction): Message.Response() {
            val txhash = tx.id?: ""
            val state = tx.state
            val error = tx.txError?: listOf()
        }

        companion object {
            fun deserialize(json : String) = klaxon.parse<SendPylons>(json)
        }

        override fun resolve() = listOf(Response(Core.sendPylons(q!!, addr!!)))
    }

    class SetItemString(val itemId : String? = null, val field : String? = null,
                        val value : String? = null) : Message() {
        class Response(tx: Transaction) : Message.Response() {
            val txhash = tx.id ?: ""
            val state = tx.state
            val error = tx.txError ?: listOf()
        }

        companion object {
            fun deserialize(json: String) = klaxon.parse<SetItemString>(json)
        }

        override fun resolve() = listOf(Response(Core.setItemString(itemId!!, field!!, value!!)))
    }

    class WalletServiceTest(val str : String? = null) : Message() {
        class Response(val str : String) : Message.Response()

        companion object {
            fun deserialize(json: String) = klaxon.parse<WalletServiceTest>(json)
        }

        override fun resolve() = listOf(Response(Core.walletServiceTest(str!!)))
    }

    class WalletUiTest : Message() {
        class Response(val str : String) : Message.Response()

        companion object {
            fun deserialize(json: String) = klaxon.parse<WalletUiTest>(json)
        }

        override fun ui(): UiHook {
            // WalletUiTest never returns until this hook is released
            return UILayer.addUiHook(UiHook(this))
        }

        override fun resolve() = listOf(Response(Core.walletUiTest()))
    }

    companion object {
        fun match(json: String) : Message? {
            val jsonObject = Parser.default().parse(StringBuilder(json)) as JsonObject
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
            msg.resolve().forEach { it.submit() }
            return this
        }
    }

    abstract class Response {
        fun submit() {
            IPCLayer.handleResponse(this)
        }
    }

    open fun ui () : UiHook {
        // Default ui() implementation just forces the ui hook through its entire lifecycle immediately.
        // This simplifies the way we need to deal w/ UI interactions - every message creates
        // a ui hook; it's just that some of them don't actually need to do any work before they're
        // done with it.
        return UILayer.releaseUiHook(UILayer.addUiHook(UiHook(this)))
    }

    abstract fun resolve() : List<Response>
}