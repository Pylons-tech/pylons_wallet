package tech.pylons.lib.types

/**
 * The TX-handling backend an Engine instance is using.
 */
enum class Backend {
    /**
     * No backend. Dummy engine, typically means org.bitcoinj.core.core hasn't been bootstrapped yet.
     */
    NONE,

    /**
     * Connects to manually-specified nodes. Usually used with a local node setup.
     */
    MANUAL,

    /**
     * Connects to Pylons testnet.
     */
    TESTNET,

    /**
     * Connects to Pylons mainnet.
     */
    MAINNET
}