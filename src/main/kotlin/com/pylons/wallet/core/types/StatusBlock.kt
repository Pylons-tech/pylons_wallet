package com.pylons.wallet.core.types

import com.squareup.moshi.Moshi

data class StatusBlock (
        val height : Long,
        val blockTime : Double,
        val walletCoreVersion : String
) {
    fun toJson () : String {
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter<StatusBlock>(StatusBlock::class.java)
        return jsonAdapter.toJson(this)
    }
}