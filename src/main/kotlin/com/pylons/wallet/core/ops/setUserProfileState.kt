package com.pylons.wallet.core.ops

import com.squareup.moshi.Moshi
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.types.*

internal fun setUserProfileState (msg : MessageData) : Response {
    TODO("Redo dummy tx engine to use local pylonsd + actual cosmos backend")
    //val moshi = Moshi.Builder().add(TxDummyEngine.CredentialsAdapter()).build()
    //val adapter = moshi.adapter<Profile>(Profile::class.java)
    //val prf = adapter.fromJson(msg.strings[Keys.JSON]!!)
    //Core.setProfile(prf!!)

    //return Response(MessageData(booleans = mutableMapOf(Keys.SUCCESS to true)), Status.OK_TO_RETURN_TO_CLIENT)
}