package com.data.app.extension.login

import com.data.app.data.response_dto.login.ResponseLoginDto

sealed class LoginState {
    data object Idle:LoginState()
    data object Loading:LoginState()
    data class Success(val accessToken:String):LoginState()
    data class Error(val message:String):LoginState()
}