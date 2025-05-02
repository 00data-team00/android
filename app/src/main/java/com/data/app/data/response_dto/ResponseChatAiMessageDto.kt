package com.data.app.data.response_dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseChatAiMessageDto (
    @SerialName("userMessage")
    val userMessage:Message,
    @SerialName("aiMessage")
    val aiMessage:Message
){
    @Serializable
    data class Message(
        @SerialName("messageId")
        val messageId:Int,
        @SerialName("text")
        val text:String,
        @SerialName("isUser")
        val isUser:Boolean,
        @SerialName("storedAt")
        val storedAt:String
    )
}