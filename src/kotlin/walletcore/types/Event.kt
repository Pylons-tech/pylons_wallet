package walletcore.types

class Event<T> {
    private val callbackStack : MutableList<Callback<T>> = mutableListOf()

    fun register (callback: Callback<T>) {
        callbackStack.add(callback)
    }

    fun unregister (callback: Callback<T>) : Boolean {
        return callbackStack.remove(callback)
    }

    fun onSuccess (result : T) {
        for (callback in callbackStack) callback.onSuccess(result)
    }

    fun onFailure (result: T) {
        for (callback in callbackStack) callback.onFailure(result)
    }

    fun onException (e : Exception) {
        for (callback in callbackStack) callback.onException(e)
    }
}