package com.pylons.wallet.core

import com.pylons.wallet.core.engine.TxPylonsEngine
import com.pylons.wallet.core.types.AccAddress
import com.pylons.wallet.core.types.SECP256K1
import com.pylons.wallet.core.types.jsonTemplate.baseJsonWeldFlow
import com.pylons.wallet.core.types.tx.msg.Msg
import org.apache.commons.codec.binary.Hex.*
import org.apache.tuweni.bytes.Bytes32

object LowLevel {
    private val local = """http://127.0.0.1:1317"""
    private val nodeUrl = getUrl()

    private fun getUrl () : String  {
        return when ((Core.config?.nodes.isNullOrEmpty())) {
            true -> local
            false -> Core.config?.nodes!!.first()
        }
    }

    fun getSignedTx (privkeyHex : String, accountNumber : Long, sequence : Long, msgJson : String) : String {
        Core.forceKeys(privkeyHex, AccAddress.getAddressFromNode(nodeUrl,
                SECP256K1.KeyPair.fromSecretKey(SECP256K1.SecretKey.fromBytes(Bytes32.wrap(decodeHex(privkeyHex))))))
        val cryptoCosmos = (Core.engine as TxPylonsEngine).cryptoCosmos
        return when (val msg = Msg.fromJson(msgJson)) {
            null -> "invalid message"
            else -> baseJsonWeldFlow(msgJson, msg.toSignStruct(), accountNumber, sequence,
                    cryptoCosmos.keyPair!!.publicKey())
        }
    }

    fun getSignBytes (privkeyHex : String, accountNumber : Long, sequence : Long, msgJson : String) : String {
        Core.forceKeys(privkeyHex, AccAddress.getAddressFromNode(getUrl(),
                SECP256K1.KeyPair.fromSecretKey(SECP256K1.SecretKey.fromBytes(Bytes32.wrap(decodeHex(privkeyHex))))))
        return when (val msg = Msg.fromJson(msgJson)) {
            null -> "invalid message"
            else -> msg.toSignStruct()
        }
    }
}