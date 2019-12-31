package com.pylons.wallet.core.types.tx.recipe

import com.squareup.moshi.Json
import com.squareup.moshi.JsonWriter
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

object JsonModelSerializer {

    fun serialize (mode : SerializationMode, p0: JsonWriter, p1: Any?) {
        processObject(mode, p0, p1)
        p0.flush()
        p0.close()
    }

    private fun processObject (mode : SerializationMode, p0: JsonWriter, p1: Any?) {
        if (p1 != null) {
            if (mode == SerializationMode.FOR_BROADCAST) p0.indent = "        "
            p0.serializeNulls = true
            val kClass = p1::class
            p0.beginObject()
            kClass.memberProperties.forEach { prop ->
                val json = prop.findAnnotation<Json>()
                if (json != null) {
                    var value = prop.getter.call(p1)
                    if (prop.returnType == String::class.java) value = value.toString()
                    p0.name(json.name)
                    when (prop.returnType.classifier) {
                        Byte::class -> p0.value(value as Byte)
                        Int::class -> {
                            val q = prop.findAnnotation<QuotedJsonNumeral>()
                            val n = prop.findAnnotation<NeverQuoteWrap>()
                            if ((q != null || mode == SerializationMode.FOR_BROADCAST) && n == null) p0.value((value as Int).toString())
                            else p0.value(value as Int)
                        }
                        Long::class -> {
                            val q = prop.findAnnotation<QuotedJsonNumeral>()
                            val n = prop.findAnnotation<NeverQuoteWrap>()
                            if ((q != null || mode == SerializationMode.FOR_BROADCAST) && n == null) p0.value((value as Long).toString())
                            else p0.value(value as Long)
                        }
                        Number::class -> p0.value(value as Number)
                        Float::class -> p0.value(value as Float)
                        Double::class -> p0.value(value as Double)
                        Boolean::class -> p0.value(value as Boolean)
                        String::class -> p0.value(value as String)
                        else -> handleComplexValues(mode, prop, value, p0)
                    }
                }
            }
            p0.endObject()
        }
        else p0.nullValue()
    }

    private fun handleComplexValues (mode : SerializationMode, prop : KProperty1<out Any, Any?>, value : Any?, p0: JsonWriter) {
        println(prop.returnType.toString())
        when {
            value == null -> p0.nullValue()
            Regex("kotlin.Array<.*>").matches(prop.returnType.toString()) -> handleArrays(mode, prop, value, p0)
            Regex("kotlin.collections.List<.*>").matches(prop.returnType.toString()) -> handleLists(mode, prop, value, p0)
            // TODO: maps?
            else -> // serialize nested object
                processObject(mode, p0, value)
        }
    }

    private fun handleArrays (mode : SerializationMode, prop : KProperty1<out Any, Any?>, value : Any?, p0: JsonWriter) {
        if ((value as Array<*>).size == 0) p0.nullValue()
        else {
            p0.beginArray()
            when (prop.returnType.toString()) {
                "kotlin.Array<kotlin.String>" -> (value as Array<String>).forEach { p0.value(it) }
                "kotlin.Array<kotlin.Byte>" -> (value as Array<Byte>).forEach { p0.value(it) }
                "kotlin.Array<kotlin.Int>" -> (value as Array<Int>).forEach { p0.value(it) }
                "kotlin.Array<kotlin.Long>" -> (value as Array<Long>).forEach { p0.value(it) }
                "kotlin.Array<kotlin.Number>" -> (value as Array<Number>).forEach { p0.value(it) }
                "kotlin.Array<kotlin.Float>" -> (value as Array<Float>).forEach { p0.value(it) }
                "kotlin.Array<kotlin.Double>" -> (value as Array<Double>).forEach { p0.value(it) }
                "kotlin.Array<kotlin.Boolean>" -> (value as Array<Boolean>).forEach { p0.value(it) }
                else -> value.forEach { processObject(mode, p0, it) }
            }
            p0.endArray()
        }
    }

    private fun handleLists (mode : SerializationMode, prop : KProperty1<out Any, Any?>, value : Any?, p0: JsonWriter) {
        if ((value as List<*>).size == 0) p0.nullValue()
        else {
            p0.beginArray()
            when (prop.returnType.toString()) {
                "kotlin.List<kotlin.String>" -> (value as List<String>).forEach { p0.value(it) }
                "kotlin.List<kotlin.Byte>" -> (value as List<Byte>).forEach { p0.value(it) }
                "kotlin.List<kotlin.Int>" -> (value as List<Int>).forEach { p0.value(it) }
                "kotlin.List<kotlin.Long>" -> (value as List<Long>).forEach { p0.value(it) }
                "kotlin.List<kotlin.Number>" -> (value as List<Number>).forEach { p0.value(it) }
                "kotlin.List<kotlin.Float>" -> (value as List<Float>).forEach { p0.value(it) }
                "kotlin.List<kotlin.Double>" -> (value as List<Double>).forEach { p0.value(it) }
                "kotlin.List<kotlin.Boolean>" -> (value as List<Boolean>).forEach { p0.value(it) }
                else -> (value).forEach { processObject(mode, p0, it) }
            }
            p0.endArray()
        }
    }
}