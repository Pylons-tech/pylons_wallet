package walletCore

internal fun walletUiTest (args : MessageData) : Result {
    val msg = MessageData()
    msg.strings["info"] = "Waller UI test OK"
    return Result(msg, Status.OK_TO_RETURN_TO_CLIENT)
}