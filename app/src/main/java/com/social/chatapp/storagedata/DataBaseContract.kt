package com.social.chatapp.storagedata

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DataBaseContract {
    companion object {
        const val email = "email"
        const val user = "user"
        const val Profile = "profile"
        const val profilePicture = "profilePicture"
        const val chatRoom = "chatroom"
        const val chats = "chats"
        const val BASE_URL = "https://fcm.googleapis.com"
        const val SERVER_KEY = "AAAACcH9H_Q:APA91bG5gRf8qOxEF6l_bxlvA_FfoGtyaAYQGHJtrGj6PQJpcF5bnHOhARIkZpGWu93sxTVyG9nYcKn4PhUyUIMpuAN-TTn0FSs27_1aUkJye818zGy5KUVhwv9Jtl872GQAEOWbW-lR"
        const val CONTENT_TYPE = "application/json"

        private const val at = "<at>"
        private const val dot = "<dot>"

        @JvmStatic
        fun convertToKey(s: String): String {
           val  result =  s.replace("@", at).replace(".", dot)
            return result
        }

        @JvmStatic
        fun convertToEmail(s: String): String {
          val result =  s.replace(at, "@").replace(dot, ".")
            return result
        }
    }

}