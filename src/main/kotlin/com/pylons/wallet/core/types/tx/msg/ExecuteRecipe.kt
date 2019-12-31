package com.pylons.wallet.core.types.tx.msg

import com.pylons.wallet.core.types.tx.Msg
import com.squareup.moshi.Json

data class ExecuteRecipe(
        @property:[Json(name = "RecipeID")]
        val recipeId : String,
        @property:[Json(name = "Sender")]
        val sender : String,
        @property:[Json(name = "ItemIDs")]
        val itemIds : List<String>?
) : Msg()