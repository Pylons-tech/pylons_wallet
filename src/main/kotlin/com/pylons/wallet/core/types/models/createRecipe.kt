package com.pylons.wallet.core.types.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation

data class CreateRecipe (
        @Json(name = "BlockInterval")
        val blockInterval : Long,
        @Json(name = "CoinInputs")
        val coinInputs : List<CoinInput>,
        @Json(name = "CookbookId")
        val cookbookId : String,
        @Json(name = "Description")
        val description: String,
        @Json(name = "Entries")
        val entries : List<WeightedParam>,
        @Json(name = "ItemInputs")
        val itemInputs : List<ItemInput>,
        @Json(name = "RecipeName")
        val recipeName : String,
        @Json(name = "Sender")
        val sender : String
) {
    val adapter: JsonAdapter<CreateRecipe> = moshi.adapter<CreateRecipe>(CreateRecipe::class.java)

    // TODO: we need a custom tojson! it will have to make sure we don't have spaces. because cocaine.

    fun toSignedTx () : String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }

    fun toSignStruct () : String = adapter.toJson(this)
}

class CreateRecipeAdapter : JsonAdapter<CreateRecipe>() {
    override fun fromJson(p0: JsonReader): CreateRecipe? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toJson(p0: JsonWriter, p1: CreateRecipe?) {
        val sb = StringBuilder("{")

        CreateRecipe::class.declaredMemberProperties.forEach {
            val json = it.findAnnotation<Json>()
            if (json != null && p1 != null) {
                var value = it.get(p1)
                // Because we have to strip the whitespaces from the string this spits out by hand,
                // we're doing some Bullshit here to protect whitespaces within our data.
                if (it.returnType == String::class.java) value = protectWhitespace(value.toString())
                p0.name(json.name)
                //p0.
            }

        }
        if (sb.length > 1) sb.setLength(sb.length - 1)
        //p0.
    }

}