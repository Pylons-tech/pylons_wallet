package com.pylons.wallet.core.types.tx.msg

import com.squareup.moshi.Json

data class GetPylons(
        @property:[Json(name = "Amount")]
        val amount : Long,
        @property:[Json(name = "Requester")]
        val requester : String
) : Msg()