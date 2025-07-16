package com.data.app.extension.my

import com.data.app.data.response_dto.community.ResponseShareDto

sealed class SharePostState {
    object Loading : SharePostState()
    data class Success(val response: ResponseShareDto) : SharePostState()
    data class Error(val message: String) : SharePostState()
}