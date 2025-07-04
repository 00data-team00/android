package com.data.app.extension.explore

import com.data.app.data.response_dto.explore.ResponseAllProgramDto

sealed class AllProgramsState {
    data object Loading : AllProgramsState()
    data class Success(val response: ResponseAllProgramDto, val isAppend: Boolean) :
        AllProgramsState()

    data class Error(val message: String) : AllProgramsState()
}