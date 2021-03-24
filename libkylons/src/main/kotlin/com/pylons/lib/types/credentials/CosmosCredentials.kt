package com.pylons.lib.types.credentials

import com.pylons.lib.types.MyProfile

class CosmosCredentials (override var address : String) : ICredentials {
    var sequence : Long = 0
    var accountNumber : Long = 0
}