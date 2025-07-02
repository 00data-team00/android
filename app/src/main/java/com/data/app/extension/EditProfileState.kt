package com.data.app.extension

import com.data.app.data.response_dto.ResponseAllProgramDto
import com.data.app.data.response_dto.ResponseEditProfileDto

sealed class EditProfileState {
    data object Loading : EditProfileState()
    data class Success(val response: ResponseEditProfileDto) :EditProfileState()

    data class Error(val message: String) : EditProfileState()
}