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
                            )))),
                    "buyFurniture_Bed" to Recipe(id = "buyFurniture_Bed",
                            coinsIn = setOf(Coin("gold", 5)),
                            itemsOut = setOf(ItemPrototype(stringConstraints = mapOf(
                                    "type" to setOf(StringConstraint("decorFurniture")),
                                    "subtype" to setOf(StringConstraint("Bed"))
                            )))),
                    "buyFurniture_Chest" to Recipe(id = "buyFurniture_Chest",
                            coinsIn = setOf(Coin("gold", 3)),
                            itemsOut = setOf(ItemPrototype(stringConstraints = mapOf(
                                    "type" to setOf(StringConstraint("decorFurniture")),
                                    "subtype" to setOf(StringConstraint("Chest"))
                            )))),
                    "buyFurniture_Table" to Recipe(id = "buyFurniture_Table",
                            coinsIn = setOf(Coin("gold", 6)),
                            itemsOut = setOf(ItemPrototype(stringConstraints = mapOf(
                                    "type" to setOf(StringConstraint("decorFurniture")),
                                    "subtype" to setOf(StringConstraint("Endtable"))
                            )))),
                    "buyFurniture_Chair" to Recipe(id = "buyFurniture_Chair",
                            coinsIn = setOf(Coin("gold", 3)),
                            itemsOut = setOf(ItemPrototype(stringConstraints = mapOf(
                                    "type" to setOf(StringConstraint("decorFurniture")),
                                    "subtype" to setOf(StringConstraint("Chair"))
                            )))),
                    "buyFurniture_Torch" to Recipe(id = "buyFurniture_Torch",
                            coinsIn = setOf(Coin("gold", 1)),
                            itemsOut = setOf(ItemPrototype(stringConstraints = mapOf(
                                    "type" to setOf(StringConstraint("decorFurniture")),
                                    "subtype" to setOf(StringConstraint("Torch"))
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