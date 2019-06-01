package com.pylons.wallet.core.types

enum class Error(val value: Int) {
    UNRECOGNIZED_ACTION_FIELD(1),
    NO_ACTION_FIELD(2),
    BAD_ARGUMENTS(3),
    MALFORMED_TX(4),
    FOREIGN_PROFILE_DOES_NOT_EXIST(5),
    COOKBOOK_DOES_NOT_EXIST(6),
    RECIPE_DOES_NOT_EXIST(7),
    NO_RESOURCES_FOR_TX(8),
    CORE_IS_NOT_SANE(9)
}