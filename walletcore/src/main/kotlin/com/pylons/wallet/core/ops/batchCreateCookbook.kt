package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.lib.types.types.*
import com.pylons.lib.types.types.Transaction.Companion.submitAll

fun Core.batchCreateCookbook (ids : List<String>, names : List<String>, developers : List<String>, descriptions : List<String>, versions : List<String>,
                              supportEmails : List<String>, levels : List<Long>, costsPerBlock : List<Long>) : List<Transaction> {
    val txs = engine.createCookbooks(
            ids = ids,
            names = names,
            developers = developers,
            descriptions = descriptions,
            versions = versions,
            supportEmails = supportEmails,
            levels = levels,
            costsPerBlock = costsPerBlock
    ).toMutableList()
    return txs.submitAll()
}