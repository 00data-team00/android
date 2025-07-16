package com.data.app.extension.my

import com.data.app.data.response_dto.community.ResponseShareDto

sealed class ShareProfileState {
    object Loading : ShareProfileState()
    data class Success(val response: ResponseShareDto) : ShareProfileState()
    data class Error(val message: String) : ShareProfileState()
}