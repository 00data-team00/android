package com.data.app.extension.home.aichat

import com.data.app.data.response_dto.ResponseChatStartDto

sealed class StartChatState {
    data object Loading:StartChatState()
    data class Success(val response: ResponseChatStartDto):StartChatState()
    data class Error(val message:String):StartChatState()
}