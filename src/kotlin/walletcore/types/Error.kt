package walletcore.types

enum class Error(val value: Int) {
    UNRECOGNIZED_ACTION_FIELD(1),
    NO_ACTION_FIELD(2)
}