package com.pylons.lib.types

import com.pylons.lib.klaxon

/**
 * Object encapsulating some basic wallet/chain state info.
 * Wallets should serialize this and pass it to clients over the IPC mechanism.
 */
data class StatusBlock (
        val height : Long,
        val blockTime : Double,
        val walletCoreVersion : String
) {
    fun toJson () : String = klaxon.toJsonString(this)
}