package com.pylons.wallet.core.types

import org.bouncycastle.util.encoders.Hex

class AccAddress (val bytes : ByteArray) {

    companion object {
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
    }

    /**
     * Returns boolean for whether two AccAddresses are Equal
     */
    fun equals(aa2 : AccAddress) : Boolean {
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
}