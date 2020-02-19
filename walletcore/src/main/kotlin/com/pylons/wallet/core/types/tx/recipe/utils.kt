package com.pylons.wallet.core.types.tx.recipe

annotation class QuotedJsonNumeral

annotation class NeverQuoteWrap

enum class SerializationMode {
    FOR_BROADCAST,
    FOR_SIGNING
}