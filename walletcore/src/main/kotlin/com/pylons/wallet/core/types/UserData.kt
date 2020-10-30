package com.pylons.wallet.core.types

import com.pylons.wallet.core.Core

const val userDataFormatVersion : String = "1.0.0"

/**
 * Object representing persistent data stored on local storage.
 * Used to configure com.pylons.wallet.core's initial state when starting.
 */
@ExperimentalUnsignedTypes
class UserData(val core : Core) {
    class Model {
        var dataSets : Map<String, MutableMap<String, String>>? = mutableMapOf()
        val version : String? = null
    }
    var dataSets : MutableMap<String, MutableMap<String, String>> = mutableMapOf()

    /**
     * UserData.version exists for forwards compatibility reasons.
     * Since we just serialize the object directly as JSON, it'd be easy
     * for changes on the backend to break existing JSON documents;
     * by ensuring that the document identifies a formal revision of
     * UserData that it corresponds to, we can handle older JSON
     * documents as a special case down the road, if needed.
     */


    fun parseFromJson(json: String) {

        when (json) {
            "" -> initializeUserData()
            else -> {
                val d =  klaxon.parse<Model>(json)
                dataSets = d?.dataSets.orEmpty().toMutableMap()
            }
        }
    }

    fun initializeUserData () {
        dataSets = core.engine.getInitialDataSets()
    }

    fun exportAsJson () : String {
        val model = Model()
        model.dataSets = dataSets
        return klaxon.toJsonString(model)
    }
}