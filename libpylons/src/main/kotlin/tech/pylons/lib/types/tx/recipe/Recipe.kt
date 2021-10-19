package tech.pylons.lib.types.tx.recipe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import tech.pylons.lib.internal.fuzzyLong
import java.lang.StringBuilder

data class Recipe(
    @property:[Json(name = "cookbookID")]
    val cookbookId : String,
    @property:[Json(name = "ID")]
    val id : String,
    @property:[Json(name = "nodeVersion")]
    val nodeVersion : String,
    @property:[Json(name = "name")]
    val name : String,
    @property:[Json(name = "description")]
    val description : String,
    @property:[Json(name = "version")]
    val version : String,
    @property:[Json(name = "coinInputs")]
    val coinInputs : List<CoinInput>,
    @property:[Json(name = "itemInputs")]
    val itemInputs : List<ItemInput>,
    @property:[Json(name = "entries")]
    val entries : EntriesList,
    @property:[Json(name = "outputs")]
    val outputs : List<WeightedOutput>,
    @property:[Json(name = "blockInterval")]
    val blockInterval : Long,
    @property:[Json(name = "enabled")]
    val enabled : Boolean,
    @property:[Json(name = "extraInfo")]
        val extraInfo: String
) {
    companion object {
        fun listFromJson(json : String) : List<Recipe> {
            val jsonArray = (Parser.default().parse(StringBuilder(json)) as JsonObject)
                    .array<JsonObject>("Recipes").orEmpty()
            val list = mutableListOf<Recipe>()
            jsonArray.forEach {
                list.add(
                        Recipe(
                            cookbookId = it.string("cookbookID")!!,
                            id = it.string("ID")!!,
                                nodeVersion = it.string("nodeVersion")!!,
                                name = it.string("name")!!,
                                description = it.string("description")!!,
                                version = it.string("version")!!,
                                coinInputs = CoinInput.listFromJson(it.array("coinInputs")),
                                itemInputs = ItemInput.listFromJson(it.array("itemInputs")),
                                entries = EntriesList.fromJson(it.obj("entries"))!!,
                                outputs = WeightedOutput.listFromJson(it.array("outputs")),
                                blockInterval = it.fuzzyLong("blockInterval"),
                                enabled = it.boolean("enabled")!!,
                                extraInfo = it.string("extraInfo")!!
                        )
                )
            }
            return list
        }
    }
}