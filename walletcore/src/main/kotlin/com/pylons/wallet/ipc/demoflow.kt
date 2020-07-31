package com.pylons.wallet.ipc

import com.pylons.wallet.core.types.klaxon
import com.pylons.wallet.ipc.Message.ApplyRecipe

var fakeIpcJson : String = ""

@IPCLayer.Implementation
class FakeIPC : IPCLayer() {


    override fun getNextJson(): String {
        return fakeIpcJson
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


    val msg = ApplyRecipe("recipe", "cookbook", listOf())
    val json = klaxon.toJsonString(msg)

    fakeIpcJson = json
    IPCLayer.getNextMessage().response?.submit()
}