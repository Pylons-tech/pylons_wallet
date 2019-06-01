package com.pylons.wallet.core.types

data class LootTable (
        val entries : List<Entry>?
) {
    data class Entry (
            val items: List<ItemPrototype>?,
            val coins : List<Coin>?,
            val likelihood: Int
    ) {
        var minRange : Int = -1
        var maxRange : Int = -1
    }


    private fun calculateTotalLikelihood () : Int {
        var t = 0
        entries.orEmpty().forEach {
            it.minRange = t
            t += it.likelihood
            it.maxRange = t
            System.out.println("${it.minRange}, ${it.maxRange}")
        }
        System.out.println(t)
        return t
    }

    fun getRandomEntry () : Entry? {
        val v = (0 until calculateTotalLikelihood()).random()
        System.out.println(v)
        entries.orEmpty().forEach {
            if (v >= it.minRange && v < it.maxRange) return it
        }
        return null // This shouldn't happen
    }
}