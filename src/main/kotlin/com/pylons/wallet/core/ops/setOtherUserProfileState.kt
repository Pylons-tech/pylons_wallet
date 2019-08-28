package com.pylons.wallet.core.ops

import com.squareup.moshi.Moshi
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.engine.OutsideWorldDummy
import com.pylons.wallet.core.internal.BadMessageException
import com.pylons.wallet.core.types.*

internal fun setOtherUserProfileState (msg : MessageData) : Response {
    require (msg.strings.containsKey(Keys.ADDRESS)) { throw BadMessageException("setUserProfileState", Keys.ADDRESS, "String") }
    require (msg.strings.containsKey(Keys.JSON)) { throw BadMessageException("setUserProfileState", Keys.JSON, "String") }
    val moshi = Moshi.Builder().build()
    val adapter = moshi.adapter<ForeignProfile>(ForeignProfile::class.java)
    val id = msg.strings[Keys.ADDRESS]!!
    val prf = adapter.fromJson(msg.strings[Keys.JSON]!!)!!
    OutsideWorldDummy.addProfile(id, prf)
    return Response(MessageData(booleans = mutableMapOf(Keys.SUCCESS to true)), Status.OK_TO_RETURN_TO_CLIENT)
}