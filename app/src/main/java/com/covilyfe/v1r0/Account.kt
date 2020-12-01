package com.covilyfe.v1r0

class Account {
    var id : Int = 0
    var username : String = ""
    var email : String? = ""
    var jwt : String = ""
    var avatar : ByteArray? = null

    constructor(username:String, email:String?, jwt:String, avatar:ByteArray?){
        this.username = username
        this.email = email
        this.jwt = jwt
        this.avatar = avatar

    }
    constructor()
}