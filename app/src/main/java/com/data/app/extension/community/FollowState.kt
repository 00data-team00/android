package com.data.app.extension.community

import com.data.app.data.response_dto.community.ResponseFollowDto
import com.data.app.data.response_dto.community.ResponseFollowersDto

sealed class FollowState {
    data object Loading:FollowState()
    data class Success(val response: ResponseFollowDto):FollowState()
    data class Error(val message: String):FollowState()
}