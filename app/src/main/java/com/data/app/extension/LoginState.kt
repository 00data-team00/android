package com.data.app.extension

import com.data.app.data.response_dto.ResponseLoginDto

sealed class LoginState {
    data object Idle:LoginState()
    data object Loading:LoginState()
    data class Success(val response: ResponseLoginDto):LoginState()
    data class Error(val message:String):LoginState()
}