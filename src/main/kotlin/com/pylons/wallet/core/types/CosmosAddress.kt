package com.pylons.wallet.core.types

import org.bouncycastle.util.encoders.Hex

class CosmosAddress {
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
        fun accAddressFromHex (address: String) : ByteArray {
            if (address.isEmpty()) throw Exception("decoding Bech32 address failed: must provide an address")
            return Hex.decode(address)
        }
    }
}