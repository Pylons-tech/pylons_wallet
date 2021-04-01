package com.pylons.ipc

import com.pylons.lib.klaxon

@IPCLayer.Implementation
class HttpIpc : IPCLayer(false) {
    override fun establishConnection() {
        println("establishing connection")
        HttpIpcWire.doHandshake()
        println("connection established")
    }

    override fun checkConnectionStatus(): ConnectionState {
        return when (clientId) {
            0 -> ConnectionState.NoClient
            else -> ConnectionState.Connected
            // todo: implement a way to identify/recover from broken connection
        }
    }

    override fun connectionBroken() {
        TODO("Not yet implemented")
    }

    override fun getNextJson(callback: (String) -> Unit) {
        callback(HttpIpcWire.readMessage().orEmpty())
    }

    override fun reject(json: String) {
        HttpIpcWire.writeString("""{"rejected_message":"$json"}""")
    }

    override fun submit(r: Message.Response) {
        HttpIpcWire.writeString(klaxon.toJsonString(r))
    }
}