package com.pylons.wallet.core.types.tx.recipe

annotation class QuotedJsonNumeral(val serializationMode : SerializationMode = SerializationMode.ALL)

annotation class NeverQuoteWrap

enum class SerializationMode {
    FOR_BROADCAST,
    FOR_SIGNING,
    ALL
}