package walletcore.tx

import com.squareup.moshi.Moshi
import walletcore.constants.*
import walletcore.types.*

internal object OutsideWorldDummy {
    private class ProfileStore {
        val m_profiles : MutableMap<String, ForeignProfile> = mutableMapOf(
                "012345678910" to ForeignProfile("012345678910", mapOf(
                        ReservedKeys.profileName to "fooBar"))
        )

    }

    private class RecipeStore {
        val m_cookbooks : Map<String, Cookbook> = mapOf(
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
                        "buyFurniture_Endtable" to Recipe(id = "buyFurniture_Endtable",
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
    }

    private class TxStore {
        val m_txRecord : MutableMap<String, Transaction> = mutableMapOf(

        )
    }

    private var profileStore : ProfileStore = ProfileStore()
    private var recipeStore : RecipeStore = RecipeStore()
    private var txStore : TxStore = TxStore()

    val profiles : Map<String, ForeignProfile> get() = profileStore.m_profiles
    val cookbooks : Map<String, Cookbook> get() = recipeStore.m_cookbooks
    val transactions : Map<String, Transaction> get() = txStore.m_txRecord

    fun addProfile (id : String, prf : ForeignProfile) {
        profileStore.m_profiles[id] = prf
    }

    fun addTx (tx : Transaction) {
        txStore.m_txRecord[tx.txId] = tx
    }

    fun dumpProfiles () : String {
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter<ProfileStore>(ProfileStore::class.java)
        return adapter.toJson(profileStore)
    }

    fun loadProfiles (json : String) {
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter<ProfileStore>(ProfileStore::class.java)
        profileStore = adapter.fromJson(json)!!
    }

    fun dumpRecipes () : String {
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter<RecipeStore>(RecipeStore::class.java)
        return adapter.toJson(recipeStore)
    }

    fun loadRecipes (json : String) {
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter<RecipeStore>(RecipeStore::class.java)
        recipeStore = adapter.fromJson(json)!!
    }

    fun dumpTransactions () : String {
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter<TxStore>(TxStore::class.java)
        return adapter.toJson(txStore)
    }

    fun loadTransactions (json : String) {
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter<TxStore>(TxStore::class.java)
        txStore = adapter.fromJson(json)!!
    }
}