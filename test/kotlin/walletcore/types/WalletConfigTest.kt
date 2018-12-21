package walletcore.types

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

internal class WalletConfigTest {
    var config = WalletConfig("Foo", "Bar")
    val jsonFixture = """{"id":"Bar","name":"Foo","version":"1.0.0"}"""

    /**
     * WalletConfig can be exported as JSON, and will produce a JSON object
     * containing the correct values for all members.
     */
    @Test
    fun exportAsJson() {
        val json = config.exportAsJson()
        assertNotEquals(json, "")
        assertEquals(json, jsonFixture)
        System.out.println(json)
    }

    /**
     * WalletConfig can be parsed from JSON, and will inherit the correct
     * values for all members.
     */
    @Test
    fun parseFromJson() {
        val newConfig = WalletConfig.parseFromJson(jsonFixture)
        assertNotNull(newConfig)
        assertEquals(config.name, newConfig!!.name)
        assertEquals(config.id, newConfig.id)
        assertEquals(config.version, newConfig.version)
    }
}