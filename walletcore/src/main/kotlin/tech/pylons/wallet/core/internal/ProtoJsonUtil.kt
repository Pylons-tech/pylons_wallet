package tech.pylons.wallet.core.internal 
import Pylonstech.pylons.pylons.Tx.* 
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.google.protobuf.*
import com.google.protobuf.util.JsonFormat
import tech.pylons.lib.core.ICryptoHandler
import tech.pylons.lib.types.hexToAscii
import cosmos.tx.v1beta1.TxOuterClass
//import pylons.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import org.spongycastle.util.encoders.Base64


object ProtoJsonUtil {

    val jsonProtoRegistry: TypeRegistry = TypeRegistry.newBuilder()
        //Tx type
        .add(TxOuterClass.TxBody.getDescriptor())
        .add(cosmos.crypto.secp256k1.Keys.PubKey.getDescriptor())
        .add(cosmos.base.v1beta1.CoinOuterClass.Coin.getDescriptor())
        .add(cosmos.base.v1beta1.CoinOuterClass.DecCoin.getDescriptor())
        .add(cosmos.base.v1beta1.CoinOuterClass.DecProto.getDescriptor())
        .add(cosmos.base.v1beta1.CoinOuterClass.IntProto.getDescriptor())
        //register pylons Msg Type
        .add(MsgCreateAccount.getDescriptor())
        //.add(MsgCheckExecution.getDescriptor())
        .add(MsgCreateCookbook.getDescriptor())
        .add(MsgCreateRecipe.getDescriptor())
        .add(MsgCreateTrade.getDescriptor())
        //.add(MsgDisableRecipe.getDescriptor())
        //.add(MsgDisableTrade.getDescriptor())
        //.add(MsgEnableRecipe.getDescriptor())
        //.add(MsgEnableTrade.getDescriptor())
        //.add(MsgGetPylons.getDescriptor())
        //.add(MsgSendCoins.getDescriptor())
        .add(MsgExecuteRecipe.getDescriptor())
        .add(MsgFulfillTrade.getDescriptor())
        //.add(MsgGoogleIAPGetPylons.getDescriptor())
        .add(MsgSendItems.getDescriptor())
        .add(MsgUpdateCookbook.getDescriptor())
        //.add(MsgUpdateItemString.getDescriptor())
        .add(MsgUpdateRecipe.getDescriptor())
        //response type
        .add(MsgCreateAccountResponse.getDescriptor())
        //.add(MsgCheckExecutionResponse.getDescriptor())
        .add(MsgCreateCookbookResponse.getDescriptor())
        .add(MsgCreateRecipeResponse.getDescriptor())
        .add(MsgCreateTradeResponse.getDescriptor())
        //.add(MsgDisableRecipeResponse.getDescriptor())
        //.add(MsgDisableTradeResponse.getDescriptor())
        //.add(MsgEnableRecipeResponse.getDescriptor())
        //.add(MsgEnableTradeResponse.getDescriptor())
        //.add(MsgGetPylonsResponse.getDescriptor())
        //.add(MsgSendCoinsResponse.getDescriptor())
        .add(MsgExecuteRecipeResponse.getDescriptor())
        .add(MsgFulfillTradeResponse.getDescriptor())
        //.add(MsgGoogleIAPGetPylonsResponse.getDescriptor())
        .add(MsgSendItemsResponse.getDescriptor())
        .add(MsgUpdateCookbookResponse.getDescriptor())
        //.add(MsgUpdateItemStringResponse.getDescriptor())
        .add(MsgUpdateRecipeResponse.getDescriptor())

        //newly added type
        //.add(MsgFiatItem.getDescriptor())
        .build()
    val jsonProtoPrinter = JsonFormat.printer().usingTypeRegistry(jsonProtoRegistry)
    val jsonProtoParser = JsonFormat.parser().usingTypeRegistry(jsonProtoRegistry)


    /**
     * Makes a Json from a given message or builder
     *
     * @param messageOrBuilder is the instance
     * @return The string representation
     * @throws IOException if any error occurs
     */
    fun toJson(builder: MessageOrBuilder): String? {
        return JsonFormat.printer().print(builder)
    }

    //Tierre; hard coded every msg here but we should find design pattern for it
    fun findTxProtoMatch(type: String): Message? {
        when(type) {
            "/Pylonstech.pylons.pylons.MsgCreateAccount"->{ return MsgCreateAccount.getDefaultInstance() }
            "/pylons.MsgCreateAccount"->{ return MsgCreateAccount.getDefaultInstance() }
            //"/pylons.MsgCheckExecution"->{ return MsgCheckExecution.getDefaultInstance() }
            "/pylons.MsgCreateCookbook"->{ return MsgCreateCookbook.getDefaultInstance() }
            "/pylons.MsgCreateRecipe"->{ return MsgCreateRecipe.getDefaultInstance() }
            "/pylons.MsgCreateTrade"->{ return MsgCreateTrade.getDefaultInstance() }
            //"/pylons.MsgDisableRecipe"->{ return MsgDisableRecipe.getDefaultInstance() }
            //"/pylons.MsgDisableTrade"->{ return MsgDisableTrade.getDefaultInstance() }
            //"/pylons.MsgEnableRecipe"->{ return MsgEnableRecipe.getDefaultInstance() }
            "/pylons.MsgExecuteRecipe"->{ return MsgExecuteRecipe.getDefaultInstance() }
            //"/pylons.MsgEnableTrade"->{ return MsgEnableTrade.getDefaultInstance() }
            //"/pylons.MsgFiatItem"->{ return MsgFiatItem.getDefaultInstance() }
            "/pylons.MsgFulfillTrade"->{ return MsgFulfillTrade.getDefaultInstance() }
            //"/pylons.MsgGetPylons"->{ return MsgGetPylons.getDefaultInstance() }
            //"/pylons.MsgGoogleIAPGetPylons"->{ return MsgGoogleIAPGetPylons.getDefaultInstance() }
            //"/pylons.MsgSendCoins"->{ return MsgSendCoins.getDefaultInstance() }
            //"/pylons.MsgUpdateItemString"->{ return MsgUpdateItemString.getDefaultInstance() }
            "/pylons.MsgUpdateCookbook"->{ return MsgUpdateCookbook.getDefaultInstance() }
            "/pylons.MsgUpdateRecipe"->{ return MsgUpdateRecipe.getDefaultInstance() }
            ""->{return null }
            null->{return null }
        }
        return null
    }
    fun fromJson(json:String?, type: String): Message? {
        val msg = findTxProtoMatch(type)
        if(msg != null) {
            val builder = msg.toBuilder()
            JsonFormat.parser().ignoringUnknownFields().merge(json, builder!!)
            return builder.build()
        }
        return null
    }

    /**
    * {
     *  "height":"30707",
     *  "txhash":"C8C06DDB0437666BF6531399F1B875443CB48F4513D5AC8B6398A9A37FC0F3B9",
     *  "data":"0A3D0A0E6372656174655F6163636F756E74122B0A207375636365737366756C6C79206372656174656420746865206163636F756E74120753756363657373","raw_log":"[{\"events\":[{\"type\":\"message\",\"attributes\":[{\"key\":\"action\",\"value\":\"create_account\"}]}]}]","logs":[{"events":[{"type":"message","attributes":[{"key":"action","value":"create_account"}]}]}],
     *  "gas_wanted":"400000","gas_used":"33100",
     *  "tx":{"type":"cosmos-sdk/StdTx",
     *  "value":{"msg":[{"type":"pylons/CreateAccount","value":{"Requester":"cosmos1f90s4purzzw0gxgcwuk64mcsukx44zv722zph6"}}],
     *  "fee":{"amount":[],"gas":"400000"},
     *  "signatures":[],"memo":"","timeout_height":"0"}},"timestamp":"2021-05-11T13:49:48Z"}
     */
    // this never works
    fun TxProtoResponseParser (response: String): String {
        val doc = Parser.default().parse(java.lang.StringBuilder(response)) as JsonObject
        val msgs = doc.obj("tx")?.obj("value")?.array<JsonObject>("msg")
        var type = ""
        msgs?.forEach {
            type = it.string("type").orEmpty()
        }

        val data = doc.string("data").orEmpty()


        val dataString = hexToAscii(data)
        var builder: MessageOrBuilder? = null
        /*
        when(type) {
            "pylons/CreateAccount"->{ builder = MsgCreateExecutionResponse.newBuilder().mergeFrom(dataString!!.toByteArray()).build() }
            "pylons/CheckExecution"->{ builder = MsgCheckExecutionResponse.newBuilder().mergeFrom(dataString!!.toByteArray()).build() }
            "pylons/CreateCookbook"->{ builder = MsgCreateCookbookResponse.newBuilder().mergeFrom(data.toByteArray()).build() }
            "pylons/CreateRecipe"->{ builder = MsgCreateRecipeResponse.newBuilder().mergeFrom(dataString!!.toByteArray()).build() }
            "pylons/CreateTrade"->{ builder = MsgCreateTradeResponse.newBuilder().mergeFrom(dataString!!.toByteArray()).build() }
            "pylons/DisableRecipe"->{ builder = MsgDisableRecipeResponse.newBuilder().mergeFrom(dataString!!.toByteArray()).build() }
            "pylons/DisableTrade"->{ builder = MsgDisableTradeResponse.newBuilder().mergeFrom(dataString!!.toByteArray()).build() }
            "pylons/EnableRecipe"->{ builder = MsgEnableRecipeResponse.newBuilder().mergeFrom(dataString!!.toByteArray()).build() }
            "pylons/ExecuteRecipe"->{ builder = MsgExecuteRecipeResponse.newBuilder().mergeFrom(dataString!!.toByteArray()).build() }
            "pylons/EnableTrade"->{ builder = MsgEnableTradeResponse.newBuilder().mergeFrom(dataString!!.toByteArray()).build() }
            "pylons/FiatItem"->{ builder = MsgFiatItemResponse.newBuilder().mergeFrom(dataString!!.toByteArray()).build() }
            "pylons/FulfillTrade"->{ builder = MsgFulfillTradeResponse.newBuilder().mergeFrom(dataString!!.toByteArray()).build() }
            "pylons/GetPylons"->{ builder = MsgGetPylonsResponse.newBuilder().mergeFrom(dataString!!.toByteArray()).build() }
            "pylons/GoogleIAPGetPylons"->{ builder = MsgGoogleIAPGetPylonsResponse.newBuilder().mergeFrom(dataString!!.toByteArray()).build() }
            "pylons/SendCoins"->{ builder = MsgSendCoinsResponse.newBuilder().mergeFrom(dataString!!.toByteArray()).build() }
            "pylons/UpdateItemString"->{ builder = MsgUpdateItemStringResponse.newBuilder().mergeFrom(dataString!!.toByteArray()).build() }
            "pylons/UpdateCookbook"->{ builder = MsgUpdateCookbookResponse.newBuilder().mergeFrom(dataString!!.toByteArray()).build() }
            "pylons/UpdateRecipe"->{ builder = MsgUpdateRecipeResponse.newBuilder().mergeFrom(dataString!!.toByteArray()).build() }
            ""->{}
        }
         */

        if (builder != null) {
            return jsonProtoPrinter.print(builder)
        }
        return ""
    }



    class TxProtoBuilder {

        var txRaw = TxOuterClass.TxRaw.newBuilder()


        fun addSignature(cryptoHandler: ICryptoHandler, signDoc: TxOuterClass.SignDoc ) {

            val signDocOutputStream = ByteArrayOutputStream()
            signDoc.writeTo(signDocOutputStream)
            val sig = cryptoHandler.signature(signDocOutputStream.toByteArray())

            this.txRaw.addSignatures(ByteString.copyFrom(sig))
        }

        fun signDoc(accountNumber: Long, chain_id:String):TxOuterClass.SignDoc? {
            val builder = TxOuterClass.SignDoc
                .newBuilder()
                .setBodyBytes(txRaw.bodyBytes)
                .setAuthInfoBytes(txRaw.authInfoBytes)
                .setChainId(chain_id)
                .setAccountNumber(accountNumber)

            return builder.build()
        }

        fun txBytes(): String? {
            val txRawOutputStream = ByteArrayOutputStream()
            txRaw.build().writeTo(txRawOutputStream)
            return Base64.toBase64String(txRawOutputStream.toByteArray())
        }

        fun buildTxbody(messages:String): String {
            return """
            {
                "messages": $messages,
                "memo":"",
                "timeout_height":"0",
                "extension_options":[],
                "non_critical_extension_options":[]
            }
            """.trimIndent()
        }

        fun buildAuthInfo(pubKey:String, sequence: Long, gas_limit: Long):String {
            return """
            {
                "signer_infos":[ {
                    "public_key": {"@type":"/cosmos.crypto.secp256k1.PubKey","key":"$pubKey"},
                    "mode_info": {
                        "single": {
                            "mode": "SIGN_MODE_DIRECT"
                        }
                    },
                    "sequence": $sequence
                    }
                ],
                "fee":{
                    "amount":[],
                    "gas_limit":"$gas_limit",
                    "payer":"",
                    "granter":""
                }
            }
            """.trimIndent()
        }


        fun buildProtoTxBuilder(body: String, authInfo: String) {


            val txBodyBuilder = TxOuterClass.TxBody.newBuilder()
            jsonProtoParser.ignoringUnknownFields().merge(body, txBodyBuilder)
            val txBodyOutputStream = ByteArrayOutputStream()
            txBodyBuilder.build().writeTo(txBodyOutputStream)

            val txAuthBuilder = TxOuterClass.AuthInfo.newBuilder()
            jsonProtoParser.ignoringUnknownFields().merge(authInfo, txAuthBuilder)
            val txAuthOutputStream = ByteArrayOutputStream()
            txAuthBuilder.build().writeTo(txAuthOutputStream)

            txRaw.bodyBytes = ByteString.copyFrom(txBodyOutputStream.toByteArray())
            txRaw.authInfoBytes = ByteString.copyFrom(txAuthOutputStream.toByteArray())
        }
    }




}