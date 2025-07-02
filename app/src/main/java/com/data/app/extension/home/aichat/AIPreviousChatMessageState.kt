package com.data.app.extension.home.aichat

import com.data.app.data.response_dto.ResponseAIPreviousChatMessagesDto

sealed class AIPreviousChatMessageState {
    data object Loading:AIPreviousChatMessageState()
    data class Success(val chatRoomId: Int, val response: ResponseAIPreviousChatMessagesDto):AIPreviousChatMessageState()
    data class Error(val message:String):AIPreviousChatMessageState()
}