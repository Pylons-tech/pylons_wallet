package com.pylons.wallet.core.types.tx

import com.jayway.jsonpath.JsonPath
import com.pylons.wallet.core.types.tx.msg.MsgParser
import com.pylons.wallet.core.types.tx.msg.MsgType
import java.lang.Exception
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation


abstract class Msg {
    abstract fun toJson() : String

    companion object {

        fun fromJson (json : String) : Msg? {
            val identifier = JsonPath.read<String>("$.type", json)
            val msgType = findMsgType(identifier) ?:
                throw Exception("No type matches message type $identifier")
            val parser = findParser(msgType) ?:
                throw Exception("No parser defined for message type $identifier")
            return parser(json)
        }

        private fun findMsgType(type : String) : KClass<out Msg>? {
            Msg::class.sealedSubclasses.forEach{
                val msgType = it.findAnnotation<MsgType>()
                if (msgType?.serializedAs == type) return it
            }
            return null
        }

        private fun findParser (type: KClass<out Msg>) : ((String) -> Msg?)? {
            type.declaredMemberFunctions.forEach {
                if (it.findAnnotation<MsgParser>() != null) {
                    return { s : String ->  it.call(s) as Msg? }
                }
            }
            return null
        }
    }
}