package com.pylons.lib.types.tx.recipe

annotation class QuotedJsonNumeral(val serializationMode : SerializationMode = SerializationMode.ALL)

annotation class NeverQuoteWrap

annotation class EmptyArray

enum class SerializationMode {
    FOR_BROADCAST,
    FOR_SIGNING,
    ALL
}