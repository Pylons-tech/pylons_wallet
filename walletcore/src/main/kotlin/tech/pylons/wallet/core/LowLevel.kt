package tech.pylons.wallet.core

import tech.pylons.lib.core.ILowLevel
import tech.pylons.lib.types.PylonsSECP256K1
import tech.pylons.lib.types.credentials.CosmosCredentials
import tech.pylons.lib.types.tx.msg.Msg
import tech.pylons.wallet.core.internal.HttpWire
import org.apache.commons.codec.binary.Hex.*
import org.apache.tuweni.bytes.Bytes32

@ExperimentalUnsignedTypes
class LowLevel (private val core : Core) : ILowLevel {
    private val local = """http://127.0.0.1:1317"""
    private val nodeUrl = getUrl()

    private fun getUrl () : String  {
        return when ((core.config?.nodes.isNullOrEmpty())) {
            true -> local
            false -> core.config?.nodes!!.first()
        }
    }

    private fun setup (privkeyHex : String, accountNumber : Long, sequence : Long) {
        core.forceKeys(privkeyHex, HttpWire.getAddressFromNode(nodeUrl,
                PylonsSECP256K1.KeyPair.fromSecretKey(PylonsSECP256K1.SecretKey.fromBytes(Bytes32.wrap(decodeHex(privkeyHex))))))
        (core.userProfile!!.credentials as CosmosCredentials).accountNumber = accountNumber
        (core.userProfile!!.credentials as CosmosCredentials).sequence = sequence
    }

    override fun getSignedTx (privkeyHex : String, accountNumber : Long, sequence : Long, msgJson : String) : String {
        setup(privkeyHex, accountNumber, sequence)
        return when (val msg = Msg.fromJson(msgJson)) {
            null -> "invalid message"
            else -> msg.toSignedTx()
        }
    }

    override fun getSignBytes (privkeyHex : String, accountNumber : Long, sequence : Long, msgJson : String) : String {
        setup(privkeyHex, accountNumber, sequence)
        return when (val msg = Msg.fromJson(msgJson)) {
            null -> "invalid message"
            else -> msg.toSignStruct()
        }
    }
}