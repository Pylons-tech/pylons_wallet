package tech.pylons.lib.types.credentials

class CosmosCredentials (override var address : String) : ICredentials {
    var sequence : Long = 0
    var accountNumber : Long = 0
}