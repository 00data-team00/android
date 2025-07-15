package com.data.app.extension.login

import com.data.app.data.response_dto.login.ResponseLoginDto

sealed class RefreshState {
    data object Loading:RefreshState()
    data class Success(val response: ResponseLoginDto):RefreshState()
    data class Error(val message:String):RefreshState()}