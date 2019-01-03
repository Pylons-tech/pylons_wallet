package walletcore.tx

import walletcore.constants.*
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
    val profiles : Map<String, ForeignProfile> = mapOf(
            "012345678910" to ForeignProfile("012345678910", mapOf(
            ReservedKeys.profileName to "fooBar"))
    )
}