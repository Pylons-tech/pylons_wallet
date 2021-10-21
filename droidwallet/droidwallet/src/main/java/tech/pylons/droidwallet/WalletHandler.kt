package tech.pylons.droidwallet

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import tech.pylons.ipc.DroidIpcWire
import tech.pylons.lib.Wallet
import tech.pylons.lib.types.Cookbook
import tech.pylons.lib.types.Profile
import tech.pylons.lib.types.Transaction
import tech.pylons.lib.types.tx.recipe.*
import tech.pylons.lib.types.tx.Coin
import java.lang.ref.WeakReference
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class WalletHandler {

    interface WalletCallback {
        fun onWalletConnected()
        fun onWalletConnectFailed()
    }

    companion object {

        private const val TAG = "PylonsSDK"

        private lateinit var appId: String
        private lateinit var appName: String

        private var wallet: Wallet? = null

        private var walletConnection: WeakReference<IpcServiceConnection>? = null

        private var walletCallback: WalletCallback? = null

        /**
         * A mutable live data of the wallet user's profile.
         */
        private var userProfile = MutableLiveData<Profile?>()

        /**
         * A mutable live data of the Cookbook for this client app.
         */
        private var userCookbook = MutableLiveData<Cookbook?>()

        /**
         * A mutable list of all created NFTs.
         */
        private var userNfts = mutableListOf<Recipe>()


        private fun getWalletConnection(): IpcServiceConnection {
            return walletConnection!!.get()!!
        }

        private fun ifWalletExists(context: Context): Boolean = try {
            val pm: PackageManager = context.packageManager
            pm.getPackageInfo("tech.pylons.wallet", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

        fun generateString(): String {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss")
                return current.format(formatter)
            } else {
                val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
                val currentDate = sdf.format(Date())
                return currentDate
            }

        }
        /**
         * Getter for the wallet instance
         */
        fun getWallet(): Wallet {
            return wallet!!
        }

        /**
         * Set up connection to the Wallet
         *
         * - set appId & appName
         * - initialize AndroidWallet instance
         * - bind IPC service connection with the Wallet-UI
         */
        fun setup(context: Context, appId: String, appName: String, callback: WalletCallback?) {
            this.appName = appName
            this.appId = appId
            this.walletCallback = callback

            if (wallet == null) {
                wallet = Wallet.android()
            }

            walletConnection = WeakReference(IpcServiceConnection(context))
            if (ifWalletExists(context)) {
                walletConnection!!.get()!!.bind() // do bind here
            } else {
                Toast.makeText(context, "Wallet not installed.", Toast.LENGTH_LONG).show()
            }
        }

        fun getLiveUserProfile(): LiveData<Profile?> = userProfile

        fun setUserProfile(profile: Profile?) {
            userProfile.postValue(profile)
        }

        fun getUserProfile(): Profile? {
            return userProfile.value
        }

        fun getLiveUserCookbook(): LiveData<Cookbook?> = userCookbook

        fun getUserCookbook(): Cookbook? {
            return userCookbook.value
        }

        fun setUserCookbook(cookbook: Cookbook?) {
            userCookbook.postValue(cookbook)
        }

        fun getUserNfts(): MutableList<Recipe> {
            return userNfts
        }

        /**
         *  Wallet API fetchProfile
         *  @param context
         *  @param address if null, default address
         *  @callback Boolean true if profile exists, else false
         */
        fun fetchProfile(context: Context?, address: String?, callback: (Boolean) -> Unit) {
            wallet?.fetchProfile(address) { profile ->
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

        fun createAutoCookbook(
            context: Context?,
            profile: Profile?,
            callback: ((Boolean) -> Unit)? = null
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                var ret: Boolean
                var message: String

                wallet?.createAutoCookbook(
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

        private fun onCreateAutoCookbook(context: Context?): (Transaction?) -> Unit {
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

        /**
         * Wallet API listCookbooks
         * Cookbook naming rule: {appName}_autocookbook_{profileaddress}
         */
        fun listCookbooks(context: Context?, callback: ((Cookbook?) -> Unit)? = null) {
            CoroutineScope(Dispatchers.IO).launch {
                wallet?.listCookbooks {

                    val cookbooks = it
                    var cookbook: Cookbook? = null

                    if (cookbooks.isNotEmpty()) {
                        cookbook = cookbooks.find { cb ->
                            cb.Creator == getUserProfile()?.address && cb.id.startsWith(
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

        /*
        private fun onListCookbooks(context:Context?): (List<Cookbook>)->Unit {
            return {
                //get all cookbooks
                val cookbooks = it

                if (cookbooks.isNotEmpty()) {
                    Companion.userCookbooks.clear()
                    cookbooks.forEach {
                        if (it.Creator == Companion.userProfile?.address)
                            Companion.userCookbooks.add(it)
                    }
                }

                //get Easel Cookbook
                //second cookbook creation always fails wtf.
                //val Easel_cookbook_name = "${appName}_autocookbook_${userProfile?.address}"
                //Companion.userCookbook = Companion.userCookbooks.find{
                //    it.name == Easel_cookbook_name
                //}
                if (Companion.userCookbooks.isNotEmpty()) {
                    Companion.userCookbook = Companion.userCookbooks.get(0)
                }

                if (Companion.userCookbook != null) {
                    //retrieve NFT list
                    listNfts(context)
                } else {
                    //this means not cookbook created for this user
                    //create cookbook
                    createAutoCookbook(context, Companion.userProfile)
                }
            }
        }
         */

        /**
         * Wallet API listRecipes
         */
        fun listNfts(context: Context?, callback: ((List<Recipe>) -> Unit)? = null) {
            // recipes
            runBlocking {
                launch {
                    wallet?.listRecipes { nfts ->
                        if (nfts.isNotEmpty()) {
                            userNfts.clear()
                            nfts.forEach { rcp ->
                                wallet?.listCookbooks { cookbooks ->
                                    if(cookbooks.isNotEmpty()){
                                        cookbooks.forEach{
                                            if(it.Creator == getUserProfile()?.address){
                                                if (rcp.cookbookId == it.id){
                                                    userNfts.add(rcp)
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        }

                        callback?.invoke(userNfts.toList())
                    }
                }
            }
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
            imageWidth: Long,
            imageHeight: Long,
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

            val NFT_id = name.replace(" ", "_") //NFT Item ID
            //val NFT_recipe_description = "this recipe is to issue NFT: $name on NFT Cookbook: ${userCookbook?.name!!}"

            runBlocking {
                launch {
                    //this creation is wrong
                    wallet?.createRecipe(
                        creator = getUserCookbook()?.Creator!!,
                        version = getUserCookbook()?.version!!,
                        id = getUserCookbook()?.Creator!! + "_" + generateString(),
                        name = name,
                        itemInputs = listOf(),          // this field is not necessary in NFT creation
                        cookbook = getUserCookbook()?.id!!,  //NFT creator's Cookbook ID
                        description = description,      //NFT_recipe_description,      //NFT Recipe Description
                        coinInputs = listOf(
                            CoinInput(
                                listOf(
                                    Coin(currency, if (currency == "USD") {
                                        (price.toDouble() * 100).toLong() //support cent
                                    } else {
                                        price.toDouble().toLong() //remove .value
                                    })
                                )
                            )
                        ), // NFT price definition

                        entries = EntriesList(
                            coinOutputs = listOf(),         //in NFT creation, coinOutput is inavailable
                            itemModifyOutputs = listOf(),   //in NFT creation, itemModifyOutputs is unecessary
                            itemOutputs = listOf(           //NFT definition
                                //NFT description
                                ItemOutput(
                                    id = NFT_id,            //NFT Item ID made with NFT Name
                                    doubles = listOf(
                                        //Residual% definition
                                        //Pls confirm if this is the right place for Residual defintion
                                        DoubleParam(
                                            key = "Residual", //this should be reserved keyword for NFT
                                            program = "1",
                                            rate = "1",
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
                                            program = "1",
                                            rate = "1",
                                            weightRanges = listOf(
                                                IntWeightRange(
                                                    upper = quantity, //quantity 10 copies
                                                    lower = quantity,
                                                    weight = 1
                                                )
                                            )
                                        ),
                                        LongParam(
                                            rate = "1",
                                            key = "Width",
                                            weightRanges = listOf(
                                                IntWeightRange(
                                                    upper = imageWidth,
                                                    lower =imageWidth,
                                                    weight = 1
                                                )
                                            ),
                                            program = ""
                                        ),
                                        LongParam(
                                            rate = "1",
                                            key = "Height",
                                            weightRanges = listOf(
                                                IntWeightRange(
                                                    upper = imageHeight,
                                                    lower =imageHeight,
                                                    weight = 1
                                                )
                                            ),
                                            program = "1"
                                        )
                                    ),
                                    strings = listOf(
                                        //pls confirm this field
                                        StringParam(
                                            rate = "1",
                                            key = "Name",
                                            value = name,
                                            program = ""
                                        ),
                                        StringParam(
                                            rate = "1",
                                            key = "NFT_URL",
                                            value = url,
                                            program = ""
                                        ),
                                        StringParam(
                                            rate = "1",
                                            key = "Description",
                                            value = description,
                                            program = ""
                                        ),
                                        StringParam(
                                            rate = "1",
                                            key = "Currency",
                                            value = currency,
                                            program = ""
                                        ),
                                        StringParam(
                                            rate = "1",
                                            key = "Price",
                                            value = if (currency == "USD") {
                                                (price.toDouble() * 100).toLong()
                                                    .toString() //support cent
                                            } else {
                                                price.toDouble().toLong().toString() //remove .value
                                            },
                                            program = "1"
                                        )
                                    ),
                                    mutableStrings = listOf(), 
                                    transferFee = listOf(Coin("upylon", 0)), //transfer Fee should be defined in NFT creation, currently set to 0 
                                    tradePercentage = "1",
                                    quantity = quantity,
                                    amountMinted = 2,
                                    tradeable = true
                                ) // NFT entry
                            )
                        ),
                        outputs = listOf(
                            WeightedOutput(
                                entryIds = listOf(
                                    NFT_id
                                ),
                                weight = 1
                            )
                        ),
                        blockInterval = 1,
                        enabled = true,
                        extraInfo = ""
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

        /*private fun onNftRetrieved(context: Context?): (Transaction?) -> Unit {
            return {
                val transaction = it
                Log.i("onNftRetrieved()", "Response from Wallet: $it")
                when (it) {
                    null -> {
                        //if this is correct logic?
                        CoroutineScope(Dispatchers.IO).launch {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Creating Nft Cancelled!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                    else -> {
                        //retrieve NFT list
                        listNfts(context)

                        CoroutineScope(Dispatchers.IO).launch {
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
                                    return@withContext
                                }
                            }
                        }
                    }
                }
            }
        }*/

        fun buyPylons(context: Context?, callback: (Transaction?) -> Unit) {
            CoroutineScope(Dispatchers.IO).launch {
                //loading screen launch
                wallet?.buyPylons(callback)
            }
        }

        fun executeRecipe(
            context: Context?,
            recipe: String,
            cookbook: String, 
            coinInputIndex: Long, 
            itemInputs: List<String>,
            callback: (Transaction?) -> Unit
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                var creator = ""
                wallet?.listCookbooks {

                    val cookbooks = it
                    var cookbook: Cookbook? = null 
                    if (cookbooks.isNotEmpty()) {
                        cookbook = cookbooks.find { cb ->
                            cb.Creator == getUserProfile()?.address && cb.id.startsWith(
                                appName,
                                true
                            )
                        }
                        creator = cookbook?.Creator!!
                    } else {
                        cookbook = null
                    }
                }

                wallet?.executeRecipe(
                    creator,
                    cookbook,
                    recipe,  
                    coinInputIndex, 
                    itemInputs,
                    callback = callback
                )

            }
        }

        fun getWebLink(recipeName: String, recipeId: String): String? {
            return wallet?.generateWebLink(recipeName, recipeId)
        }

        /**
         * testCreateNft flow
         * 1. fetch Wallet Profile
         * 2. list Creator Cookbooks, if cookbook is null, createAutoCookbook
         * 3. list Recipes for Creator Cookbook
         * 4. Create test NFT Recipe
         * 5. Execute test NFT Recipe
         */
//        fun testCreateNft(context: Context?) {
//
//            CoroutineScope(Dispatchers.IO).launch {
//                var profile: Profile? = null
//                var cookbooks = ArrayList<Cookbook>()
//                var cookbook: Cookbook? = null
//                var recipes = ArrayList<Recipe>()
//
//                // fetch profile
//                runBlocking {
//                    launch {
//                        getWallet().fetchProfile(null) {
//                            profile = it
//                        }
//                    }
//                }
//
//                if (profile == null) {
//                    return@launch
//                }
//
//                runBlocking {
//                    launch {
//                        getWallet().listCookbooks {
//                            it.forEach {
//                                //my cookbook
//                                if (it.Creator == profile?.address)
//                                    cookbooks.add(it)
//                            }
//                        }
//                    }
//                }
//
//                if (cookbooks.isEmpty()) {
//                    //account has not cookbook
//                    //create cookbook
//                    runBlocking {
//                        launch {
//                            wallet?.createAutoCookbook(
//                                profile!!,
//                                appName
//                            ) {
//                                if (it != null) {
//
//                                }
//                            }
//                        }
//                    }
//                }
//
//                if (cookbooks.isEmpty()) {
//                    //something wrong with cookbook creation
//                    return@launch
//                }
//
//                cookbook = cookbooks.find {
//                    it.Creator == profile?.address
//                }
//                if (cookbook == null) {
//                    //not yet created cookbook
//                    return@launch
//                }
//
//
//                runBlocking {
//                    launch {
//                        getWallet().listRecipes {
//                            it.forEach {
//                                recipes.add(it)
//                            }
//                        }
//                    }
//                }
//
//                val nftRecipe = recipes.find {
//                    it.name == "test NFT recipe"
//                }
//
//                if (nftRecipe == null) {
//                    //nft creation
//                    getWallet().createRecipe(
//                        name = "test NFT recipe",
//                        itemInputs = listOf(), // this field is not necessary in NFT creation
//                        cookbook = cookbook.id,
//                        description = "test recipe description for nft image",
//                        blockInterval = 0,
//                        coinInputs = listOf(
//                            CoinInput(
//                                coin = "pylon",
//                                count = 100
//                            )
//                        ), // NFT price
//                        outputTable = EntriesList(
//                            coinOutputs = listOf(),
//                            itemModifyOutputs = listOf(),
//                            itemOutputs = listOf(
//                                //NFT description
//                                ItemOutput(
//                                    id = "test_NFT_v1",
//                                    doubles = listOf(
//
//                                        DoubleParam(
//                                            key = "Residual%", //this should be reserved keyword for NFT
//                                            program = "",
//                                            rate = "1.0",
//                                            weightRanges = listOf(
//                                                DoubleWeightRange(
//                                                    upper = "20", //20%
//                                                    lower = "20",
//                                                    weight = 1
//                                                )
//                                            )
//                                        )
//                                    ),
//                                    longs = listOf(
//                                        LongParam(
//                                            key = "Quantity",
//                                            program = "",
//                                            rate = "1.0",
//                                            weightRanges = listOf(
//                                                LongWeightRange(
//                                                    upper = BigDecimal.valueOf(10).setScale(18)
//                                                        .toString(), //quantity 10 copies
//                                                    lower = BigDecimal.valueOf(10).setScale(18)
//                                                        .toString(),
//                                                    weight = 1
//                                                )
//                                            )
//                                        )
//                                    ),
//                                    strings = listOf(
//                                        //pls confirm this field
//                                        StringParam(
//                                            rate = "1.0",
//                                            key = "Name",
//                                            value = "NFT",
//                                            program = ""
//                                        ),
//                                        StringParam(
//                                            rate = "1.0",
//                                            key = "NFT_URL",
//                                            value = "http://127.0.0.1/test_nft.html",
//                                            program = ""
//                                        )
//                                    ),
//                                    transferFee = 0
//                                ) // NFT entry
//                            )
//                        ),
//                        outputs = listOf(
//                            WeightedOutput(
//                                entryIds = listOf(
//                                    "test_NFT_v1"
//                                ),
//                                weight = "1"
//                            )
//                        ),
//                        extraInfo = ""
//                    ) {
//                        val transaction = it
//
//                    }
//
//                    return@launch
//                }
//
//                //create NFT
//                runBlocking {
//                    launch {
//                        getWallet().executeRecipe(
//                            nftRecipe.,
//                            nftRecipe.name,
//                            nftRecipe.cookbookId,
//                            listOf()
//                        ) {
//                            if (it?.code == Transaction.ResponseCode.OK) {
//
//                            }
//                        }
//                    }
//                }
//
//                //check
//                runBlocking {
//                    launch {
//                        getWallet().fetchProfile(null) {
//                            profile = it
//                        }
//                    }
//                }
//            }
//        }
    }

    /**
     * DroidIpcWire implementation (a bridge between libpylons and client app), referenced by the Pylons SDK.
     */
    class DroidIpcWireImpl : DroidIpcWire() {

        companion object {
            private const val TAG = "DroidIpcWireImpl"

            init {
                implementation = DroidIpcWireImpl()
                Log.i(TAG, "DroidIpcWireImpl has just been instantiated.")
            }

            /**
             * initWallet
             * call when IpcService initiated. IpcService::onServiceConnected()
             * all ipc actions will be come after initWallet() succeed
             */
            fun initWallet() {
                runBlocking {
                    launch {
                        establishConnection(appName, appId) {
                            if (it) { // only if handshake is succeeded
                                println("Wallet Initiated")
                                walletCallback?.onWalletConnected()
                            } else {
                                walletCallback?.onWalletConnectFailed()
                            }
                        }
                    }
                }
            }
        }

        override fun readString(): String? {
            return getWalletConnection().getFromWallet()
        }

        override fun writeString(s: String) {
            getWalletConnection().submitToWallet(s)
        }

    }
}
