package tech.pylons.lib.types

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import tech.pylons.lib.internal.fuzzyLong
import tech.pylons.lib.types.tx.Coin
import java.lang.StringBuilder

data class Cookbook (
        @property:[Json(name = "creator")]
        val Creator : String,
        @property:[Json(name = "ID")]
        val id : String,
        @property:[Json(name = "nodeVersion")]
        val nodeVersion : String,
        @property:[Json(name = "name")]
        val name : String,
        @property:[Json(name = "description")]
        val description : String,
        @property:[Json(name = "developer")]
        val developer : String,
        @property:[Json(name = "version")]
        val version : String,
        @property:[Json(name = "supportEmail")]
        val supportEmail : String,
        @property:[Json(name ="costPerBlock")]
        val costPerBlock : Coin,
        @property:[Json(name ="enabled")]
        val Enabled : Boolean
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
                            Creator = obj.string("creator")!!,
                                id = obj.string("ID")!!,
                                name = obj.string("name")!!,
                                nodeVersion = obj.string("nodeVersion")!!,
                                description = obj.string("description")!!,
                                version = obj.string("version")!!,
                                developer = obj.string("developer")!!,
                                supportEmail = obj.string("supportEmail")!!,
                                costPerBlock = Coin.fromJson(obj.obj("costPerBlock")!!),
                                Enabled = obj.boolean("enabled")!!
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
                        Creator = obj.string("creator")!!,
                        id = obj.string("ID")!!,
                        name = obj.string("name")!!,
                        nodeVersion = obj.string("nodeVersion")!!,
                        description = obj.string("description")!!,
                        version = obj.string("version")!!,
                        developer = obj.string("developer")!!,
                        supportEmail = obj.string("supportEmail")!!,
                        costPerBlock = Coin.fromJson(obj.obj("costPerBlock")!!),
                        Enabled = obj.boolean("enabled")!!
                    )
                }catch(e: Error){
                }
            }
            return null
        }
    }
}