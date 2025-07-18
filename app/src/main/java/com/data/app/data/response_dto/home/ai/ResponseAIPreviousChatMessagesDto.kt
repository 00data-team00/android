package com.data.app.data.response_dto.home.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseAIPreviousChatMessagesDto (
    @SerialName("messages")
    val messages:List<Message>
){
    @Serializable
    data class Message(
        @SerialName("messageId")
        val messageId:Int,
        @SerialName("text")
        var text:String,
        @SerialName("isUser")
        val isUser:Boolean,
        @SerialName("storedAt")
        val storedAt:String,
        @SerialName("feedbackLang")
        val feedbackLang:String?,
        @SerialName("feedbackContent")
        val feedbackContent:String?,
    )
}