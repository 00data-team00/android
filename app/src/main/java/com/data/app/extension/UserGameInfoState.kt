package com.data.app.extension

import com.data.app.data.response_dto.ResponseEditProfileDto
import com.data.app.data.response_dto.ResponseUserGameInfoDto

sealed class UserGameInfoState {
    data object Loading : UserGameInfoState()
    data class Success(val response: ResponseUserGameInfoDto) :    UserGameInfoState()

    data class Error(val message: String) :     UserGameInfoState()
}