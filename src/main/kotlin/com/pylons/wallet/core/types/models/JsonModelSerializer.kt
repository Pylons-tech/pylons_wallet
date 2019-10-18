package com.pylons.wallet.core.types.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonWriter
import com.sun.org.apache.xpath.internal.operations.Bool
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

object JsonModelSerializer {

    fun serialize(p0: JsonWriter, p1: Any?) {
        if (p1 != null) {
            val kClass = p1::class
            kClass.declaredMemberProperties.forEach {
                val json = it.findAnnotation<Json>()
                if (json != null) {
                    p0.beginObject()
                    var value = it.getter.call(p1)
                    // Because we have to strip the whitespaces from the string this spits out by hand,
                    // we're doing some Bullshit here to protect whitespaces within our data.
                    if (it.returnType == String::class.java) value = protectWhitespace(value.toString())
                    p0.name(json.name)
                    println(it.returnType)
                    println(String::class)

                    when (it.returnType.classifier) {
                        Byte::class -> p0.value(value as Byte)
                        Int::class -> p0.value(value as Int)
                        Long::class -> p0.value(value as Long)
                        Number::class -> p0.value(value as Number)
                        Float::class -> p0.value(value as Float)
                        Double::class -> p0.value(value as Double)
                        Boolean::class -> p0.value(value as Boolean)
                        String::class -> {
                            println("oas")
                            p0.value(value as String)
                            println(value as String)
                            println("oas")
                        }
                        // else: arrays/maps/objects...
                        else -> {
                            when {
                                value == null -> p0.nullValue()
                                it.returnType.javaClass.isArray -> {
                                    p0.beginArray()
                                    when (it.returnType.classifier) {
                                        Array<String>::class -> (it as Array<String>).forEach { p0.value(protectWhitespace(it)) }
                                        Array<Byte>::class -> (it as Array<Byte>).forEach { p0.value(it) }
                                        Array<Int>::class -> (it as Array<Int>).forEach { p0.value(it) }
                                        Array<Long>::class -> (it as Array<Long>).forEach { p0.value(it) }
                                        Array<Number>::class -> (it as Array<Number>).forEach { p0.value(it) }
                                        Array<Float>::class -> (it as Array<Float>).forEach { p0.value(it) }
                                        Array<Double>::class -> (it as Array<Double>).forEach { p0.value(it) }
                                        Array<Boolean>::class -> (it as Array<Boolean>).forEach { p0.value(it) }
                                        else -> (it as Array<*>).forEach { serialize(p0, it) }
                                    }
                                    p0.endArray()
                                }
                                // TODO: maps?
                                else -> // serialize nested object
                                    serialize(p0, value)
                            }
                        }
                    }
                    p0.endObject()
                }
            }
        }
        else p0.nullValue()
        p0.flush()
        p0.close()
    }
}