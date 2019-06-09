package com.pylons.wallet.core.types

import org.bitcoinj.core.Bech32

internal class PylonsBech32 {
    class Data (
            val data : ByteArray = byteArrayOf(),
            val hrp : String = ""
    )

    companion object {
        fun decode (input : String) : Data {
            // rn: just passing through to bitcoinj to see if it works
            val bcj = Bech32.decode(input)
            return Data (bcj.data, bcj.hrp)
        }
    }

    fun fromKey(key : String) : String {
        return key.toString()
    }
}
