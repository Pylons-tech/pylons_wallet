package com.pylons.lib.types

/**
 * Status.INCOMING_MESSAGE_MALFORMED: message contains data to be returned to
 * the client detailing the nature of the error. (no pylonsAction field, invalid pylonsAction,
 * bad arguments, etc.)
 *
 * Status.OK_TO_RETURN_TO_CLIENT: message contains data to be returned to the
 * client, appropriate for whatever the call it originally made into the wallet was.
 * This might be the status of an account, the results of a transaction, etc.
 *
 * Status.REQUIRE_UI_ELEVATION: message is null. The pylonsAction in question requires
 * additional arguments, which the wallet app needs to generate (usually by getting user
 * input) and pass to resolveMessage in its arguments.
 */
enum class Status(val value: Int) {
    INCOMING_MESSAGE_MALFORMED(-1),
    OK_TO_RETURN_TO_CLIENT(0),
    REQUIRE_UI_ELEVATION(2),
}