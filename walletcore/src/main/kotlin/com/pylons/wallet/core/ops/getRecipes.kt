package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.types.*

internal fun getRecipes(): Response {
    val cookbooks = Core.getCookbooks()
    val recipes = Core.getRecipes()
    val cList = mutableListOf<String>()
    val rList = mutableListOf<String>()
    cookbooks.forEach {
        cList.add(klaxon.toJsonString(it))
    }
    recipes.forEach {
        rList.add(klaxon.toJsonString(it))
    }
    val outgoingMsg = MessageData(
            stringArrays = mutableMapOf(Keys.COOKBOOK to cList, Keys.RECIPE to rList)
    )
    return Response(outgoingMsg, Status.OK_TO_RETURN_TO_CLIENT)
}

fun Core.getCookbooks () : List<Cookbook> = engine.listCookbooks()

fun Core.getRecipes () : List<Recipe> = engine.listRecipes()