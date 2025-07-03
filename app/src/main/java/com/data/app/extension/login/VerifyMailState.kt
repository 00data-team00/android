package com.data.app.extension.login

import com.data.app.data.response_dto.login.ResponseRegisterDto

sealed class VerifyMailState {
    data object Loading:VerifyMailState()
    data class Success(val response: ResponseRegisterDto):VerifyMailState()
    data class Error(val message:String):VerifyMailState()
}