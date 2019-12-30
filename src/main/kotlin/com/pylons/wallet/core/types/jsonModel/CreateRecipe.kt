package com.pylons.wallet.core.types.jsonModel

import com.pylons.wallet.core.Core
import com.pylons.wallet.core.engine.TxPylonsEngine
import com.pylons.wallet.core.types.jsonTemplate.baseJsonWeldFlow
import com.squareup.moshi.*

data class CreateRecipe (
        @property:[Json(name = "BlockInterval")]
        val blockInterval : Long,
        @property:[Json(name = "CoinInputs")]
        val coinInputs : List<CoinInput>,
        @property:[Json(name = "CookbookID")]
        val cookbookId : String,
        @property:[Json(name = "Description")]
        val description: String,
        @property:[Json(name = "Entries")]
        val entries : WeightedParamList,
        @property:[Json(name = "ItemInputs")]
        val itemInputs : List<ItemInput>,
        @property:[Json(name = "Name")]
        val name : String,
        @property:[Json(name = "Sender")]
        val sender : String,
        @property:[Json(name = "RType")]
        val rType : Long,
        @property:[Json(name = "ToUpgrade")]
        val toUpgrade : ItemUpgradeParams
) {
    companion object {
        val msgAdapter = CreateRecipeAdapter(SerializationMode.FOR_BROADCAST)
        val signingAdapter = CreateRecipeAdapter(SerializationMode.FOR_SIGNING)
    }

    private fun toMsgJson () : String = """
        [
        {
            "type": "pylons/CreateRecipe",
            "value": ${msgAdapter.toJson(this)}
        }
        ]"""

    fun toSignedTx () : String {
        println("tosignedtx")
        val c = Core.userProfile!!.credentials as TxPylonsEngine.Credentials
        val crypto = (Core.engine as TxPylonsEngine).cryptoHandler
        return baseJsonWeldFlow(toMsgJson(), toSignStruct(), c.accountNumber, c.sequence, crypto.keyPair!!.publicKey())
    }

    fun toSignStruct () : String {
        println("tosignedtx")
        return "[${signingAdapter.toJson(this)}]"
    }
}

class CreateRecipeAdapter(val mode: SerializationMode = SerializationMode.FOR_BROADCAST) : JsonAdapter<CreateRecipe>() {
    override fun fromJson(p0: JsonReader): CreateRecipe? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @ToJson
    override fun toJson(p0: JsonWriter, p1: CreateRecipe?) {
        JsonModelSerializer.serialize(mode, p0, p1)
    }

}