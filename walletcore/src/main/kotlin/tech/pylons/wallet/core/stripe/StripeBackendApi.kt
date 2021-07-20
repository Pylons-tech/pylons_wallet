package tech.pylons.wallet.core.stripe

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import tech.pylons.wallet.core.internal.HttpWire
import tech.pylons.lib.klaxon
import tech.pylons.lib.types.stripe.*
import java.lang.StringBuilder

class StripeBackendApi(val BaseUrl: String) {
    companion object {
        const val URL_STRIPE_CREATEPRODUCTSKU = "stripe_create_product_sku"
        const val URL_STRIPE_CREATEPAYMENTINTENT = "stripe_create_payment_intent"
        const val URL_STRIPE_STRIPEINFO = "stripe_info"
        const val URL_STRIPE_OAUTHTOKEN = "stripe_oauth_token"
    }

    /**
     * STRIPE API to create PaymentIntent
     *
     * URL: "stripe_create_payment_intent"
     * METHOD: POST
     *
     * REQUEST: {
     *  Amount : String,    //usd amount in cent
     *  Country: String,    //customer's country ?
     *  SKUID: String,      //sku id of target purchase item
     *  Sender:String       //wallet address of sender
     *  }
     *
     * RESPONSE: {
     *  err: "",
     *  result: {
     *      stripe_payment_intent_id: String,   //payment intent id
     *      client_secret: String               //client secret
     *  }
     * }
     */
    fun StripeCreatePaymentIntentBackendApi(req: StripCreatePaymentIntentRequest): StripeCreatePaymentIntentResponse? {
        val response = HttpWire.post("${BaseUrl}${URL_STRIPE_CREATEPAYMENTINTENT}", klaxon.toJsonString(req))
        val jsonObj = (Parser.default().parse(StringBuilder(response)) as JsonObject)
        return StripeCreatePaymentIntentResponse.fromJson(jsonObj)
    }

    /**
     * STRIPE API to retrieve OAUTH TOKEN
     *
     * URL: "stripe_oauth_token"
     * METHOD: POST
     *
     * REQUEST: {
     *  Sender : String,                //wallet address of sender
     *  GrantType: String,              //GrantType - default value: "authorization_code"
     *  Code: String,                   //oauth ret code
     *  }
     *
     * RESPONSE: {
     *  err: "",
     *  result: {
     *      access_token: String,
     *      livemode: String,
     *      refresh_token: String,
     *      token_type: String,
     *      stripe_publishable_key: String,
     *      stripe_user_id: String,     //connected account client id
     *      scope: String
     *  }
     * }
     */
    fun StripeOauthTokenBackendApi(req: StripeOauthTokenRequest): StripeOauthTokenResponse? {
        val response = HttpWire.post("${BaseUrl}${URL_STRIPE_OAUTHTOKEN}", klaxon.toJsonString(req))
        val jsonObj = (Parser.default().parse(StringBuilder(response)) as JsonObject)
        return StripeOauthTokenResponse.fromJson(jsonObj)
    }

    /**
     * STRIPE API to retrieve CREATE stripe_create_product_sku SKU
     *
     * URL: "stripe_create_product_sku"
     * METHOD: POST
     *
     * REQUEST: {
     *  Name : String,              //product name
     *  Description: String,        //product description
     *  Images: List<String>,       //product image list
     *  Attributes: List<String>,   //product attribute
     *  Price: String,              //product price
     *  Currency: String,           //product currency
     *  Inventory: StripeInventory, //{type: "finite", quantity:"100" }
     *  val ClientId: String,
     *  val Sender: String,     //wallet address of sender
     *  }
     *
     * RESPONSE: {
     *  err: "",
     *  result: {
     *      stripe_sku_id: String   //sku id of the current product
     *  }
     * }
     */
    fun StripeCreateProductSKUBackendApi(req: StripeCreateProductSKURequest): StripeCreateProductSKUResponse? {
        val response = HttpWire.post("${BaseUrl}${URL_STRIPE_CREATEPRODUCTSKU}", klaxon.toJsonString(req))
        val jsonObj = (Parser.default().parse(StringBuilder(response)) as JsonObject)
        return StripeCreateProductSKUResponse.fromJson(jsonObj)
    }

    /**
     *  * STRIPE API to retrieve Stripe Info
     *
     * URL: "stripe_info"
     * METHOD: GET
     *
     * RESPONSE: {
     *  err: "",
     *  result: {
            Public_Key : String,    //Stripe publishable key
            Redirect_Uri: String,   //Stripe redirect_uri
            ClientID: String        //Stripe client_id
     *  }
     * }
     */
    fun StripeInfoBackendApi(): StripeInfoResponse? {
        val response = HttpWire.get("${BaseUrl}${URL_STRIPE_STRIPEINFO}")
        val jsonObj = (Parser.default().parse(StringBuilder(response)) as JsonObject)
        return StripeInfoResponse.fromJson(jsonObj)
    }
}