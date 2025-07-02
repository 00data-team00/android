package com.data.app.data.response_dto.home.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseChatStartDto (
    @SerialName("chatRoomId")
    val chatRoomId:Int,
    @SerialName("createdAt")
    val createdAt:String,
    @SerialName("message")
    val message:String,
)