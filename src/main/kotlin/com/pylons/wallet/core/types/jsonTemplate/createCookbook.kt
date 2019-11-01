package com.pylons.wallet.core.types.jsonTemplate

import com.pylons.wallet.core.types.SECP256K1

internal fun createCookbook (name : String, devel : String, desc : String, version : String,
                    supportEmail : String, level : Long, sender : String,
                    pubkey: SECP256K1.PublicKey, accountNumber: Long, sequence: Long, costPerBlock: Long) : String =
      baseJsonWeldFlow(createCookbookMsgTemplate(name, devel, desc, version, supportEmail, level, sender, costPerBlock),
                createCookbookSignTemplate(name, devel, desc, version, supportEmail, level, sender, costPerBlock),
                accountNumber, sequence, pubkey)

private fun createCookbookMsgTemplate (name : String, devel : String, desc : String, version : String,
                                       supportEmail : String, level : Long, sender : String, costPerBlock: Long) = """
        [
        {
            "type": "pylons/CreateCookbook",
            "value": {
                "Name": "$name",
                "Description": "$desc",
                "Developer": "$devel",
                "Version": "$version",
                "SupportEmail": "$supportEmail",
                "Level": "$level",
                "Sender": "$sender",
                "CostPerBlock": "$costPerBlock"
            }
        }
        ]
    """

internal fun createCookbookSignTemplate (name : String, devel : String, desc : String, version : String,
                                supportEmail : String, level : Long, sender : String, costPerBlock: Long) : String =
        """[{"CostPerBlock":$costPerBlock,"Description":"$desc","Developer":"$devel","Level":$level,"Name":"$name","Sender":"$sender","SupportEmail":"$supportEmail","Version":"$version"}]"""