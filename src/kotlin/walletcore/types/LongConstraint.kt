package walletcore.types

data class LongConstraint (
        val value : Long = 0,
        val mode : ConstraintMode = ConstraintMode.EXACT_MATCH
)