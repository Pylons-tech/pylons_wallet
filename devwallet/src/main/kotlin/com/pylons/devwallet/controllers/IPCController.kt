package com.pylons.devwallet.controllers

import com.pylons.ipc.ipcProtos
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.logging.Logger
import com.pylons.wallet.core.constants.Actions
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.logging.LogEvent
import com.pylons.wallet.core.logging.LogTag
import com.pylons.wallet.core.types.MessageData
import com.pylons.wallet.core.types.Response
import com.pylons.wallet.core.types.Status
import com.pylons.wallet.core.types.klaxon
import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.util.Duration
import org.apache.commons.codec.binary.Hex
import tornadofx.*
import java.lang.Exception
import java.net.ServerSocket
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.charset.Charset

class GotIpcMessageEvent (val msg : String, val bytesDown : Long) : FXEvent()
class MessageResolvedEvent(val pylonsAction: String, val messageData: MessageData,
                           val status: Status, val response: Response) : FXEvent()

/**
 * Called regularly by IpcController.timeline.
 * On each HeartbeatRequest, we run HeartBeatEvent and pump IPC messages.
 */
object HeartbeatRequest : FXEvent(EventBus.RunOn.BackgroundThread)

/**
 * Runs every HEARTBEAT_INTERVAL. Gets current system state/health info from walletcore.
 */
class HeartbeatEvent(val version : String, val started : Boolean, val sane : Boolean,
                     val suspendedAction : String) : FXEvent()

const val HEARTBEAT_INTERVAL = 1.0
const val HANDSHAKE_MAGIC = "DEVWALLET_SERVER"
const val HANDSHAKE_REPLY_MAGIC = "DEVWALLET_CLIENT"

@ExperimentalUnsignedTypes
class IPCController  : Controller() {

    private enum class State {
        Error,
        WaitForHandshake,
        OperationInProgress,
        AwaitingMessage
    }
    private var socket: ServerSocket? = null
    private var client: Socket? = null
    private var state = State.WaitForHandshake
    private val ascii = Charset.forName("US-ASCII")
    private var timeline : Timeline? = null

    init {
        socket = ServerSocket(50001)
        startCoreUpdateTicks()
        subscribe<HeartbeatRequest> { heartbeat() }
    }

    private fun heartbeat () {
        val started = Core.started
        val sane = Core.sane
        val suspendedAction = Core.suspendedAction.orEmpty()
        fire(HeartbeatEvent(Core.VERSION_STRING, started, sane, suspendedAction))
        if (client?.isConnected != true) resetConnectionState()
        pumpIpcMessages()
    }

    private fun resetConnectionState () {
        state = State.WaitForHandshake
        client = null
        socket?.close()
    }

    private fun handleIpcHandshake () {
        val chkOutput = checkForHandshake()
        state = if (chkOutput.success) State.AwaitingMessage
        else {
            Logger.implementation.log(LogEvent.IPC_HANDSHAKE_FAIL, """{"data":"${chkOutput.data}"}""", LogTag.walletError)
            State.Error
        }
    }

    private fun pumpIpcMessages () {
        when (state) {
            State.Error -> {
                Logger.implementation.log(LogEvent.RESET_IPC_STATE, "", LogTag.walletError)
                resetConnectionState()
            }
            State.WaitForHandshake -> handleIpcHandshake()
            State.AwaitingMessage -> {
                val chk = checkForIncomingMessage()
                if (chk.result!!) processMessage(chk.msg!!) {
                    when (it.status) {
                        Status.OK_TO_RETURN_TO_CLIENT -> returnMessageToClient(it)
                        Status.INCOMING_MESSAGE_MALFORMED -> rejectMessage(it)
                        Status.REQUIRE_UI_ELEVATION -> requireUiElevation(chk.msg)
                    }
                }
                else resetConnectionState()
            }
            State.OperationInProgress -> {
                // pass
            }
        }
    }

    private fun processMessage(msg: MessageData, callback : (Response) -> Unit) {
        if (!msg.strings.containsKey("@P__ACTION")) {
            Logger.implementation.log(LogEvent.NO_ACTION_FIELD, """{"msg":$msg}""", LogTag.malformedData)
            state = State.Error
        }
        else Core.resolveMessage(msg, callback)
    }

    private fun returnMessageToClient (response: Response) {
        Logger.implementation.log(LogEvent.RETURN_MESSAGE_TO_CLIENT,
                """{"status":"${response.status}", "msg":${response.msg}}""",
                LogTag.info)
        sendMessage(response.msg!!.merge(MessageData(
                strings = mutableMapOf("@P__STATUS_BLOCK" to response.statusBlock.toJson()))))
    }

    private fun rejectMessage (response: Response) {
        Logger.implementation.log(LogEvent.REJECT_MESSAGE, """{"status":"${response.status}", "msg":${response.msg}}""", LogTag.info)
        sendMessage(response.msg!!.merge(MessageData(
                strings = mutableMapOf("@P__STATUS_BLOCK" to response.statusBlock.toJson()))))
    }

    private fun requireUiElevation (msg: MessageData) {
        val pylonsAction = msg.strings["@P__ACTION"]
        returnMessageToClient(Core.finishSuspendedActionWithArgs(produceArguments(pylonsAction!!))!!)
    }

    private class CheckOutput(val result: Boolean?, val msg: MessageData?)

    private fun checkForIncomingMessage(): CheckOutput {
        val msg = readMessage()
        return CheckOutput(msg != null, msg)
    }

    private fun produceArguments(pylonsAction: String): MessageData {
        val args: MessageData
        when (pylonsAction) {
            Actions.WALLET_UI_TEST -> {
                println("Press any key")
                readLine()
                args = MessageData()
                args.booleans["hasArguments"] = true
            }
            Actions.NEW_PROFILE -> {
                args = MessageData()
                args.strings[Keys.NAME] = readLine()!!
            }
            else -> throw Exception()
        }
        return args
    }

    class HandshakeCheckOutput(
            val data : String,
            val success : Boolean
    )

    private fun checkForHandshake(): HandshakeCheckOutput {
        println(socket)
        client = socket!!.accept()
        val pid = ProcessHandle.current().pid()
        val byteBuffer = ByteBuffer.allocate(Long.SIZE_BYTES).putLong(pid)
        writeBytes(ascii.encode(HANDSHAKE_MAGIC).array() + byteBuffer.array())
        return confirmHandshakeReply()
    }

    private fun confirmHandshakeReply(): HandshakeCheckOutput {
        var m: ByteArray? = null
        while (m == null) m = readNext()
        val s = ascii.decode(ByteBuffer.wrap(m)).toString()
        return HandshakeCheckOutput(s, s == HANDSHAKE_REPLY_MAGIC)
    }

    private fun readMessage(): MessageData? {
        val bytes = readNext()?: return null
        val ipc = ipcProtos.ipcMessage.parseFrom(bytes)
        Logger.implementation.log(LogEvent.PARSED_MESSAGE,
                """{"bytes":"${Hex.encodeHexString(bytes)}", "msg":${klaxon.toJsonString(ipc)}}""", LogTag.info)
        return parseMessageFromProto(ipc)
    }

    private fun parseMessageFromProto(ipc: ipcProtos.ipcMessage): MessageData {
        val msg = MessageData.empty()
        msg.booleans.putAll(ipc.eBooleansMap)
        ipc.eBooleanArraysMap.keys.forEach {
            val vList = ipc.eBooleanArraysMap[it]!!.vList
            val array = BooleanArray(vList.size)
            for (v in array.indices) array[v] = vList[v]
            msg.booleanArrays[it] = array
        }
        ipc.eBytesMap.keys.forEach { msg.bytes.put(it, ipc.eBytesMap[it]!!.toByte()) }
        ipc.eByteArraysMap.keys.forEach {
            val vList = ipc.eByteArraysMap[it]!!.vList
            val array = ByteArray(vList.size)
            for (v in array.indices) array[v] = vList[v].toByte()
            msg.byteArrays[it] = array
        }
        ipc.eCharsMap.keys.forEach { msg.chars.put(it, ipc.eCharsMap[it]!!.toChar()) }
        ipc.eCharArraysMap.keys.forEach {
            val vList = ipc.eCharArraysMap[it]!!.vList
            val array = CharArray(vList.size)
            for (v in array.indices) array[v] = vList[v].toInt().toChar()
            msg.charArrays[it] = array
        }
        msg.doubles.putAll(ipc.eDoublesMap)
        ipc.eDoubleArraysMap.keys.forEach {
            val vList = ipc.eDoubleArraysMap[it]!!.vList
            val array = DoubleArray(vList.size)
            for (v in array.indices) array[v] = vList[v]
            msg.doubleArrays[it] = array
        }
        msg.floats.putAll(ipc.eFloatsMap)
        ipc.eFloatArraysMap.keys.forEach {
            val vList = ipc.eFloatArraysMap[it]!!.vList
            val array = FloatArray(vList.size)
            for (v in array.indices) array[v] = vList[v]
            msg.floatArrays[it] = array
        }
        msg.ints.putAll(ipc.eIntsMap)
        ipc.eIntArraysMap.keys.forEach {
            val vList = ipc.eIntArraysMap[it]!!.vList
            val array = IntArray(vList.size)
            for (v in array.indices) array[v] = vList[v]
            msg.intArrays[it] = array
        }
        msg.longs.putAll(ipc.eLongsMap)
        ipc.eLongArraysMap.keys.forEach  {
            val vList = ipc.eLongArraysMap[it]!!.vList
            val array = LongArray(vList.size)
            for (v in array.indices) array[v] = vList[v]
            msg.longArrays[it] = array
        }
        ipc.eShortsMap.keys.forEach {
            msg.shorts[it] = ipc.eBytesMap[it]!!.toShort()
        }
        ipc.eShortArraysMap.keys.forEach {
            val vList = ipc.eShortArraysMap[it]!!.vList
            val array = ShortArray(vList.size)
            for (v in array.indices) array[v] = vList[v].toShort()
            msg.shortArrays[it] = array
        }
        msg.strings.putAll(ipc.eStringsMap)
        ipc.eStringArraysMap.keys.forEach {
            msg.stringArrays[it] = ipc.eStringArraysMap[it]!!.vList
        }
        return msg
    }

    private fun readNext(): ByteArray? {
        Logger.implementation.log(LogEvent.AWAIT_MESSAGE, "", LogTag.info)
        val s = client!!.getInputStream()
        while (s.read() == -1) Thread.sleep(100)
        val buffer = ByteArray(1024 * 512) // 512kb is overkill
        val len = s.read(buffer, 0, buffer.size)
        Logger.implementation.log(LogEvent.RESOLVED_MESSAGE,
                """{"msg":"${ascii.decode(ByteBuffer.wrap(buffer)).subSequence(0, len)}"}""", LogTag.info)
        if (len == 0) return null
        val m = ByteArray(len)
        for (i in 0 until len) m[i] = buffer[i]
        return m
    }

    private fun buildMessage(msg: MessageData): ipcProtos.ipcMessage {
        val ipc = ipcProtos.ipcMessage.newBuilder()
        ipc.putAllEBooleans(msg.booleans)
        msg.booleanArrays.keys.forEach {
            val pArray = ipcProtos.ipcMessage.booleanArray.newBuilder()
            for (v in msg.booleanArrays[it]!!) pArray.addV(v)
            ipc.putEBooleanArrays(it, pArray.build())
        }
        msg.bytes.keys.forEach { ipc.putEBytes(it, msg.bytes[it]!!.toInt()) }
        msg.byteArrays.keys.forEach {
            val pArray = ipcProtos.ipcMessage.byteArray.newBuilder()
            for (v in msg.byteArrays[it]!!) pArray.addV(v.toInt())
            ipc.putEByteArrays(it, pArray.build())
        }
        msg.chars.keys.forEach { ipc.putEChars(it, msg.chars[it]!!.toInt()) }
        msg.charArrays.keys.forEach {
            val pArray = ipcProtos.ipcMessage.charArray.newBuilder()
            for (v in msg.charArrays[it]!!) pArray.addV(v.toInt())
            ipc.putECharArrays(it, pArray.build())
        }
        ipc.putAllEDoubles(msg.doubles)
        msg.doubleArrays.keys.forEach{
            val pArray = ipcProtos.ipcMessage.doubleArray.newBuilder()
            for (v in msg.doubleArrays[it]!!) pArray.addV(v)
            ipc.putEDoubleArrays(it, pArray.build())
        }
        ipc.putAllEFloats(msg.floats)
        msg.floatArrays.keys.forEach {
            val pArray = ipcProtos.ipcMessage.floatArray.newBuilder()
            for (v in msg.floatArrays[it]!!) pArray.addV(v)
            ipc.putEFloatArrays(it, pArray.build())
        }
        ipc.putAllEInts(msg.ints)
        msg.intArrays.keys.forEach {
            val pArray = ipcProtos.ipcMessage.intArray.newBuilder()
            for (v in msg.intArrays[it]!!) pArray.addV(v)
            ipc.putEIntArrays(it, pArray.build())
        }
        ipc.putAllELongs(msg.longs)
        msg.longArrays.keys.forEach {
            val pArray = ipcProtos.ipcMessage.longArray.newBuilder()
            for (v in msg.longArrays[it]!!) pArray.addV(v)
            ipc.putELongArrays(it, pArray.build())
        }
        msg.shorts.keys.forEach { ipc.putEShorts(it, msg.shorts[it]!!.toInt()) }
        msg.shortArrays.keys.forEach {
            val pArray = ipcProtos.ipcMessage.shortArray.newBuilder()
            for (v in msg.shortArrays[it]!!) pArray.addV(v.toInt())
            ipc.putEShortArrays(it, pArray.build())
        }
        ipc.putAllEStrings(msg.strings)
        msg.stringArrays.keys.forEach {
            val pArray = ipcProtos.ipcMessage.stringArray.newBuilder()
            for (v in msg.stringArrays[it]!!) pArray.addV(v)
            ipc.putEStringArrays(it, pArray.build())
        }
        return ipc.build()
    }

    private fun sendMessage(msg: MessageData?) {
        writeBytes(buildMessage(msg!!).toByteArray())
    }

    private fun writeBytes(bytes: ByteArray) {
        println("Start write... (" + bytes.size + ") bytes")
        val s = client!!.getOutputStream()
        s.write(0xFF) // meaningless, just prepending message w/ an irrelevant byte so we can use ReadByte() to check for stream end w/o losing data
        s.write(bytes, 0, bytes.size)
        s.flush()
        println("Write complete")
    }

    private fun startCoreUpdateTicks() {
        timeline = Timeline(
                KeyFrame(
                        Duration.seconds(HEARTBEAT_INTERVAL),
                        coreUpdateTick
                )
        )
        timeline!!.cycleCount = Animation.INDEFINITE
        timeline!!.play()
    }

    private val coreUpdateTick = EventHandler<ActionEvent> {
        fire(HeartbeatRequest)
    }

}