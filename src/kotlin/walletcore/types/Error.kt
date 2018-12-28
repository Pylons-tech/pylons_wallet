package walletcore.types

enum class Error(val value: Int) {
    UNRECOGNIZED_ACTION_FIELD(1),
    NO_ACTION_FIELD(2),
    BAD_ARGUMENTS(3),
    MALFORMED_TX(4),
    FOREIGN_PROFILE_DOES_NOT_EXIST(5)
}