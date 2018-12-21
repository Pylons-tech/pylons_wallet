package walletcore.types

import com.squareup.moshi.*

/**
 * Object representing persistent data stored on local storage.
 * Used to configure walletcore's initial state when starting.
 */
data class WalletConfig (
    val name : String = "",
    val id : String = "",
    /**
     * WalletConfig.version exists for forwards compatibility reasons.
     * Since we just serialize the object directly as JSON, it'd be easy
     * for changes on the backend to break existing JSON documents;
     * by ensuring that the document identifies a formal revision of
     * WalletConfig that it corresponds to, we can handle older JSON
     * documents as a special case down the road, if needed.
     */
    val version : String = "1.0.0"
) {
    companion object {
        @JvmStatic()
        fun parseFromJson (json : String) : WalletConfig? {
            val moshi = Moshi.Builder().build()
            val jsonAdapter = moshi.adapter<WalletConfig>(WalletConfig::class.java)
            return jsonAdapter.fromJson(json)
        }
    }

    fun exportAsJson () : String {
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter<WalletConfig>(WalletConfig::class.java)
        return jsonAdapter.toJson(this)
    }
}