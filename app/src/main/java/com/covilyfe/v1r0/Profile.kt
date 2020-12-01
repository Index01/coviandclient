package com.covilyfe.v1r0


class Profile {
    var profile_avatar: String = ""
    var profile_location: String = ""
    var profile_publish_freq: String = ""

    constructor(profile_avatar:String, profile_location:String, profile_publish_freq:String){
        this.profile_avatar = profile_avatar
        this.profile_location = profile_location
        this.profile_publish_freq = profile_publish_freq

    }
    constructor()
}