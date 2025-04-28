package com.data.app.extension

import com.data.app.data.response_dto.ResponseRegisterDto

sealed class VerifyMailState {
    data object Loading:VerifyMailState()
    data class Success(val response: ResponseRegisterDto):VerifyMailState()
    data class Error(val message:String):VerifyMailState()
}