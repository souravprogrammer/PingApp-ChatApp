package com.social.chatapp.storagedata

class ChatItem {

    var chatRoomRef: String
    var email: String
    var lastMessage: ChatMessage? = null
    var profilePicture: String? = null
    lateinit var name: String

    constructor() {
        chatRoomRef = ""
        email = ""
        profilePicture = null
    }

    constructor(
        chatRoomRef: String,
        number: String,
        profilePicture: String?, lastMessage: ChatMessage?, name: String
    ) {
        this.chatRoomRef = chatRoomRef
        this.email = number
        this.profilePicture = profilePicture
        this.lastMessage = lastMessage
        this.name = name
    }
}
