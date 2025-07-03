package com.data.app.extension

import com.data.app.data.response_dto.ResponseTranslateDto
import okhttp3.Response

sealed class TranslateState {
    data object Loading:TranslateState()
    data class Success(val response: ResponseTranslateDto):TranslateState()
    data class Error(val message:String):TranslateState()
}