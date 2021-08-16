package tech.pylons.lib

object ErrorMsgUtil {
    fun toErrorMsg(msg: String?): String? {
        var ret: String? = null
        if(msg == null) {ret = null}
        else{
            if(msg.contains("insufficient funds")){ ret = "Insufficient funds" }
            else if(msg.contains("insufficient coin balance")) {ret = "Insufficient coin balance" }
            else if (msg.contains("this trade is already completed")) {ret = "This trade s already completed"}
            else {ret = msg}
        }
        return ret
    }
}