package com.pylons.wallet.core.types

data class ItemPrototype (
        val stringConstraints : Map<String, Set<StringConstraint>>? = mapOf(),
        val longConstraints : Map<String, Set<LongConstraint>>? = mapOf(),
        val doubleConstraints : Map<String, Set<DoubleConstraint>>? = mapOf()
)