package com.pylons.ipc


abstract class DroidIpcWire {

    protected abstract fun writeString(s: String)

    protected abstract fun readString(): String?

    companion object {
        var implementation: DroidIpcWire? = null

//        private fun findImplementation(): ClientIpcHelper {
        //return Class.forName("com.pylons.ipc.EaselIpcHelper").getConstructor().newInstance() as ClientIpcHelper
        /*val scanResult = ClassGraph().enableAllInfo().acceptPackages().scan()
        val c = scanResult.getClassesWithAnnotation(Implementation::class.jvmName)
        if (c.size == 0) throw Exception("No registered implementation of ClientIpcHelper in classpath")
        else if (!c[0].extendsSuperclass(ClientIpcHelper::class.qualifiedName))
            throw Exception("${c[0].name} does not extend ClientIpcHelper")
        return c[0].loadClass().getConstructor().newInstance() as ClientIpcHelper*/
//        }

        fun writeMessage(s: String) {
            implementation!!.writeString(s)
        }

        fun readMessage(): String? {
            var ret: String?
            var elapsedMillis = 0L
            while (true) {
                ret = implementation!!.readString()
                if (ret == null) {
                    Thread.sleep(100)
                    elapsedMillis += 100L
                } else {
                    return ret
                }

                if (elapsedMillis > 10 * 1000L) { // no response for 10 secs
                    println("Error: Wallet is busy or connection to wallet is unavailable. Try again later!")
                    return null
                }
            }
        }
    }

}