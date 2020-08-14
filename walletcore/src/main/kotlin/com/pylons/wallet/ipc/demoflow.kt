package com.pylons.wallet.ipc

import com.pylons.wallet.core.types.klaxon

var fakeIpcJson : String = ""

@IPCLayer.Implementation
class FakeIPC : IPCLayer() {
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
    val msg = Message.WalletServiceTest()
    val json = klaxon.toJsonString(msg)

    fakeIpcJson = json
    IPCLayer.getNextMessage {
        it.response?.submit()
    }
}