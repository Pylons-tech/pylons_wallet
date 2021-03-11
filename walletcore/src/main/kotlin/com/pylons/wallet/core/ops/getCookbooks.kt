package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.lib.types.types.Cookbook

fun Core.getCookbooks () : List<Cookbook> = engine.listCookbooks()