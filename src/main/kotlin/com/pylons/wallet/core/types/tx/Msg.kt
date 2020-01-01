package com.pylons.wallet.core.types.tx

abstract class Msg {
    abstract fun toJson : String
}