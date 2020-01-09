package com.pylons.wallet.core.types

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import java.lang.StringBuilder

data class Cookbook (
        @property:[Json(name = "ID")]
        val id : String,
        @property:[Json(name = "Name")]
        val name : String,
        @property:[Json(name = "Description")]
        val description : String,
        @property:[Json(name = "Version")]
        val version : String,
        @property:[Json(name = "Developer")]
        val developer : String,
        @property:[Json(name = "Level")]
        val level : Long,
        @property:[Json(name = "Sender")]
        val sender : String,
        @property:[Json(name = "SupportEmail")]
        val supportEmail : String
) {
    companion object {
        fun getListFromJson(json : String) : List<Cookbook> {
            val jsonArray =
                    (Parser.default().parse(StringBuilder(json)) as JsonObject).array<JsonObject>("Cookbooks")
            val list = mutableListOf<Cookbook>()
            for (i in jsonArray!!.indices) {
                val obj = jsonArray[i]
                list.add(
                        Cookbook(
                                id = obj.string("ID")!!,
                                name = obj.string("Name")!!,
                                description = obj.string("Description")!!,
                                version = obj.string("Version")!!,
                                developer = obj.string("Developer")!!,
                                level = obj.string("Level")!!.toLong(),
                                sender = obj.string("Sender")!!,
                                supportEmail = obj.string("SupportEmail")!!
                        )
                )
            }
            return list
        }
    }
}