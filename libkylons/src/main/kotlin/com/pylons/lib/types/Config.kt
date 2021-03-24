package com.pylons.lib.types

/**
 * Walletcore configuration object.
 * Passed to Core.start as an argument; used to encapsulate org.bitcoinj.core.core arguments
 * set per wallet implementation and distinguish them from org.bitcoinj.core.core arguments meant
 * to be derived from user data.
 */
data class Config(val backend: Backend, val nodes : List<String>)