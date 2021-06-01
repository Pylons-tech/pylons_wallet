package tech.pylons.lib.types

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import tech.pylons.lib.types.tx.StdTx
import tech.pylons.lib.types.tx.TxData

data class Transaction(
    val txData: TxData = TxData("", "", listOf()),
    val stdTx: StdTx? = null,
    @Json(ignored = true)
    val _id: String? = null,
    @Json(ignored = true)
    val resolver: ((Transaction) -> Unit)? = null,
    @Json(ignored = true)
    var state: State = State.TX_NOT_YET_SENT,
    var code: ResponseCode = ResponseCode.OK,
    var raw_log: String = ""
) {
    var id: String? = _id
        get() {
            if (field == null) println("Warning: got tx id, but tx id was unset. Was the transaction submitted?")
            return field
        }

    enum class State(val value: Int) {
        TX_REFUSED(-1),
        TX_NOT_YET_SENT(0),
        TX_NOT_YET_COMMITTED(1),
        TX_ACCEPTED(2),
    }

    enum class ResponseCode(val value : Int) {
        UNKNOWN_ERROR(-1),
        OK(0);

        companion object  {
            fun of (v : Int?) : ResponseCode {
                return when (v) {
                    OK.value -> OK
                    else -> UNKNOWN_ERROR
                }
            }
        }
    }

    fun submit(): Transaction {
        if (state != State.TX_NOT_YET_SENT) throw Exception("Transaction.submit() should only be called on" +
                "Transactions of state TX_NOT_YET_SENT")
        state = State.TX_NOT_YET_COMMITTED
        state = try {
            resolver?.invoke(this)
            State.TX_ACCEPTED
        } catch (e: Exception) {
            // todo: this should get some data
            //Logger().log(LogEvent.TX_SUBMIT_EXCEPTION, e.toString(), LogTag.error)
            State.TX_REFUSED
        }
        return this
    }

    companion object {
        fun List<Transaction>.submitAll() : List<Transaction> {
            val ml = this.toMutableList()
            ml.indices.forEach { ml[it] = ml[it].submit() }
            return ml
        }

        /**
         * listFromJson
         * parse Response's txs only, carefully use in other cases
         */
        fun listFromJson(jsonArray: JsonArray<JsonObject>): List<Transaction> {
            val list = mutableListOf<Transaction>()
            jsonArray.forEach {
                val doc = it
                var stdTx:StdTx? = null
                var txData:TxData? = null
                when {
                    doc.obj("stdTx") != null ->{
                        //do not need other data
                        //stdTx = StdTx.fromJson(doc.obj("stdTx")!!)
                    }
                }

                list.add(
                    Transaction(
                        stdTx = stdTx,
                        txData = TxData.fromJson(doc.obj("txData")!!),
                        raw_log = doc.string("raw_log")!!,
                        code = ResponseCode.valueOf(doc.string("code")!!),
                        _id = doc.string("id")
                    )
                )
            }

            return list
        }
        /*
        fun parseTransactionResponse(id: String, response: String): Transaction {
            val doc = Parser.default().parse(java.lang.StringBuilder(response)) as JsonObject
            val type = doc.obj("tx")?.obj("value")?.string("type").orEmpty()

            when {
                doc.contains("code") -> {
                    return Transaction(
                        stdTx = StdTx.fromJson((doc.obj("tx")!!).obj("value")!!),
                        _id = id,
                        code = ResponseCode.of(doc.int("code")),
                        raw_log = doc.string("raw_log") ?: "Unknown Error"
                    )
                }
                doc.containsKey("data") -> {
                    try {
                        //tierre change to proto tx response data parsing
                        val dataString = hexToAscii(doc.string("data")!!)
                        val dataObject = (Parser.default().parse(java.lang.StringBuilder(dataString)) as JsonObject)
                        if (dataObject.containsKey("Output") && dataObject.string("Output") != null) {
                            val arrayString = String(Base64.getDecoder().decode(dataObject.string("Output")))
                            val outputArray = Parser.default().parse(StringBuilder(arrayString)) as JsonArray<JsonObject>
                            dataObject["Output"] = outputArray
                        }

                        return Transaction(
                            txData = TxData.fromJson(dataObject),
                            stdTx = StdTx.fromJson((doc.obj("tx")!!).obj("value")!!),
                            _id = id
                        )
                    } catch (e: Exception) {
                        return Transaction(
                            stdTx = StdTx.fromJson((doc.obj("tx")!!).obj("value")!!),
                            _id = id
                        )
                    }
                }
                else -> {
                    return Transaction(
                        stdTx = StdTx.fromJson((doc.obj("tx")!!).obj("value")!!),
                        _id = id
                    )
                }
            }
        }
        */


        fun parseTransactionResponse(id: String, response: String, data: String): Transaction {
            val doc = Parser.default().parse(java.lang.StringBuilder(response)) as JsonObject
            when {
                doc.contains("code") -> {
                    return Transaction(
                        stdTx = StdTx.fromJson((doc.obj("tx")!!).obj("value")!!),
                        _id = id,
                        code = ResponseCode.of(doc.int("code")),
                        raw_log = doc.string("raw_log") ?: "Unknown Error"
                    )
                }
                doc.containsKey("data") -> {
                    try {
                        //tierre change to proto tx response data parsing
                        //val dataString = hexToAscii(doc.string("data")!!)
                        val dataString = data

                        /*
                        val dataObject = (Parser.default().parse(java.lang.StringBuilder(dataString)) as JsonObject)
                        if (dataObject.containsKey("Output") && dataObject.string("Output") != null) {
                            val arrayString = String(Base64.getDecoder().decode(dataObject.string("Output")))
                            val outputArray = Parser.default().parse(StringBuilder(arrayString)) as JsonArray<JsonObject>
                            dataObject["Output"] = outputArray
                        }
                         */

                        return Transaction(
                            txData = TxData(msg = "", status = "", output = listOf()), //TxData.fromJson(dataObject),
                            stdTx = StdTx.fromJson((doc.obj("tx")!!).obj("value")!!),
                            _id = id
                        )
                    } catch (e: Exception) {
                        return Transaction(
                            stdTx = StdTx.fromJson((doc.obj("tx")!!).obj("value")!!),
                            _id = id
                        )
                    }
                }
                else -> {
                    return Transaction(
                        stdTx = StdTx.fromJson((doc.obj("tx")!!).obj("value")!!),
                        _id = id
                    )
                }
            }
        }
    }
}

fun hexToAscii(hexStr: String): String? {
    val output = StringBuilder("")
    var i = 0
    while (i < hexStr.length) {
        val str = hexStr.substring(i, i + 2)
        output.append(str.toInt(16).toChar())
        i += 2
    }
    return output.toString()
}