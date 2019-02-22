package walletcore.tx

import walletcore.constants.*
import walletcore.types.*

internal object OutsideWorldDummy {
    val cookbooks : Map<String, Cookbook> = mapOf(
            "foo" to Cookbook(id = "foo", recipes = mapOf(
                    "bar" to Recipe(id = "bar",
                            coinsIn = setOf(Coin("pylons", 1)),
                            itemsOut = setOf(ItemPrototype(stringConstraints = mapOf(
                                    "type" to setOf(StringConstraint("thingy")),
                                    "foo" to setOf(StringConstraint("bar"))
                            ))))
            ))
    )
    val profiles : Map<String, ForeignProfile> get() = m_profiles
    private val m_profiles : MutableMap<String, ForeignProfile> = mutableMapOf(
            "012345678910" to ForeignProfile("012345678910", mapOf(
            ReservedKeys.profileName to "fooBar"))
    )

    fun addProfile (id : String, prf : ForeignProfile) {
        m_profiles[id] = prf
    }
}