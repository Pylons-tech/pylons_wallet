package com.pylons.wallet.core.types.jsonModel

import com.squareup.moshi.*
import okio.Okio
import java.io.ByteArrayOutputStream
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation

data class CreateRecipe (
        @property:[Json(name = "BlockInterval")]
        val blockInterval : Long,
        @property:[Json(name = "CoinInputs")]
        val coinInputs : List<CoinInput>,
        @property:[Json(name = "CookbookID")]
        val cookbookId : String,
        @property:[Json(name = "Description")]
        val description: String,
        @property:[Json(name = "Entries")]
        val entries : WeightedParamList,
        @property:[Json(name = "ItemInputs")]
        val itemInputs : List<ItemInput>,
        @property:[Json(name = "Name")]
        val name : String,
        @property:[Json(name = "Sender")]
        val sender : String
) {
    companion object {
        val adapter: JsonAdapter<CreateRecipe> = moshi.adapter<CreateRecipe>(CreateRecipe::class.java)
    }

    fun toJson (writer: JsonWriter, value : CreateRecipe) = JsonModelSerializer.serialize(writer, value)

    fun toSignedTx () : String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }

    fun toSignStruct () : String {
        return "[${adapter.toJson(this)}]"
    }
}

class CreateRecipeAdapter : JsonAdapter<CreateRecipe>() {
    override fun fromJson(p0: JsonReader): CreateRecipe? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @ToJson
    override fun toJson(p0: JsonWriter, p1: CreateRecipe?) {
        JsonModelSerializer.serialize(p0, p1)
    }

}