package com.data.app.extension

import com.data.app.data.response_dto.ResponseAITopicsDto
import com.data.app.data.response_dto.ResponseLoginDto

sealed class AIChatTopicsState {
    data object Loading:AIChatTopicsState()
    data class Success(val response: ResponseAITopicsDto):AIChatTopicsState()
    data class Error(val message:String):AIChatTopicsState()
}