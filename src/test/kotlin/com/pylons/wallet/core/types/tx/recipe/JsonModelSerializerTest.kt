package com.pylons.wallet.core.types.tx.recipe

import com.beust.klaxon.Json
import java.io.*
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
        val expected =
                """{"bleh_prop":"asdfjkl;","byte":3,"inner_1":{"name":"first","nested":true},"inner_2":{"name":"second","nested":true},"int_array":[1,2,3,4,5]}"""

        val actual = JsonModelSerializer.serialize(SerializationMode.FOR_SIGNING, TestObject())
        println(actual)
        assertEquals(expected, actual)
    }


}