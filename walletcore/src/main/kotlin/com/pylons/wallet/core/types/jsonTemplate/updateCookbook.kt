package com.pylons.wallet.core.types.jsonTemplate

import com.pylons.wallet.core.types.SECP256K1

internal fun updateCookbook (id : String, devel : String, desc : String, version : String,
                    supportEmail : String, sender : String,
                    pubkey: SECP256K1.PublicKey, accountNumber: Long, sequence: Long) : String =
        baseJsonWeldFlow(updateCookbookMsgTemplate(id, devel, desc, version, supportEmail, sender),
                updateCookbookSignTemplate(id, devel, desc, version, supportEmail, sender),
                accountNumber, sequence, pubkey)

private fun updateCookbookMsgTemplate (id : String, devel : String, desc : String, version : String,
                                       supportEmail : String, sender : String) = """
                            [
                            {
                                "type": "pylons/UpdateCookbook",
                                "value": {
                                    "ID": "$id",
                                    "Description": "$desc",
                                    "Developer": "$devel",
                                    "Version": "$version",
                                    "SupportEmail": "$supportEmail",
                                    "Sender": "$sender"
                                }
                            }
                            ]
                        """

internal fun updateCookbookSignTemplate (id : String, devel : String, desc : String, version : String,
                                supportEmail : String, sender : String) : String =
        """[{"Description":"$desc","Developer":"$devel","ID":"$id","Sender":"$sender","SupportEmail":"$supportEmail","Version":"$version"}]"""