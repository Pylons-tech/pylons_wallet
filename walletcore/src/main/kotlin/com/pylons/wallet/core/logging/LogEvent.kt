package com.pylons.wallet.core.logging

object LogEvent {
    const val MISC = "misc"
    const val TX_POST  = "tx_post"
    const val TX_RESPONSE = "tx_response"
    const val HTTP_POST = "http_post"
    const val HTTP_GET = "http_get"
    const val USER_DATA_PARSE_FAIL = "user_data_parse_fail"
    const val RESOLVED_MESSAGE = "resolved_message"
    const val GENERATED_NEW_KEYS = "generated_new_keys"
    const val IMPORTED_KEYS = "imported_keys"
    const val SET_SUSPENDED_ACTION = "set_suspended_action"
    const val IPC_HANDSHAKE_FAIL = "ipc_handshake_fail"
    const val RESET_IPC_STATE = "reset_ipc_state"
    const val NO_ACTION_FIELD = "no_action_field"
    const val RETURN_MESSAGE_TO_CLIENT  = "return_message_to_client"
    const val REJECT_MESSAGE = "reject_message"
    const val PARSED_MESSAGE = "parsed_message"
    const val AWAIT_MESSAGE = "await_message"
    const val GOT_DATA_FROM_CLIENT = "got_data_from_client"
    const val REQUIRE_UI_ELEVATION = "require_ui_elevation"
}