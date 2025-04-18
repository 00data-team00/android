package com.data.app.data

data class PreviousPractice (
    val title:String,
    val summation:String,
    val date:String,
    val type:String,
    val chatList:List<ChatItem>
){
    sealed class ChatItem {
        abstract val chat: String
        abstract val time: String

        data class My(
            override val chat: String,
            override val time: String
        ) : ChatItem()

        data class Ai(
            val name: String,
            val profile: Int,
            override val chat: String,
            override val time: String
        ) : ChatItem()
    }
}