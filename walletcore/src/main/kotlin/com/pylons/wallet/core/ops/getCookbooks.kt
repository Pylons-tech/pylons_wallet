package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.Cookbook

fun Core.getCookbooks () : List<Cookbook> = engine.listCookbooks()