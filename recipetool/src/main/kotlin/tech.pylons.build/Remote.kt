package tech.pylons.build

import org.spongycastle.jcajce.provider.digest.SHA256
import org.spongycastle.util.encoders.Hex

data class Remote (val chainId : String, val txsUrl : String, val queryUrl : String) {
    fun hash () : String =
        Hex.toHexString(
            SHA256.Digest().digest((
                chainId + txsUrl + queryUrl).toByteArray(Charsets.UTF_8)))
}