package com.pylons.wallet.core.internal

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.Actions
import com.pylons.wallet.core.constants.Keys
import com.pylons.wallet.core.constants.ReservedKeys
import com.pylons.wallet.core.engine.TxPylonsDevEngine
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.wallet.core.internal.actionResolutionTable
import com.pylons.wallet.core.types.*
import org.junit.jupiter.api.MethodOrderer
import kotlin.random.Random

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
internal class ActionResolutionTableTest {
    private fun basicMsgTestFlow (msgs : List<MessageData>, followUp : List<((TxPylonsDevEngine, Response) -> Unit)?>) {
        Core.start(Config(Backend.LIVE_DEV, listOf("http://127.0.0.1:1317")), "")
        val engine = Core.engine as TxPylonsDevEngine
        engine.cryptoHandler = engine.getNewCryptoHandler() as CryptoCosmos
        for (i in msgs.indices) {
            val msg = msgs[i]
            val func = followUp[i]
            val action = msg.getAction()!!
            println("submitting msg w/ action $action")
            val r = actionResolutionTable(action, msg)
            println("${Core.userProfile?.credentials?.address}")
            func?.invoke(engine, r)
        }
    }

    @Order(0)
    @Test
    fun caseBatchCreateCookbook () {
        val newPrfMsg = MessageData(strings = mutableMapOf(ReservedKeys.wcAction to Actions.NEW_PROFILE, Keys.NAME to "foo"))
        val getPrfMsg = MessageData(strings = mutableMapOf(ReservedKeys.wcAction to Actions.GET_PROFILE))
        val getPylonsMsg = MessageData(strings = mutableMapOf(ReservedKeys.wcAction to Actions.GET_PYLONS),
                longs = mutableMapOf(Keys.PYLONS to 90000L))
        val bCreateCookbookMsg = MessageData(strings = mutableMapOf(ReservedKeys.wcAction to Actions.BATCH_CREATE_COOKBOOK),
                stringArrays = mutableMapOf(
                        Keys.ID to mutableListOf("${Random.nextInt()}"),
                        Keys.NAME to mutableListOf("tst_cookbook_name"),
                        Keys.DESCRIPTION to mutableListOf("addghjkllsdfdggdgjkkk"),
                        Keys.DEVELOPER to mutableListOf("asdfasdfasdf"),
                        Keys.VERSION to mutableListOf("1.0.0"),
                        Keys.SUPPORT_EMAIL to mutableListOf("a@example.com")),
                longArrays = mutableMapOf(Keys.LEVEL to longArrayOf(0), Keys.COST_PER_BLOCK to longArrayOf(5)))
        basicMsgTestFlow(listOf(newPrfMsg, getPrfMsg, getPylonsMsg, getPrfMsg, bCreateCookbookMsg), listOf(null, null, null, null, null))
    }
}