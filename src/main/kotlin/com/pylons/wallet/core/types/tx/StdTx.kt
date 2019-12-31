package com.pylons.wallet.core.types.tx

import com.squareup.moshi.Json

data class StdTx(
        @property:[Json(name = "msg")]
        val msg : List<Msg>,
        @property:[Json(name = "fee")]
        val fee : StdFee,
        @property:[Json(name = "signatures")]
        val signatures : List<StdSignature>,
        @property:[Json(name = "memo")]
        val memo : String
)