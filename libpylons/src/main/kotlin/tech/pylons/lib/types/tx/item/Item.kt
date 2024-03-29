package tech.pylons.lib.types.tx.item

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import tech.pylons.lib.*
import tech.pylons.lib.internal.fuzzyLong
import java.math.BigDecimal

data class Item(
        @property:[Json(name = "NodeVersion")]
        val nodeVersion: String,
        @property:[Json(name = "ID")]
        val id: String,
        @property:[Json(name = "CookbookID")]
        val cookbookId: String,
        @property:[Json(name = "Sender")]
        val sender: String,
        @property:[Json(name = "OwnerRecipeID")]
        val ownerRecipeID: String,
        @property:[Json(name = "OwnerTradeID")]
        val ownerTradeID: String,
        @property:[Json(name = "Tradable")]
        val tradable: Boolean,
        @property:[Json(name = "LastUpdate")]
        val lastUpdate: Long,
        @property:[Json(name = "Doubles") QuotedJsonNumeral]
        val doubles: Map<String, String>,
        @property:[Json(name = "Longs") QuotedJsonNumeral]
        val longs: Map<String, String>,
        @property:[Json(name = "Strings")]
        val strings: Map<String, String>,
        @property:[Json(name = "TransferFee")]
        val transferFee: Long
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
                nodeVersion = it.string("NodeVersion") ?: "",
                id = it.string("ID") ?: "",
                cookbookId = it.string("CookbookID") ?: "",
                sender = it.string("Sender") ?: "",
                ownerRecipeID = it.string("OwnerRecipeID") ?: "",
                ownerTradeID = it.string("OwnerTradeID") ?: "",
                tradable = it.boolean("Tradable") ?: false,
                lastUpdate = it.string("LastUpdate")?.toLong() ?: 0,
                doubles = doubleMapFromJson(it.array("Doubles")),
                longs = longDictFromJson(it.array("Longs")),
                strings = stringDictFromJson(it.array("Strings")),
                transferFee = it.string("TransferFee")?.toLong() ?: 0
        )

        fun fromJsonOpt(it : JsonObject) = Item(
            nodeVersion = it.string("NodeVersion")!!,
            id = it.string("ID")!!,
            cookbookId = it.string("CookbookID")!!,
            sender = it.string("Sender")!!,
            ownerRecipeID = it.string("OwnerRecipeID")!!,
            ownerTradeID = it.string("OwnerTradeID")!!,
            tradable = it.boolean("Tradable")!!,
            lastUpdate = it.fuzzyLong("LastUpdate"),
            doubles = doubleKeyValueFromJson(it.obj("Doubles")),
            longs = longKeyValueFromJson(it.obj("Longs")),
            strings = stringKeyValueFromJson(it.obj("Strings")),
            transferFee = it.fuzzyLong("TransferFee"))


        private fun doubleMapFromJson(jsonArray: JsonArray<JsonObject>?): Map<String, String> {
            if (jsonArray == null) return mapOf()
            val mm = mutableMapOf<String, String>()
            jsonArray.forEach { mm[it.string("Key")!!] = it.string("Value")!! }
            return mm
        }

        private fun longDictFromJson(jsonArray: JsonArray<JsonObject>?): Map<String, String> {
            if (jsonArray == null) return mapOf()
            val mm = mutableMapOf<String, String>()
            jsonArray.forEach {
                mm[it.string("Key")!!] = it.string("Value")!!
            }
            return mm
        }

        private fun stringDictFromJson(jsonArray: JsonArray<JsonObject>?): Map<String, String> {
            if (jsonArray == null) return mapOf()
            val mm = mutableMapOf<String, String>()
            jsonArray.forEach { mm[it.string("Key")!!] = it.string("Value")!! }
            return mm
        }

        private fun doubleKeyValueFromJson(jsonArray: JsonObject?): Map<String, String> {
            if (jsonArray == null) return mapOf()
            val mm = mutableMapOf<String, String>()

            jsonArray.map.forEach { t, u ->

                mm[t] = u.toString()
            }
            return mm
        }

        private fun longKeyValueFromJson(jsonArray:JsonObject?): Map<String, String> {
            if (jsonArray == null) return mapOf()
            val mm = mutableMapOf<String, String>()
            jsonArray.map.forEach { t, u ->
                mm[t] = u.toString()
            }
            return mm
        }

        private fun stringKeyValueFromJson(jsonArray: JsonObject?): Map<String, String> {
            if (jsonArray == null) return mapOf()
            val mm = mutableMapOf<String, String>()
            jsonArray.map.forEach { t, u ->
                mm[t] = u.toString()
            }
            return mm
        }
    }
}