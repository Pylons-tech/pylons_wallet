package com.pylons.wallet.core.types.jsonTemplate

import com.pylons.wallet.core.types.SECP256K1

internal fun createCookbook (name : String, devel : String, desc : String, version : String,
                    supportEmail : String, level : Long, sender : String,
                    pubkey: SECP256K1.PublicKey, accountNumber: Long, sequence: Long) : String =
      baseJsonWeldFlow(createCookbookMsgTemplate(name, devel, desc, version, supportEmail, level, sender),
                createCookbookSignTemplate(name, devel, desc, version, supportEmail, level, sender),
                accountNumber, sequence, pubkey)

private fun createCookbookMsgTemplate (name : String, devel : String, desc : String, version : String,
                                       supportEmail : String, level : Long, sender : String) = """
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
                "Sender": "$sender"
            }
        }
        ]
    """

internal fun createCookbookSignTemplate (name : String, devel : String, desc : String, version : String,
                                supportEmail : String, level : Long, sender : String) : String =
        """[{"Description":"$desc","Developer":"$devel","Level":$level,"Name":"$name","Sender":"$sender","SupportEmail":"$supportEmail","Version":"$version"}]"""