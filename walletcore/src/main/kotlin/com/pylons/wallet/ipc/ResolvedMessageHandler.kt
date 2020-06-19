package com.pylons.wallet.ipc

abstract class ResolvedMessageHandler {


    companion object {
        private var handler : ResolvedMessageHandler? = null


    }

    init {
        handler = this
    }
}