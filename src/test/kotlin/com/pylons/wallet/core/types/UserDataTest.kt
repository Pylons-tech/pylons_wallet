// UserData rewrite completely broke the way this worked
// TODO: write new UserData tests compatible w/ current implementation

//package com.pylons.wallet.core.types
//
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.Assertions.*
//
//internal class UserDataTest {
//    var userData = UserData(name = "foo", id = "bar", keys = "fasfasf",
//            extras = mapOf("spam" dsPrivate "eggs"))
//    val jsonFixture = """{"cryptoKeys":{"key":[68,69,65,68,66,69,69,70]},"extras":{"spam":"eggs"},"friends":[],"id":"bar","name":"foo","version":"1.0.0"}"""
//
//    /**
//     * UserData can be exported as JSON, and will produce a JSON object
//     * containing the correct values for all members.
//     */
//    @Test
//    fun exportAsJson() {
//        val json = userData.exportAsJson()
//        System.out.println(json)
//        assertNotEquals(json, "")
//        assertEquals(json, jsonFixture)
//    }
//
//    /**
//     * UserData can be parsed from JSON, and will inherit the correct
//     * values for all members.
//     */
//    @Test
//    fun parseFromJson() {
//        UserData.parseFromJson(jsonFixture)
//        assertEquals(userData.name, newConfig!!.name)
//        assertEquals(userData.id, newConfig.id)
//        assertEquals(userData.version, newConfig.version)
//        assertEquals(userData.keys, newConfig.keys)
//        userData.extras.forEach { s, _ -> assertEquals(userData.extras[s], newConfig.extras[s])}
//    }
//}