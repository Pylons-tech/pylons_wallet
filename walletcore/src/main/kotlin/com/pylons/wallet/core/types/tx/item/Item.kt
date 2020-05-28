package com.pylons.wallet.core.types.tx.item

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.pylons.wallet.core.types.klaxon
import com.pylons.wallet.core.types.tx.recipe.*
import java.lang.ClassCastException
import java.lang.StringBuilder
import java.util.*

data class Item(
        @property:[Json(name = "ID")]
        val id: String,
        @property:[Json(name = "CookbookID")]
        val cookbookId: String,
        @property:[Json(name = "Sender")]
        val sender: String,
        @property:[Json(name = "OwnerRecipeID")]
        val ownerRecipeID: String,
        @property:[Json(name = "Tradable")]
        val tradable: Boolean,
        @property:[Json(name = "LastUpdate")]
        val lastUpdate: Long,
        @property:[Json(name = "Doubles") QuotedJsonNumeral]
        val doubles: Map<String, Double>,
        @property:[Json(name = "Longs") QuotedJsonNumeral]
        val longs: Map<String, Long>,
        @property:[Json(name = "Strings")]
        val strings: Map<String, String>
) {
    fun serialize(mode: SerializationMode? = null): String {
        return when (mode) {
            null -> klaxon.toJsonString(this)
            else -> JsonModelSerializer.serialize(mode, this)
        }
    }

    companion object {
        fun listFromJson(jsonArray: JsonArray<JsonObject>?): List<Item> {
            if (jsonArray == null) return listOf()
            val list = mutableListOf<Item>()
            jsonArray.forEach {
                println(it.toJsonString())
                list.add(
                        Item(
                                id = it.string("ID")!!,
                                cookbookId = it.string("CookbookID")!!,
                                sender = it.string("Sender")!!,
                                ownerRecipeID = it.string("OwnerRecipeID")!!,
                                tradable = it.boolean("Tradable")!!,
                                // TODO this is an EXTREMELY ugly hack and a general-purpose
                                // custom serializer would obsolete it
                                // (we need a custom serializer now, too, bc this is not acceptable)
                                lastUpdate = try {
                                    it.long("LastUpdate")!!
                                } catch (c: ClassCastException) {
                                    it.string("LastUpdate")!!.toLong()
                                },
                                doubles = doubleMapFromJson(it.array("Doubles")),
                                longs = longDictFromJson(it.array("Longs")),
                                strings = stringDictFromJson(it.array("Strings"))
                        )
                )
            }
            return list
        }

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
                mm[it.string("Key")!!] = try {
                    it.string("Value")!!.toLong()
                } catch (c: ClassCastException) {
                    it.int("Value")!!.toLong()
                }
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