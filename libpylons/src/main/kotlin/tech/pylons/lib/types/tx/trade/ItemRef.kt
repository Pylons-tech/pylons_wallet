package tech.pylons.lib.types.tx.trade

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import tech.pylons.lib.types.tx.recipe.*

data class ItemRef(
        @property:[Json(name = "cookbookID")]
        var cookbookID: String,
        @property:[Json(name = "itemID")]
        var itemID: String
) {
    companion object {
        fun fromJson(jsonObject: JsonObject): ItemRef =
            ItemRef(
                jsonObject.string("cookbookID") ?: "",
                jsonObject.string("itemID") ?: "",
                )

        fun listFromJson(jsonArray: JsonArray<JsonObject>?): List<ItemRef> {
            if (jsonArray == null) return listOf()
            val ls = mutableListOf<ItemRef>()
            jsonArray.forEach { ls.add(fromJson(it)) }
            return ls
        }

        fun listFromString(itemIDList: List<ItemRef>) : List<String>{
            if (itemIDList == null) return listOf()
            val ls = mutableListOf<String>()
            for(i in itemIDList.indices){
                ls.add(itemIDList[i].itemID)
            }
            return ls
        }
    }
}