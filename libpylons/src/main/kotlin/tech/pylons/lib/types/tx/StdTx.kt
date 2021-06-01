package tech.pylons.lib.types.tx

import tech.pylons.lib.types.tx.msg.Msg
import com.beust.klaxon.*

data class StdTx(
    @property:[Json(name = "msg")]
        val msg : List<Msg>,
    @property:[Json(name = "fee")]
        val fee : StdFee,
    @property:[Json(name = "signatures")]
        val signatures : List<StdSignature>?,
    @property:[Json(name = "memo")]
        val memo : String
) {
        companion object {
                fun fromJson (jsonObject: JsonObject) : StdTx? {
                        val msgArray = jsonObject.array<JsonObject>("msg")!!
                        val mList = mutableListOf<Msg>()
                        msgArray.forEach { mList.add(
                            Msg.fromJson(it)?:
                               throw Exception("Failed to parse message:\n ${it.toJsonString()}")) }
                        return StdTx(
                                msg = mList,
                                fee = StdFee.fromJson(jsonObject.obj("fee")!!),
                                memo = jsonObject.string("memo")!!,
                                signatures = StdSignature.fromJson(jsonObject.array("signatures"))
                        )
                }
        }
}