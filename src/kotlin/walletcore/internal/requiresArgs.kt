package walletcore.internal

import walletcore.types.*

/**
 * If an operation is called without additional arguments, and it requires
 * additional arguments, it generates a Response object which signals to
 * the wallet application that the operation in question requires additional
 * arguments. In general, this means that we need to bring up the wallet UI,
 * which tends to involve the application jumping through some sort of
 * platform-specific hoops. This response's Response.msg field is null,
 * and if you try to go back to the client with that it'll crash, so
 * don't do that.
 */
internal fun requiresArgs(extraArgs: MessageData?, func: (MessageData) -> Response): Response {
    return if (extraArgs != null) func(extraArgs)
    else Response(null, Status.REQUIRE_UI_ELEVATION)
}