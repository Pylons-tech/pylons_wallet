package com.pylons.wallet.core.types

import com.squareup.moshi.Moshi
import com.pylons.wallet.core.Core

/**
 * Object representing persistent data stored on local storage.
 * Used to configure com.pylons.wallet.core's initial state when starting.
 */
internal object UserData {
    class Model {
        var dataSets : Map<String, MutableMap<String, String>>? = mutableMapOf()
        val version : String? = null
    }
    var dataSets : MutableMap<String, MutableMap<String, String>> = mutableMapOf()
    //var friends : List<Friend>? = listOf()
    /**
     * UserData.version exists for forwards compatibility reasons.
     * Since we just serialize the object directly as JSON, it'd be easy
     * for changes on the backend to break existing JSON documents;
     * by ensuring that the document identifies a formal revision of
     * UserData that it corresponds to, we can handle older JSON
     * documents as a special case down the road, if needed.
     */
    const val version : String = "1.0.0"

    fun parseFromJson(json: String) {

        when (json) {
            "" -> initializeUserData()
            else -> {
                val moshi = Moshi.Builder().build()
                val jsonAdapter = moshi.adapter<Model>(Model::class.java)
                val d=  jsonAdapter.fromJson(json)
                dataSets = d?.dataSets.orEmpty().toMutableMap()
            }
        }
    }

    fun initializeUserData () {
        //dataSets = Core.engine.getInitialDataSets()
    }

    fun exportAsJson () : String {
        val moshi = Moshi.Builder().build()
        val jsonAdapter = moshi.adapter<UserData>(UserData::class.java)
        return jsonAdapter.toJson(this)
    }
}