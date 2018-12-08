package walletCore

internal fun walletServiceTest () : Result  {
    val msg = MessageData()
    msg.strings["info"] = "Wallet service test OK"
    return Result(msg, Status.OK_TO_RETURN_TO_CLIENT)
}