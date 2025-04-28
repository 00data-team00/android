package com.data.app.extension

import com.data.app.data.response_dto.ResponseRegisterDto

sealed class RegisterState {
    data object Loading:RegisterState()
    data class Success(val response: ResponseRegisterDto):RegisterState()
    data class Error(val message:String):RegisterState()
}