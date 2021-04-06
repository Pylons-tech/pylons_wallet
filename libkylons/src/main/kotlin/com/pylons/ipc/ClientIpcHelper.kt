package com.pylons.ipc

abstract class ClientIpcHelper {

    protected abstract fun writeString(s: String)

    companion object {
        private val IMPLEMENTATION: ClientIpcHelper = findImplementation()

        private fun findImplementation(): ClientIpcHelper {
            return Class.forName("com.pylons.ipc.EaselIpcHelper").getConstructor().newInstance() as ClientIpcHelper
            /*val scanResult = ClassGraph().enableAllInfo().acceptPackages().scan()
            val c = scanResult.getClassesWithAnnotation(Implementation::class.jvmName)
            if (c.size == 0) throw Exception("No registered implementation of ClientIpcHelper in classpath")
            else if (!c[0].extendsSuperclass(ClientIpcHelper::class.qualifiedName))
                throw Exception("${c[0].name} does not extend ClientIpcHelper")
            return c[0].loadClass().getConstructor().newInstance() as ClientIpcHelper*/
        }

        fun callWriteString(s: String) {
            IMPLEMENTATION.writeString(s)
        }
    }

}