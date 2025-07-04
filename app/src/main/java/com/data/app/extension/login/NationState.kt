package com.data.app.extension.login

import com.data.app.data.response_dto.login.ResponseNationDto
import com.data.app.data.response_dto.login.ResponseRegisterDto

sealed class NationState{
    data object Loading: NationState()
    data class Success(val response: ResponseNationDto) : NationState()
    data class Error(val message: String) : NationState()
}