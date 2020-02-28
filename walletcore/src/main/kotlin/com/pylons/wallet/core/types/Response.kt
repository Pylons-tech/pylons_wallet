package com.pylons.wallet.core.types

import com.pylons.wallet.core.Core

/**
 * The object that's returned from WalletCore.resolveMessage calls.
 * status is a value of either INCOMING_MESSAGE_MALFORMED, OK_TO_RETURN_TO_CLIENT,
 * or REQUIRE_UI_ELEVATION to be processed by the platform-appropriate wallet app.
 *
 * msg is a MessageData object, the usage of which will differ depending on what
 * status is. (see documentation on Status for details)
 */
@ExperimentalUnsignedTypes
data class Response (val msg : MessageData?, val status : Status, val statusBlock: StatusBlock = Core.statusBlock)