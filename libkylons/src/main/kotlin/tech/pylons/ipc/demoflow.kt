package tech.pylons.ipc

import java.util.*
import tech.pylons.lib.klaxon

var fakeIpcJson : String = ""

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

    override fun submit(r: Response) {
        println("fake submit ${klaxon.toJsonString(r)}")
    }

    override fun reject(json: String) {
        println("fake reject $json")
    }

}

class FakeUI : UILayer() {
    override fun onAddUiHook(uiHook: Message.UiHook) {
        //in the UI thread or some where else call one of this
        //if to continue the ui flow call UILayer.confirmUiHook()
        //else to reject call UILayer.rejectUiHook()

        //UILayer.confirmUiHook(UILayer.uiHooks.last())
        //UILayer.rejectUiHook(UILayer.uiHooks.last())
        UILayer.confirmUiHook(uiHook)
        //UILayer.rejectUiHook(uiHook)
    }

    override fun onConfirmUiHook(uiHook: Message.UiHook) {

    }

    override fun onRejectUiHook(uiHook: Message.UiHook) {

    }

    override fun onReleaseUiHook(uiHook: Message.UiHook) {

    }

}

fun demoflow () {
    IPCLayer.implementation = FakeIPC()
    UILayer.implementation = FakeUI()
    val msg = Message.WalletServiceTest("input")
    val msgJson =
            Base64.getEncoder().encodeToString(
                    klaxon.toJsonString(msg).toByteArray(Charsets.US_ASCII))
    val json = """{"type":"WalletServiceTest", "msg":"$msgJson", "messageId":0, "clientId":0, "walletId":${IPCLayer.implementation!!.walletId}}"""

    fakeIpcJson = json
    IPCLayer.getNextMessage {
        UILayer.getUiHook(it)?.release()
    }
}