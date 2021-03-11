package com.pylons.lib.core
import com.pylons.lib.types.MyProfile
import com.pylons.lib.types.StatusBlock
import com.pylons.lib.types.UserData
import com.pylons.lib.types.PylonsSECP256K1

@ExperimentalUnsignedTypes
interface ICore {
    companion object {
        var current : ICore? = null
            private set
    }

    val userData : UserData
    val lowLevel : ILowLevel
    var engine: IEngine
    var userProfile: MyProfile?
    var sane : Boolean
    var started : Boolean
    var suspendedAction : String?
    var statusBlock : StatusBlock
    var onWipeUserData : (() -> Unit)?

    /**
     * Serializes persistent user data as a JSON string. All wallet apps will need to take care of calling
     * backupUserData() and storing the results in local storage on their own.
     */
    fun backupUserData () : String?

    fun setProfile (myProfile: MyProfile)

    fun forceKeys (keyString : String, address : String)

    fun dumpKeys () : List<String>

    fun updateStatusBlock ()

    fun use() : ICore

    fun start (userJson : String)

    var onCompletedOperation : (() -> Unit)?

    fun isReady () : Boolean {
        return sane && started
    }

    fun buildJsonForTxPost(msg: String, signComponent: String, accountNumber: Long, sequence: Long, pubkey: PylonsSECP256K1.PublicKey, gas: Long) : String
}