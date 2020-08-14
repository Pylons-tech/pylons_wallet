package com.pylons.wallet.ipc

import io.github.classgraph.*
import kotlin.reflect.jvm.jvmName
import kotlin.reflect.typeOf

abstract class IPCLayer {
    class NoClientException : Exception() { }
    class ConnectionBrokenException : Exception() { }

    enum class ConnectionState {
        NoClient,
        Connected,
        ConnectionBroken
    }

    annotation class Implementation

    annotation class OnGetNext

    protected open fun initIpcChannel() { initialized = true }
    protected abstract fun getNextJson(callback: (String) -> Unit)
    protected open fun preprocessResponse(r :Message.Response,
                                          callback: (Message.Response) -> Unit) {callback(r)}
    protected open fun cleanup() {}
    abstract fun establishConnection()
    abstract fun checkConnectionStatus() : ConnectionState
    abstract fun connectionBroken()
    abstract fun submit (r : Message.Response)
    abstract fun reject (json : String)

    var connectionState : ConnectionState = ConnectionState.NoClient
        private set
    protected var initialized = false

    fun onMessage (msg : Message) {
        msg.ui()
    }

    fun onUiReleased (uiHook: Message.UiHook) {
        uiHook.msg.resolve().forEach { handleResponse(it) }
    }

    companion object {
        private val implementation : IPCLayer = findImplementation()
        private val onGetNextList : List<MethodInfo> = findAllOnGetNextMethods()

        private fun updateConnectionState() {
            implementation.connectionState = implementation.checkConnectionStatus()
            if (implementation.connectionState == ConnectionState.NoClient)
                throw NoClientException()
            else if (implementation.connectionState == ConnectionState.ConnectionBroken)
                throw ConnectionBrokenException()
        }

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

        fun onUiReleased(uiHook: Message.UiHook) = implementation.onUiReleased(uiHook)

        fun getNextMessage(callback : (Message) -> Unit) {
            safelyDoIpcOperation {
                val next = implementation.getNextJson {
                    val msg = Message.match(it)
                    when (msg) {
                        null -> implementation.reject(it)
                        else -> {
                            onGetNextList.forEach { method ->
                                method.loadClassAndGetMethod().invoke(method.classInfo, msg)
                            }
                            msg.ui()
                            implementation.onMessage(msg)
                            callback(msg)
                        }
                    }
                }
            }
        }

        fun handleResponse(r : Message.Response) {
            safelyDoIpcOperation {
                implementation.preprocessResponse(r) {
                    implementation.submit(r)
                    implementation.cleanup()
                }
            }
        }

        private fun safelyDoIpcOperation (action : () -> Unit) {
            try {
                updateConnectionState()
                action()
            } catch (e : Exception) {
                when (e) {
                    is NoClientException -> {
                        implementation.establishConnection()
                    }
                    is ConnectionBrokenException -> {
                        implementation.connectionBroken()
                    }
                    else -> throw e // todo: this should do some logging before it dies
                }
            }
        }
    }
}