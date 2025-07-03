package com.data.app.extension.home.aichat

import com.data.app.data.response_dto.home.ai.ResponseTranslateDto

sealed class TranslateState {
    data object Idle : TranslateState()
    data object Loading:TranslateState()
    data class Success(val response: ResponseTranslateDto):TranslateState()
    data class Error(val message:String):TranslateState()
}