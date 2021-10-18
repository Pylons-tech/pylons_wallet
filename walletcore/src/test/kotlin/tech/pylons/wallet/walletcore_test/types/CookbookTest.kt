package tech.pylons.wallet.walletcore_test.types

import tech.pylons.lib.types.Cookbook
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tech.pylons.lib.types.tx.Coin

class CookbookTest {
    private val sampleJson = """
        {
          "height": "0",
          "result": {
            "Cookbooks": [
              {
                "NodeVersion": "0.0.1",
                "ID": "-124015487",
                "Name": "blyyah -1706385095",
                "Description": "this is a description for updatescookbook test",
                "Version": "1.0.0",
                "Developer": "tst",
                "Level": "0",
                "SupportEmail": "example@example.com",
                "CostPerBlock": "50",
                "Sender": "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337"
              }
            ]
          }
        }
    """.trimIndent()

    @Test
    fun parseJson() {
        val expected = listOf(
                Cookbook(
                        id = "-124015487",
                        nodeVersion = "v0.1.3",
                        name = "blyyah -1706385095",
                        description = "this is a description for updatescookbook test",
                        version = "1.0.0",
                        developer = "tst",
                    supportEmail = "example@example.com",
                        costPerBlock = Coin("upylon", 50),
                        Creator = "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337",
                    Enabled = true
                )
        )

        assertEquals(expected, Cookbook.getListFromJson(sampleJson))
    }
}