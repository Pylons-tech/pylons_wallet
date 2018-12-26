package walletcore.types

import kotlinx.coroutines.*
import kotlinx.coroutines.Deferred

internal class InternalUiInterrupts : UiInterrupts () {
    override fun produceUserDataForFirstRun(): Deferred<UserData> {
        val userData = UserData("fooBar", "12345")
        return CompletableDeferred(userData)
    }
}