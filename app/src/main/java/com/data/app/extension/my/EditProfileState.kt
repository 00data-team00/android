package com.data.app.extension.my

import com.data.app.data.response_dto.community.ResponseEditProfileDto

sealed class EditProfileState {
    data object Loading : EditProfileState()
    data class Success(val response: ResponseEditProfileDto) :EditProfileState()

    data class Error(val message: String) : EditProfileState()
}