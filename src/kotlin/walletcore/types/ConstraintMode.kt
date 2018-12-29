package walletcore.types

enum class ConstraintMode {
    EXACT_MATCH,
    NOT,
    STRING_INCLUDES,
    STRING_EXCLUDES,
    NUM_MORE_THAN,
    NUM_LESS_THAN,
    KEY_EXISTS,
    KEY_DOES_NOT_EXIST
    // TODO: Strings should really work w/ some sort of environment-agnostic regex scheme but ughhhhhh
}