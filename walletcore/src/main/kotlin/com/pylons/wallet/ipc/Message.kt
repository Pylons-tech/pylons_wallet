package com.pylons.wallet.ipc

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.Multicore
import com.pylons.wallet.core.engine.TxPylonsEngine
import com.pylons.wallet.core.engine.crypto.CryptoCosmos
import com.pylons.wallet.core.ops.*
import com.pylons.wallet.core.types.*
import com.pylons.wallet.core.types.Transaction.Companion.submitAll
import com.pylons.wallet.core.types.tx.Trade
import com.pylons.wallet.core.types.tx.recipe.Recipe
import org.apache.tuweni.bytes.Bytes
import org.apache.tuweni.bytes.Bytes32
import org.bouncycastle.util.encoders.Hex
import java.lang.StringBuilder
import java.util.*
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.functions
import kotlin.reflect.full.companionObjectInstance

sealed class Message {
    class ApplyRecipe(
            var recipe : String? = null,
            var cookbook : String? = null,
            var itemInputs : List<String>? = null
    ) : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<ApplyRecipe>(json)
        }

        override fun resolve() =
                TxResponse(core!!.applyRecipe(recipe!!, cookbook!!, itemInputs!!)).pack()
    }

    class CancelTrade (
            var tradeId : String? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<CancelTrade>(json)
        }

        override fun resolve() = TxResponse(core!!.cancelTrade(tradeId!!)).pack()
    }

    class CheckExecution(
            var id : String? = null,
            var payForCompletion : Boolean? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<CheckExecution>(json)
        }

        override fun resolve() = TxResponse(core!!.checkExecution(id!!, payForCompletion!!)).pack()
    }

    class CreateCookbooks(
            var ids : List<String>? = null,
            var names : List<String>? = null,
            var developers : List<String>? = null,
            var descriptions : List<String>? = null,
            var versions : List<String>? = null,
            var supportEmails : List<String>? = null,
            var levels : List<Long>? = null,
            var costsPerBlock : List<Long>? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<CreateCookbooks>(json)
        }

        override fun resolve() = TxResponse(core!!.batchCreateCookbook(ids!!, names!!, developers!!,
                descriptions!!, versions!!, supportEmails!!, levels!!, costsPerBlock!!)).pack()
    }

    class CreateRecipes(
            var names : List<String>? = null,
            var cookbooks : List<String>? = null,
            var descriptions : List<String>? = null,
            var blockIntervals : List<Long>? = null,
            var coinInputs : List<String>? = null,
            var itemInputs: List<String>? = null,
            var outputTables : List<String>? = null,
            var outputs : List<String>? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<CreateRecipes>(json)
        }

        override fun resolve() = TxResponse(core!!.batchCreateRecipe(names!!, cookbooks!!,
                descriptions!!, blockIntervals!!, coinInputs!!, itemInputs!!, outputTables!!,
                outputs!!)).pack()
    }

    class CreateTrade (
            var coinInputs : List<String>? = null,
            var itemInputs: List<String>? = null,
            var coinOutputs: List<String>? = null,
            var itemOutputs : List<String>? = null,
            var extraInfo : String? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<CreateTrade>(json)
        }

        override fun resolve() = TxResponse(core!!.createTrade(coinInputs!!, itemInputs!!,
                coinOutputs!!, itemOutputs!!, extraInfo!!)).pack()
    }

    class DisableRecipes (
            var recipes : List<String>? = null
    ) : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<DisableRecipes>(json)
        }

        override fun resolve() = TxResponse(core!!.batchDisableRecipe(recipes!!)).pack()
    }

    class EnableRecipes (
            var recipes : List<String>? = null
    ) : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<EnableRecipes>(json)
        }

        override fun resolve() = TxResponse(core!!.batchEnableRecipe(recipes!!)).pack()
    }

    class FulfillTrade(
            var tradeId : String? = null,
            var itemIds : List<String>? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<FulfillTrade>(json)
        }

        override fun resolve() = TxResponse(core!!.fulfillTrade(tradeId!!, itemIds!!)).pack()
    }

    class GetCookbooks : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetCookbooks>(json)
        }

        override fun resolve() = CookbookResponse(core!!.getCookbooks()).pack()
    }

    class GetPendingExecutions : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetPendingExecutions>(json)
        }

        override fun resolve() = ExecutionResponse(core!!.getPendingExecutions()).pack()
    }

    class GetProfile(
            var address : String? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetProfile>(json)
        }

        override fun resolve(): Response {
            val p = core!!.getProfile(address)
            val ls = mutableListOf<Profile>()
            if (p != null) ls.add(p)
            return ProfileResponse(ls).pack()
        }
    }

    class GetPylons(
            var count : Long? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetPylons>(json)
        }

        override fun resolve() = TxResponse(core!!.getPylons(count!!)).pack()
    }

    class GetRecipes : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetRecipes>(json)
        }

        override fun resolve() = RecipeResponse(core!!.getRecipes()).pack()
    }

    class GoogleIapGetPylons(
            var productId : String? = null,
            var purchaseToken : String? = null,
            var receiptData : String? = null,
            var signature : String? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GoogleIapGetPylons>(json)
        }

        override fun resolve() = TxResponse(
                core!!.googleIapGetPylons(productId!!, purchaseToken!!, receiptData!!, signature!!)).pack()
    }

    class GetTransaction(
            var txHash : String? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetTransaction>(json)
        }

        override fun resolve() = TxResponse(core!!.getTransaction(txHash!!)).pack()
    }

    class RegisterProfile(
            var name : String? = null,
            var makeKeys : Boolean? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<RegisterProfile>(json)
        }

        override fun resolve(): Response {
            return when (makeKeys!!) {
                // HACK: We shouldn't accept empty name field once names actually exist on the backend
                true -> TxResponse(core!!.newProfile(name.orEmpty())).pack()
                false -> TxResponse(core!!.newProfile(name.orEmpty(), (core!!.engine.cryptoHandler as CryptoCosmos).keyPair)).pack()
            }
        }
    }

    class SendCoins(
            var coins : String?,
            var receiver : String?
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<SendCoins>(json)
        }

        override fun resolve() = TxResponse(core!!.sendCoins(coins!!, receiver!!)).pack()
    }

    class SetItemString(
            var itemId : String? = null,
            var field : String? = null,
            var value : String? = null
    ) : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<SetItemString>(json)
        }

        override fun resolve() = TxResponse(core!!.setItemString(itemId!!, field!!, value!!)).pack()
    }

    class UpdateCookbooks(
            var ids : List<String>? = null,
            var names : List<String>? = null,
            var developers : List<String>? = null,
            var descriptions : List<String>? = null,
            var versions : List<String>? = null,
            var supportEmails : List<String>? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<UpdateCookbooks>(json)
        }

        override fun resolve() = TxResponse(core!!.batchUpdateCookbook(names!!, developers!!, descriptions!!,
                    versions!!, supportEmails!!, ids!!)).pack()

    }

    class UpdateRecipes(
            var ids : List<String>? = null,
            var names : List<String>? = null,
            var cookbooks : List<String>? = null,
            var descriptions : List<String>? = null,
            var blockIntervals : List<Long>? = null,
            var coinInputs : List<String>? = null,
            var itemInputs: List<String>? = null,
            var outputTables : List<String>? = null,
            var outputs : List<String>? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<UpdateRecipes>(json)
        }

        override fun resolve() = TxResponse(core!!.batchUpdateRecipe(ids!!, names!!, cookbooks!!,
                descriptions!!, blockIntervals!!, coinInputs!!, itemInputs!!, outputTables!!,
                outputs!!)).pack()
    }

    class WalletServiceTest(
            val input : String? = null
    ) : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<WalletServiceTest>(json)
        }

        override fun resolve() = TestResponse(core!!.walletServiceTest(input!!)).pack()
    }

    class WalletUiTest : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<WalletUiTest>(json)
        }

        override fun ui(): UiHook {
            // WalletUiTest never returns until this hook is released
            return UILayer.addUiHook(UiHook(this))
        }

        override fun resolve() = TestResponse(core!!.walletUiTest()).pack()
    }

    class ExportKeys : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<ExportKeys>(json)
        }

        override fun resolve(): Response {
            val keys = core!!.dumpKeys()
            return KeyResponse(
                    address = core!!.userProfile!!.address,
                    name = core!!.userProfile!!.getName().orEmpty(),
                    privateKey = keys[0],
                    publicKey = keys[1]
            ).pack()
        }
    }

    class AddKeypair(
            val privkey : String? = null
    ) : Message() {
        companion object{
            fun deserialize(json : String) = klaxon.parse<AddKeypair>(json)
        }

        override fun resolve(): Response {
            if (!Multicore.enabled) throw Exception("Multicore is not enabled")
            val kp = PylonsSECP256K1.KeyPair.fromSecretKey(
                    PylonsSECP256K1.SecretKey.fromBytes(Bytes32.fromHexString(privkey!!)))
            val credentials = TxPylonsEngine.Credentials(TxPylonsEngine.getAddressString(CryptoCosmos.getAddressFromKeyPair(kp).toArray()))
            Multicore.addCore(kp)
            return ProfileResponse(listOf(core!!.getProfile(credentials.address)!!)).pack()
        }
    }

    class SwitchKeys : Message() {
        val address : String? = null
        companion object{
            fun deserialize(json : String) = klaxon.parse<SwitchKeys>(json)
        }

        override fun resolve(): Response {
            if (!Multicore.enabled) throw Exception("Multicore is not enabled")
            val core = Multicore.switchCore(address!!)
            return ProfileResponse(listOf(core.getProfile(address)!!)).pack()
        }
    }

    companion object {
        protected var core : Core? = null
        fun useCore(core : Core) {
            this.core = core
        }

        fun match(json: String) : Message? {
            println("Trying to match message \n$json")
            val jsonObject = Parser.default().parse(StringBuilder(json)) as JsonObject
            IPCLayer.implementation.messageId = jsonObject.int("messageId")!!
            println("Set messageId to ${IPCLayer.implementation.messageId}")
            val cid = jsonObject.int("clientId")
            val mid = jsonObject.int("walletId")
            if (cid != IPCLayer.implementation.clientId || mid != IPCLayer.implementation.walletId)
                throw Exception("Client/wallet ID mismatch - got ${jsonObject.int("clientId").toString()} ${jsonObject.int("walletId").toString()}, expected ${IPCLayer.implementation.clientId} ${IPCLayer.implementation.walletId}")
            val type = jsonObject.string("type")!!
            val msg =
                    Base64.getDecoder().decode(
                            jsonObject.string("msg")!!).toString(Charsets.US_ASCII)
            var ret : Message? = null
            Message::class.sealedSubclasses.forEach { kClass ->
                if (kClass.simpleName == type) {
                    println("attempting to match ${kClass.simpleName}")
                    val func = kClass.companionObject?.functions?.find { it.name == "deserialize" }
                    ret = func?.call(kClass.companionObjectInstance, msg) as Message?
                }
            }
            return ret
        }
    }

    open class UiHook(val msg : Message) {

        var live : Boolean = true
            private set

        fun release() : UiHook {
            live = false
            return this
        }
    }

    data class Response (
            val messageId : Int,
            val clientId : Int,
            val walletId : Int,
            val statusBlock : StatusBlock,
            val responseData: ResponseData
    ) {
        fun submit() {
            IPCLayer.handleResponse(this)
        }
    }

    abstract class ResponseData {
        fun pack () : Response = Response(IPCLayer.implementation.messageId,
                IPCLayer.implementation.clientId, IPCLayer.implementation.walletId,
                Message.core!!.statusBlock, this)
    }

    class CookbookResponse (val cookbooks: List<Cookbook>) : ResponseData()
    class ExecutionResponse (val executions: List<Execution>) : ResponseData()
    class KeyResponse (val name : String, val address : String, val privateKey : String, val publicKey : String) : ResponseData()
    class RecipeResponse (val recipes : List<Recipe>) : ResponseData()
    class ProfileResponse (val profiles : List<Profile>) : ResponseData()
    class TradeResponse (val trades : List<Trade>) : ResponseData()
    class TestResponse(val output : String) : ResponseData()

    class TxResponse : ResponseData {
        val transactions : List<Transaction>
        constructor(txs : List<Transaction>) {
            transactions = txs
        }
        constructor(tx : Transaction) {
            transactions = listOf(tx)
        }

    }

    open fun ui () : UiHook {
        // Default ui() implementation just forces the ui hook through its entire lifecycle immediately.
        // This simplifies the way we need to deal w/ UI interactions - every message creates
        // a ui hook; it's just that some of them don't actually need to do any work before they're
        // done with it.
        return UILayer.releaseUiHook(UILayer.addUiHook(UiHook(this)))
    }

    abstract fun resolve() : Response
}