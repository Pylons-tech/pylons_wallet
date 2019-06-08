package com.pylons.wallet.core.engine

import com.squareup.moshi.Moshi
import com.pylons.wallet.core.constants.*
import com.pylons.wallet.core.gamerules.OneTimeContract
import com.pylons.wallet.core.gamerules.SimpleContract
import com.pylons.wallet.core.types.*

object OutsideWorldDummy {
    data class GameRuleData (
            val json : String,
            val type : String
    )
    var loadRuleJson : ((String, String) -> GameRuleData)? = null
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
        val data = loadRuleJson!!(cookbook, id)
        return when (data.type) {
            "SimpleContract" -> {
                val adapter = moshi.adapter<SimpleContract>(SimpleContract::class.java)
                adapter.fromJson(data.json)!!
            }
            "OneTimeContract" -> {
                val adapter = moshi.adapter<SimpleContract>(OneTimeContract::class.java)
                adapter.fromJson(data.json)!!
            }
            else -> throw Exception("${data.type} is not a valid type for LoadExternalGameRuleDef")
        }

    }

    fun addTx (tx : Transaction) {
        //txStore.m_txRecord[tx.txId] = tx
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