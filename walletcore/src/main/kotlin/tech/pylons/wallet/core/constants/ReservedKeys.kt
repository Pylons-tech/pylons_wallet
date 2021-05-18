package tech.pylons.wallet.core.constants

class ReservedKeys {
    companion object {
        const val prefix = "@P__"
        const val wcAction = "${prefix}ACTION"
        const val itemName = "${prefix}ITEM_NAME"
        const val profileName = "${prefix}PROFILE_NAME"
        const val statusBlock = "${prefix}STATUS_BLOCK"
    }
}