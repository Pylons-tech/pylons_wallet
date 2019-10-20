package com.pylons.wallet.core.types.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonWriter
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation

object JsonModelSerializer {

    fun serialize(p0: JsonWriter, p1: Any?) {
        if (p1 != null) {
            val kClass = p1::class
            p0.beginObject()
            kClass.declaredMemberProperties.forEach { prop ->
                val json = prop.findAnnotation<Json>()
                if (json != null) {
                    var value = prop.getter.call(p1)
                    // Because we have to strip the whitespaces from the string this spits out by hand,
                    // we're doing some Bullshit here to protect whitespaces within our data.
                    if (prop.returnType == String::class.java) value = protectWhitespace(value.toString())
                    p0.name(json.name)
                    when (prop.returnType.classifier) {
                        Byte::class -> p0.value(value as Byte)
                        Int::class -> p0.value(value as Int)
                        Long::class -> p0.value(value as Long)
                        Number::class -> p0.value(value as Number)
                        Float::class -> p0.value(value as Float)
                        Double::class -> p0.value(value as Double)
                        Boolean::class -> p0.value(value as Boolean)
                        String::class -> p0.value(value as String)
                        // else: arrays/maps/objects...
                        else -> {
                            when {
                                value == null -> p0.nullValue()
                                Regex("kotlin.Array<.*>").matches(prop.returnType.toString()) -> {
                                    p0.beginArray()
                                    when (prop.returnType.toString()) {
                                        "kotlin.Array<kotlin.String>" -> (value as Array<String>).forEach { p0.value(protectWhitespace(it)) }
                                        "kotlin.Array<kotlin.Byte>" -> (value as Array<Byte>).forEach { p0.value(it) }
                                        "kotlin.Array<kotlin.Int>" -> (value as Array<Int>).forEach { p0.value(it) }
                                        "kotlin.Array<kotlin.Long>" -> (value as Array<Long>).forEach { p0.value(it) }
                                        "kotlin.Array<kotlin.Number>" -> (value as Array<Number>).forEach { p0.value(it) }
                                        "kotlin.Array<kotlin.Float>" -> (value as Array<Float>).forEach { p0.value(it) }
                                        "kotlin.Array<kotlin.Double>" -> (value as Array<Double>).forEach { p0.value(it) }
                                        "kotlin.Array<kotlin.Boolean>" -> (value as Array<Boolean>).forEach { p0.value(it) }
                                        else -> (prop as Array<*>).forEach { serialize(p0, it) }
                                    }
                                    p0.endArray()
                                }
                                // TODO: maps?
                                else -> // serialize nested object
                                    serialize(p0, value)
                            }
                        }
                    }

                }
            }
            p0.endObject()
        }
        else p0.nullValue()
        p0.flush()
        p0.close()
    }
}