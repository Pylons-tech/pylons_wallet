package com.pylons.wallet.core.internal


import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.constants.*
import com.pylons.wallet.core.ops.*
import com.pylons.wallet.core.types.*

internal class ActionResolutionTableTest {
    @AfterEach
    internal fun tearDown() {
        Core.tearDown()
    }

    private fun bootstrapCoreForBasicCases () {
        //Core.start(backend = Backend.DUMMY, json = "")
        //Core.userProfile = Profile(TxDummyEngine.Credentials("12345"), mutableMapOf(ReservedKeys.profileName to "fooBar"), mutableMapOf(), mutableListOf())
    }

    @Test
    fun case_noAction () {
        //Core.start(backend = Backend.DUMMY, json = "")
        val noActionError = noAction()
        val actualResponse = actionResolutionTable("", MessageData.empty())
        assertEquals(noActionError.status, actualResponse.status)
        assertEquals(noActionError.msg!!.strings[Keys.ERROR], actualResponse.msg!!.strings[Keys.ERROR])
    }

    @Test
    fun case_invalidAction () {
        //Core.start(backend = Backend.DUMMY, json = "")
        val badActionError = unrecognizedAction("not_a_real_action")
        val actualResponse = actionResolutionTable("not_a_real_action", MessageData.empty())
        assertEquals(badActionError.status, actualResponse.status)
        assertEquals(badActionError.msg!!.strings[Keys.ERROR], actualResponse.msg!!.strings[Keys.ERROR])
    }

    @Test
    fun case_walletServiceTest () {
        //Core.start(backend = Backend.DUMMY, json = "")
        val svcTestResponse = walletServiceTest()
        val actualResponse = actionResolutionTable(Actions.WALLET_SERVICE_TEST, MessageData.empty())
        assertEquals(svcTestResponse.status, actualResponse.status)
        assertEquals(svcTestResponse.msg!!.strings[Keys.INFO], actualResponse.msg!!.strings[Keys.INFO])
    }

    @Test
    fun case_walletUiTest() {
        //Core.start(backend = Backend.DUMMY, json = "")
        val uiTestResponse = walletUiTest(MessageData.empty()) // ui test only checks that it _has_ args
        val noArgsResponse = requiresArgs(Actions.WALLET_UI_TEST, MessageData.empty(), null) { m -> walletUiTest(m) }
        val actualNoArgsResponse = actionResolutionTable(Actions.WALLET_UI_TEST, MessageData.empty())
        val actualResponse = actionResolutionTable(Actions.WALLET_UI_TEST, MessageData.empty(), MessageData.empty())
        assertEquals(noArgsResponse.status, actualNoArgsResponse.status)
        assertEquals(noArgsResponse.msg, actualNoArgsResponse.msg) // both should be null
        assertEquals(uiTestResponse.status, actualResponse.status)
        assertEquals(uiTestResponse.msg!!.strings[Keys.INFO], actualResponse.msg!!.strings[Keys.INFO])
    }

    @Test
    fun case_getUserDetailsNoProfile() {
        //Core.start(backend = Backend.DUMMY, json = "")
        val noProfileResponse = Response(MessageData(booleans = mutableMapOf(Keys.PROFILE_EXISTS to false)), Status.OK_TO_RETURN_TO_CLIENT)
        val actualResponse = actionResolutionTable(Actions.GET_USER_DETAILS, MessageData.empty())
        assertEquals(noProfileResponse.status, actualResponse.status)
        assertEquals(noProfileResponse.msg!!.booleans[Keys.PROFILE_EXISTS], actualResponse.msg!!.booleans[Keys.PROFILE_EXISTS])
    }

    @Test
    fun case_getUserDetailsProfileExists() {
        bootstrapCoreForBasicCases()
        val profileResponse = Response(Core.userProfile!!.detailsToMessageData(), Status.OK_TO_RETURN_TO_CLIENT)
        val actualResponse = actionResolutionTable(Actions.GET_USER_DETAILS, MessageData.empty())
        assertEquals(profileResponse.status, actualResponse.status)
        assertEquals(profileResponse.msg!!.booleans[Keys.PROFILE_EXISTS], actualResponse.msg!!.booleans[Keys.PROFILE_EXISTS])
        assertEquals(profileResponse.msg!!.strings[Keys.ADDRESS], actualResponse.msg!!.strings[Keys.ADDRESS])
        assertEquals(profileResponse.msg!!.strings[Keys.NAME], actualResponse.msg!!.strings[Keys.NAME])
        assertEquals(profileResponse.msg!!.strings[Keys.COINS], actualResponse.msg!!.strings[Keys.COINS])
    }

    @Test
    fun case_wipeUserData () {
        bootstrapCoreForBasicCases()
        assertNotNull(Core.userProfile)
        actionResolutionTable(Actions.WIPE_USER_DATA, MessageData.empty())
        assertNull(Core.userProfile)
    }

    @Test
    fun case_registerProfile () {
        //Core.start(backend = Backend.DUMMY, json = "")
        val successResponse = Response(MessageData(booleans = mutableMapOf(Keys.PROFILE_EXISTS to true)), Status.OK_TO_RETURN_TO_CLIENT)
        val actualResponse = actionResolutionTable(Actions.NEW_PROFILE, MessageData(strings = mutableMapOf(Keys.NAME to "fooBar")))
        assertEquals(successResponse.status, actualResponse.status)
        assertEquals(successResponse.msg!!.booleans[Keys.PROFILE_EXISTS], actualResponse.msg!!.booleans[Keys.PROFILE_EXISTS])
    }

    @Test
    fun case_submitTx () {
        bootstrapCoreForBasicCases()
        val successResponse = Response(MessageData(booleans = mutableMapOf(Keys.SUCCESS to true)), Status.OK_TO_RETURN_TO_CLIENT)
        val txMessage = MessageData()
        txMessage.strings[Keys.OTHER_ADDRESS] = "012345678910"
        txMessage.strings[Keys.COINS_OUT] = "pylons,2"
        val actualResponse = actionResolutionTable(Actions.PERFORM_TRANSACTION, txMessage)
        System.out.println(actualResponse.msg?.strings?.get("info"))
        assertEquals(successResponse.status, actualResponse.status)
        assertEquals(successResponse.msg!!.booleans[Keys.SUCCESS], actualResponse.msg!!.booleans[Keys.SUCCESS])
        assertEquals(2, Core.userProfile!!.countOfCoin(Keys.PYLONS))
    }

    @Test
    fun case_getOtherUserDetails () {
        bootstrapCoreForBasicCases()
        val successResponse = Response(MessageData(booleans = mutableMapOf(Keys.PROFILE_EXISTS to true)), Status.OK_TO_RETURN_TO_CLIENT)
        val actualResponse = actionResolutionTable(Actions.GET_OTHER_USER_DETAILS, MessageData(strings = mutableMapOf(Keys.OTHER_ADDRESS to "012345678910")))
        assertEquals(successResponse.status, actualResponse.status)
        assertEquals(successResponse.msg!!.booleans[Keys.PROFILE_EXISTS], actualResponse.msg!!.booleans[Keys.PROFILE_EXISTS])
        assertEquals("fooBar", actualResponse.msg!!.strings[Keys.NAME])
    }

    @Test
    fun case_applyRecipe () {
        //OutsideWorldDummy.loadRuleJson = this::getJsonForRecipe
        bootstrapCoreForBasicCases()
        Core.userProfile!!.coins["pylons"] = 99
        val successResponse = Response(MessageData(booleans = mutableMapOf(Keys.SUCCESS to true)), Status.OK_TO_RETURN_TO_CLIENT)
        val actualResponse = actionResolutionTable(Actions.APPLY_RECIPE,
                MessageData(strings = mutableMapOf(Keys.COOKBOOK to "foo", Keys.RECIPE to "bar")))
        assertEquals(successResponse.status, actualResponse.status)
        assertEquals(successResponse.msg!!.booleans[Keys.SUCCESS], actualResponse.msg!!.booleans[Keys.SUCCESS])
        assertEquals(100, Core.userProfile!!.coins["pylons"])
    }

    @Test
    fun case_setUserProfileState () {
        bootstrapCoreForBasicCases()
        val incomingMsg = MessageData(strings = mutableMapOf(ReservedKeys.wcAction to Actions.SET_USER_PROFILE_STATE, "json" to
        """{
  "coins": { "gold": 999998 },
  "address": "just whatever for now afa address goes",
  "items": [
    {
      "doubles": {},
      "address": "0_[ITEM]_DUMMY",
      "longs": {},
      "strings": {
        "type": "decorFurniture",
        "subtype": "Torch"
      }
    },
    {
      "doubles": {},
      "address": "1_[ITEM]_DUMMY",
      "longs": {},
      "strings": {
        "type": "decorFurniture",
        "subtype": "Torch"
      }
    },
    {
      "doubles": {},
      "address": "2_[ITEM]_DUMMY",
      "longs": {},
      "strings": {
        "type": "decorFurniture",
        "subtype": "Torch"
      }
    }
  ],
  "provisional": false,
  "strings": { "@P__profileName": "nooo" }
}"""))
        val successResponse = Response(MessageData(booleans = mutableMapOf(Keys.SUCCESS to true)), Status.OK_TO_RETURN_TO_CLIENT)
        val actualResponse = actionResolutionTable(Actions.SET_USER_PROFILE_STATE, incomingMsg)
        assertEquals("nooo", Core.userProfile!!.getName())
        assertEquals(successResponse.status, actualResponse.status)
    }

    @Test
    fun case_getTransaction () {
        assertFalse(true)
//        bootstrapCoreForBasicCases()
//        val tx = Transaction("tst", "0", "1", listOf(), listOf(), listOf(), listOf(), Transaction.State.TX_ACCEPTED,
//                listOf(), listOf())
//        OutsideWorldDummy.addTx(tx)
//        val successResponse = Response(tx.detailsToMessageData().merge(MessageData(booleans = mutableMapOf(Keys.success to true))), Status.OK_TO_RETURN_TO_CLIENT)
//        val actualResponse = actionResolutionTable(Actions.getTransaction,
//                MessageData(strings = mutableMapOf("txId" to "tst")))
//        assertEquals(successResponse.status, actualResponse.status)
//        assertEquals(successResponse.msg!!.strings["txId"], actualResponse.msg!!.strings["txId"])
    }

    @Test
    fun case_getPylons () {
        bootstrapCoreForBasicCases()
        val successResponse = Response(MessageData(booleans = mutableMapOf(Keys.SUCCESS to true)), Status.OK_TO_RETURN_TO_CLIENT)
        val actualResponse = actionResolutionTable(Actions.GET_PYLONS,
                MessageData(ints = mutableMapOf("pylons" to 43)))
        assertEquals(successResponse.status, actualResponse.status)
        assertEquals(43, Core.userProfile!!.coins["pylons"])
    }
}