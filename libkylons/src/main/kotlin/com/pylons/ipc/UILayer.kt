package com.pylons.ipc

abstract class UILayer {
    protected abstract fun onAddUiHook(uiHook: Message.UiHook)
    protected abstract fun onConfirmUiHook(uiHook: Message.UiHook)
    protected abstract fun onReleaseUiHook(uiHook: Message.UiHook)

    companion object {
        val uiHooks : MutableList<Message.UiHook> = mutableListOf()

        var implementation : UILayer? = null

        fun addUiHook(uiHook: Message.UiHook) : Message.UiHook {
            uiHooks.add(uiHook)
            implementation!!.onAddUiHook(uiHook)
            return uiHook
        }

        fun confirmUiHook(uiHook: Message.UiHook) : Message.UiHook {
            uiHook.confirm()
            implementation!!.onConfirmUiHook(uiHook)
            IPCLayer.implementation!!.onUiConfirmed(uiHook)
            return uiHook
        }

        fun releaseUiHook(uiHook: Message.UiHook) : Message.UiHook {
            uiHooks.remove(uiHook)
            uiHook.release()
            implementation!!.onReleaseUiHook(uiHook)
            IPCLayer.implementation!!.onUiReleased(uiHook)
            return uiHook
        }

        fun getUiHook(msg : Message) : Message.UiHook? {
            uiHooks.forEach {
                if (it.msg == msg) return it
            }
            return null
        }
    }
}