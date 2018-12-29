package walletcore.internal

import walletcore.Core
import walletcore.types.*

internal fun bufferForeignProfile (id : String) : ForeignProfile? {
    val prf = Core.txHandler.getForeignBalances(id)
    return when (prf) {
        null -> null
        else -> {
            Core.foreignProfilesBuffer = Core.foreignProfilesBuffer + prf!!
            return prf
        }
    }
}