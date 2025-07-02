package com.data.app.extension.login

import com.data.app.data.response_dto.login.ResponseRegisterDto

sealed class SendMailState {
    data object Loading:SendMailState()
    data class Success(val response: ResponseRegisterDto):SendMailState()
    data class Error(val message:String):SendMailState()
}