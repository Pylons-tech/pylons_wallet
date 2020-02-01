package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.internal.BadMessageException
import com.pylons.wallet.core.types.*

internal fun batchCreateCookbook(msg: MessageData) : Response {
    checkValid(msg)
    val txs = Core.batchCreateCookbook(
            ids = msg.stringArrays[Keys.ID]!!,
            names = msg.stringArrays[Keys.NAME]!!,
            developers = msg.stringArrays[Keys.DEVELOPER]!!,
            descriptions = msg.stringArrays[Keys.DESCRIPTION]!!,
            versions = msg.stringArrays[Keys.VERSION]!!,
            supportEmails = msg.stringArrays[Keys.SUPPORT_EMAIL]!!,
            levels = msg.longArrays[Keys.LEVEL]!!.toList(),
            costsPerBlock = msg.longArrays[Keys.COST_PER_BLOCK]!!.toList()
    )
    val txList = mutableListOf<String>()
    val idList = mutableListOf<String>()
    txs.forEach {
        waitUntilCommitted(it.id!!)
        txList.add(it.id!!)
    }
    val outgoingMessage = MessageData(
            stringArrays = mutableMapOf(
                    Keys.TX to txList,
                    Keys.COOKBOOK to idList
            )
    )
    return Response(outgoingMessage, Status.OK_TO_RETURN_TO_CLIENT)
}

private fun checkValid (msg : MessageData) {
    require (msg.stringArrays.containsKey(Keys.ID)) { throw BadMessageException("batchCreateCookbook", Keys.ID, "StringArray") }
    require (msg.stringArrays.containsKey(Keys.NAME)) { throw BadMessageException("batchCreateCookbook", Keys.NAME, "StringArray") }
    require (msg.stringArrays.containsKey(Keys.DEVELOPER)) { throw BadMessageException("batchCreateCookbook", Keys.DEVELOPER, "StringArray") }
    require (msg.stringArrays.containsKey(Keys.DESCRIPTION)) { throw BadMessageException("batchCreateCookbook", Keys.DESCRIPTION, "StringArray") }
    require (msg.stringArrays.containsKey(Keys.VERSION)) { throw BadMessageException("batchCreateCookbook", Keys.VERSION, "StringArray") }
    require (msg.stringArrays.containsKey(Keys.SUPPORT_EMAIL)) { throw BadMessageException("batchCreateCookbook", Keys.SUPPORT_EMAIL, "StringArray") }
    require (msg.longArrays.containsKey(Keys.LEVEL)) { throw BadMessageException("batchCreateCookbook", Keys.LEVEL, "LongArray") }
    require (msg.longArrays.containsKey(Keys.COST_PER_BLOCK)) { throw BadMessageException("batchCreateCookbook", Keys.COST_PER_BLOCK, "LongArray") }
}

fun Core.batchCreateCookbook (ids : List<String>, names : List<String>, developers : List<String>, descriptions : List<String>, versions : List<String>,
                              supportEmails : List<String>, levels : List<Long>, costsPerBlock : List<Long>) : List<Transaction> {
    var txs = engine.createCookbooks(
            ids = ids,
            names = names,
            developers = developers,
            descriptions = descriptions,
            versions = versions,
            supportEmails = supportEmails,
            levels = levels,
            costsPerBlock = costsPerBlock
    ).toMutableList()
    txs.indices.forEach { txs[it] = txs[it].submit() }
    return txs
}