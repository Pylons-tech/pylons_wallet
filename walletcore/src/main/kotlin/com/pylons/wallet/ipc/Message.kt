package com.pylons.wallet.ipc

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.ops.applyRecipe
import com.pylons.wallet.core.types.Transaction
import com.pylons.wallet.core.types.klaxon
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.functions
import kotlin.reflect.full.companionObjectInstance

sealed class Message {
    class ApplyRecipe(    var recipe : String? = null,
                          var cookbook : String? = null,
                          var itemInputs : List<String>? = null) : Message() {
        class Response(tx : Transaction): Message.Response() {
            val txhash = tx.id?: ""
            val state = tx.state
            val error = tx.txError?: listOf()
        }

        companion object {
            fun deserialize(json: String) = klaxon.parse<ApplyRecipe>(json)
        }

        override fun resolve() =
                Response(Core.applyRecipe(recipe!!, cookbook!!, itemInputs!!).submit())
    }

    companion object {
        fun match(json: String) : Message {
            Message::class.sealedSubclasses.forEach { kClass ->
                val func = kClass.companionObject?.functions?.find { it.name == "deserialize" }
                val ret = func?.call(kClass.companionObjectInstance, json) as Message?
                if (ret != null) return ret
            }
            throw Exception("Couldn't match input json to a message type\n\n\n$json")
        }
    }

    var uiHook : UiHook? = null
    var response : Response? = null

    open class UiHook(val msg : Message) {

        var live : Boolean = true
            private set

        fun release() : UiHook {
            live = false
            msg.resolve().submit()
            return this
        }
    }

    abstract class Response {
        fun submit() {
            println(klaxon.toJsonString(this))
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