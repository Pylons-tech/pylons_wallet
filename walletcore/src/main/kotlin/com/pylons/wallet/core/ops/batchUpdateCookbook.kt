package com.pylons.wallet.core.ops

import com.pylons.wallet.core.Core
import com.pylons.lib.types.types.*
import com.pylons.lib.types.types.Transaction.Companion.submitAll

fun Core.batchUpdateCookbook (names : List<String>, developers : List<String>, descriptions : List<String>, versions : List<String>,
                              supportEmails : List<String>, ids : List<String>) : List<Transaction> {
    val txs = engine.updateCookbooks(
            ids = ids,
            names = names,
            developers = developers,
            descriptions = descriptions,
            versions = versions,
            supportEmails = supportEmails
    ).toMutableList()
    return txs.submitAll()
}