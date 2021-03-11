package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.lib.types.types.*
import com.pylons.lib.types.types.tx.recipe.Recipe

fun Core.getRecipes () : List<Recipe> = engine.listRecipes()