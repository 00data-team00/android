package com.data.app.extension.home.aichat

import com.data.app.data.response_dto.ResponseAITopicsDto

sealed class AIChatTopicsState {
    data object Loading:AIChatTopicsState()
    data class Success(val response: ResponseAITopicsDto):AIChatTopicsState()
    data class Error(val message:String):AIChatTopicsState()
}