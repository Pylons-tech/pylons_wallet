package com.pylons.wallet.app

import org.junit.Assert
import org.junit.Test
import android.content.Intent
import android.support.test.runner.AndroidJUnit4
import android.support.test.InstrumentationRegistry
import org.junit.runner.RunWith
import walletCore.MessageData
import kotlin.reflect.KClass

@RunWith(AndroidJUnit4::class)
class MessageData {

    @Test
    fun intentToMessage_ElementTypeSafety_Bool() {
        val i0 = Intent()
        i0.putExtra("elem", true)
        val msg = messageDataFromIntent(i0)
        val i1 = Intent()
        CoreUtil.copyMessageDataToIntent(msg, i1)
        Assert.assertTrue(msg.booleans.containsKey("elem"))
        Assert.assertTrue(i1.extras["elem"] is Boolean)
    }

    @Test
    fun intentToMessage_ElementTypeSafety_BoolArray() {
        val i0 = Intent()
        i0.putExtra("elem", booleanArrayOf(true, false, false))
        val msg = messageDataFromIntent(i0)
        val i1 = Intent()
        CoreUtil.copyMessageDataToIntent(msg, i1)
        Assert.assertTrue(msg.booleanArrays.containsKey("elem"))
        Assert.assertTrue(i1.extras["elem"] is BooleanArray)
    }

    @Test
    fun intentToMessage_ElementTypeSafety_Byte() {
        val i0 = Intent()
        i0.putExtra("elem", 0xFF.toByte())
        val msg = messageDataFromIntent(i0)
        val i1 = Intent()
        CoreUtil.copyMessageDataToIntent(msg, i1)
        Assert.assertTrue(msg.bytes.containsKey("elem"))
        Assert.assertTrue(i1.extras["elem"] is Byte)
    }

    @Test
    fun intentToMessage_ElementTypeSafety_ByteArray() {
        val i0 = Intent()
        i0.putExtra("elem", byteArrayOf(0xff.toByte(), 0x00.toByte(), 0x01.toByte()))
        val msg = messageDataFromIntent(i0)
        val i1 = Intent()
        CoreUtil.copyMessageDataToIntent(msg, i1)
        Assert.assertTrue(msg.byteArrays.containsKey("elem"))
        Assert.assertTrue(i1.extras["elem"] is ByteArray)
    }

    @Test
    fun intentToMessage_ElementTypeSafety_Char() {
        val i0 = Intent()
        i0.putExtra("elem", '?')
        val msg = messageDataFromIntent(i0)
        val i1 = Intent()
        CoreUtil.copyMessageDataToIntent(msg, i1)
        Assert.assertTrue(msg.chars.containsKey("elem"))
        Assert.assertTrue(i1.extras["elem"] is Char)
    }

    @Test
    fun intentToMessage_ElementTypeSafety_CharArray() {
        val i0 = Intent()
        i0.putExtra("elem", charArrayOf('f', 'o', 'o'))
        val msg = messageDataFromIntent(i0)
        val i1 = Intent()
        CoreUtil.copyMessageDataToIntent(msg, i1)
        Assert.assertTrue(msg.charArrays.containsKey("elem"))
        Assert.assertTrue(i1.extras["elem"] is CharArray)
    }

    @Test
    fun intentToMessage_ElementTypeSafety_Double() {
        val i0 = Intent()
        i0.putExtra("elem", 3.33)
        val msg = messageDataFromIntent(i0)
        val i1 = Intent()
        CoreUtil.copyMessageDataToIntent(msg, i1)
        Assert.assertTrue(msg.doubles.containsKey("elem"))
        Assert.assertTrue(i1.extras["elem"] is Double)
    }

    @Test
    fun intentToMessage_ElementTypeSafety_DoubleArray() {
        val i0 = Intent()
        i0.putExtra("elem", doubleArrayOf(3.33, 6.66, 9.99))
        val msg = messageDataFromIntent(i0)
        val i1 = Intent()
        CoreUtil.copyMessageDataToIntent(msg, i1)
        Assert.assertTrue(msg.doubleArrays.containsKey("elem"))
        Assert.assertTrue(i1.extras["elem"] is DoubleArray)
    }

    @Test
    fun intentToMessage_ElementTypeSafety_Float() {
        val i0 = Intent()
        i0.putExtra("elem", 3.33f)
        val msg = messageDataFromIntent(i0)
        val i1 = Intent()
        CoreUtil.copyMessageDataToIntent(msg, i1)
        Assert.assertTrue(msg.floats.containsKey("elem"))
        Assert.assertTrue(i1.extras["elem"] is Float)
    }

    @Test
    fun intentToMessage_ElementTypeSafety_FloatArray() {
        val i0 = Intent()
        i0.putExtra("elem", floatArrayOf(3.33f, 6.66f, 9.99f))
        val msg = messageDataFromIntent(i0)
        val i1 = Intent()
        CoreUtil.copyMessageDataToIntent(msg, i1)
        Assert.assertTrue(msg.floatArrays.containsKey("elem"))
        Assert.assertTrue(i1.extras["elem"] is FloatArray)
    }

    @Test
    fun intentToMessage_ElementTypeSafety_Int() {
        val i0 = Intent()
        i0.putExtra("elem", 3)
        val msg = messageDataFromIntent(i0)
        val i1 = Intent()
        CoreUtil.copyMessageDataToIntent(msg, i1)
        Assert.assertTrue(msg.ints.containsKey("elem"))
        Assert.assertTrue(i1.extras["elem"] is Int)
    }

    @Test
    fun intentToMessage_ElementTypeSafety_IntArray() {
        val i0 = Intent()
        i0.putExtra("elem", intArrayOf(3, 7, 10))
        val msg = messageDataFromIntent(i0)
        val i1 = Intent()
        CoreUtil.copyMessageDataToIntent(msg, i1)
        Assert.assertTrue(msg.intArrays.containsKey("elem"))
        Assert.assertTrue(i1.extras["elem"] is IntArray)
    }

    @Test
    fun intentToMessage_ElementTypeSafety_Long() {
        val i0 = Intent()
        i0.putExtra("elem", 3L)
        val msg = messageDataFromIntent(i0)
        val i1 = Intent()
        CoreUtil.copyMessageDataToIntent(msg, i1)
        Assert.assertTrue(msg.longs.containsKey("elem"))
        Assert.assertTrue(i1.extras["elem"] is Long)
    }

    @Test
    fun intentToMessage_ElementTypeSafety_LongArray() {
        val i0 = Intent()
        i0.putExtra("elem", longArrayOf(3L, 7L, 10L))
        val msg = messageDataFromIntent(i0)
        val i1 = Intent()
        CoreUtil.copyMessageDataToIntent(msg, i1)
        Assert.assertTrue(msg.longArrays.containsKey("elem"))
        Assert.assertTrue(i1.extras["elem"] is LongArray)
    }

    @Test
    fun intentToMessage_ElementTypeSafety_Short() {
        val i0 = Intent()
        i0.putExtra("elem", 3.toShort())
        val msg = messageDataFromIntent(i0)
        val i1 = Intent()
        CoreUtil.copyMessageDataToIntent(msg, i1)
        Assert.assertTrue(msg.shorts.containsKey("elem"))
        Assert.assertTrue(i1.extras["elem"] is Short)
    }

    @Test
    fun intentToMessage_ElementTypeSafety_ShortArray() {
        val i0 = Intent()
        i0.putExtra("elem", shortArrayOf(3.toShort(), 7.toShort(), 10.toShort()))
        val msg = messageDataFromIntent(i0)
        val i1 = Intent()
        CoreUtil.copyMessageDataToIntent(msg, i1)
        Assert.assertTrue(msg.shortArrays.containsKey("elem"))
        Assert.assertTrue(i1.extras["elem"] is ShortArray)
    }

    @Test
    fun intentToMessage_ElementTypeSafety_String() {
        val i0 = Intent()
        i0.putExtra("elem", "bar")
        val msg = messageDataFromIntent(i0)
        val i1 = Intent()
        CoreUtil.copyMessageDataToIntent(msg, i1)
        Assert.assertTrue(msg.strings.containsKey("elem"))
        Assert.assertTrue(i1.extras["elem"] is String)
    }

    @Test
    fun intentToMessage_ElementTypeSafety_StringArray() {
        val i0 = Intent()
        i0.putExtra("elem", arrayOf("uncountably", "many", "strings"))
        val msg = messageDataFromIntent(i0)
        val i1 = Intent()
        CoreUtil.copyMessageDataToIntent(msg, i1)
        Assert.assertTrue(msg.stringArrays.containsKey("elem"))
        Assert.assertTrue(i1.getStringArrayExtra("elem") != null)
    }
}


