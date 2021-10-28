package tech.pylons.lib.types.tx.item

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import tech.pylons.lib.*
import tech.pylons.lib.internal.fuzzyLong
import tech.pylons.lib.types.tx.Coin
import tech.pylons.lib.types.tx.recipe.DoubleKeyValue
import tech.pylons.lib.types.tx.recipe.LongKeyValue
import tech.pylons.lib.types.tx.recipe.StringKeyValue
import java.math.BigDecimal

data class Item(
    @property:[Json(name = "owner")]
    val owner: String,
    @property:[Json(name = "cookbookID")]
    val cookbookId: String,
    @property:[Json(name = "ID")]
    val id: String,
    @property:[Json(name = "nodeVersion")]
    val nodeVersion: String,
    @property:[Json(name = "doubles")]
    val doubles: List<DoubleKeyValue>,
    @property:[Json(name = "longs")]
    val longs: List<LongKeyValue>,
    @property:[Json(name = "strings")]
    val strings: List<StringKeyValue>,
    @property:[Json(name = "mutableStrings")]
    val MutableStrings: List<StringKeyValue>,
    @property:[Json(name = "tradeable")]
    val tradable: Boolean,
    @property:[Json(name = "lastUpdate")]
    val lastUpdate: Long,
    @property:[Json(name = "transferFee")]
    val transferFee: List<Coin>
) {
    fun serialize(mode: SerializationMode? = null): String {
        return when (mode) {
            null -> klaxon.toJsonString(this)
            else -> JsonModelSerializer.serialize(mode, this)
        }
    }

    companion object {
        fun List<Item>.filterByCookbook(cb : String) : List<Item> {
            val ls = mutableListOf<Item>()
            this.forEach { if (it.cookbookId == cb) ls.add(it) }
            return ls
        }

        fun listFromJson(jsonArray: JsonArray<JsonObject>?): List<Item> {
            if (jsonArray == null) return listOf()
            val list = mutableListOf<Item>()
            jsonArray.forEach { list.add(fromJson(it)) }
            return list
        }

        fun fromJson(it : JsonObject) = Item(
            owner = it.string("owner") ?: "",
            cookbookId = it.string("cookbookID") ?: "",
            id = it.string("ID") ?: "",
            nodeVersion = it.string("nodeVersion") ?: "",
            doubles = DoubleKeyValue.listFromJson(it.array("doubles")),
            longs = LongKeyValue.listFromJson(it.array("longs")),
            strings = StringKeyValue.listFromJson(it.array("strings")),
            MutableStrings = StringKeyValue.listFromJson(it.array("mutableStrings")),
            tradable = it.boolean("tradeable") ?: false,
            lastUpdate = it.string("lastUpdate")?.toLong() ?: 0,
            transferFee =  Coin.listFromJson(it.array("transferFee"))
        )

//        private fun doubleMapFromJson(jsonArray: JsonArray<JsonObject>?): Map<String, String> {
//            if (jsonArray == null) return mapOf()
//            val mm = mutableMapOf<String, String>()
//            jsonArray.forEach { mm[it.string("Key")!!] = it.string("Value")!! }
//            return mm
//        }
//
//        private fun longDictFromJson(jsonArray: JsonArray<JsonObject>?): Map<String, String> {
//            if (jsonArray == null) return mapOf()
//            val mm = mutableMapOf<String, String>()
//            jsonArray.forEach {
//                mm[it.string("Key")!!] = it.string("Value")!!
//            }
//            return mm
//        }
//
//        private fun stringDictFromJson(jsonArray: JsonArray<JsonObject>?): Map<String, String> {
//            if (jsonArray == null) return mapOf()
//            val mm = mutableMapOf<String, String>()
//            jsonArray.forEach { mm[it.string("Key")!!] = it.string("Value")!! }
//            return mm
//        }
//
//        private fun doubleKeyValueFromJson(jsonArray: JsonObject?): Map<String, String> {
//            if (jsonArray == null) return mapOf()
//            val mm = mutableMapOf<String, String>()
//
//            jsonArray.map.forEach { t, u ->
//
//                mm[t] = u.toString()
//            }
//            return mm
//        }
//
//        private fun longKeyValueFromJson(jsonArray:JsonObject?): Map<String, String> {
//            if (jsonArray == null) return mapOf()
//            val mm = mutableMapOf<String, String>()
//            jsonArray.map.forEach { t, u ->
//                mm[t] = u.toString()
//            }
//            return mm
//        }
//
//        private fun stringKeyValueFromJson(jsonArray: JsonObject?): Map<String, String> {
//            if (jsonArray == null) return mapOf()
//            val mm = mutableMapOf<String, String>()
//            jsonArray.map.forEach { t, u ->
//                mm[t] = u.toString()
//            }
//            return mm
//        }
    }
}