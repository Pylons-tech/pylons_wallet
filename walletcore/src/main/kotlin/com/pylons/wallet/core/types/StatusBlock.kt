package com.pylons.wallet.core.types

data class StatusBlock (
        val height : Long,
        val blockTime : Double,
        val walletCoreVersion : String
) {
    fun toJson () : String = klaxon.toJsonString(this)
}