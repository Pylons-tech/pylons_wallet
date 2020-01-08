package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.BadMessageException
import com.pylons.wallet.core.types.*

internal fun batchUpdateCookbook(msg: MessageData) : Response {
    checkValid(msg)
    val txs = Core.batchUpdateCookbook(
            ids = msg.stringArrays[Keys.ID]!!,
            names = msg.stringArrays[Keys.NAME]!!,
            developers = msg.stringArrays[Keys.DEVELOPER]!!,
            descriptions = msg.stringArrays[Keys.DESCRIPTION]!!,
            versions = msg.stringArrays[Keys.VERSION]!!,
            supportEmails = msg.stringArrays[Keys.SUPPORT_EMAIL]!!
    )
    val txList = mutableListOf<String>()
    txs.forEach {
        waitUntilCommitted(it.id!!)
        txList.add(it.id!!)
    }
    val outgoingMessage = MessageData(
            stringArrays = mutableMapOf(
                    Keys.TX to txList
            )
    )
    return Response(outgoingMessage, Status.OK_TO_RETURN_TO_CLIENT)
}

private fun checkValid (msg : MessageData) {
    require (msg.stringArrays.containsKey(Keys.ID)) { throw BadMessageException("batchUpdateCookbook", Keys.ID, "StringArray") }
    require (msg.stringArrays.containsKey(Keys.NAME)) { throw BadMessageException("batchUpdateCookbook", Keys.NAME, "StringArray") }
    require (msg.stringArrays.containsKey(Keys.DEVELOPER)) { throw BadMessageException("batchUpdateCookbook", Keys.DEVELOPER, "StringArray") }
    require (msg.stringArrays.containsKey(Keys.DESCRIPTION)) { throw BadMessageException("batchUpdateCookbook", Keys.DESCRIPTION, "StringArray") }
    require (msg.stringArrays.containsKey(Keys.VERSION)) { throw BadMessageException("batchUpdateCookbook", Keys.VERSION, "StringArray") }
    require (msg.stringArrays.containsKey(Keys.SUPPORT_EMAIL)) { throw BadMessageException("batchUpdateCookbook", Keys.SUPPORT_EMAIL, "StringArray") }
}

fun Core.batchUpdateCookbook (names : List<String>, developers : List<String>, descriptions : List<String>, versions : List<String>,
                              supportEmails : List<String>, ids : List<String>) : List<Transaction> {
    var txs = engine.updateCookbooks(
            ids = ids,
            names = names,
            developers = developers,
            descriptions = descriptions,
            versions = versions,
            supportEmails = supportEmails
    ).toMutableList()
    txs.indices.forEach { txs[it] = txs[it].submit() }
    return txs
}