package com.pylons.wallet.core.types.tx.msg

import com.pylons.wallet.core.types.tx.Msg
import com.squareup.moshi.Json

data class SendPylons(
        @property:[Json(name = "Amount")]
        val amount : Long,
        @property:[Json(name = "Receiver")]
        val receiver : String,
        @property:[Json(name = "Sender")]
        val sender : String
) : Msg()