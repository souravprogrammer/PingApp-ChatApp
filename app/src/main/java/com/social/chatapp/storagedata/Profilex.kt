package com.social.chatapp.storagedata

class Profilex {

    var name: String
    var email: String
    var profilePicture: String? = null
    var token : String = ""

    constructor() {
        name = "x"
        email = "x"
        token = ""
    }
    constructor(name: String, email: String, profilePicture: String?,token : String) {
        this.name = name
        this.email = email
        this.profilePicture = profilePicture
        this.token = token
    }
    constructor(name: String, email: String, profilePicture: String?) {
        this.name = name
        this.email = email
        this.profilePicture = profilePicture
    }
}