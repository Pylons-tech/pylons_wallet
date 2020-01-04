package com.pylons.wallet.core.types

import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.constants.ReservedKeys
import kotlin.jvm.JvmStatic

/**
 * Container for data being passed into or out of WalletCore.
 */
data class MessageData(
    val booleans: MutableMap<String, Boolean> = mutableMapOf(),
    val booleanArrays: MutableMap<String, BooleanArray> = mutableMapOf(),
    val bytes: MutableMap<String, Byte> = mutableMapOf(),
    val byteArrays: MutableMap<String, ByteArray> = mutableMapOf(),
    val chars: MutableMap<String, Char> = mutableMapOf(),
    val charArrays: MutableMap<String, CharArray> = mutableMapOf(),
    val doubles: MutableMap<String, Double> = mutableMapOf(),
    val doubleArrays: MutableMap<String, DoubleArray> = mutableMapOf(),
    val floats: MutableMap<String, Float> = mutableMapOf(),
    val floatArrays: MutableMap<String, FloatArray> = mutableMapOf(),
    val ints: MutableMap<String, Int> = mutableMapOf(),
    val intArrays: MutableMap<String, IntArray> = mutableMapOf(),
    val longs: MutableMap<String, Long> = mutableMapOf(),
    val longArrays: MutableMap<String, LongArray> = mutableMapOf(),
    val shorts: MutableMap<String, Short> = mutableMapOf(),
    val shortArrays: MutableMap<String, ShortArray> = mutableMapOf(),
    val strings: MutableMap<String, String> = mutableMapOf(),
    val stringArrays: MutableMap<String, MutableList<String>> = mutableMapOf()
) { companion object {
        //@JvmStatic
        //fun deleteProfile() = MessageData(strings = mutableMapOf(ReservedKeys.wcAction to Actions.WIPE_USER_DATA))

        @JvmStatic
        fun empty() = MessageData()

        //@JvmStatic
        //fun getUserDetails () = MessageData(strings = mutableMapOf(ReservedKeys.wcAction to Actions.GET_USER_DETAILS))
    }

    fun isError () : Boolean = strings.containsKey(Keys.ERROR)

    fun errorToString () : String = "ERROR | ${ints[Keys.ERROR_CODE]} | ${strings[Keys.ERROR]} | ${strings[Keys.INFO]}"

    fun getAction () : String? = strings[ReservedKeys.wcAction]

    fun merge (other : MessageData) : MessageData {
        booleans.putAll(other.booleans)
        booleanArrays.putAll(other.booleanArrays)
        bytes.putAll(other.bytes)
        byteArrays.putAll(other.byteArrays)
        chars.putAll(other.chars)
        charArrays.putAll(other.charArrays)
        doubles.putAll(other.doubles)
        doubleArrays.putAll(other.doubleArrays)
        floats.putAll(other.floats)
        floatArrays.putAll(other.floatArrays)
        ints.putAll(other.ints)
        intArrays.putAll(other.intArrays)
        longs.putAll(other.longs)
        longArrays.putAll(other.longArrays)
        shorts.putAll(other.shorts)
        shortArrays.putAll(other.shortArrays)
        strings.putAll(other.strings)
        stringArrays.putAll(other.stringArrays)
        return this
    }

    override fun toString(): String = klaxon.toJsonString(this)
}