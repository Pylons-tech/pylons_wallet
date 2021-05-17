package com.pylons.lib.types.tx

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject

data class StdSignature(
        @property:[Json(name = "signature")]
        val signature : String,
        @property:[Json(name = "pub_key")]
        val pubKey: PubKey
) {
        companion object {
                fun fromJson (jsonArray: JsonArray<JsonObject>?) : List<StdSignature>? {
                        if (jsonArray == null ) return null
                        val mList = mutableListOf<StdSignature>()
                        jsonArray.forEach {
                                mList.add(StdSignature(
                                        //tierre confirm keys
                                        signature = it.string("signature")!!,
                                        pubKey = PubKey.fromJson(it.obj("pub_key")!!)
                                ))
                        }
                        return mList
                }
        }
}