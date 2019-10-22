package com.pylons.wallet.core.types.models

import com.google.common.io.CharStreams
import com.squareup.moshi.Json
import com.squareup.moshi.JsonWriter
import com.sun.org.apache.xpath.internal.operations.Bool
import okio.BufferedSink
import okio.Okio
import java.io.*
import java.util.stream.Stream
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

class TestObject {
    @Json(name = "bleh_prop")
    val bleh : String = "asdfjkl;"
    val not_present : String = "no json prop so this shouldn't show"
    @Json(name = "byte")
    val byte : Byte = 3
    @Json(name = "int_array")
    val intArray : Array<Int> = arrayOf(1, 2, 3, 4, 5)
    @Json(name = "inner_1")
    val inner1 = InnerObject("first")
    @Json(name = "inner_2")
    val inner2 = InnerObject("second")
}

class InnerObject constructor (
        @property:[Json(name = "name")] val name : String) {
    @Json(name = "nested")
    val nested : Boolean = true
}

class JsonModelSerializerTest {
    @Test
    fun serializes () {
        val stream = ByteArrayOutputStream()
        val expected =
                """{"bleh_prop":"asdfjkl;","byte":3,"inner_1":{"name":"first","nested":true},"inner_2":{"name":"second","nested":true},"int_array":[1,2,3,4,5]}"""
        JsonModelSerializer.serialize(JsonWriter.of(Okio.buffer(Okio.sink(stream))), TestObject())
        val actual = stream.toString()
        println(actual)
        assertEquals(expected, actual)
    }


}