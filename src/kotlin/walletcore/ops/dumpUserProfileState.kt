package walletcore.ops

import com.squareup.moshi.Moshi
import walletcore.Core
import walletcore.constants.Keys
import walletcore.types.*

fun dumpUserProfileState (msg : MessageData) : Response {
    val moshi = Moshi.Builder().build()
    val adapter = moshi.adapter<Profile>(Profile::class.java)
    val json = adapter.toJson(Core.userProfile!!)
    return Response(MessageData(booleans = mutableMapOf(Keys.success to true), strings = mutableMapOf(Keys.json to json)), Status.OK_TO_RETURN_TO_CLIENT)
}