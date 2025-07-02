package com.data.app.extension.my

import com.data.app.data.response_dto.ResponseProfileDto

sealed class UserProfileState {
    object Loading : UserProfileState()
    data class Success(val response: ResponseProfileDto) : UserProfileState()
    data class Error(val message: String) : UserProfileState()
}