package walletcore.gamerules

import walletcore.Core

open class OneTimeContract (preferredItemIds : Set<String>): SimpleContract(preferredItemIds) {

    override fun applyOffline() {
        System.out.println("One-time contract $id ${Core.userProfile!!.singletonGameRules.contains(id)}")
        Core.userProfile!!.singletonGameRules.add(id!!)
        System.out.println(Core.userProfile!!.singletonGameRules.contains(id!!))
        super.applyOffline()
    }

    override fun canApply(): Boolean {
        System.out.println("One-time contract apply check should return ${Core.userProfile!!.singletonGameRules.contains(id)}")
        if (Core.userProfile!!.singletonGameRules.contains(id)) return false
        return super.canApply()
    }
}