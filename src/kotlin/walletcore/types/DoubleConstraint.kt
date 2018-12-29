package walletcore.types

data class DoubleConstraint (
        val value : Double = 0.0,
        val mode : ConstraintMode = ConstraintMode.EXACT_MATCH
)