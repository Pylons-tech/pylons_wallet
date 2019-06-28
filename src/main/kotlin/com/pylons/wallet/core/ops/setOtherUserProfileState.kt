package com.pylons.wallet.core.ops

import com.squareup.moshi.Moshi
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.engine.OutsideWorldDummy
import com.pylons.wallet.core.types.*

internal fun setOtherUserProfileState (msg : MessageData) : Response {
    val moshi = Moshi.Builder().build()
    val adapter = moshi.adapter<ForeignProfile>(ForeignProfile::class.java)
    val id = msg.strings[Keys.id]!!
    val prf = adapter.fromJson(msg.strings[Keys.json]!!)!!
    OutsideWorldDummy.addProfile(id, prf)
    return Response(MessageData(booleans = mutableMapOf(Keys.success to true)), Status.OK_TO_RETURN_TO_CLIENT)
}