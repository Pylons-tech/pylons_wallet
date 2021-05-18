package tech.pylons.wallet.core.engine.crypto

import tech.pylons.lib.PubKeyUtil
import tech.pylons.lib.core.ICryptoHandler
import tech.pylons.wallet.core.Core
import tech.pylons.lib.logging.LogEvent
import tech.pylons.lib.logging.LogTag
import tech.pylons.lib.logging.Logger
import tech.pylons.lib.types.PylonsSECP256K1
import org.apache.commons.codec.binary.Base32
import org.apache.tuweni.bytes.Bytes
import org.apache.tuweni.bytes.Bytes32
import org.spongycastle.util.encoders.Hex

class CryptoCosmos(val core : Core) : ICryptoHandler {
    override fun getPrefix() : String = "__CRYPTO_COSMOS__"
    override var keyPair : PylonsSECP256K1.KeyPair? = null

    @ExperimentalUnsignedTypes
    override fun importKeysFromUserData() {
        val bytes = Hex.decode(core.userData.dataSets[getPrefix()]!!["key"]!!.removePrefix("0x"))
        keyPair =  PylonsSECP256K1.KeyPair.fromSecretKey(PylonsSECP256K1.SecretKey.fromBytes(Bytes32.wrap(bytes)))
        Logger.implementation.log(LogEvent.IMPORTED_KEYS, PubKeyUtil.getLogMsgForKeys(keyPair), LogTag.info)
    }

    override fun generateNewKeys() {
        keyPair = PubKeyUtil.generateKeyPairFromMnemonic(PubKeyUtil.generateMnemonic())
    }

    override fun signature(bytes: ByteArray): ByteArray = PylonsSECP256K1.sign(bytes, keyPair!!).bytes().toArray().slice(0 until 64).toByteArray()

    override fun verify(bytes: ByteArray, signature : ByteArray): Boolean =
            PylonsSECP256K1.verify(bytes, PylonsSECP256K1.Signature.fromBytes(Bytes.wrap(signature)), keyPair!!.publicKey())

    override fun getEncodedPublicKey() : String = Base32().encodeToString(keyPair!!.publicKey()!!.bytesArray())
}