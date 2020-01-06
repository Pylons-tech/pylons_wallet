package com.pylons.wallet.core.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

object JsonModelSerializer {

    fun serialize (mode : SerializationMode, obj: Any?) : String {
        val jo = processObject(mode, obj)
        return jo?.toJsonString(
                prettyPrint = mode == SerializationMode.FOR_BROADCAST,
                canonical = true
        ).orEmpty()
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

    private fun handleArrays (mode : SerializationMode, prop : KProperty1<out Any, Any?>, value : Any?) : JsonArray<*>? {
        if ((value as Array<*>).size == 0) return null
        else {
            val jsonArray = JsonArray<Any?>()
            when (prop.returnType.toString()) {
                "kotlin.Array<kotlin.String>" -> (value as Array<String>).forEach { jsonArray.add(it) }
                "kotlin.Array<kotlin.Byte>" -> (value as Array<Byte>).forEach { jsonArray.add(it) }
                "kotlin.Array<kotlin.Int>" -> (value as Array<Int>).forEach { jsonArray.add(it) }
                "kotlin.Array<kotlin.Long>" -> (value as Array<Long>).forEach { jsonArray.add(it) }
                "kotlin.Array<kotlin.Number>" -> (value as Array<Number>).forEach { jsonArray.add(it) }
                "kotlin.Array<kotlin.Float>" -> (value as Array<Float>).forEach { jsonArray.add(it) }
                "kotlin.Array<kotlin.Double>" -> (value as Array<Double>).forEach { jsonArray.add(it) }
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
            when (prop.returnType.toString()) {
                "kotlin.Array<kotlin.String>" -> (value as Array<String>).forEach { jsonArray.add(it) }
                "kotlin.Array<kotlin.Byte>" -> (value as Array<Byte>).forEach { jsonArray.add(it) }
                "kotlin.Array<kotlin.Int>" -> (value as Array<Int>).forEach { jsonArray.add(it) }
                "kotlin.Array<kotlin.Long>" -> (value as Array<Long>).forEach { jsonArray.add(it) }
                "kotlin.Array<kotlin.Number>" -> (value as Array<Number>).forEach { jsonArray.add(it) }
                "kotlin.Array<kotlin.Float>" -> (value as Array<Float>).forEach { jsonArray.add(it) }
                "kotlin.Array<kotlin.Double>" -> (value as Array<Double>).forEach { jsonArray.add(it) }
                "kotlin.Array<kotlin.Boolean>" -> (value as Array<Boolean>).forEach { jsonArray.add(it) }
                else -> value.forEach { jsonArray.add(processObject(mode, it)) }
            }
            return jsonArray
        }
    }
}