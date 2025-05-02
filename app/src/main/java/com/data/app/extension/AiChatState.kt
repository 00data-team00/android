package com.data.app.extension

import com.data.app.data.response_dto.ResponseChatAiMessageDto
import com.data.app.data.response_dto.ResponseChatStartDto

sealed class AiChatState {
     data object Loading:AiChatState()
     data class Success(val response: ResponseChatAiMessageDto):AiChatState()
     data class Error(val message:String):AiChatState()
}