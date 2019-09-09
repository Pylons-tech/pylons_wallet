package com.pylons.wallet.core.types

data class ItemPrototype (
        val stringConstraints : Map<String, Set<StringConstraint>>? = mapOf(),
        val longConstraints : Map<String, Set<LongConstraint>>? = mapOf(),
        val doubleConstraints : Map<String, Set<DoubleConstraint>>? = mapOf()
) {
    fun exportItemOutput (quoteNumerals : Boolean) : String {
        TODO("idk how to do this")
    }

    fun exportItemInput (quoteNumerals : Boolean) : String {
        TODO("idk how to do this")

    }
}