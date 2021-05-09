package com.pylons.lib
import com.google.protobuf.Message
import com.google.protobuf.MessageOrBuilder
import com.google.protobuf.util.JsonFormat
import java.lang.reflect.InvocationTargetException
import com.google.protobuf.AbstractMessage.Builder
import com.google.protobuf.ByteString
import com.pylons.lib.core.ICore
import com.pylons.lib.logging.LogEvent
import com.pylons.lib.logging.LogTag
import com.pylons.lib.logging.Logger
import cosmos.crypto.secp256k1.Keys
import cosmos.tx.v1beta1.TxOuterClass
import org.spongycastle.util.encoders.Base64.toBase64String
import pylons.Tx
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
            "/pylons.MsgCreateAccount"->{ return Tx.MsgCreateAccount.getDefaultInstance() }
            "/pylons.MsgCheckExecution"->{ return Tx.MsgCheckExecution.getDefaultInstance() }
            "/pylons.MsgCreateCookbook"->{ return Tx.MsgCreateCookbook.getDefaultInstance() }
            "/pylons.MsgCreateRecipe"->{ return Tx.MsgCreateRecipe.getDefaultInstance() }
            "/pylons.MsgCreateTrade"->{ return Tx.MsgCreateTrade.getDefaultInstance() }
            "/pylons.MsgDisableRecipe"->{ return Tx.MsgDisableRecipe.getDefaultInstance() }
            "/pylons.MsgDisableTrade"->{ return Tx.MsgDisableTrade.getDefaultInstance() }
            "/pylons.MsgEnableRecipe"->{ return Tx.MsgEnableRecipe.getDefaultInstance() }
            "/pylons.MsgExecuteRecipe"->{ return Tx.MsgExecuteRecipe.getDefaultInstance() }
            "/pylons.MsgEnableTrade"->{ return Tx.MsgEnableTrade.getDefaultInstance() }
            "/pylons.MsgFiatItem"->{ return Tx.MsgFiatItem.getDefaultInstance() }
            "/pylons.MsgFulfillTrade"->{ return Tx.MsgFulfillTrade.getDefaultInstance() }
            "/pylons.MsgGetPylons"->{ return Tx.MsgGetPylons.getDefaultInstance() }
            "/pylons.MsgGoogleIAPGetPylons"->{ return Tx.MsgGoogleIAPGetPylons.getDefaultInstance() }
            "/pylons.MsgSendCoins"->{ return Tx.MsgSendCoins.getDefaultInstance() }
            "/pylons.MsgUpdateItemString"->{ return Tx.MsgUpdateItemString.getDefaultInstance() }
            "/pylons.MsgUpdateCookbook"->{ return Tx.MsgUpdateCookbook.getDefaultInstance() }
            "/pylons.MsgUpdateRecipe"->{ return Tx.MsgUpdateRecipe.getDefaultInstance() }
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
        companion object {
        }


        public var txRaw = TxOuterClass.TxRaw.newBuilder()


        fun addSignature(privKey:Keys.PrivKey, signDoc: TxOuterClass.SignDoc ) {
            val cryptoHandler = ICore.current?.engine?.cryptoHandler

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

        fun txBytes(txraw: TxOuterClass.TxRaw): String? {
            val txRawOutputStream = ByteArrayOutputStream()
            txraw.writeTo(txRawOutputStream)
            return Base64.getEncoder().encodeToString(txRawOutputStream.toByteArray())
        }


        fun buildProtoTxBuilder(body: String, authInfo: String) {

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