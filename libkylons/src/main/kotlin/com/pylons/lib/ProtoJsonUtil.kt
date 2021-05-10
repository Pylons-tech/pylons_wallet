package com.pylons.lib
import com.google.protobuf.Any
import com.google.protobuf.ByteString
import com.google.protobuf.Message
import com.google.protobuf.MessageOrBuilder
import com.google.protobuf.TypeRegistry
import com.google.protobuf.util.JsonFormat
import com.pylons.lib.core.ICryptoHandler
import cosmos.tx.v1beta1.TxOuterClass
import pylons.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*


object ProtoJsonUtil {
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
            "/pylons.MsgCreateAccount"->{ return MsgCreateAccount.getDefaultInstance() }
            "/pylons.MsgCheckExecution"->{ return MsgCheckExecution.getDefaultInstance() }
            "/pylons.MsgCreateCookbook"->{ return MsgCreateCookbook.getDefaultInstance() }
            "/pylons.MsgCreateRecipe"->{ return MsgCreateRecipe.getDefaultInstance() }
            "/pylons.MsgCreateTrade"->{ return MsgCreateTrade.getDefaultInstance() }
            "/pylons.MsgDisableRecipe"->{ return MsgDisableRecipe.getDefaultInstance() }
            "/pylons.MsgDisableTrade"->{ return MsgDisableTrade.getDefaultInstance() }
            "/pylons.MsgEnableRecipe"->{ return MsgEnableRecipe.getDefaultInstance() }
            "/pylons.MsgExecuteRecipe"->{ return MsgExecuteRecipe.getDefaultInstance() }
            "/pylons.MsgEnableTrade"->{ return MsgEnableTrade.getDefaultInstance() }
            "/pylons.MsgFiatItem"->{ return MsgFiatItem.getDefaultInstance() }
            "/pylons.MsgFulfillTrade"->{ return MsgFulfillTrade.getDefaultInstance() }
            "/pylons.MsgGetPylons"->{ return MsgGetPylons.getDefaultInstance() }
            "/pylons.MsgGoogleIAPGetPylons"->{ return MsgGoogleIAPGetPylons.getDefaultInstance() }
            "/pylons.MsgSendCoins"->{ return MsgSendCoins.getDefaultInstance() }
            "/pylons.MsgUpdateItemString"->{ return MsgUpdateItemString.getDefaultInstance() }
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
     * Makes a new instance of message based on the json and the class
     * @param <T> is the class type
     * @param json is the json instance
     * @param clazz is the class instance
     * @return An instance of T based on the json values
     * @throws IOException if any error occurs
     */
    /*
    fun <T : Message?> fromJson(json: String?, clazz: Class<T>): T? {
        // https://stackoverflow.com/questions/27642021/calling-parsefrom-method-for-generic-protobuffer-class-in-java/33701202#33701202
        var builder: Builder? = null
        builder = try {
            // Since we are dealing with a Message type, we can call newBuilder()
            clazz.getMethod("newBuilder").invoke(null) as Builder
        } catch (e: IllegalAccessException) {
            return null
        } catch (e: IllegalArgumentException) {
            return null
        } catch (e: InvocationTargetException) {
            return null
        } catch (e: NoSuchMethodException) {
            return null
        } catch (e: SecurityException) {
            return null
        }

        // The instance is placed into the builder values
        JsonFormat.parser().ignoringUnknownFields().merge(json, builder)

        // the instance will be from the build
        return builder.build()
    }
     */

    class TxProtoBuilder {

        public var txRaw = TxOuterClass.TxRaw.newBuilder()


        fun addSignature(cryptoHandler: ICryptoHandler, signDoc: TxOuterClass.SignDoc ) {

            val signDocOutputStream = ByteArrayOutputStream()
            signDoc.writeTo(signDocOutputStream)
            val sig = cryptoHandler?.signature(signDocOutputStream.toByteArray())

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
            return Base64.getEncoder().encodeToString(txRawOutputStream.toByteArray())
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
                    "public_key": {"type_url":"/cosmos.crypto.secp256k1.PubKey","key":"$pubKey"},
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

            val registry: TypeRegistry = TypeRegistry.newBuilder().add(TxOuterClass.TxBody.getDescriptor())
                .add(MsgCreateAccount.getDescriptor())
                .build()
            val printer = JsonFormat.printer().usingTypeRegistry(registry)
            val parser = JsonFormat.parser().usingTypeRegistry(registry)

            val test_msg = MsgCreateAccount.newBuilder().setRequester("12123412341234").build()
            val test_body = TxOuterClass.TxBody.newBuilder().addMessages( Any.pack(test_msg)).build()
            val test_json = printer.print(test_body)


            val test_txBodyBuilder = TxOuterClass.TxBody.newBuilder()
            parser.ignoringUnknownFields().merge(body, test_txBodyBuilder)


            val msg = MsgCreateAccount.newBuilder().setRequester("12341234123").build()
            val desc = msg.descriptorForType

            val txBodyBuilder = TxOuterClass.TxBody.newBuilder()
            JsonFormat.parser().ignoringUnknownFields().merge(body, txBodyBuilder)
            val txBodyOutputStream = ByteArrayOutputStream()
            txBodyBuilder.build().writeTo(txBodyOutputStream)

            val txAuthBuilder = TxOuterClass.AuthInfo.newBuilder()
            JsonFormat.parser().ignoringUnknownFields().merge(authInfo, txAuthBuilder)
            val txAuthOutputStream = ByteArrayOutputStream()
            txAuthBuilder.build().writeTo(txAuthOutputStream)

            txRaw.setBodyBytes(ByteString.copyFrom(txBodyOutputStream.toByteArray()))
            txRaw.setAuthInfoBytes(ByteString.copyFrom(txAuthOutputStream.toByteArray()))
        }
    }


}