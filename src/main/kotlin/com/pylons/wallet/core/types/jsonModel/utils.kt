package com.pylons.wallet.core.types.jsonModel

import com.squareup.moshi.Moshi

internal val moshi = Moshi.Builder().add(CreateRecipeAdapter()).build()

annotation class QuotedJsonNumeral