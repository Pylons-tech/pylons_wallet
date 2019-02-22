package walletcore.ops

import com.squareup.moshi.Moshi
import walletcore.Core
import walletcore.constants.Keys
import walletcore.internal.bufferForeignProfile
import walletcore.internal.generateErrorMessageData
import walletcore.tx.OutsideWorldDummy
import walletcore.types.*

fun setOtherUserProfileState (msg : MessageData) : Response {
    val moshi = Moshi.Builder().build()
    val adapter = moshi.adapter<ForeignProfile>(ForeignProfile::class.java)
    val id = msg.strings[Keys.id]!!
    val prf = adapter.fromJson(msg.strings[Keys.json]!!)!!
    OutsideWorldDummy.addProfile(id, prf)
    return Response(MessageData(booleans = mutableMapOf(Keys.success to true)), Status.OK_TO_RETURN_TO_CLIENT)
}