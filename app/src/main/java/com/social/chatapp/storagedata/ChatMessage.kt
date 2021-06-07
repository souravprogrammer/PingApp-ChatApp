package com.social.chatapp.storagedata


class ChatMessage {
    var text: String? = null
     var timestamp: String
     var sender: String
   lateinit  var receiver: String
     var feeling: String
    var imageUrl: String? = null
    var videoUrl: String? = null

    constructor() {
        this.timestamp = "0"
        this.sender = ""
        this.feeling = "0"
        this.receiver = ""

    }

    constructor(
        text: String,
        timestamp: String,
        sender: String,
        receiver: String,
        feeling: String,
        imageUrl: String?, videoUrl: String?
    ) {

        this.text = text
        this.timestamp = timestamp
        this.sender = sender
        this.feeling = feeling
        this.receiver = receiver
        this.imageUrl = imageUrl
        this.videoUrl = videoUrl

    }

}