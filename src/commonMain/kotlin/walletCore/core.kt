package walletCore

/**
 * The object that's returned from WalletCore.resolveMessage calls.
 * status is a value of either INCOMING_MESSAGE_MALFORMED, OK_TO_RETURN_TO_CLIENT,
 * or REQUIRE_UI_ELEVATION to be processed by the platform-appropriate wallet app.
 *
 * msg is a MessageData object, the usage of which will differ depending on what
 * status is. (see documentation on Status for details)
 */
data class Result (val msg : MessageData?, val status : Status)

/**
 * Container for data being passed into or out of WalletCore.
 */
data class MessageData (
	val booleans : MutableMap<String, Boolean> = mutableMapOf(),
	val booleanArrays : MutableMap<String, BooleanArray> = mutableMapOf(),
	val bytes : MutableMap<String, Byte> = mutableMapOf(),
	val byteArrays : MutableMap<String, ByteArray> = mutableMapOf(),
	val chars : MutableMap<String, Char> = mutableMapOf(),
	val charArrays : MutableMap<String, CharArray> = mutableMapOf(),
	val doubles : MutableMap<String, Double> = mutableMapOf(),
	val doubleArrays : MutableMap<String, DoubleArray> = mutableMapOf(),
	val floats : MutableMap<String, Float> = mutableMapOf(),
	val floatArrays : MutableMap<String, FloatArray> = mutableMapOf(),
	val ints : MutableMap<String, Int> = mutableMapOf(),
    val intArrays : MutableMap<String, IntArray> = mutableMapOf(),
    val longs : MutableMap<String, Long> = mutableMapOf(),
    val longArrays : MutableMap<String, LongArray> = mutableMapOf(),
    val shorts : MutableMap<String, Short> = mutableMapOf(),
    val shortArrays : MutableMap <String, ShortArray> = mutableMapOf(),
    val strings : MutableMap<String, String> = mutableMapOf(),
    val stringArrays : MutableMap<String, MutableList<String>> = mutableMapOf())

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
enum class Status(val value : Int) {
    INCOMING_MESSAGE_MALFORMED(-1),
    OK_TO_RETURN_TO_CLIENT(0),
    REQUIRE_UI_ELEVATION(2),
}

/**
 * resolveMessage is the main entry point which platform-specific wallet apps should use
 * in order to call into WalletCore. It takes two arguments:
 *
 * msg: MessageData object containing the data passed to us by the client. How, exactly,
 * the client does this will of course depend on the platform-specific IPC behavior, but
 * in general it should be packed in a relatively analogous form to this internal structure.
 * On Android it's the extras attached to the intent with which we invoke the service, on
 * Windows we just pass Message objects around directly, etc.
 *
 * args: A second MessageData object, provided for pylonsActions which require additional
 * data that cannot or should not be provided by the client in order to be resolved.
 * Null by default.
 */
fun resolveMessage(msg : MessageData, args : MessageData? = null) : Result? {
    var out : Result? = null
    val pylonsAction = msg.strings["pylonsAction"].orEmpty()
    when (pylonsAction) {
        "" -> out = noPylonsAction()
        "WALLET_SERVICE_TEST" -> out = walletServiceTest()
        "WALLET_UI_TEST" -> out = requiresArgs(args) { m -> walletUiTest(m)}
    }
    return out
}

private fun generateErrorMessageData (error : String, info : String) : Result {
    val msg = MessageData()
    msg.strings["error"] = error
    msg.strings["info"] = info
    return Result(msg, Status.INCOMING_MESSAGE_MALFORMED)
}

private fun requiresArgs (args : MessageData?, func : (MessageData) -> Result) : Result {
    return if (args != null) func(args)
    else Result(null, Status.REQUIRE_UI_ELEVATION)
}

private fun noPylonsAction () : Result {
    return generateErrorMessageData("NO_PYLONSACTION_FIELD", "The sent message didn't contain a pylonsAction field, so the wallet was unable to resolve it.")
}