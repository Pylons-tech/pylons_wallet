package tech.pylons.lib.types.stripe

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject
import tech.pylons.lib.types.LockedCoin
import tech.pylons.lib.types.LockedCoinDescribe
import tech.pylons.lib.types.tx.Coin


data class StripeInventory(
    @property:[Json(name = "Type")]
    val Type: String,
    @property:[Json(name = "Quantity")]
    val Quantity: String){
}

data class StripeCreateProductSKURequest(
    @property:[Json(name = "Name")]
    val Name: String,
    @property:[Json(name = "Description")]
    val Description: String,
    @property:[Json(name = "Images")]
    val Images: List<String>,
    @property:[Json(name = "Attributes")]
    val Attributes: List<String>,
    @property:[Json(name = "Price")]
    val Price: String,
    @property:[Json(name = "Currency")]
    val Currency: String,
    @property:[Json(name = "Inventory")]
    val Inventory: StripeInventory,
    @property:[Json(name = "ClientId")]
    val ClientId: String,
    @property:[Json(name = "Sender")]
    val Sender: String,
){

}

data class StripeCreateProductSKUResponse(
    @property:[Json(name = "err")]
    val err: String,
    @property:[Json("stripe_sku_id")]
    val SKU: String
){
    companion object {
        fun fromJson(jsonObject: JsonObject): StripeCreateProductSKUResponse {
            val err = jsonObject.string("err") ?: ""
            val resultObject = jsonObject.obj("result")
            var SKU = ""
            if (resultObject != null){
                SKU = resultObject.string("stripe_sku_id") ?: ""
            }
            return StripeCreateProductSKUResponse(
                SKU = SKU,
                err = err
            )
        }
    }
}

data class StripCreatePaymentIntentRequest(
    @property:[Json(name = "SKUID")]
    val SKUID: String,
    @property:[Json(name = "Country")]
    val Country: String,
    @property:[Json(name = "Amount")]
    val amount: String,
    @property:[Json(name = "Sender")]
    val Sender: String
){}

data class StripeCreatePaymentIntentResponse(
    @property:[Json(name = "stripe_payment_intent_id")]
    val payment_intent_id: String,
    @property:[Json(name = "client_secret")]
    val client_secret: String,
    @property:[Json(name = "err")]
    val err: String
){
    companion object {
        fun fromJson(jsonObject: JsonObject): StripeCreatePaymentIntentResponse {
            val err = jsonObject.string("err") ?: ""
            val resultObject = jsonObject.obj("result")
            var payment_intent_id = ""
            var client_secret = ""
            if (resultObject != null){
                payment_intent_id = resultObject.string("stripe_payment_intent_id") ?: ""
                client_secret = resultObject.string("client_secrect") ?: ""
            }
            return StripeCreatePaymentIntentResponse(
                payment_intent_id = payment_intent_id,
                client_secret = client_secret,
                err = err
            )
        }
    }
}

data class StripeInfoResponse(
    @property:[Json(name = "Public_Key")]
    val publishableKey: String,
    @property:[Json(name = "Redirect_Uri")]
    val callbackUrl: String,
    @property:[Json(name = "ClientID")]
    val client_id: String,
    @property:[Json(name = "err")]
    val err: String
){
    companion object {
        fun fromJson(jsonObject: JsonObject): StripeInfoResponse {
            val err = jsonObject.string("err") ?: ""
            val resultObject = jsonObject.obj("result")
            var publishableKey = ""
            var callbackUrl = ""
            var client_id = ""
            if (resultObject != null){
                publishableKey = resultObject.string("Public_Key") ?: ""
                callbackUrl = resultObject.string("Redirect_Uri") ?: ""
                client_id = resultObject.string("ClientID") ?: ""
            }
            return StripeInfoResponse(
                publishableKey = publishableKey,
                callbackUrl = callbackUrl,
                client_id = client_id,
                err = err
            )
        }
    }
}

data class StripeOauthTokenRequest(
    @property: [Json(name = "Sender")]
    val sender: String,
    @property: [Json(name = "GrantType")]
    val GrantType: String,
    @property: [Json(name = "Code")]
    val code: String,
){}

data class StripeOauthTokenResponse(
    @property: [Json(name = "access_token")]
    val access_token: String,
    @property: [Json(name = "livemode")]
    val livemode: Boolean,
    @property: [Json(name = "refresh_token")]
    val refresh_token: String,
    @property: [Json(name = "token_type")]
    val token_type: String,
    @property: [Json(name = "stripe_publishable_key")]
    val stripe_publishable_key: String,
    @property: [Json(name = "stripe_user_id")]
    val stripe_user_id: String,
    @property: [Json(name = "scope")]
    val scope: String,
    @property: [Json(name = "err")]
    val err: String,
){

    companion object {
        fun fromJson(jsonObject: JsonObject): StripeOauthTokenResponse {
            val err = jsonObject.string("err") ?: ""
            val resultObject = jsonObject.obj("result")
            var access_token = ""
            var livemode = false
            var refresh_token = ""
            var token_type = ""
            var stripe_publishable_key = ""
            var stripe_user_id = ""
            var scope = ""
            if (resultObject != null){
                access_token = resultObject.string("access_token") ?: ""
                livemode = resultObject.boolean ("livemode") ?: false
                refresh_token = resultObject.string("refresh_token") ?: ""
                token_type = resultObject.string("token_type") ?: ""
                stripe_publishable_key = resultObject.string("stripe_publishable_key") ?: ""
                stripe_user_id = resultObject.string("stripe_user_id") ?: ""
                scope = resultObject.string("scope") ?: ""

            }
            return StripeOauthTokenResponse(
                access_token = access_token,
                livemode = livemode,
                refresh_token = refresh_token,
                token_type = token_type,
                stripe_publishable_key = stripe_publishable_key,
                stripe_user_id = stripe_user_id,
                scope = scope,
                err = err
            )
        }
    }
}


