package tech.pylons.wallet.core

import tech.pylons.lib.core.ILowLevel
import tech.pylons.lib.types.PylonsSECP256K1
import tech.pylons.lib.types.credentials.CosmosCredentials
import tech.pylons.lib.types.tx.msg.Msg
import tech.pylons.wallet.core.internal.HttpWire
import org.apache.commons.codec.binary.Hex.*
import org.apache.tuweni.bytes.Bytes32
import tech.pylons.lib.logging.LogEvent
import tech.pylons.lib.logging.LogTag
import tech.pylons.lib.logging.Logger
import tech.pylons.lib.types.Backend
import tech.pylons.wallet.core.constants.MAINNET_API_URL
import tech.pylons.wallet.core.constants.MAINNET_TX_URL
import tech.pylons.wallet.core.constants.TESTNET_API_URL
import tech.pylons.wallet.core.constants.TESTNET_TX_URL

@ExperimentalUnsignedTypes
class LowLevel (private val core : Core) : ILowLevel {
    companion object {
        private const val LOCAL = """http://127.0.0.1:1317"""

        fun getUrlForQueries () : String {
            return when (Core.current?.config?.backend) {
                Backend.MAINNET -> MAINNET_API_URL
                Backend.TESTNET -> TESTNET_API_URL
                else -> when (Core.current?.config?.nodes.isNullOrEmpty()) {
                    true -> {
                        Logger.implementation.log(
                            LogEvent.MISC,
                            "No defined URL for Pylons API - defaulting to $LOCAL",
                            LogTag.error)
                        LOCAL
                    }
                    else -> Core.current?.config?.nodes?.first()!!
                }
            }
        }

        fun getUrlForTxs () : String {
            return when (Core.current?.config?.backend) {
                Backend.MAINNET -> MAINNET_TX_URL
                Backend.TESTNET -> TESTNET_TX_URL
                else -> when (Core.current?.config?.nodes.isNullOrEmpty()) {
                    true -> {
                        Logger.implementation.log(
                            LogEvent.MISC,
                            "No defined URL for Pylons API - defaulting to $LOCAL",
                            LogTag.error)
                        LOCAL
                    }
                    else -> Core.current?.config?.nodes?.first()!!
                }
            }
        }
    }

    private fun setup (privkeyHex : String, accountNumber : Long, sequence : Long) {
        core.forceKeys(privkeyHex, HttpWire.getAddressFromNode(getUrlForQueries(),
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