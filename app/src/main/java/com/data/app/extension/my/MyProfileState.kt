package com.data.app.extension.my

import com.data.app.data.response_dto.my.ResponseProfileDto

sealed class MyProfileState {
    object Loading : MyProfileState()
    data class Success(val response: ResponseProfileDto) : MyProfileState()
    data class Error(val message: String) : MyProfileState()

}