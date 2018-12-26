package walletcore.internal


import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import walletcore.Core
import walletcore.constants.*
import walletcore.ops.*
import walletcore.types.*

internal class ActionResolutionTableTest {
    @Test
    fun case_noAction () {
        val noActionError = noAction()
        val actualResponse = actionResolutionTable("")
        assertEquals(noActionError.status, actualResponse.status)
        assertEquals(noActionError.msg!!.strings[Keys.error], actualResponse.msg!!.strings[Keys.error])
    }

    @Test
    fun case_invalidAction () {
        val badActionError = unrecognizedAction("not_a_real_action")
        val actualResponse = actionResolutionTable("not_a_real_action")
        assertEquals(badActionError.status, actualResponse.status)
        assertEquals(badActionError.msg!!.strings[Keys.error], actualResponse.msg!!.strings[Keys.error])
    }

    @Test
    fun case_walletServiceTest () {
        val svcTestResponse = walletServiceTest()
        val actualResponse = actionResolutionTable(Actions.walletServiceTest)
        assertEquals(svcTestResponse.status, actualResponse.status)
        assertEquals(svcTestResponse.msg!!.strings[Keys.info], actualResponse.msg!!.strings[Keys.info])
    }

    @Test
    fun case_walletUiTest() {
        val uiTestResponse = walletUiTest(MessageData.empty()) // ui test only checks that it _has_ args
        val noArgsResponse = requiresArgs(null) { m -> walletUiTest(m) }
        val actualNoArgsResponse = actionResolutionTable(Actions.walletUiTest)
        val actualResponse = actionResolutionTable(Actions.walletUiTest, MessageData.empty())
        assertEquals(noArgsResponse.status, actualNoArgsResponse.status)
        assertEquals(noArgsResponse.msg, actualNoArgsResponse.msg) // both should be null
        assertEquals(uiTestResponse.status, actualResponse.status)
        assertEquals(uiTestResponse.msg!!.strings[Keys.info], actualResponse.msg!!.strings[Keys.info])
    }

    @Test
    fun case_getUserDetails() {
        Core.uiInterrupts = InternalUiInterrupts()
        Core.start()
        val noProfileResponse = Response(MessageData(booleans = mutableMapOf(Keys.profileExists to false)), Status.OK_TO_RETURN_TO_CLIENT)
        var actualResponse = actionResolutionTable(Actions.getUserDetails)
        assertEquals(noProfileResponse.status, actualResponse.status)
        assertEquals(noProfileResponse.msg!!.booleans[Keys.profileExists], actualResponse.msg!!.booleans[Keys.profileExists])
        Core.userProfile = Profile("fooBar", mapOf(Keys.name to "user"), mapOf(Keys.pylons to 99, "gold" to 42))
        val profileResponse = Response(Core.userProfile!!.detailsToMessageData(), Status.OK_TO_RETURN_TO_CLIENT)
        actualResponse = actionResolutionTable(Actions.getUserDetails)
        assertEquals(profileResponse.status, actualResponse.status)
        assertEquals(profileResponse.msg!!.booleans[Keys.profileExists], actualResponse.msg!!.booleans[Keys.profileExists])
        assertEquals(profileResponse.msg!!.strings[Keys.id], actualResponse.msg!!.strings[Keys.id])
        assertEquals(profileResponse.msg!!.strings[Keys.name], actualResponse.msg!!.strings[Keys.name])
        assertEquals(profileResponse.msg!!.strings[Keys.coins], actualResponse.msg!!.strings[Keys.coins])
    }
}