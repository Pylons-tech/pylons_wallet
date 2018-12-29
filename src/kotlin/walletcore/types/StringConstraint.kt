package walletcore.types

data class StringConstraint (
        val value : String = "",
        val mode : ConstraintMode = ConstraintMode.EXACT_MATCH
)