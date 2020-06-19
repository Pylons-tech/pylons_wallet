package com.pylons.ipc

import javax.json.JsonObject

abstract class IPCMessage {
    abstract class Response

    /**
     * Deserializes a JsonObject into the k
     */
    abstract fun deserializes(out IPCMessagejsonObject: JsonObject) : Boolean

    abstract fun resolve()
}