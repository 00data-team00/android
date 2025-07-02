package com.data.app.extension.home

import com.data.app.data.response_dto.ResponseUserGameInfoDto

sealed class UserGameInfoState {
    data object Loading : UserGameInfoState()
    data class Success(val response: ResponseUserGameInfoDto) :    UserGameInfoState()

    data class Error(val message: String) :     UserGameInfoState()
}