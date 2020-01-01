package com.pylons.wallet.core.types.tx

import com.jayway.jsonpath.JsonPath
import com.squareup.moshi.Moshi
import com.squareup.moshi.Json
import net.minidev.json.JSONArray

data class StdTx(
        @property:[Json(name = "msg")]
        val msg : List<Msg>,
        @property:[Json(name = "fee")]
        val fee : StdFee,
        @property:[Json(name = "signatures")]
        val signatures : List<StdSignature>,
        @property:[Json(name = "memo")]
        val memo : String
) {
        companion object {
                private val moshi = Moshi.Builder().build()

                fun fromJson (json : String) : StdTx? {
                        val msgArray = JsonPath.read<JSONArray>("$.msg", json)
                        val mList = mutableListOf<Msg>()
                        msgArray.forEach { mList.add(Msg.fromJson(it.toString())?:
                                throw Exception("Failed to parse message:\n $it")) }
                        return moshi.adapter<StdTx>(StdTx::class.java).fromJson(json)
                }
        }
}