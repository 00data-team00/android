package com.data.app.extension.explore

import com.data.app.data.response_dto.ResponseDeadlineDto

sealed class DeadLineProgramState {
    data object Loading : DeadLineProgramState()
    data class Success(val response: ResponseDeadlineDto) :
        DeadLineProgramState()

    data class Error(val message: String) : DeadLineProgramState()
}