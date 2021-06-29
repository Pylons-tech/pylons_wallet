package tech.pylons.ipc

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import tech.pylons.lib.PubKeyUtil
import tech.pylons.lib.core.ICore
import tech.pylons.lib.core.IMulticore
import org.apache.tuweni.bytes.Bytes32
import java.lang.StringBuilder
import java.util.*
import kotlin.reflect.full.*
import tech.pylons.lib.klaxon
import tech.pylons.lib.types.*
import tech.pylons.lib.types.credentials.CosmosCredentials
import tech.pylons.lib.types.tx.Coin
import java.io.StringReader

sealed class Message {

    class CancelTrade (
            var tradeId : String? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<CancelTrade>(json)
        }

        override fun resolve() = Response.emit(this, true,
            txs = listOf(core!!.cancelTrade(tradeId!!)), tradesIn = listOf(tradeId!!))
    }

    class CheckExecution(
            var id : String? = null,
            var payForCompletion : Boolean? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<CheckExecution>(json)
        }

        override fun resolve() = Response.emit(this, true,
            txs = listOf(core!!.checkExecution(id!!, payForCompletion!!)))
            // we need structured exec data but we can't do it atm
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

        override fun resolve() = Response.emit(this, true,
            txs = core!!.batchCreateCookbook(ids!!, names!!, developers!!,
            descriptions!!, versions!!, supportEmails!!, levels!!, costsPerBlock!!))
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

        override fun resolve() = Response.emit(this, true, txs = core!!.batchCreateRecipe(names!!, cookbooks!!,
            descriptions!!, blockIntervals!!, coinInputs!!, itemInputs!!, outputTables!!, outputs!!),
            cookbooksIn = cookbooks!!)
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

        override fun resolve() = Response.emit(this, true,
            txs = listOf(core!!.createTrade(coinInputs!!, itemInputs!!, coinOutputs!!, itemOutputs!!, extraInfo!!)))
        // this feels like it should have some structured input data but i'm not sure atm, need to look at how trades
        // work on the node some more
    }

    class DisableRecipes (
            var recipes : List<String>? = null
    ) : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<DisableRecipes>(json)
        }

        override fun resolve() = Response.emit(this, true,
            txs = core!!.batchDisableRecipe(recipes!!), recipesIn = recipes!!)
    }

    class EnableRecipes (
            var recipes : List<String>? = null
    ) : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<EnableRecipes>(json)
        }

        override fun resolve() = Response.emit(this, true,
            txs = core!!.batchEnableRecipe(recipes!!), recipesIn = recipes!!)
    }

    class ExecuteRecipe(
            var recipe : String? = null,
            var cookbook : String? = null,
            var itemInputs : List<String>? = null
    ) : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<ExecuteRecipe>(json)
        }

        override fun resolve() = Response.emit(this, true,
            txs = listOf(core!!.applyRecipe(recipe!!, cookbook!!, itemInputs!!)),
            recipesIn = listOf(recipe!!), cookbooksIn = listOf(cookbook!!),
            itemsIn = itemInputs!!)
    }

    class FulfillTrade(
            var tradeId : String? = null,
            var itemIds : List<String>? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<FulfillTrade>(json)
        }

        override fun resolve() = Response.emit(this, true,
            txs = listOf(core!!.fulfillTrade(tradeId!!, itemIds!!)),
            tradesIn = listOf(tradeId!!), itemsIn = itemIds!!)
    }

    class GetCookbooks : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetCookbooks>(json)
        }

        override fun resolve() = Response.emit(this, true,
        cookbooksOut = core!!.getCookbooks())
    }

    class GetPendingExecutions : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetPendingExecutions>(json)
        }

        override fun resolve() = Response.emit(this, true,
        executionsOut = core!!.getPendingExecutions())
    }

    class GetTrades : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetTrades>(json)
        }

        override fun resolve() = Response.emit(this, true,
        tradesOut = core!!.listTrades())
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
            println("GetProfile Response")
            return Response.emit(this, true, profilesOut = ls)
        }
    }

    class GetPylons(
            var count : Long? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetPylons>(json)
        }

        override fun resolve() = Response.emit(this, true,
            txs = listOf(core!!.getPylons(count!!)))
    }

    class GetRecipes : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetRecipes>(json)
        }

        override fun resolve() = Response.emit(this, true,
            recipesOut = core!!.getRecipes())
    }

    class GetRecipesBySender(): Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<GetRecipesBySender>(json)
        }

        override fun resolve() = Response.emit(this, true,
            recipesOut = core!!.getRecipesBySender())
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

        override fun resolve() = Response.emit(this, true,
            txs = listOf(core!!.googleIapGetPylons(productId!!, purchaseToken!!, receiptData!!, signature!!)))
    }

    class GetTransaction(
            var txHash : String? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetTransaction>(json)
        }

        override fun resolve() = Response.emit(this, true, txs = listOf(core!!.getTransaction(txHash!!)))
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
            val tx = when (makeKeys!!) {
                // HACK: We shouldn't accept empty name field once names actually exist on the backend
                true -> core!!.newProfile(name.orEmpty())
                false -> core!!.newProfile(name.orEmpty(), core!!.engine.cryptoHandler.keyPair)
            }
            return Response.emit(this, true, txs = listOf(tx))
        }
    }

    class SendCoins(
            var coins : String?,
            var receiver : String?
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<SendCoins>(json)
        }

        override fun resolve() = Response.emit(this, true, txs = listOf(core!!.sendCoins(coins!!, receiver!!)),
        coinsIn = Coin.listFromJson(klaxon.parseJsonArray(StringReader(coins)) as JsonArray<JsonObject>))
    }

    class SetItemString(
            var itemId : String? = null,
            var field : String? = null,
            var value : String? = null
    ) : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<SetItemString>(json)
        }

        override fun resolve() = Response.emit(this, true, txs = listOf(core!!.setItemString(itemId!!, field!!, value!!)),
        itemsIn = listOf(itemId!!))
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

        override fun resolve() = Response.emit(this, true, txs =
            core!!.batchUpdateCookbook(names!!, developers!!, descriptions!!, versions!!, supportEmails!!, ids!!),
            cookbooksIn = ids!!)

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

        override fun resolve() : Response =
            Response.emit(this, true, txs = core!!.batchUpdateRecipe(ids!!, names!!, cookbooks!!,
                descriptions!!, blockIntervals!!, coinInputs!!, itemInputs!!, outputTables!!,
                outputs!!),
            recipesIn = ids!!, cookbooksIn = cookbooks!!)
    }

    class WalletServiceTest(
            val input : String? = null
    ) : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<WalletServiceTest>(json)
        }

        override fun resolve() = Response.emit(this, true, unstructured = listOf(core!!.walletServiceTest(input!!)))
    }

    class WalletUiTest : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<WalletUiTest>(json)
        }

        override fun ui(): UiHook {
            // WalletUiTest never returns until this hook is released
            return UILayer.addUiHook(UiHook(this))
        }

        override fun resolve() = Response.emit(this, true, unstructured = listOf(core!!.walletUiTest()))
    }

    class ExportKeys : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<ExportKeys>(json)
        }

        override fun resolve(): Response {
            val keys = core!!.dumpKeys()
            return Response.emit(this, true, unstructured = listOf(core!!.userProfile!!.address, core!!.userProfile!!.strings["name"].orEmpty(),
                keys[0], keys[1]))
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
            return Response.emit(this, true, profilesOut = listOf(core!!.getProfile(credentials.address)!!))
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
            return Response.emit(this, true, profilesOut = listOf(core.getProfile(address)!!))
        }
    }

    /**
     * BuyPylons
     * do wallet UI action for buy pylons
     * wallet ui set txHash of the Purchase Transaction
     * return Purchase Transaction to counterpart
     */
    class BuyPylons: Message() {

        //
        var txHash:String? = null

        companion object{
            fun deserialize(json: String) = klaxon.parse<BuyPylons>(json)
        }

        override fun resolve(): Response {
            //transactions can fail
            if (txHash.isNullOrEmpty()) {
                return Response.emit(this, true)
            }

            return Response.emit(this, true,
                txs=listOf(core!!.getTransaction(txHash!!))
            )
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
        var response : Response? = null
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