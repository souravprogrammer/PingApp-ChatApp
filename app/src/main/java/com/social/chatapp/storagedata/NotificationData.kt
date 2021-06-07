package com.social.chatapp.storagedata

import android.icu.text.CaseMap

class NotificationData {
    val title: String
    val message: String

    constructor() {
        title = ""
        message = ""
    }

    constructor(title: String, message: String) {
        this.title = title
        this.message = message

    }
}