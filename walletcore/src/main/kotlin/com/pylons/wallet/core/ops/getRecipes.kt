package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.tx.recipe.Recipe

fun Core.getRecipes () : List<Recipe> = engine.listRecipes()