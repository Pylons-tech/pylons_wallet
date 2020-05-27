package com.pylons.wallet.core.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType

object JsonModelSerializer {

    fun serialize (mode : SerializationMode, obj: Any?) : String {
        val jo = processObject(mode, obj)
        return jo?.toJsonString(
                prettyPrint = mode == SerializationMode.FOR_BROADCAST,
                canonical = true
        ).orEmpty().replace("{}", "null")
        // HACK! this is used to not serialize empty maps atm.
        // TODO: write proper map handling so we don't need Bullshit here
    }

    private fun processObject (mode : SerializationMode, obj: Any?) : JsonObject? {
        if (obj != null) {
            val kClass = obj::class
            val o = JsonObject()
            kClass.memberProperties.forEach { prop ->
                val json = prop.findAnnotation<Json>()
                if (json != null) {
                    var value = prop.getter.call(obj)
                    if (prop.returnType == String::class.java) value = value.toString()
                    when (prop.returnType.classifier) {
                        // HACK: klaxon's serialization of byte values is actually broken!
                        // We have to do this b/c if we just set value w/o doing the Bullshit
                        // it'll be serialized as... an empty object. Thanks, klaxon!
                        Byte::class -> o[json.name] = (value as Byte).toInt()
                        Int::class -> {
                            val q = prop.findAnnotation<QuotedJsonNumeral>()
                            val n = prop.findAnnotation<NeverQuoteWrap>()
                            if ((q != null || mode == SerializationMode.FOR_BROADCAST) && n == null)
                                o[json.name] = (value as Int).toString()
                            else o[json.name] = value
                        }
                        Long::class -> {
                            val q = prop.findAnnotation<QuotedJsonNumeral>()
                            val n = prop.findAnnotation<NeverQuoteWrap>()
                            if ((q != null || mode == SerializationMode.FOR_BROADCAST) && n == null)
                                o[json.name] = (value as Long).toString()
                            else o[json.name] = value
                        }
                        Number::class -> o[json.name] = value
                        Float::class -> o[json.name] = value
                        Double::class -> o[json.name] = value
                        Boolean::class -> o[json.name] = value
                        String::class -> o[json.name] = value
                        else -> o[json.name] = handleComplexValues(mode, prop, value)
                    }
                }
            }
            return o
        }
        else return null
    }

    private fun handleComplexValues (mode : SerializationMode, prop : KProperty1<out Any, Any?>, value : Any?) : Any? {
        return when {
            value == null -> null
            Regex("kotlin.Array<.*>").matches(prop.returnType.toString()) -> handleArrays(mode, prop, value)
            Regex("kotlin.collections.List<.*>").matches(prop.returnType.toString()) -> handleLists(mode, prop, value)
            // TODO: maps?
            else -> processObject(mode, value) // serialize nested object
        }
    }

    private fun <T> numeralArrayElement (mode: SerializationMode, it : T, q : QuotedJsonNumeral?, arr: JsonArray<Any?>) {
        if (q != null && (q.serializationMode == SerializationMode.ALL || q.serializationMode == mode))
            arr.add(it.toString())
        else arr.add(it)
    }

    private fun handleArrays (mode : SerializationMode, prop : KProperty1<out Any, Any?>, value : Any?) : JsonArray<*>? {
        if ((value as Array<*>).size == 0) return null
        else {
            val jsonArray = JsonArray<Any?>()
            val q = prop.findAnnotation<QuotedJsonNumeral>()
            when (prop.returnType.toString()) {
                "kotlin.Array<kotlin.String>" -> (value as Array<String>).forEach { jsonArray.add(it) }
                "kotlin.Array<kotlin.Byte>" -> (value as Array<Byte>).forEach { numeralArrayElement(mode, it, q, jsonArray) }
                "kotlin.Array<kotlin.Int>" -> (value as Array<Int>).forEach { numeralArrayElement(mode, it, q, jsonArray) }
                "kotlin.Array<kotlin.Long>" -> (value as Array<Long>).forEach { numeralArrayElement(mode, it, q, jsonArray) }
                "kotlin.Array<kotlin.Number>" -> (value as Array<Number>).forEach { numeralArrayElement(mode, it, q, jsonArray) }
                "kotlin.Array<kotlin.Float>" -> (value as Array<Float>).forEach { numeralArrayElement(mode, it, q, jsonArray) }
                "kotlin.Array<kotlin.Double>" -> (value as Array<Double>).forEach { numeralArrayElement(mode, it, q, jsonArray) }
                "kotlin.Array<kotlin.Boolean>" -> (value as Array<Boolean>).forEach { jsonArray.add(it) }
                else -> value.forEach { jsonArray.add(processObject(mode, it)) }
            }
            return jsonArray
        }
    }

    private fun handleLists (mode : SerializationMode, prop : KProperty1<out Any, Any?>, value : Any?) : JsonArray<*>? {
        if ((value as List<*>).size == 0) return null
        else {
            val jsonArray = JsonArray<Any?>()
            val q = prop.findAnnotation<QuotedJsonNumeral>()
            when (prop.returnType.toString()) {
                "kotlin.collections.List<kotlin.String>" -> (value as List<String>).forEach { jsonArray.add(it) }
                "kotlin.collections.List<kotlin.Byte>" -> (value as List<Byte>).forEach { numeralArrayElement(mode, it, q, jsonArray) }
                "kotlin.collections.List<kotlin.Int>" -> (value as List<Int>).forEach { numeralArrayElement(mode, it, q, jsonArray) }
                "kotlin.collections.List<kotlin.Long>" -> (value as List<Long>).forEach { numeralArrayElement(mode, it, q, jsonArray) }
                "kotlin.collections.List<kotlin.Number>" -> (value as List<Number>).forEach { numeralArrayElement(mode, it, q, jsonArray) }
                "kotlin.collections.List<kotlin.Float>" -> (value as List<Float>).forEach { numeralArrayElement(mode, it, q, jsonArray) }
                "kotlin.collections.List<kotlin.Double>" -> (value as List<Double>).forEach { numeralArrayElement(mode, it, q, jsonArray) }
                "kotlin.collections.List<kotlin.Boolean>" -> (value as List<Boolean>).forEach { jsonArray.add(it) }
                else -> value.forEach { jsonArray.add(processObject(mode, it)) }
            }
            return jsonArray
        }
    }
}