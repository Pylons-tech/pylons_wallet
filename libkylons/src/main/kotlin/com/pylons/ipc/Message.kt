package com.pylons.ipc

import com.beust.klaxon.Klaxon

private val klaxon = Klaxon()

// todo: this is just copy-pasted from walletcore, but com.pylons.ipc should probably actually live in
// this library. additionally, this doesn't even handle responses rn.

sealed class Message {

    class CancelTrade (
            var tradeId : String? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<CancelTrade>(json)
        }

    }

    class CheckExecution(
            var id : String? = null,
            var payForCompletion : Boolean? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<CheckExecution>(json)
        }

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

    }

    class DisableRecipes (
            var recipes : List<String>? = null
    ) : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<DisableRecipes>(json)
        }

    }

    class EnableRecipes (
            var recipes : List<String>? = null
    ) : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<EnableRecipes>(json)
        }

    }

    class ExecuteRecipe(
            var recipe : String? = null,
            var cookbook : String? = null,
            var itemInputs : List<String>? = null
    ) : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<ExecuteRecipe>(json)
        }

    }

    class FulfillTrade(
            var tradeId : String? = null,
            var itemIds : List<String>? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<FulfillTrade>(json)
        }

    }

    class GetCookbooks : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetCookbooks>(json)
        }

    }

    class GetPendingExecutions : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetPendingExecutions>(json)
        }

    }

    class GetProfile(
            var address : String? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetProfile>(json)
        }

    }

    class GetPylons(
            var count : Long? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetPylons>(json)
        }

    }

    class GetRecipes : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetRecipes>(json)
        }

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

    }

    class GetTransaction(
            var txHash : String? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<GetTransaction>(json)
        }

    }

    class RegisterProfile(
            var name : String? = null,
            var makeKeys : Boolean? = null
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<RegisterProfile>(json)
        }

    }

    class SendCoins(
            var coins : String?,
            var receiver : String?
    ) : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<SendCoins>(json)
        }

    }

    class SetItemString(
            var itemId : String? = null,
            var field : String? = null,
            var value : String? = null
    ) : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<SetItemString>(json)
        }

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

    }

    class WalletServiceTest(
            val input : String? = null
    ) : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<WalletServiceTest>(json)
        }

    }

    class WalletUiTest : Message() {
        companion object {
            fun deserialize(json: String) = klaxon.parse<WalletUiTest>(json)
        }

    }

    class ExportKeys : Message() {
        companion object {
            fun deserialize(json : String) = klaxon.parse<ExportKeys>(json)
        }

    }

    class AddKeypair(
            val privkey : String? = null
    ) : Message() {
        companion object{
            fun deserialize(json : String) = klaxon.parse<AddKeypair>(json)
        }

    }

    class SwitchKeys : Message() {
        val address : String? = null
        companion object{
            fun deserialize(json : String) = klaxon.parse<SwitchKeys>(json)
        }

    }
}