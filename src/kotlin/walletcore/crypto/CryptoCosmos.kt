package walletcore.crypto

import org.apache.tuweni.bytes.Bytes
import walletcore.types.UserData
import java.security.KeyFactory
//import net.i2p.crypto.eddsa.KeyFactory
import java.security.KeyPair
import java.security.PrivateKey
import java.security.spec.KeySpec
import kotlin.random.Random
import org.apache.tuweni.crypto.*

class CryptoCosmos (userData: UserData?) : CryptoHandler (userData) {
    var privateKey : PrivateKey? = null
    var seed : ByteArray? = null
    var keyPair : SECP256K1.KeyPair? = null
    //var scheme : SignatureScheme = Crypto.ECDSA_SECP256K1_SHA256


    override fun importKeysFromUserData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun generateNewKeys() {
        //val pk= Random.nextBytes(scheme.keySize!!)
        //val s= Random.nextBytes(scheme.keySize!!)
        //keyPair = SECP256K1.KeyPair.create(SECP256K1.SecretKey.fromBytes(pk),
        //        SECP256K1.PublicKey.fromSecretKey())

    }

    override fun signature(bytes: ByteArray): ByteArray = SECP256K1.sign(bytes, keyPair).bytes().toArray()

    override fun verify(bytes: ByteArray, signature : ByteArray): Boolean =
        SECP256K1.verify(bytes, SECP256K1.Signature.fromBytes(Bytes.wrap(signature)), keyPair!!.publicKey())

}