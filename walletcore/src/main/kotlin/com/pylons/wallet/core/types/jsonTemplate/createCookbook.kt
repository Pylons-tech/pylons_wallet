package com.pylons.wallet.core.types.jsonTemplate

import com.pylons.wallet.core.types.PylonsSECP256K1

internal fun createCookbook (id : String, name : String, devel : String, desc : String, version : String,
                             supportEmail : String, level : Long, sender : String,
                             pubkey: PylonsSECP256K1.PublicKey, accountNumber: Long, sequence: Long, costPerBlock: Long) : String =
      baseJsonWeldFlow(createCookbookMsgTemplate(id, name, devel, desc, version, supportEmail, level, sender, costPerBlock),
                createCookbookSignTemplate(id, name, devel, desc, version, supportEmail, level, sender, costPerBlock),
                accountNumber, sequence, pubkey)

private fun createCookbookMsgTemplate (id : String, name : String, devel : String, desc : String, version : String,
                                       supportEmail : String, level : Long, sender : String, costPerBlock: Long) = """
        [
        {
            "type": "pylons/CreateCookbook",
            "value": {
                "CookbookID": "$id",
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

fun createCookbookSignTemplate (id : String, name : String, devel : String, desc : String, version : String,
                                supportEmail : String, level : Long, sender : String, costPerBlock: Long) : String =
        """[{"CookbookID":"$id","CostPerBlock":$costPerBlock,"Description":"$desc","Developer":"$devel","Level":$level,"Name":"$name","Sender":"$sender","SupportEmail":"$supportEmail","Version":"$version"}]"""