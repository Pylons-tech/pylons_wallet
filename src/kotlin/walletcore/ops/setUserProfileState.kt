package walletcore.ops

import com.squareup.moshi.Moshi
import walletcore.Core
import walletcore.constants.Keys
import walletcore.internal.bufferForeignProfile
import walletcore.internal.generateErrorMessageData
import walletcore.types.*

fun setUserProfileState (msg : MessageData) : Response {
    val moshi = Moshi.Builder().build()
    val adapter = moshi.adapter<Profile>(Profile::class.java)
    val prf = adapter.fromJson(msg.strings[Keys.json]!!)
    Core.setProfile(prf!!)

    return Response(MessageData(booleans = mutableMapOf(Keys.success to true)), Status.OK_TO_RETURN_TO_CLIENT)
}