package com.pylons.wallet.core

import com.pylons.wallet.core.engine.TxPylonsEngine
import com.pylons.wallet.core.types.AccAddress
import com.pylons.wallet.core.types.PylonsSECP256K1
import com.pylons.wallet.core.types.tx.msg.Msg
import org.apache.commons.codec.binary.Hex.*
import org.apache.tuweni.bytes.Bytes32

@ExperimentalUnsignedTypes
class LowLevel (private val core : Core) {
    private val local = """http://127.0.0.1:1317"""
    private val nodeUrl = getUrl()

    private fun getUrl () : String  {
        return when ((core.config?.nodes.isNullOrEmpty())) {
            true -> local
            false -> core.config?.nodes!!.first()
        }
    }

    private fun setup (privkeyHex : String, accountNumber : Long, sequence : Long) {
        core.forceKeys(privkeyHex, AccAddress.getAddressFromNode(nodeUrl,
                PylonsSECP256K1.KeyPair.fromSecretKey(PylonsSECP256K1.SecretKey.fromBytes(Bytes32.wrap(decodeHex(privkeyHex))))))
        (core.userProfile!!.credentials as TxPylonsEngine.Credentials).accountNumber = accountNumber
        (core.userProfile!!.credentials as TxPylonsEngine.Credentials).sequence = sequence
    }

    fun getSignedTx (privkeyHex : String, accountNumber : Long, sequence : Long, msgJson : String) : String {
        setup(privkeyHex, accountNumber, sequence)
        return when (val msg = Msg.fromJson(msgJson)) {
            null -> "invalid message"
            else -> msg.toSignedTx()
        }
    }

    fun getSignBytes (privkeyHex : String, accountNumber : Long, sequence : Long, msgJson : String) : String {
        setup(privkeyHex, accountNumber, sequence)
        return when (val msg = Msg.fromJson(msgJson)) {
            null -> "invalid message"
            else -> msg.toSignStruct()
        }
    }
}