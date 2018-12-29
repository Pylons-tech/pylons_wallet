package walletcore.tx

import walletcore.types.*

internal object OutsideWorldDummy {
    val cookbooks : Map<String, Cookbook> = mapOf(
            "foo" to Cookbook(id = "foo", recipes = mapOf(
                    "bar" to Recipe(id = "bar",
                            coinsIn = setOf(Coin("pylons", 1)),
                            itemsOut = setOf(ItemPrototype(stringConstraints = mapOf(
                                    "type" to setOf(StringConstraint("thingy"))
                            ))))
            ))
    )
}