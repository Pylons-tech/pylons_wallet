package walletcore.types

interface Callback<T> {
    fun onSuccess(result: T)
    fun onFailure(result: T)
    fun onException(e: Exception?)
}