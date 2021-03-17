package com.pylons.wallet.walletcore_test.types

import com.pylons.lib.types.Cookbook
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

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
                        nodeVersion = "0.0.1",
                        id = "-124015487",
                        name = "blyyah -1706385095",
                        description = "this is a description for updatescookbook test",
                        version = "1.0.0",
                        developer = "tst",
                        level = 0,
                        supportEmail = "example@example.com",
                        costPerBlock = 50,
                        sender = "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337"
                )
        )

        assertEquals(expected, Cookbook.getListFromJson(sampleJson))
    }
}