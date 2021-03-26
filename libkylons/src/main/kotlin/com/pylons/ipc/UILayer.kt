package com.pylons.ipc

import io.github.classgraph.ClassGraph
import kotlin.reflect.jvm.jvmName

abstract class UILayer {
    annotation class Implementation

    protected abstract fun onAddUiHook(uiHook: Message.UiHook)
    protected abstract fun onReleaseUiHook(uiHook: Message.UiHook)

    companion object {
        val uiHooks : MutableList<Message.UiHook> = mutableListOf()

        private val implementation : UILayer = findImplementation()

        private fun findImplementation () : UILayer {
            val scanResult = ClassGraph().enableAllInfo().acceptPackages().scan()
            val c = scanResult.getClassesWithAnnotation(Implementation::class.jvmName)
            if (c.size == 0) throw Exception("No registered implementation of UILayer in classpath")
            else if (!c[0].extendsSuperclass(UILayer::class.qualifiedName))
                throw Exception("${c[0].name} does not extend UILayer")
            return c[0].loadClass().getConstructor().newInstance() as UILayer
        }

        fun addUiHook(uiHook: Message.UiHook) : Message.UiHook {
            uiHooks.add(uiHook)
            implementation.onAddUiHook(uiHook)
            return uiHook
        }

        fun releaseUiHook(uiHook: Message.UiHook) : Message.UiHook {
            uiHooks.remove(uiHook)
            uiHook.release()
            implementation.onReleaseUiHook(uiHook)
            IPCLayer.onUiReleased(uiHook)
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