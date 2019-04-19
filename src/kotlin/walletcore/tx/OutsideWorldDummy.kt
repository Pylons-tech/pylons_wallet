package walletcore.tx

import com.squareup.moshi.Moshi
import walletcore.constants.*
import walletcore.types.*

object OutsideWorldDummy {
    var loadRuleJson : ((String, String) -> String)? = null
    private val moshi : Moshi = Moshi.Builder().build()

    private class ProfileStore {
        val m_profiles : MutableMap<String, ForeignProfile> = mutableMapOf(
                "012345678910" to ForeignProfile("012345678910", mapOf(
                        ReservedKeys.profileName to "fooBar"))
        )

    }

    private class TxStore {
        val m_txRecord : MutableMap<String, Transaction> = mutableMapOf(

        )
    }

    private var profileStore : ProfileStore = ProfileStore()
    private var txStore : TxStore = TxStore()

    val profiles : Map<String, ForeignProfile> get() = profileStore.m_profiles
    val transactions : Map<String, Transaction> get() = txStore.m_txRecord
    val builtinGameRules : Map<String, Map<String, GameRule>> = mapOf()

    fun addProfile (id : String, prf : ForeignProfile) {
        profileStore.m_profiles[id] = prf
    }

    fun loadExternalGameRuleDef (cookbook: String, id : String) : GameRule {
        if (loadRuleJson == null) throw Exception("GameRule JSON load function hasn't been set!")
        val json = loadRuleJson!!(cookbook, id)
        val adapter = moshi.adapter<GameRule>(GameRule::class.java)
        return adapter.fromJson(json)!!
    }

    fun addTx (tx : Transaction) {
        txStore.m_txRecord[tx.txId] = tx
    }

    fun dumpProfiles () : String {
        val adapter = moshi.adapter<ProfileStore>(ProfileStore::class.java)
        return adapter.toJson(profileStore)
    }

    fun loadProfiles (json : String) {
        val adapter = moshi.adapter<ProfileStore>(ProfileStore::class.java)
        profileStore = adapter.fromJson(json)!!
    }

    fun dumpTransactions () : String {
        val adapter = moshi.adapter<TxStore>(TxStore::class.java)
        return adapter.toJson(txStore)
    }

    fun loadTransactions (json : String) {
        val adapter = moshi.adapter<TxStore>(TxStore::class.java)
        txStore = adapter.fromJson(json)!!
    }
}