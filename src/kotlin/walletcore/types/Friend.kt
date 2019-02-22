package walletcore.types

import java.util.*

data class Friend (
        val id : String,
        val dateAdded : Long
) {
    companion object {
        fun deserializeFriendsList (str : String) : List<Friend> {
            val mList = mutableListOf<Friend>()
            val split = str.split(",")
            for (i in 0 until split.count()) if (i % 2 == 0) mList.add(Friend(split[i], (split[i + 1]).toLong()))
            return mList
        }
    }
}

fun List<Friend>.serialize () : String {
    val sb = StringBuilder()
    forEach{
        sb.append("${it.id},${it.dateAdded},")
    }
    return when (sb.isNotEmpty()) {
        true -> sb.delete(sb.lastIndex, sb.length).toString()
        false -> ""
    }
}