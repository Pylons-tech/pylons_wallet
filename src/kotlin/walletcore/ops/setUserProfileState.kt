package walletcore.ops

import walletcore.Core
import walletcore.constants.Keys
import walletcore.internal.bufferForeignProfile
import walletcore.internal.generateErrorMessageData
import walletcore.types.*

fun setUserProfileState (msg : MessageData) : Response {
    val strings = msg.stringArrays[Keys.strings]!!
    val coins = msg.stringArrays[Keys.coins]!!
    val items = msg.stringArrays[Keys.items]!!
    val stringsMap = mutableMapOf<String, String>()
    for (i in 0 until strings.size - 1) {
        if (i % 2 == 0) stringsMap[strings[i + 1]] = strings[i]
    }
    val coinsMap = mutableMapOf<String, Int>()
    for (i in 0 until coins.size - 1) {
        if (i % 2 == 0) coinsMap[coins[i + 1]] = coins[i].toInt()
    }
    val itemsSet = mutableSetOf<Item>()
    items.forEach {
        itemsSet.add(Item.fromJson(it))
    }
    val prf = Profile(id = Core.userProfile!!.id, strings = stringsMap, coins = coinsMap, items = itemsSet, provisional = false)
    Core.userProfile = prf
    return Response(MessageData(booleans = mutableMapOf(Keys.success to true)), Status.OK_TO_RETURN_TO_CLIENT)
}