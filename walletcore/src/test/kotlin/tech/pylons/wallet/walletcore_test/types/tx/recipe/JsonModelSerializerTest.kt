package tech.pylons.wallet.walletcore_test.types.tx.recipe

import com.beust.klaxon.Json
import tech.pylons.lib.JsonModelSerializer
import tech.pylons.lib.QuotedJsonNumeral
import tech.pylons.lib.SerializationMode
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


    @Test
    fun mapsForBroadcast() {
        val expected = """{"Doubles":[{"Key":"XP","Value":"1.23"}],"Longs":[{"Key":"level","Value":"1"},{"Key":"GiantKill","Value":"0"},{"Key":"Special","Value":"0"},{"Key":"SpecialDragonKill","Value":"0"},{"Key":"UndeadDragonKill","Value":"0"}],"Strings":[{"Key":"Name","Value":"Tiger"},{"Key":"Type","Value":"Character"}]}""".trimIndent()
        val actual = JsonModelSerializer.serialize(SerializationMode.FOR_BROADCAST, TestObject2(mapOf("XP" to 1.23), mapOf("level" to 1L, "GiantKill" to 0L, "Special" to 0L, "SpecialDragonKill" to 0L, "UndeadDragonKill" to 0L), mapOf("Name" to "Tiger", "Type" to "Character")))
        assertEquals(expected, actual)
    }

    @Test
    fun mapsForSigning() {
        val expected = """{"Doubles":[{"Key":"XP","Value":"1.23"}],"Longs":[{"Key":"level","Value":1},{"Key":"GiantKill","Value":0},{"Key":"Special","Value":0},{"Key":"SpecialDragonKill","Value":0},{"Key":"UndeadDragonKill","Value":0}],"Strings":[{"Key":"Name","Value":"Tiger"},{"Key":"Type","Value":"Character"}]}""".trimIndent()
        val actual = JsonModelSerializer.serialize(SerializationMode.FOR_SIGNING, TestObject2(mapOf("XP" to 1.23), mapOf("level" to 1L, "GiantKill" to 0L, "Special" to 0L, "SpecialDragonKill" to 0L, "UndeadDragonKill" to 0L), mapOf("Name" to "Tiger", "Type" to "Character")))
        assertEquals(expected, actual)
    }
}

data class TestObject2(
        @property:[Json(name = "Doubles") QuotedJsonNumeral]
        val doubles: Map<String, Double>,
        @property:[Json(name = "Longs") QuotedJsonNumeral]
        val longs: Map<String, Long>,
        @property:[Json(name = "Strings")]
        val strings: Map<String, String>
)