package com.data.app.data.response_dto.home.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseAIPreviousRecordsDto (
    @SerialName("chatRooms")
    val chatRooms:List<ChatRoom>,
){
    @Serializable
    data class ChatRoom(
        @SerialName("chatRoomId")
        val chatRoomId:Int,
        @SerialName("title")
        val title:String,
        @SerialName("createdAt")
        val createdAt:String,
        @SerialName("isFinished")
        val isFinished:Boolean,
        @SerialName("description")
        val description:String,
        var isExpanded:Boolean = false              // 클라이언트 전용 ui 상태
    )
}