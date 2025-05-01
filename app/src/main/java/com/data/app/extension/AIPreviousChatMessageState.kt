package com.data.app.extension

import com.data.app.data.response_dto.ResponseAIPreviousChatMessagesDto
import com.data.app.data.response_dto.ResponseAIPreviousRecordsDto

sealed class AIPreviousChatMessageState {
    data object Loading:AIPreviousChatMessageState()
    data class Success(val chatRoomId: Int, val response: ResponseAIPreviousChatMessagesDto):AIPreviousChatMessageState()
    data class Error(val message:String):AIPreviousChatMessageState()
}