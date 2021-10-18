package tech.pylons.wallet.walletcore_test.types

import tech.pylons.lib.types.tx.Coin
import tech.pylons.lib.types.Execution
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExecutionTest {
    private val sampleJson = """
        {
          "height": "0",
          "result": {
            "Executions": [
              {
                "NodeVersion": "0.0.1",
                "ID": "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337252777ae-b371-4562-8979-a4f5ff0fb2ac",
                "RecipeID": "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt33773169682-d20c-4aca-ba1c-f1f886189b2e",
                "CookbookID": "-124015487",
                "CoinInputs": [
                  {
                    "denom": "upylon",
                    "amount": "10"
                  }
                ],
                "ItemInputs": null,
                "BlockHeight": "41",
                "Sender": "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337",
                "Completed": false
              }
            ]
          }
        }
    """.trimIndent()

    @Test
    fun parseJson() {
        val expected = listOf(Execution(
                nodeVersion = "0.0.1",
                id = "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337252777ae-b371-4562-8979-a4f5ff0fb2ac",
                recipeId = "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt33773169682-d20c-4aca-ba1c-f1f886189b2e",
                cookbookId = "-124015487",
                coinInputs = listOf(
                        Coin(
                                denom = "upylon",
                                amount = 10
                        )
                ),
                itemInputs = listOf(),
                blockHeight = 41,
                sender = "cosmos1y8vysg9hmvavkdxpvccv2ve3nssv5avm0kt337",
                completed = false
        ))

        assertEquals(expected, Execution.getListFromJson(sampleJson))
    }
}