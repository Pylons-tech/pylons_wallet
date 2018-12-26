package walletcore.types

import kotlinx.coroutines.Deferred

/**
 * Object exposed to wallet apps, used to enable them to bind UI-dependent functionality
 * to be called by WalletCore under exceptional circumstances.
 */
abstract class UiInterrupts {
   abstract fun produceUserDataForFirstRun () : Deferred<UserData>
}