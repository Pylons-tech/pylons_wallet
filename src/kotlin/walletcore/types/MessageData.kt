package walletcore.types

import com.squareup.moshi.Moshi
import walletcore.constants.Actions
import walletcore.constants.Keys
import walletcore.constants.ReservedKeys
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
) {
    companion object {
        @JvmStatic
        fun deleteProfile() = MessageData(strings = mutableMapOf(ReservedKeys.wcAction to Actions.wipeUserData))

        @JvmStatic
        fun empty() = MessageData()

        @JvmStatic
        fun getUserDetails () = MessageData(strings = mutableMapOf(ReservedKeys.wcAction to Actions.getUserDetails))
    }

    fun isError () : Boolean = strings.containsKey(Keys.error)

    fun errorToString () : String = "ERROR | ${ints[Keys.errorCode]} | ${strings[Keys.error]} | ${strings[Keys.info]}"

    fun getAction () : String? = strings[ReservedKeys.wcAction]

    override fun toString(): String {
        val moshi = Moshi.Builder().build()
        return moshi.adapter<MessageData>(MessageData::class.java).toJson(this)
    }
}