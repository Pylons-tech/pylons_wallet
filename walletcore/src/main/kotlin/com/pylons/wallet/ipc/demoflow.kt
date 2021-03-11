package com.pylons.wallet.ipc

import java.util.*

var fakeIpcJson : String = ""

@IPCLayer.Implementation
class FakeIPC : IPCLayer(false) {
    override fun getNextJson(callback: (String) -> Unit) {
        callback(fakeIpcJson)
    }

    override fun establishConnection() {
        println("fake establish connection")
    }

    override fun checkConnectionStatus(): ConnectionState {
        return ConnectionState.Connected
    }

    override fun connectionBroken() {
        println("fake break connection")
    }

    override fun submit(r: Message.Response) {
        println("fake submit ${klaxon.toJsonString(r)}")
    }

    override fun reject(json: String) {
        println("fake reject $json")
    }

}

@UILayer.Implementation
class FakeUI : UILayer() {
    override fun onAddUiHook(uiHook: Message.UiHook) {

    }

    override fun onReleaseUiHook(uiHook: Message.UiHook) {

    }

}

fun demoflow () {
    val msg = Message.WalletServiceTest("input")
    val msgJson =
            Base64.getEncoder().encodeToString(
                    klaxon.toJsonString(msg).toByteArray(Charsets.US_ASCII))
    val json = """{"type":"WalletServiceTest", "msg":"$msgJson", "messageId":0, "clientId":0, "walletId":${IPCLayer.implementation.walletId}}"""

    fakeIpcJson = json
    IPCLayer.getNextMessage {
        UILayer.getUiHook(it)?.release()
    }
}