package walletcore.types

data class LootTable (
        val entries : List<Entry>
) {
    data class Entry (
            val items: List<ItemPrototype>?,
            val coins : List<Coin>?,
            val likelihood: Int
    ) {
        var minRange : Int = -1
        var maxRange : Int = -1
    }

    private val totalLikelihood : Int = calculateTotalLikelihood()

    private fun calculateTotalLikelihood () : Int {
        var t = 0
        entries.forEach {
            it.minRange = t
            t += it.likelihood
            it.maxRange = t
        }
        return t
    }

    fun getRandomEntry () : Entry? {
        val v = (0..totalLikelihood).random()
        entries.forEach {
            if (v >= it.minRange && v < it.maxRange) return it
        }
        return null // This shouldn't happen
    }
}