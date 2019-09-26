package com.pylons.wallet.core.types

import com.jayway.jsonpath.JsonPath
import net.minidev.json.JSONArray

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
        fun getArrayFromJson(json : String) : Array<Cookbook> {
            val doc = JsonPath.parse(json)
            val jsonArray = doc.read<JSONArray>("$.Cookbooks[*]")
            val list = mutableListOf<Cookbook>()
            for (i in jsonArray.indices) {
                val root = "$.Cookbooks[$i]"
                val id = doc.read<String>("$root.ID")
                val name = doc.read<String>("$root.Name")
                val description = doc.read<String>("$root.Description")
                val version = doc.read<String>("$root.Version")
                val developer = doc.read<String>("$root.Developer")
                val level = doc.read<String>("$root.Level").toLong()
                val sender = doc.read<String>("$root.Sender")
                val supportEmail = doc.read<String>("$root.SupportEmail")
                list.add(Cookbook(id, name, description, version, developer, level, sender, supportEmail))
            }
            return list.toTypedArray()
        }
    }
}