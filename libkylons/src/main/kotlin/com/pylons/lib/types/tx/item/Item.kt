package com.pylons.lib.types.tx.item

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import java.lang.ClassCastException

fun JsonObject.fuzzyDouble(key : String) : Double {
    return when (val v= this.getValue(key)) {
        is String -> this.string(key)!!.toDouble()
        is Number -> this.double(key)!!
        else -> throw ClassCastException("$v is not convertible to Double")
    }
}

fun JsonObject.fuzzyFloat(key : String) : Float {
    return when (val v= this.getValue(key)) {
        is String -> this.string(key)!!.toFloat()
        is Number -> this.float(key)!!
        else -> throw ClassCastException("$v is not convertible to Float")
    }
}

fun JsonObject.fuzzyInt(key : String) : Int {
    return when (val v= this.getValue(key)) {
        is String -> this.string(key)!!.toInt()
        is Number -> this.int(key)!!
        else -> throw ClassCastException("$v is not convertible to Int")
    }
}

fun JsonObject.fuzzyLong(key : String) : Long {
    return when (val v= this.getValue(key)) {
        is String -> this.string(key)!!.toLong()
        is Number -> this.long(key)!!
        else -> throw ClassCastException("$v is not convertible to Long")
    }
}

private val klaxon = Klaxon()

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
        @property:[Json(name = "Doubles")]
        val doubles: Map<String, Double>,
        @property:[Json(name = "Longs")]
        val longs: Map<String, Long>,
        @property:[Json(name = "Strings")]
        val strings: Map<String, String>,
        @property:[Json(name = "TransferFee")]
        val transferFee: Long
) {
    fun serialize(): String {
        return klaxon.toJsonString(this)
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
                nodeVersion = it.string("NodeVersion")!!,
                id = it.string("ID")!!,
                cookbookId = it.string("CookbookID")!!,
                sender = it.string("Sender")!!,
                ownerRecipeID = it.string("OwnerRecipeID")!!,
                ownerTradeID = it.string("OwnerTradeID")!!,
                tradable = it.boolean("Tradable")!!,
                lastUpdate = it.fuzzyLong("LastUpdate"),
                doubles = doubleMapFromJson(it.array("Doubles")),
                longs = longDictFromJson(it.array("Longs")),
                strings = stringDictFromJson(it.array("Strings")),
                transferFee = it.fuzzyLong("TransferFee"))

        private fun doubleMapFromJson(jsonArray: JsonArray<JsonObject>?): Map<String, Double> {
            if (jsonArray == null) return mapOf()
            val mm = mutableMapOf<String, Double>()
            jsonArray.forEach { mm[it.string("Key")!!] = it.string("Value")!!.toDouble() }
            return mm
        }

        private fun longDictFromJson(jsonArray: JsonArray<JsonObject>?): Map<String, Long> {
            if (jsonArray == null) return mapOf()
            val mm = mutableMapOf<String, Long>()
            jsonArray.forEach {
                mm[it.string("Key")!!] = it.fuzzyLong("Value")
            }
            return mm
        }

        private fun stringDictFromJson(jsonArray: JsonArray<JsonObject>?): Map<String, String> {
            if (jsonArray == null) return mapOf()
            val mm = mutableMapOf<String, String>()
            jsonArray.forEach { mm[it.string("Key")!!] = it.string("Value")!! }
            return mm
        }
    }
}