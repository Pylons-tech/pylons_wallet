package tech.pylons.wallet.walletcore_test.core

import com.beust.klaxon.JsonObject
import tech.pylons.lib.PubKeyUtil
import tech.pylons.lib.core.IMulticore
import tech.pylons.wallet.core.Core
import tech.pylons.wallet.core.Multicore
import tech.pylons.wallet.core.engine.TxPylonsEngine
import tech.pylons.lib.types.*
import tech.pylons.lib.types.credentials.CosmosCredentials
import tech.pylons.lib.types.tx.recipe.*
import tech.pylons.wallet.core.internal.HttpWire
import tech.pylons.wallet.core.internal.InternalPrivKeyStore
import org.apache.tuweni.bytes.Bytes32
import org.spongycastle.jce.provider.BouncyCastleProvider
import org.junit.jupiter.api.*
import tech.pylons.ipc.Message
import tech.pylons.lib.klaxon
import tech.pylons.lib.types.tx.Coin
import tech.pylons.lib.types.tx.item.Item
import tech.pylons.lib.types.tx.trade.ItemRef
import tech.pylons.lib.types.tx.trade.TradeItemInput
import java.io.StringReader
import java.security.Security
import java.util.*

@ExperimentalUnsignedTypes
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class MulticoreTest {
    init {
        // Ensure the BouncyCastleProvider is registered before doing anything further
        Security.removeProvider("BC")
        Security.addProvider(BouncyCastleProvider())
    }

    private val config = Config(
            Backend.MANUAL,
            "pylons-devtestnet",
        //"pylons",
            true,
        //listOf("http://192.168.1.121:1317"),
        listOf("https://api.devtestnet.pylons.tech")
    )

    private val testKeys =
            PylonsSECP256K1.KeyPair.fromSecretKey(PylonsSECP256K1.SecretKey.fromBytes(Bytes32.fromHexString(InternalPrivKeyStore.BANK_TEST_KEY)))
    private val altTestKeys =
            PylonsSECP256K1.KeyPair.fromSecretKey(PylonsSECP256K1.SecretKey.fromBytes(Bytes32.fromHexString(
                InternalPrivKeyStore.TUWENI_FIXTURES_SECRET)))
    // todo: this is really ugly; we need to make this much lower-friction
    private val testCredentials = CosmosCredentials(TxPylonsEngine.getAddressString(PubKeyUtil.getAddressFromKeyPair(testKeys).toArray()))
    private val altTestCredentials = CosmosCredentials(TxPylonsEngine.getAddressString(PubKeyUtil.getAddressFromKeyPair(altTestKeys).toArray()))

    /**
     * Can we enable multi-org.bitcoinj.core.core?
     */
    @Test
    @Order(0)
    fun enableMulticore() {
        Multicore.enable(config)
        assert(IMulticore.enabled)
    }

    /**
     * Can we add a org.bitcoinj.core.core and verify that it has the correct credentials?
     */
    @Test
    @Order(1)
    fun addCore() {
        // todo: this should actually take a keypair as an argument
        val c = Multicore.addCore(testKeys)
        assert(Core.current == c)
        assert(c.userProfile!!.credentials.address == testCredentials.address)
    }

    /**
     * Can we use the newly-added org.bitcoinj.core.core to resolve operations?
     */
    @Test
    @Order(2)
    fun useAddedCoreForOperation() {
        val r = Core.current!!.walletServiceTest("fooBar")
        assert(r == "Wallet service test OK input fooBar")
    }

    /**
     * Can we add another org.bitcoinj.core.core and start using it?
     */
    @Test
    @Order(3)
    fun addAnotherCore() {
        val c = Multicore.addCore(altTestKeys)
        assert(Core.current == c)
        assert(c.userProfile!!.credentials.address == altTestCredentials.address)
        val r = Core.current!!.walletServiceTest("fooBar")
        assert(r == "Wallet service test OK input fooBar")
    }

    /**
     * Can we grab the first org.bitcoinj.core.core again and use it?
     */
    @Test
    @Order(4)
    fun getFirstCore() {
        val c = Multicore.switchCore(testCredentials.address)
        assert(Core.current == c)
        assert(c.userProfile!!.credentials.address == testCredentials.address)
        val r = Core.current!!.walletServiceTest("fooBar")
        assert(r == "Wallet service test OK input fooBar")
    }

    /**
     * Make sure we didn't break crypto doing the reference-juggling
     */
    @Test
    @Order(5)
    fun doCryptoWithRetrievedCore() {
        val tx = Core.current!!.newProfile("foo", testKeys)
        // We don't care if it's accepted, just that we can make a signature
        assert(tx.state == Transaction.State.TX_ACCEPTED || tx.state == Transaction.State.TX_REFUSED)
    }

    @Test
    @Order(6)
    fun testCreateAccount() {
        // todo: this should actually take a keypair as an argument
        Multicore.enable(config)
        val c = Multicore.addCore(null)
        val prof = Core.current?.newProfile("ddddd", testKeys)

        val profile = Core.current?.getProfile()
        Core.current?.getPylons(4000, profile!!.address)
        Core.current?.getTransaction("7A3BD67C41640DB810191613F5B7C3EC33130390DF81E02298F2A58FA8F8642E")

        var item_one = Core.current?.getItem("5rLWQQkJdm1", "Easel_autocookbook_pylo1tlkyrc7ek0lsuv4wtchmd4et8rwcgs4plkxceh")

        var items = Core.current?.listItemsBySender("pylo14c5yepsgtvrtsk9dz3yzvl6jgv3gxzltl83cas")

        var item = Core.current?.getItem("pylo14c5yepsgtvrtsk9dz3yzvl6jgv3gxzltl83cas")

        //Core.current?.getPylons(4000, profile!!.address)
        var ssdsds  = Core.current?.batchCreateCookbook(
            creators = mutableListOf(profile!!.address),
            ids = mutableListOf("Easel_autocookbooks_" + profile!!.address),
            names = mutableListOf("tst_cookbook_name"),
            developers = mutableListOf("addghjkllsdfdggdgjkkk"),
            descriptions = mutableListOf("asdfasdfasdfaaaaaaaaaaaaaaaaaaaaasssssssss"),
            versions = mutableListOf("v1.0.0"),
            supportEmails = mutableListOf("a@example.com"),
            costsPerBlock = mutableListOf(Coin("upylon", 1)),
            enableds = mutableListOf(true)
        )
//        val cookbooks = Core.current?.engine?.listCookbooks()
//        val trades = Core.current?.engine?.listTrades(profile!!.address)
//        val cookbook = Core.current?.engine?.getCookbook("Easel_autocookbook_pylo149haucpqld30pksrzqyff67prswul9vmmle27v")
//        val recipesss = Core.current?.engine?.getRecipe("pylo149haucpqld30pksrzqyff67prswul9vmmle27v_2021_10_19_17_00_32")
//
//        val trades = Core.current?.engine?.listTrades("pylo1zxa6az4kc0254ppmjfk0fyrc5sf2m4c36umtmr")
//        val trade = Core.current?.engine?.getTrade("0")


        Core.current?.createTrade(
            profile!!.address,
            listOf(
                CoinInput(listOf(Coin("upylon", 66)))
            ),
            mutableListOf(),
            mutableListOf(),
            mutableListOf(
                ItemRef(
                    cookbookID = "Easel_autocookbook_pylo1zchw46gccslpj6ffznn3cm9r33jdv36ua39v3q",
                    itemID = "4M2TH9NsAMM"
                )
            ),
            "")

//        Core.current?.engine?.applyRecipe("pylo149haucpqld30pksrzqyff67prswul9vmmle27v", recipesss?.cookbookId!!,
//            recipesss?.name, 0, listOf())

        return
        val transaction_recipe = Core.current?.batchCreateRecipe(
            creators = listOf(profile!!.address),
            cookbooks = listOf("Easel_autocookbook_" + profile!!.address),
            ids = listOf("Easel_autorecipe_" + profile!!.address),
            names = listOf("xxxxxxxxxxxxxx"),
            descriptions = listOf("yyyyyyyyydsfdsdfsd fsdfsdyyyyyy"),
            versions = listOf("v1.0.0"),
            coinInputs = listOf(listOf(
                CoinInput(listOf(Coin("upylon", 100)))
            )),
            itemInputs = listOf(listOf(
            )),
            outputTables = listOf(EntriesList(
                coinOutputs = listOf(),
                itemModifyOutputs = listOf(),
                itemOutputs = listOf(
                    ItemOutput(
                        id = "nft_test2",
                        doubles = listOf(
                            DoubleParam(
                                key="Residual",
                                program = "1",
                                rate = "1",
                                weightRanges = listOf(
                                    DoubleWeightRange(
                                        upper="20",
                                        lower="20",
                                        weight = 1
                                    )
                                )
                            )
                        ),
                        longs = listOf(
                            LongParam(
                                key="Quantity",
                                program = "1",
                                rate = "1",
                                weightRanges = listOf(
                                    IntWeightRange(
                                        upper = 10,
                                        lower = 10,
                                        weight = 1
                                    )
                                )
                            )
                        ),
                        strings = listOf(
                            StringParam(
                                rate = "1",
                                key = "Name",
                                value="nft_2",
                                program = "1"
                            ),
                            StringParam(
                                rate = "1",
                                key = "NFT_URL",
                                value="http://192.168.1.1",
                                program = "1"
                            ),
                            StringParam(
                                rate = "1",
                                key = "Description",
                                value = "nft description description",
                                program = "1"
                            )
                        ),
                        mutableStrings = listOf(
                            StringKeyValue(
                                Key = "Name",
                                Value="nft_2"
                            ),
                        ),
                        transferFee = listOf(Coin("upylon", 1)),
                        tradePercentage = "1",
                        quantity = 10,
                        amountMinted = 2,
                        tradeable = true
                    )
                )
            )),
            outputs = listOf(listOf(
                WeightedOutput(
                    entryIds = listOf(
                        "nft_test2"
                    ),
                    weight = 1
                )
            )),
            extraInfos = listOf(""),
            enableds = listOf(true),
            blockIntervals = listOf(1)
        )

        return

        //http://10.0.2.2:1317/txs/B4A8B0DE37A77C68FFB48AB6D47ADCAA0623FAA7EC50ED11C03A9A4B26B94592
        val transaction = Core.current?.getTransaction("B4A8B0DE37A77C68FFB48AB6D47ADCAA0623FAA7EC50ED11C03A9A4B26B94592")

        val url = "http://127.0.0.1:1317/custom/pylons/list_recipe/cosmos139rpmrte2x6gyrnmlkr73pfeeqnfcpdqt5sf86"
        val msg = HttpWire.get(url)
        val recipes = Recipe.listFromJson(
            msg
        )

        //val recipes = Core.current?.engine?.listRecipes()



        //val transaction = Core.current?.getTransaction("E90C069556189847248D8B45890316D424414ACC72C331482C6F82A6DC20AD0F")



        assert(Core.current == c)
        println(c.userProfile!!.credentials.address)
        println(testCredentials.address)
        assert(c.userProfile!!.credentials.address == testCredentials.address)
    }


    @Test
    @Order(7)
    fun testKlaxonValidation() {
//        Multicore.enable(config)
        val c = Multicore.addCore(null)

        val transaction = Core.current?.getTransaction("E1D9AD271502D7A7C9FCF0FE30313D40F35E883303BEEBCA4C26CDD05E1D6070")
    }

}