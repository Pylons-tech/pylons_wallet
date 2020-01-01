package com.pylons.wallet.core.types.tx.msg

import com.pylons.wallet.core.types.tx.Msg
import com.squareup.moshi.Json

@MsgType("pylons/CreateCookbook")
sealed class CreateCookbook(
        @property:[Json(name = "Name")]
        val name : String,
        @property:[Json(name = "Description")]
        val description : String,
        @property:[Json(name = "Developer")]
        val developer : String,
        @property:[Json(name = "Version")]
        val version : String,
        @property:[Json(name = "SupportEmail")]
        val supportEmail : String,
        @property:[Json(name = "Sender")]
        val sender : String,
        @property:[Json(name = "Level")]
        val level : Long,
        @property:[Json(name = "CostPerBlock")]
        val costPerBlock : Long
):Msg() {
    override fun toJson(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}