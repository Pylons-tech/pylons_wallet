package com.pylons.wallet.core.internal

class BadMessageException: Exception {
    constructor() : super()
    constructor(message : String) : super(message)
    constructor(message: String, cause : Throwable) : super(message, cause)
    constructor(cause : Throwable) : super(cause)

    constructor(func : String, key : String, type : String) : super("$func requires a $type argument of name $key")
}
