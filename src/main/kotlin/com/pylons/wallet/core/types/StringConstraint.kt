package com.pylons.wallet.core.types

data class StringConstraint (
        val value : String = "",
        val mode : ConstraintMode = ConstraintMode.EXACT_MATCH
)