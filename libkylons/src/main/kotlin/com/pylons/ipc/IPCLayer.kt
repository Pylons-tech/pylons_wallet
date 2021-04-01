package com.pylons.ipc

import java.lang.reflect.Method
import kotlin.random.Random
import kotlin.reflect.jvm.jvmName

abstract class IPCLayer(val permitUnboundOperations : Boolean) {
    var clientId : Int = 0
    var walletId : Int = 0 //Random.nextInt()
    var messageId : Int = 0

    class NoClientException : Exception() { }
    class ConnectionBrokenException : Exception() { }

    enum class ConnectionState {
        NoClient,
        Connected,
        ConnectionBroken
    }

    annotation class OnGetNext

    protected open fun initIpcChannel() { if (!permitUnboundOperations) establishConnection(); initialized = true }
    protected abstract fun getNextJson(callback: (String) -> Unit)
    protected open fun preprocessResponse(r : Message.Response,
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

    fun onUiConfirmed (uiHook: Message.UiHook) {
        uiHook.response = uiHook.msg.resolve()
        handleResponse(uiHook.response!!)
    }

    fun onUiReleased (uiHook: Message.UiHook) {
        // Unclear on what if anything needs to be done here rn
    }

    companion object {
        var implementation : IPCLayer? = null
            set(value) { field = value; init() }
        private lateinit var onGetNextList : List<Method>

        private fun init () {
            onGetNextList = findAllOnGetNextMethods()
        }

        private fun updateConnectionState() {
            implementation!!.connectionState = implementation!!.checkConnectionStatus()
            if (implementation!!.connectionState == ConnectionState.NoClient && !implementation!!.permitUnboundOperations)
                throw NoClientException()
            else if (implementation!!.connectionState == ConnectionState.ConnectionBroken && !implementation!!.permitUnboundOperations)
                throw ConnectionBrokenException()
        }

        private fun findAllOnGetNextMethods () : List<Method> {
            val ls = mutableListOf<Method>()
            implementation!!::class.java.declaredMethods.forEach {
                if (it.getAnnotation(OnGetNext::class.java) != null)
                    ls.add(it)
            }
            return ls
        }

        fun getNextMessage(callback : (Message) -> Unit) {
            safelyDoIpcOperation {
                implementation?.getNextJson {
                    println("got: \n$it")
                    val msg = Message.match(it)
                    println("matched!")
                    when (msg) {
                        null -> implementation!!.reject(it)
                        else -> {
                            onGetNextList.forEach { method ->
                                method.invoke(implementation, msg)
                            }
                            implementation!!.onMessage(msg)
                            println("trying to do callback")
                            callback(msg)
                        }
                    }
                }
            }
        }

        fun SetImplementation(impl:IPCLayer){
            implementation = impl
        }

        fun handleResponse(r : Message.Response) {
            safelyDoIpcOperation {
                implementation!!.preprocessResponse(r) {
                    implementation!!.submit(r)
                    implementation!!.cleanup()
                }
            }
        }

        private fun safelyDoIpcOperation (action : () -> Unit) {
            if (!implementation!!.initialized) implementation!!.initIpcChannel()
            try {
                updateConnectionState()
                action()
            } catch (e : Exception) {
                when (e) {
                    is NoClientException -> {
                        implementation!!.establishConnection()
                    }
                    is ConnectionBrokenException -> {
                        implementation!!.connectionBroken()
                    }
                    else -> throw e // todo: this should do some logging before it dies
                }
            }
        }
    }
}