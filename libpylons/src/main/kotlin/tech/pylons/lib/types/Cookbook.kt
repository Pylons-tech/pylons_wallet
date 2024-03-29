package tech.pylons.lib.types

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import tech.pylons.lib.internal.fuzzyLong
import java.lang.StringBuilder

data class Cookbook (
        @property:[Json(name = "NodeVersion")]
        val nodeVersion : String,
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
        @property:[Json(name = "Sender")]
        val sender : String,
        @property:[Json(name = "SupportEmail")]
        val supportEmail : String,
        @property:[Json(name ="CostPerBlock")]
        val costPerBlock : Long
) {
    companion object {
        fun getListFromJson(json : String) : List<Cookbook> {
            val jsonArray =
                    (Parser.default().parse(StringBuilder(json)) as JsonObject).array<JsonObject>("Cookbooks").orEmpty()
            val list = mutableListOf<Cookbook>()
            for (i in jsonArray.indices) {
                val obj = jsonArray[i]
                list.add(
                        Cookbook(
                                nodeVersion = obj.string("NodeVersion")!!,
                                id = obj.string("ID")!!,
                                name = obj.string("Name")!!,
                                description = obj.string("Description")!!,
                                version = obj.string("Version")!!,
                                developer = obj.string("Developer")!!,
                            sender = obj.string("Sender")!!,
                                supportEmail = obj.string("SupportEmail")!!,
                                costPerBlock = obj.fuzzyLong("CostPerBlock")
                        )
                )
            }
            return list
        }

        fun parseFromJson(json: String?) : Cookbook? {
            if (json != null) {
                try {
                    val obj = Parser.default().parse(StringBuilder(json)) as JsonObject
                    return Cookbook(
                        nodeVersion = obj.string("NodeVersion")!!,
                        id = obj.string("ID")!!,
                        name = obj.string("Name")!!,
                        description = obj.string("Description")!!,
                        version = obj.string("Version")!!,
                        developer = obj.string("Developer")!!,
                        sender = obj.string("Sender")!!,
                        supportEmail = obj.string("SupportEmail")!!,
                        costPerBlock = obj.fuzzyLong("CostPerBlock")
                    )
                }catch(e: Error){
                }
            }
            return null
        }
    }
}