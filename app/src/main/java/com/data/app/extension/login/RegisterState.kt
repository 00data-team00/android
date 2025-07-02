package com.data.app.extension.login

import com.data.app.data.response_dto.login.ResponseRegisterDto

sealed class RegisterState {
    data object Loading:RegisterState()
    data class Success(val response: ResponseRegisterDto):RegisterState()
    data class Error(val message:String):RegisterState()
}