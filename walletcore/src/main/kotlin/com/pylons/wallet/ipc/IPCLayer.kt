package com.pylons.wallet.ipc

import io.github.classgraph.*
import kotlin.reflect.jvm.jvmName

abstract class IPCLayer {
    annotation class Implementation

    annotation class OnGetNext

    protected abstract fun getNextJson() : String
    protected open fun preprocessResponse(r : Message.Response) = r

    fun onMessage (msg : Message) {
        msg.ui()
    }

    fun onUiReleased (uiHook: Message.UiHook) {
        uiHook.msg.resolve().forEach { handleResponse(it) }
    }

    companion object {
        private val implementation : IPCLayer = findImplementation()
        private val onGetNextList : List<MethodInfo> = findAllOnGetNextMethods()

        private fun findImplementation () : IPCLayer {
            val scanResult = ClassGraph().enableAllInfo().acceptPackages().scan()
            val c = scanResult.getClassesWithAnnotation(Implementation::class.jvmName)
            if (c.size == 0) throw Exception("No registered implementation of IPCLayer in classpath")
            else if (!c[0].extendsSuperclass(IPCLayer::class.qualifiedName))
                throw Exception("${c[0].name} does not extend IPCLayer")
            return c[0].loadClass().getConstructor().newInstance() as IPCLayer
        }

        private fun findAllOnGetNextMethods () : List<MethodInfo> {
            val m = mutableListOf<MethodInfo>()
            val scanResult = ClassGraph().enableAllInfo().acceptPackages().scan()
            scanResult.allClasses.forEach { classInfo ->
                classInfo.methodInfo.forEach {
                    if (it.hasAnnotation(OnGetNext::class.qualifiedName)) m.add(it)
                }
            }
            return m
        }

        fun getNextMessage() : Message {
            val next = implementation.getNextJson()
            val msg = Message.match(next)
            onGetNextList.forEach {
                it.loadClassAndGetMethod().invoke(it.classInfo, msg)
            }
            msg.ui()
            implementation.onMessage(msg)
            return msg
        }

        fun handleResponse(r : Message.Response) {
            implementation.preprocessResponse(r)
            r.submit()
        }
    }
}