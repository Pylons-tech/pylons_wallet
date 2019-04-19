package walletcore.gamerules

import walletcore.Core

open class OneTimeContract (preferredItemIds : Set<String>): SimpleContract(preferredItemIds) {

    override fun applyOffline() {
        Core.userProfile!!.singletonGameRules.add(id)
        super.applyOffline()
    }

    override fun canApply(): Boolean {
        return when (Core.userProfile!!.singletonGameRules.contains(id)){
            true -> false
            false -> super.canApply()
        }
    }
}