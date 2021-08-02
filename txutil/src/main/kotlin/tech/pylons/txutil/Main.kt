package tech.pylons.txutil

import com.beust.klaxon.JsonObject
import tech.pylons.wallet.core.Core
import tech.pylons.lib.types.Backend
import tech.pylons.lib.types.Config
import tech.pylons.lib.types.PylonsSECP256K1
import tech.pylons.wallet.core.Multicore
import org.apache.tuweni.bytes.Bytes32
import java.lang.Exception
import java.util.*
import tech.pylons.lib.klaxon

@ExperimentalUnsignedTypes
object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        try {
            var op = args[0]
            val privkeyHex = args[1]
            Multicore.enable(Config(Backend.TESTNET, "pylons-testnet", true, listOf("http://127.0.0.1:1317")))
            println(when (op) {
                "SIGN_BYTES" -> try {
                    val accountNumber = args[2].toLong()
                    val sequence = args[3].toLong()
                    val msgJson = args[4]
                    Core.current!!.lowLevel.getSignBytes(privkeyHex, accountNumber, sequence, msgJson)
                } catch (e : Exception) {
                    error(e, "Exception occurred in walletcore")
                }
                "SIGNED_TX" -> try {
                    val accountNumber = args[2].toLong()
                    val sequence = args[3].toLong()
                    val msgJson = args[4]
                    Core.current!!.lowLevel.getSignedTx(privkeyHex, accountNumber, sequence, msgJson)
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
        Core.current!!.newProfile("foo",
                PylonsSECP256K1.KeyPair.fromSecretKey(
                        PylonsSECP256K1.SecretKey.fromBytes(Bytes32.fromHexString(privkey))))
        println("Waiting 5 seconds to allow chain to catch up")
        Thread.sleep(5000)
        Core.current!!.getProfile()
        Core.current!!.getPylons(50000)
        println("Waiting 5 seconds to allow chain to catch up")
        Thread.sleep(5000)
        Core.current!!.getProfile()
        Core.current!!.batchCreateCookbook(
                ids = mutableListOf(Calendar.getInstance().time.toInstant().toString()),
                names = mutableListOf("tst_cookbook_name"),
                developers = mutableListOf("addghjkllsdfdggdgjkkk"),
                descriptions = mutableListOf("asdfasdfasdfaaaaaaaaaaaaaaaaaaaaasssssssss"),
                versions = mutableListOf("1.0.0"),
                supportEmails = mutableListOf("a@example.com"),
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