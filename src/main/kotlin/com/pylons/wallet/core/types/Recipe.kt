package com.pylons.wallet.core.types

data class Recipe(val id : String, val sender : String, val disabled : Boolean, val name : String, val cookbook : String, val desc : String, val executionTime : Long,
                  val coinInputs : Map<String, Int>, val coinOutputs : Map<String, Int>) {
    companion object {
        fun getArrayFromJson(json : String) : Array<Recipe> {
            TODO("actually write this")
        }
    }
}

