package com.pylons.wallet.core.ops

import com.squareup.moshi.Moshi
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.bufferForeignProfile
import com.pylons.wallet.core.internal.generateErrorMessageData
import com.pylons.wallet.core.types.*

fun setUserProfileState (msg : MessageData) : Response {
    val moshi = Moshi.Builder().build()
    val adapter = moshi.adapter<Profile>(Profile::class.java)
    val prf = adapter.fromJson(msg.strings[Keys.json]!!)
    Core.setProfile(prf!!)

    return Response(MessageData(booleans = mutableMapOf(Keys.success to true)), Status.OK_TO_RETURN_TO_CLIENT)
}