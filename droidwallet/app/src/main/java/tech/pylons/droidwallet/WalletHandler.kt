package tech.pylons.droidwallet

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.startup.Initializer
import kotlinx.coroutines.*
import tech.pylons.lib.Wallet
import tech.pylons.lib.types.Cookbook
import tech.pylons.lib.types.Profile
import tech.pylons.lib.types.Transaction
import tech.pylons.lib.types.tx.recipe.*
import java.lang.ref.WeakReference

class WalletHandler : Initializer<Wallet> {

    companion object {
        private const val appName = BuildConfig.APP_NAME
        private const val appPkgName = BuildConfig.APPLICATION_ID
        private const val TAG: String = "Pylons/$appName"

        /**
         * @DoNotModify
         *
         * The wallet object which is instantiated by this initializer when the app starts.
         */
        private var wallet: Wallet? = null

        /**
         * @DoNotModify
         *
         * A weak reference to the instance of IPC service connection with the Wallet-UI.
         */
        private var walletConnection: WeakReference<IpcServiceConnection>? = null

        /**
         * A mutable live data of the Cookbook for this client app.
         */
        private var userCookbook = MutableLiveData<Cookbook?>()

        /**
         * A mutable live data of the wallet user's profile.
         */
        private var userProfile = MutableLiveData<Profile?>()

        /**
         * A mutable list of all created NFTs.
         */
        var userNfts = mutableListOf<Recipe>()


        /**
         * @DoNotModify
         *
         * Getter for the wallet instance.
         */
        fun getWallet(): Wallet {
            return wallet!!
        }

        /**
         * @DoNotModify
         *
         * Getter for the ipc connection with the Wallet app.
         */
        fun getWalletConnection(): IpcServiceConnection {
            return walletConnection!!.get()!!
        }

        fun ifWalletExists(context: Context): Boolean = try {
            val pm: PackageManager = context.packageManager
            pm.getPackageInfo("tech.pylons.wallet", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

        fun getLiveUserCookbook(): LiveData<Cookbook?> = userCookbook

        fun getUserCookbook(): Cookbook? {
            return userCookbook.value
        }

        fun setUserCookbook(cookbook: Cookbook?) {
            userCookbook.postValue(cookbook)
        }

        fun getLiveUserProfile(): LiveData<Profile?> = userProfile

        fun getUserProfile(): Profile? {
            return userProfile.value
        }

        fun setUserProfile(profile: Profile?) {
            userProfile.postValue(profile)
        }

        /**
         * CreateNft
         * @param context
         * @param name
         * @param price
         * @param royalty
         * @param quantity
         * @param url
         * @param description
         * @param royalty
         * @param quantity
         * @param url
         * @param description
         */
        fun createNft(
            context: Context,
            name: String,
            price: String,
            currency: String,
            royalty: String,
            quantity: Long,
            url: String,
            description: String,
            callback: (Boolean) -> Unit
        ) {
            //if cookbook not created, create cookbook
            if (getUserCookbook() == null) {
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Portfolio not initiated",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    createAutoCookbook(context, getUserProfile())
                }
                callback(false)
                return
            }

            val nftId = name.replace(" ", "_") //NFT Item ID
            //val NFT_recipe_description = "this recipe is to issue NFT: $name on NFT Cookbook: ${userCookbook?.name!!}"

            runBlocking {
                launch {
                    //this creation is wrong
                    getWallet().createRecipe(
                        name = name,
                        itemInputs = listOf(),          // this field is not necessary in NFT creation
                        cookbook = getUserCookbook()?.id!!,  //NFT creator's Cookbook ID
                        description = description,      //NFT_recipe_description,      //NFT Recipe Description
                        blockInterval = 0,
                        coinInputs = listOf(
                            CoinInput(
                                coin = currency, //"pylon, usd",
                                count = if(currency == "USD") {
                                    (price.toDouble() * 100).toLong() //support cent
                                }else{
                                   price.toDouble().toLong() //remove .value
                                }
                            )
                        ), // NFT price definition

                        outputTable = EntriesList(
                            coinOutputs = listOf(),         //in NFT creation, coinOutput is inavailable
                            itemModifyOutputs = listOf(),   //in NFT creation, itemModifyOutputs is unecessary
                            itemOutputs = listOf(           //NFT definition
                                //NFT description
                                ItemOutput(
                                    id = nftId,            //NFT Item ID made with NFT Name
                                    doubles = listOf(
                                        //Residual% definition
                                        //Pls confirm if this is the right place for Residual defintion
                                        DoubleParam(
                                            key = "Residual%", //this should be reserved keyword for NFT
                                            program = "",
                                            rate = "1.0",
                                            weightRanges = listOf(
                                                DoubleWeightRange(
                                                    upper = royalty, //"${royalty}000000000000000000",  //20%
                                                    lower = royalty,
                                                    weight = 1
                                                )
                                            )
                                        )
                                    ),
                                    longs = listOf(
                                        //NFT Quantity
                                        //Pls confirm if this is the right place for NFT Quantity defintion
                                        LongParam(
                                            key = "Quantity",
                                            program = "",
                                            rate = "1.0",
                                            weightRanges = listOf(
                                                LongWeightRange(
                                                    upper = quantity.toString(), //quantity 10 copies
                                                    lower = quantity.toString(),
                                                    weight = 1
                                                )
                                            )
                                        )
                                    ),
                                    strings = listOf(
                                        //pls confirm this field
                                        StringParam(
                                            rate = "1.0",
                                            key = "Name",
                                            value = name,
                                            program = ""
                                        ),
                                        StringParam(
                                            rate = "1.0",
                                            key = "NFT_URL",
                                            value = url,
                                            program = ""
                                        ),
                                        StringParam(
                                            rate = "1.0",
                                            key = "Description",
                                            value = description,
                                            program = ""
                                        ),
                                        StringParam(
                                            rate = "1.0",
                                            key = "Currency",
                                            value = currency,
                                            program = ""
                                        ),
                                        StringParam(
                                            rate = "1.0",
                                            key = "Price",
                                            value = if(currency == "USD") {
                                                (price.toDouble() * 100).toLong().toString() //support cent
                                            }else{
                                                price.toDouble().toLong().toString() //remove .value
                                            },
                                            program = ""
                                        )
                                    ),
                                    transferFee = 0 //transfer Fee should be defined in NFT creation, currently set to 0
                                ) // NFT entry
                            )
                        ),
                        outputs = listOf(
                            WeightedOutput(
                                entryIds = listOf(
                                    nftId
                                ),
                                weight = "1"
                            )
                        ),
                        extraInfo = listOf()
                    ) {
                        val transaction = it
                        var ret = false
                        Log.i("onNftRetrieved()", "Response from Wallet: $it")
                        CoroutineScope(Dispatchers.IO).launch {
                            when (it) {
                                null -> {
                                    //if this is correct logic?
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            context,
                                            "Creating Nft Cancelled!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                                else -> {
                                    //retrieve NFT list
                                    listNfts(context)
                                    withContext(Dispatchers.Main) {

                                        //this part should be considered
                                        val id = transaction?.id
                                        val code = transaction?.code
                                        val rawLog = transaction?.raw_log

                                        if (code != Transaction.ResponseCode.OK) {
                                            Toast.makeText(
                                                context,
                                                "Creating NFT failed: $rawLog",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            return@withContext
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Successfully Created Nft!",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            ret = true
                                            return@withContext
                                        }
                                    }
                                }
                            }

                            callback(ret)
                        }
                    }
                }
            }
        }

        /**
         *  Wallet API fetchProfile
         *  @param context
         *  @param address if null, default address
         *  @callback Boolean true if profile exists, else false
         */
        fun fetchProfile(context: Context?, address: String?, callback: (Boolean) -> Unit) {
            runBlocking {
                launch {
                    getWallet().fetchProfile(address) { profile ->
                        val ret: Boolean = when (profile) {
                            null -> {
                                false
                            }
                            else -> {
                                val address = profile.address
                                val items = profile.items
                                val coins = profile.coins
                                //liveUserData.postValue(profile)
                                setUserProfile(profile)
                                true
                            }
                        }

                        callback.invoke(ret)
                    }
                }
            }
        }


        /**
         * Wallet API listCookbooks
         * Cookbook naming rule: {appName}_autocookbook_{profileaddress}
         */
        fun listCookbooks(context: Context?, callback: ((Cookbook?) -> Unit)? = null) {
            CoroutineScope(Dispatchers.IO).launch {
                getWallet().listCookbooks {

                    val cookbooks = it
                    var cookbook:Cookbook? = null

                    if (cookbooks.isNotEmpty()) {
                        cookbook = cookbooks.find { cb ->
                            cb.sender == getUserProfile()?.address && cb.id.startsWith(
                                appName,
                                true
                            )
                        }
                    } else {
                        cookbook = null
                    }
                    setUserCookbook(cookbook)

                    callback?.invoke(cookbook)
                }
            }
        }

        fun buyPylons(context: Context?) {
            CoroutineScope(Dispatchers.IO).launch {
                //loading screen launch
                getWallet().buyPylons(onBuyPylons(context))
            }
        }

        fun onBuyPylons(context: Context?): (Transaction?) -> Unit {
            //loading screen dismiss
            return {
                val transaction = it
                if (transaction != null) {

                }
            }
        }

        /**
         * Wallet API listRecipes
         */
        fun listNfts(context: Context?, callback: ((List<Recipe>) -> Unit)? = null) {
            // recipes
            runBlocking {
                launch {
                    getWallet().listRecipesBySender { nfts ->
                        if (nfts.isNotEmpty()) {
                            userNfts.clear()
                            nfts.forEach { rcp ->
                                if (rcp.sender == getUserProfile()?.address)
                                    userNfts.add(rcp)
                            }
                        }

                        callback?.invoke(userNfts.toList())
                    }

                }

            }
        }

        fun createAutoCookbook(
            context: Context?,
            profile: Profile?,
            callback: ((Boolean) -> Unit)? = null
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                var ret: Boolean
                var message: String

                getWallet().createAutoCookbook(
                    profile!!,
                    appName
                ) { transaction ->
                    when (transaction) {
                        null -> {
                            message = "Create portfolio failed"
                            ret = false
                        }
                        else -> {
                            if (transaction.code == Transaction.ResponseCode.OK) {
                                message = "Create Portfolio Success"
                                ret = true
                            } else {
                                //transaction failed
                                message = "Create Portfolio failed. Error: ${transaction.raw_log}"
                                ret = false
                            }
                        }
                    }
                    //reject or exception handling
                    CoroutineScope(Dispatchers.IO).launch {

                        withContext(Dispatchers.Main) {
                            if (context != null) {
                                Toast.makeText(
                                    context,
                                    message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            callback?.invoke(ret)
                        }
                    }
                }
            }
        }

        fun onCreateAutoCookbook(context: Context?): (Transaction?) -> Unit {
            return { transaction ->
                when (transaction) {
                    null -> {
                        //reject or exception handling
                        if (context != null) {
                            CoroutineScope(Dispatchers.IO).launch {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        "Create Portfolio failed",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }
                    else -> {
                        if (transaction.code == Transaction.ResponseCode.OK) {
                            //transaction ok
                            //retrieve NFT list
                            CoroutineScope(Dispatchers.IO).launch {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        "Create Portfolio Success",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                            listCookbooks(context)
                        } else {
                            //transaction failed
                            if (context != null) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            context,
                                            "Create Portfolio failed. Error: ${transaction.raw_log}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        fun initUserInfo(context: Context?) {
            CoroutineScope(Dispatchers.IO).launch {
                if (getUserProfile() == null) {
                    //testCreateNft(context)
                    fetchProfile(context, null) {
                        when (it) {
                            true -> {
                                //retrieved profile
                            }
                            false -> {
                                //no profile
                            }
                        }
                    }

                }
            }
        }

        fun executeRecipe(
            recipe: String,
            cookbook: String,
            itemInputs: List<String>,
            context: Context
        ) {

            CoroutineScope(Dispatchers.IO).launch {
                getWallet().executeRecipe(
                    recipe,
                    cookbook,
                    itemInputs,
                    callback = onExecuteRecipe(context)
                )
            }
        }

        fun onExecuteRecipe(context: Context): (Transaction?) -> Unit {
            return {

            }
        }

        fun getWebLink(recipeName: String, recipeId: String): String {
            return getWallet().generateWebLink(recipeName, recipeId)
        }

    }

    /**
     * @DoNotModify
     *
     * Entry point to instantiate the Wallet Connection and then returns the Wallet instance.
     *
     * - Initialize AndroidWallet instance
     * - Bind IPC service connection with the Wallet-UI
     */
    override fun create(context: Context): Wallet {
        if (wallet == null) {
            wallet = Wallet.android()
        }

        walletConnection = WeakReference(IpcServiceConnection(context))
        if (ifWalletExists(context)) {
            walletConnection!!.get()!!.bind() // do bind here
        } else {
            Toast.makeText(context, "Wallet not installed.", Toast.LENGTH_LONG).show()
        }

        return wallet as Wallet
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }

}
