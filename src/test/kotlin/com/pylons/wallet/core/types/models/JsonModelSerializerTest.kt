package com.pylons.wallet.core.types.models

import com.google.common.io.CharStreams
import com.squareup.moshi.Json
import com.squareup.moshi.JsonWriter
import okio.BufferedSink
import okio.Okio
import java.io.*
import java.util.stream.Stream
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

class TestObject {
    @Json(name = "bleh_prop")
    val bleh : String = "asdfjkl;"
}

class JsonModelSerializerTest {
    @Test
    fun a () {
        val stream = ByteArrayOutputStream()
        val expected = """{"bleh_prop":"asdfjkl;"}"""
        JsonModelSerializer.serialize(JsonWriter.of(Okio.buffer(Okio.sink(stream))), TestObject())
        val actual = stream.toString()
        assertEquals(expected, actual)
    }


}