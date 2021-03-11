package com.pylons.lib.types

import org.spongycastle.util.encoders.Hex

@ExperimentalUnsignedTypes
class AccAddress (val bytes : ByteArray = byteArrayOf()) {
    companion object {
        fun getAddressFromNode (nodeUrl : String, keyPair: PylonsSECP256K1.KeyPair) : String {
            val json = HttpWire.get("$nodeUrl/pylons/addr_from_pub_key/" +
                    Hex.toHexString(CryptoCosmos.getCompressedPubkey(keyPair.publicKey()).toArray()))
            return klaxon.parse<TxPylonsEngine.AddressResponse>(json)!!.Bech32Addr!!
        }

        // AddrLen defines a valid address length
        const val addrLen = 20
        // Bech32PrefixAccAddr defines the Bech32 prefix of an account's address
        const val bech32MainPrefix = "cosmos"

        // PrefixAccount is the prefix for account keys
        const val prefixAccount = "acc"
        // PrefixPublic is the prefix for public keys
        const val prefixPublic = "pub"

        // PrefixAddress is the prefix for addresses
        const val prefixAddress = "addr"

        // Bech32PrefixAccAddr defines the Bech32 prefix of an account's address
        const val bech32PrefixAccAddr = bech32MainPrefix
        // Bech32PrefixAccPub defines the Bech32 prefix of an account's public key
        const val bech32PrefixAccPub = bech32MainPrefix + prefixPublic

        /**
         * AccAddressFromHex creates an AccAddress from a hex string.
         */
        fun accAddressFromHex (address: String) : AccAddress {
            if (address.isEmpty()) throw Exception("decoding hex address failed: must provide an address")
            return AccAddress(Hex.decode(address))
        }

        /**
         * VerifyAddressFormat verifies that the provided bytes form a valid address
         * according to the default address rules or a custom address verifier set by
         * GetConfig().SetAddressVerifier()
         */
        fun verifyAddressFormat (bz : ByteArray) {
            TODO("how does this actually work")
        }

        /**
         * AccAddressFromBech32 creates an AccAddress from a Bech32 string.
         */
        fun accAddressFromBech32 (address: String) : AccAddress {
            if (address.isBlank()) return AccAddress(byteArrayOf()) // This seems Wrong but it's what Cosmos does...
            val bz = getFromBech32(address, bech32PrefixAccAddr)
            verifyAddressFormat(bz)
            return AccAddress(bz)
        }

        /**
         * GetFromBech32 decodes a bytestring from a Bech32 encoded string.
         */
        fun getFromBech32 (bech32str : String, prefix : String) : ByteArray {
            if (bech32str.isEmpty()) throw Exception("decoding Bech32 address failed: must provide an address")
            val decoded = Bech32Cosmos.decodeAndConvert(bech32str)
            if (decoded.hrp != prefix) throw Exception("invalid Bech32 prefix; expected $prefix, got ${decoded.hrp}")
            return decoded.data
        }

        // Auxiliary

        /**
         * Bech32ifyAccPub returns a Bech32 encoded string containing the
         * Bech32PrefixAccPub prefix for a given account PubKey.
         * (Because we don't implement the full PubKey interface, we have
         * two entry points for different key types.)
         */
        fun bech32ifyAccPubEd25519(pub : ByteArray) : String {
            return Bech32Cosmos.convertAndEncode(bech32PrefixAccPub, AminoCompat.pubKeyEd25519(pub))
        }

        /**
         * Bech32ifyAccPub returns a Bech32 encoded string containing the
         * Bech32PrefixAccPub prefix for a given account PubKey.
         * (Because we don't implement the full PubKey interface, we have
         * two entry points for different key types.)
         */
        fun bech32ifyAccPubSecp256k1(pub : ByteArray) : String {
            return Bech32Cosmos.convertAndEncode(bech32PrefixAccPub, AminoCompat.pubKeySecp256k1(pub))
        }

        /**
         * GetAccPubKeyBech32 creates a PubKey for an account with a given public key
         * string using the Bech32 Bech32PrefixAccPub prefix.
         * (Because we don't implement the full PubKey interface, we have
         * two entry points for different key types.)
         */
        fun getAccPubKeyBech32Ed25519(pubkey : String) : ByteArray {
            val bz = getFromBech32(pubkey, bech32PrefixAccPub)
            return AminoCompat.stripPrefixBytes(bz)
        }

        /**
         * GetAccPubKeyBech32 creates a PubKey for an account with a given public key
         * string using the Bech32 Bech32PrefixAccPub prefix.
         * (Because we don't implement the full PubKey interface, we have
         * two entry points for different key types.)
         */
        fun getAccPubKeyBech32Secp256k1(pubkey : String) : ByteArray {
            val bz = getFromBech32(pubkey, bech32PrefixAccPub)
            return AminoCompat.stripPrefixBytes(bz)
        }
    }

    /**
     * Returns boolean for whether two AccAddresses are Equal
     */
    override fun equals(other : Any?) : Boolean {
        val aa2 = other as AccAddress? ?: return false
        return when (empty() && aa2.empty()) {
            true -> true
            false -> bytes.contentEquals(aa2.bytes)
        }
    }

    /**
     * Returns boolean for whether an AccAddress is empty
     */
    fun empty() = bytes.isEmpty()

    /**
     * Bytes returns the raw address bytes.
     */
    fun bytes() = bytes

    /**
     * String implements the Stringer interface.
     * (This translates to kotlin's toString.())
     */
    override fun toString() : String {
        return when (empty()) {
            true -> ""
            false -> Bech32Cosmos.convertAndEncode(bech32PrefixAccAddr, bytes)
        }
    }

    override fun hashCode(): Int {
        return bytes.contentHashCode()
    }
}