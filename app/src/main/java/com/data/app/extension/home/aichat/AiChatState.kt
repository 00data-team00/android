package com.data.app.extension.home.aichat

import com.data.app.data.response_dto.home.ai.ResponseChatAiMessageDto

sealed class AiChatState {
     data object Loading:AiChatState()
     data class Success(val response: ResponseChatAiMessageDto):AiChatState()
     data class Error(val message:String):AiChatState()
}