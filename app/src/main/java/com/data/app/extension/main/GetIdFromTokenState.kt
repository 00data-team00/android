package com.data.app.extension.main

import com.data.app.data.response_dto.community.ResponseGetIdFromTokenDto

sealed class GetIdFromTokenState {
    data object Loading : GetIdFromTokenState()
    data class Success(val response: ResponseGetIdFromTokenDto) :    GetIdFromTokenState()

    data class Error(val message: String) : GetIdFromTokenState()
}