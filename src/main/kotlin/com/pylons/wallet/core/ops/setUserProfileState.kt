package com.pylons.wallet.core.ops

import com.squareup.moshi.Moshi
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.engine.TxDummyEngine
import com.pylons.wallet.core.internal.bufferForeignProfile
import com.pylons.wallet.core.internal.generateErrorMessageData
import com.pylons.wallet.core.types.*

internal fun setUserProfileState (msg : MessageData) : Response {
    val moshi = Moshi.Builder().add(TxDummyEngine.CredentialsAdapter()).build()
    val adapter = moshi.adapter<Profile>(Profile::class.java)
    val prf = adapter.fromJson(msg.strings[Keys.json]!!)
    Core.setProfile(prf!!)

    return Response(MessageData(booleans = mutableMapOf(Keys.success to true)), Status.OK_TO_RETURN_TO_CLIENT)
}