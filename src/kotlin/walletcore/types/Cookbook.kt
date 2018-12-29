package walletcore.types

data class Cookbook (
        val id : String = "",
        val recipes : Map<String, Recipe> = mapOf()
)