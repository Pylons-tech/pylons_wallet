package walletcore.internal


import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import walletcore.Core
import walletcore.constants.*
import walletcore.ops.*
import walletcore.tx.OutsideWorldDummy
import walletcore.types.*

internal class ActionResolutionTableTest {
    @AfterEach
    internal fun tearDown() {
        Core.tearDown()
    }

    @Test
    fun case_noAction () {
        val noActionError = noAction()
        val actualResponse = actionResolutionTable("", MessageData.empty())
        assertEquals(noActionError.status, actualResponse.status)
        assertEquals(noActionError.msg!!.strings[Keys.error], actualResponse.msg!!.strings[Keys.error])
    }

    @Test
    fun case_invalidAction () {
        val badActionError = unrecognizedAction("not_a_real_action")
        val actualResponse = actionResolutionTable("not_a_real_action", MessageData.empty())
        assertEquals(badActionError.status, actualResponse.status)
        assertEquals(badActionError.msg!!.strings[Keys.error], actualResponse.msg!!.strings[Keys.error])
    }

    @Test
    fun case_walletServiceTest () {
        val svcTestResponse = walletServiceTest()
        val actualResponse = actionResolutionTable(Actions.walletServiceTest, MessageData.empty())
        assertEquals(svcTestResponse.status, actualResponse.status)
        assertEquals(svcTestResponse.msg!!.strings[Keys.info], actualResponse.msg!!.strings[Keys.info])
    }

    @Test
    fun case_walletUiTest() {
        val uiTestResponse = walletUiTest(MessageData.empty()) // ui test only checks that it _has_ args
        val noArgsResponse = requiresArgs(Actions.walletUiTest, MessageData.empty(), null) { m -> walletUiTest(m) }
        val actualNoArgsResponse = actionResolutionTable(Actions.walletUiTest, MessageData.empty())
        val actualResponse = actionResolutionTable(Actions.walletUiTest, MessageData.empty(), MessageData.empty())
        assertEquals(noArgsResponse.status, actualNoArgsResponse.status)
        assertEquals(noArgsResponse.msg, actualNoArgsResponse.msg) // both should be null
        assertEquals(uiTestResponse.status, actualResponse.status)
        assertEquals(uiTestResponse.msg!!.strings[Keys.info], actualResponse.msg!!.strings[Keys.info])
    }

    @Test
    fun case_getUserDetailsNoProfile() {
        Core.uiInterrupts = InternalUiInterrupts()
        Core.start()
        val noProfileResponse = Response(MessageData(booleans = mutableMapOf(Keys.profileExists to false)), Status.OK_TO_RETURN_TO_CLIENT)
        val actualResponse = actionResolutionTable(Actions.getUserDetails, MessageData.empty())
        assertEquals(noProfileResponse.status, actualResponse.status)
        assertEquals(noProfileResponse.msg!!.booleans[Keys.profileExists], actualResponse.msg!!.booleans[Keys.profileExists])
    }

    @Test
    fun case_getUserDetailsProfileExists() {
        val json = UserData("fooBar", "12345").exportAsJson()
        Core.uiInterrupts = InternalUiInterrupts()
        Core.start(json)
        val profileResponse = Response(Core.userProfile!!.detailsToMessageData(), Status.OK_TO_RETURN_TO_CLIENT)
        val actualResponse = actionResolutionTable(Actions.getUserDetails, MessageData.empty())
        assertEquals(profileResponse.status, actualResponse.status)
        assertEquals(profileResponse.msg!!.booleans[Keys.profileExists], actualResponse.msg!!.booleans[Keys.profileExists])
        assertEquals(profileResponse.msg!!.strings[Keys.id], actualResponse.msg!!.strings[Keys.id])
        assertEquals(profileResponse.msg!!.strings[Keys.name], actualResponse.msg!!.strings[Keys.name])
        assertEquals(profileResponse.msg!!.strings[Keys.coins], actualResponse.msg!!.strings[Keys.coins])
    }

    @Test
    fun case_wipeUserData () {
        val json = UserData("fooBar", "12345").exportAsJson()
        Core.uiInterrupts = InternalUiInterrupts()
        Core.start(json)
        assertNotNull(Core.userProfile)
        actionResolutionTable(Actions.wipeUserData, MessageData.empty())
        assertNull(Core.userProfile)
    }

    @Test
    fun case_registerProfile () {
        Core.start()
        val successResponse = Response(MessageData(booleans = mutableMapOf(Keys.profileExists to true)), Status.OK_TO_RETURN_TO_CLIENT)
        val actualResponse = actionResolutionTable(Actions.newProfile, MessageData.empty(), MessageData(strings = mutableMapOf(Keys.name to "fooBar")))
        assertEquals(successResponse.status, actualResponse.status)
        assertEquals(successResponse.msg!!.booleans[Keys.profileExists], actualResponse.msg!!.booleans[Keys.profileExists])
    }

    @Test
    fun case_submitTx () {
        val json = UserData("fooBar", "12345").exportAsJson()
        Core.uiInterrupts = InternalUiInterrupts()
        Core.start(json)
        val successResponse = Response(MessageData(booleans = mutableMapOf(Keys.success to true)), Status.OK_TO_RETURN_TO_CLIENT)
        val txMessage = MessageData()
        txMessage.strings[Keys.otherProfileId] = "012345678910"
        txMessage.strings[Keys.coinsOut] = "pylons,2"
        val actualResponse = actionResolutionTable(Actions.performTransaction, txMessage)
        System.out.println(actualResponse.msg?.strings?.get("info"))
        assertEquals(successResponse.status, actualResponse.status)
        assertEquals(successResponse.msg!!.booleans[Keys.success], actualResponse.msg!!.booleans[Keys.success])
        assertEquals(2, Core.userProfile!!.countOfCoin(Keys.pylons))
    }

    @Test
    fun case_getOtherUserDetails () {
        Core.uiInterrupts = InternalUiInterrupts()
        Core.start()
        val successResponse = Response(MessageData(booleans = mutableMapOf(Keys.profileExists to true)), Status.OK_TO_RETURN_TO_CLIENT)
        val actualResponse = actionResolutionTable(Actions.getOtherUserDetails, MessageData(strings = mutableMapOf(Keys.otherProfileId to "012345678910")))
        assertEquals(successResponse.status, actualResponse.status)
        assertEquals(successResponse.msg!!.booleans[Keys.profileExists], actualResponse.msg!!.booleans[Keys.profileExists])
        assertEquals("fooBar", actualResponse.msg!!.strings[Keys.name])
    }

    @Test
    fun case_applyRecipe () {
        val json = UserData("fooBar", "12345").exportAsJson()
        Core.uiInterrupts = InternalUiInterrupts()
        Core.start(json)
        Core.userProfile = Core.userProfile!!.addCoins(setOf(Coin(Keys.pylons, 99)))
        val successResponse = Response(MessageData(booleans = mutableMapOf(Keys.success to true)), Status.OK_TO_RETURN_TO_CLIENT)
        val actualResponse = actionResolutionTable(Actions.applyRecipe,
                MessageData(strings = mutableMapOf(Keys.cookbook to "foo", Keys.recipe to "bar")))
        assertEquals(successResponse.status, actualResponse.status)
        assertEquals(successResponse.msg!!.booleans[Keys.success], actualResponse.msg!!.booleans[Keys.success])
        assertEquals("thingy", Core.userProfile!!.items.toList().first().strings["type"])
        System.out.println(Core.userProfile!!.items.serialize())
    }

    @Test
    fun case_setUserProfileState () {
        Core.uiInterrupts = InternalUiInterrupts()
        Core.start()
        val incomingMsg = MessageData(strings = mutableMapOf(ReservedKeys.wcAction to Actions.setUserProfileState, "json" to
        """{
  "coins": { "gold": 999998 },
  "id": "just whatever for now afa id goes",
  "items": [
    {
      "doubles": {},
      "id": "0_[ITEM]_DUMMY",
      "longs": {},
      "strings": {
        "type": "decorFurniture",
        "subtype": "Torch"
      }
    },
    {
      "doubles": {},
      "id": "1_[ITEM]_DUMMY",
      "longs": {},
      "strings": {
        "type": "decorFurniture",
        "subtype": "Torch"
      }
    },
    {
      "doubles": {},
      "id": "2_[ITEM]_DUMMY",
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
        val successResponse = Response(MessageData(booleans = mutableMapOf(Keys.success to true)), Status.OK_TO_RETURN_TO_CLIENT)
        val actualResponse = actionResolutionTable(Actions.setUserProfileState, incomingMsg)
        assertEquals("nooo", Core.userProfile!!.getName())
        assertEquals(successResponse.status, actualResponse.status)
    }

    @Test
    fun case_getTransaction () {
        val json = UserData("fooBar", "12345").exportAsJson()
        Core.uiInterrupts = InternalUiInterrupts()
        Core.start(json)
        val tx = Transaction("tst", "0", "1", setOf(), setOf(), setOf(), setOf(), Transaction.State.TX_ACCEPTED,
                setOf(), setOf())
        OutsideWorldDummy.addTx(tx)
        val successResponse = Response(tx.detailsToMessageData().merge(MessageData(booleans = mutableMapOf(Keys.success to true))), Status.OK_TO_RETURN_TO_CLIENT)
        val actualResponse = actionResolutionTable(Actions.getTransaction,
                MessageData(strings = mutableMapOf("txId" to "tst")))
        assertEquals(successResponse.status, actualResponse.status)
        assertEquals(successResponse.msg!!.strings["txId"], actualResponse.msg!!.strings["txId"])
    }
}