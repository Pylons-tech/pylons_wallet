package com.pylons.lib.types

/**
 * The TX-handling backend an Engine instance is using.
 */
enum class Backend {
    /**
     * No backend. Dummy engine, typically means org.bitcoinj.core.core hasn't been bootstrapped yet.
     */
    NONE,

    /**
     * Standard live-blockchain codepaths. This is what should typically be used in
     * wallets built for end users.
     */
    LIVE,

    /**
     * Live-blockchain codepath w/ access to developer tools. This should be used in wallets
     * build for client game/application developers.
     */
    LIVE_DEV
}