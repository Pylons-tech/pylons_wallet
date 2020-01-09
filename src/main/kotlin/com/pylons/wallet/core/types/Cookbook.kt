package com.pylons.wallet.core.types

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import java.lang.StringBuilder

data class Cookbook (
    val id : String,
    val name : String,
    val description : String,
    val version : String,
    val developer : String,
    val level : Long,
    val sender : String,
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