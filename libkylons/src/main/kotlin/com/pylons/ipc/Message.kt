package com.pylons.ipc

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.pylons.lib.PubKeyUtil
import com.pylons.lib.core.ICore
import com.pylons.lib.core.IMulticore
import org.apache.tuweni.bytes.Bytes32
import java.lang.StringBuilder
import java.util.*
import kotlin.reflect.full.*
import com.pylons.lib.klaxon
import com.pylons.lib.types.*
import com.pylons.lib.types.credentials.CosmosCredentials
import com.pylons.lib.types.tx.Trade
import com.pylons.lib.types.tx.recipe.Recipe

sealed class Message {

    class CancelTrade (
            var tradeId : String? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<CancelTrade>(json)
        }

        override fun resolve() = TxResponse(core!!.cancelTrade(tradeId!!)).wait().pack()
    }

    class CheckExecution(
            var id : String? = null,
            var payForCompletion : Boolean? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<CheckExecution>(json)
        }

        override fun resolve() = TxResponse(core!!.checkExecution(id!!, payForCompletion!!)).wait().pack()
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

        override fun resolve() = TxResponse(
            core!!.batchCreateCookbook(ids!!, names!!, developers!!,
                descriptions!!, versions!!, supportEmails!!, levels!!, costsPerBlock!!)).wait().pack()
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

        override fun resolve() = TxResponse(
            core!!.batchCreateRecipe(names!!, cookbooks!!,
                descriptions!!, blockIntervals!!, coinInputs!!, itemInputs!!, outputTables!!,
                outputs!!)).wait().pack()
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

        override fun resolve() = TxResponse(
            core!!.createTrade(coinInputs!!, itemInputs!!,
                coinOutputs!!, itemOutputs!!, extraInfo!!)).wait().pack()
    }

    class DisableRecipes (
            var recipes : List<String>? = null
    ) : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<DisableRecipes>(json)
        }

        override fun resolve() = TxResponse(core!!.batchDisableRecipe(recipes!!)).wait().pack()
    }

    class EnableRecipes (
            var recipes : List<String>? = null
    ) : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<EnableRecipes>(json)
        }

        override fun resolve() = TxResponse(core!!.batchEnableRecipe(recipes!!)).wait().pack()
    }

    class ExecuteRecipe(
            var recipe : String? = null,
            var cookbook : String? = null,
            var itemInputs : List<String>? = null
    ) : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<ExecuteRecipe>(json)
        }

        override fun resolve() =
                TxResponse(core!!.applyRecipe(recipe!!, cookbook!!, itemInputs!!)).wait().pack()
    }

    class FulfillTrade(
            var tradeId : String? = null,
            var itemIds : List<String>? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<FulfillTrade>(json)
        }

        override fun resolve() = TxResponse(core!!.fulfillTrade(tradeId!!, itemIds!!)).wait().pack()
    }

    class GetCookbooks : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetCookbooks>(json)
        }

        override fun resolve() = CookbookResponse(core!!.getCookbooks()).wait().pack()
    }

    class GetPendingExecutions : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetPendingExecutions>(json)
        }

        override fun resolve() = ExecutionResponse(core!!.getPendingExecutions()).wait().pack()
    }

    class GetTrades : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetTrades>(json)
        }

        override fun resolve() = TradeResponse(core!!.listTrades()).wait().pack()
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
            if (p != null) ls.add(Profile(p.address, p.strings, p.coins, p.items)) // hack because klaxon will die if we aren't specifically an instance of the base class
            return ProfileResponse(ls).wait().pack()
        }
    }

    class GetPylons(
            var count : Long? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetPylons>(json)
        }

        override fun resolve() = TxResponse(core!!.getPylons(count!!)).wait().pack()
    }

    class GetRecipes : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetRecipes>(json)
        }

        override fun resolve() = RecipeResponse(core!!.getRecipes()).wait().pack()
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
                core!!.googleIapGetPylons(productId!!, purchaseToken!!, receiptData!!, signature!!)).wait().pack()
    }

    class GetTransaction(
            var txHash : String? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetTransaction>(json)
        }

        override fun resolve() = TxResponse(core!!.getTransaction(txHash!!)).wait().pack()
    }

    class RegisterProfile(
            var name : String? = null,
            var makeKeys : Boolean? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<RegisterProfile>(json)
        }

        override fun resolve(): Response {
            if (IMulticore.enabled && makeKeys == true) {
                IMulticore.instance!!.addCore(null)
                makeKeys = false // we made the keys!
            }
            return when (makeKeys!!) {
                // HACK: We shouldn't accept empty name field once names actually exist on the backend
                true -> TxResponse(core!!.newProfile(name.orEmpty())).wait().pack()
                false -> TxResponse(core!!.newProfile(name.orEmpty(), core!!.engine.cryptoHandler.keyPair)).wait().pack()
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

        override fun resolve() = TxResponse(core!!.sendCoins(coins!!, receiver!!)).wait().pack()
    }

    class SetItemString(
            var itemId : String? = null,
            var field : String? = null,
            var value : String? = null
    ) : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<SetItemString>(json)
        }

        override fun resolve() = TxResponse(core!!.setItemString(itemId!!, field!!, value!!)).wait().pack()
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

        override fun resolve() = TxResponse(
            core!!.batchUpdateCookbook(names!!, developers!!, descriptions!!,
                    versions!!, supportEmails!!, ids!!)).wait().pack()

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

        override fun resolve() = TxResponse(
            core!!.batchUpdateRecipe(ids!!, names!!, cookbooks!!,
                descriptions!!, blockIntervals!!, coinInputs!!, itemInputs!!, outputTables!!,
                outputs!!)).wait().pack()
    }

    class WalletServiceTest(
            val input : String? = null
    ) : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<WalletServiceTest>(json)
        }

        override fun resolve() = TestResponse(core!!.walletServiceTest(input!!)).wait().pack()
    }

    class WalletUiTest : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<WalletUiTest>(json)
        }

        override fun ui(): UiHook {
            // WalletUiTest never returns until this hook is released
            return UILayer.addUiHook(UiHook(this))
        }

        override fun resolve() = TestResponse(core!!.walletUiTest()).wait().pack()
    }

    class ExportKeys : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<ExportKeys>(json)
        }

        override fun resolve(): Response {
            val keys = core!!.dumpKeys()
            return KeyResponse(
                    address = core!!.userProfile!!.address,
                    name = core!!.userProfile!!.strings["name"].orEmpty(),
                    privateKey = keys[0],
                    publicKey = keys[1]
            ).wait().pack()
        }
    }

    class AddKeypair(
            val privkey : String? = null
    ) : Message() {
        companion object{
            fun deserialize(json : String) = klaxon.parse<AddKeypair>(json)
        }

        override fun resolve(): Response {
            if (!IMulticore.enabled) throw Exception("Multicore is not enabled")
            val kp = PylonsSECP256K1.KeyPair.fromSecretKey(
                    PylonsSECP256K1.SecretKey.fromBytes(Bytes32.fromHexString(privkey!!)))
            val credentials = CosmosCredentials(PubKeyUtil.getAddressString(PubKeyUtil.getAddressFromKeyPair(kp).toArray()))
            IMulticore.instance!!.addCore(kp)
            return ProfileResponse(listOf(core!!.getProfile(credentials.address)!!)).wait().pack()
        }
    }

    class SwitchKeys : Message() {
        val address : String? = null
        companion object{
            fun deserialize(json : String) = klaxon.parse<SwitchKeys>(json)
        }

        override fun resolve(): Response {
            if (!IMulticore.enabled) throw Exception("Multicore is not enabled")
            val core = IMulticore.instance!!.switchCore(address!!)
            return ProfileResponse(listOf(core.getProfile(address)!!)).wait().pack()
        }
    }

    companion object {
        protected var core : ICore? = null
        fun useCore(core : ICore) {
            Companion.core = core
        }

        fun match(json: String) : Message? {
            println("Trying to match message \n$json")
            val jsonObject = Parser.default().parse(StringBuilder(json)) as JsonObject
            IPCLayer.implementation!!.messageId = jsonObject.int("messageId")!!
            println("Set messageId to ${IPCLayer.implementation!!.messageId}")
            val cid = jsonObject.int("clientId")
            val mid = jsonObject.int("walletId")
            if (cid != IPCLayer.implementation!!.clientId || mid != IPCLayer.implementation!!.walletId)
                throw Exception("Client/wallet ID mismatch - got ${jsonObject.int("clientId").toString()} ${jsonObject.int("walletId").toString()}, expected ${IPCLayer.implementation!!.clientId} ${IPCLayer.implementation!!.walletId}")
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
        var response : Message.Response? = null
        var live : Boolean = true
            private set
        var confirmed : Boolean = false
            private set

        var rejected: Boolean = false
            private set

        fun confirm() : UiHook {
            confirmed = true
            return this
        }

        fun reject() : UiHook {
            rejected = true
            return this
        }

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
        fun pack () : Response = Response(
            IPCLayer.implementation!!.messageId,
                IPCLayer.implementation!!.clientId, IPCLayer.implementation!!.walletId,
                core!!.statusBlock, this)

        open fun wait () : ResponseData = this
    }

    class CookbookResponse (val cookbooks: List<Cookbook>) : ResponseData()
    class ExecutionResponse (val executions: List<Execution>) : ResponseData()
    class KeyResponse (val name : String, val address : String, val privateKey : String, val publicKey : String) : ResponseData()
    class RecipeResponse (val recipes : List<Recipe>) : ResponseData()
    class ProfileResponse (val profiles : List<Profile>) : ResponseData()
    class TradeResponse (val trades : List<Trade>) : ResponseData()
    class TestResponse(val output : String) : ResponseData()

    class RejectResponse(): ResponseData()

    class TxResponse : ResponseData {
        val transactions : List<Transaction>
        constructor(txs : List<Transaction>) {
            transactions = txs
        }
        constructor(tx : Transaction) {
            transactions = listOf(tx)
        }

        override fun wait(): ResponseData {
            for (tx in transactions) {
                if (tx.id != null) {
                    var retries = 0
                    var code = core!!.getTransaction(tx.id!!).code
                    while (code != Transaction.ResponseCode.OK && retries < 12 ) {
                        code = core!!.getTransaction(tx.id!!).code
                        retries++
                    }
                }
            }
            return this
        }
    }

    open fun ui () : UiHook {
        // Default ui() implementation just forces the ui hook through its entire lifecycle immediately.
        // This simplifies the way we need to deal w/ UI interactions - every message creates
        // a ui hook; it's just that some of them don't actually need to do any work before they're
        // done with it.
        //return UILayer.releaseUiHook(UILayer.confirmUiHook(UILayer.addUiHook(UiHook(this))))

        //break main ui loop
        return UILayer.addUiHook(UiHook(this))
    }

    abstract fun resolve() : Response
}