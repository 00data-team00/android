package com.data.app.extension.my

import com.data.app.data.response_dto.my.ResponseMyPostDto
import com.data.app.data.response_dto.my.ResponseQuitDto

sealed class QuitState {
    object Loading : QuitState()
    data class Success(val response: ResponseQuitDto) : QuitState()
    data class Error(val message: String) : QuitState()
}