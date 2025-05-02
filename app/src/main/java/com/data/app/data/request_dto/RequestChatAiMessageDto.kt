package com.data.app.data.request_dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestChatAiMessageDto (
    @SerialName("chatRoomId")
    val chatRoomId:Int,
    @SerialName("text")
    val text:String,
    @SerialName("isUser")
    val isUser:Boolean
)