package com.pylons.txutil

import com.beust.klaxon.JsonObject
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.LowLevel
import com.pylons.wallet.core.ops.batchCreateCookbook
import com.pylons.wallet.core.ops.getProfile
import com.pylons.wallet.core.ops.getPylons
import com.pylons.wallet.core.ops.newProfile
import com.pylons.wallet.core.types.Backend
import com.pylons.wallet.core.types.Config
import com.pylons.wallet.core.types.PylonsSECP256K1
import com.pylons.wallet.core.types.klaxon
import org.apache.tuweni.bytes.Bytes32
import java.lang.Exception
import java.util.*

@ExperimentalUnsignedTypes
object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        try {
            var op = args[0]
            val privkeyHex = args[1]
            Core.start(Config(Backend.LIVE_DEV, listOf("http://127.0.0.1:1317")), "")
            println(when (op) {
                "SIGN_BYTES" -> try {
                    val accountNumber = args[2].toLong()
                    val sequence = args[3].toLong()
                    val msgJson = args[4]
                    LowLevel.getSignBytes(privkeyHex, accountNumber, sequence, msgJson)
                } catch (e : Exception) {
                    error(e, "Exception occurred in walletcore")
                }
                "SIGNED_TX" -> try {
                    val accountNumber = args[2].toLong()
                    val sequence = args[3].toLong()
                    val msgJson = args[4]
                    LowLevel.getSignedTx(privkeyHex, accountNumber, sequence, msgJson)
                } catch (e : Exception) {
                    error(e, "Exception occurred in walletcore")
                }
                "AUTO_CREATE_COOKBOOK" -> {
                    doMessages(privkeyHex)
                }
                else -> "Invalid operation $op"
            })
        }
        catch (e : IndexOutOfBoundsException) {
            println(error(e, ("Expected 5 arguments (privkey, account no, sequence, msg, op), got ${args.size} instead")))
        }
    }

    private fun doMessages (privkey : String) {
        Core.newProfile("foo",
                PylonsSECP256K1.KeyPair.fromSecretKey(
                        PylonsSECP256K1.SecretKey.fromBytes(Bytes32.fromHexString(privkey))))
        println("Waiting 5 seconds to allow chain to catch up")
        Thread.sleep(5000)
        Core.getProfile()
        Core.getPylons(50000)
        println("Waiting 5 seconds to allow chain to catch up")
        Thread.sleep(5000)
        Core.getProfile()
        Core.batchCreateCookbook(
                ids = mutableListOf(Calendar.getInstance().time.toInstant().toString()),
                names = mutableListOf("tst_cookbook_name"),
                developers = mutableListOf("addghjkllsdfdggdgjkkk"),
                descriptions = mutableListOf("asdfasdfasdfaaaaaaaaaaaaaaaaaaaaasssssssss"),
                versions = mutableListOf("1.0.0"),
                supportEmails = mutableListOf("a@example.com"),
                levels = mutableListOf(0),
                costsPerBlock = mutableListOf(5))
    }

    private fun error (e : Exception, info : String) : String {
        val eSerialized = klaxon.toJsonString(e)
        val outObj = JsonObject()
        outObj["exception"] = eSerialized
        outObj["info"] = info
        outObj["stackTrace"] = e.stackTrace!!.contentDeepToString()
        outObj["exceptionType"] = e.javaClass.name
        return outObj.toJsonString()
    }
}