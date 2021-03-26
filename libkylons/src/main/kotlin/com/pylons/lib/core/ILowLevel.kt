package com.pylons.lib.core

@ExperimentalUnsignedTypes
interface ILowLevel {
    fun getSignedTx (privkeyHex : String, accountNumber : Long, sequence : Long, msgJson : String) : String
    fun getSignBytes (privkeyHex : String, accountNumber : Long, sequence : Long, msgJson : String) : String
}