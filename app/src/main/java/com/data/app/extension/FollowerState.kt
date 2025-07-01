package com.data.app.extension

import com.data.app.data.response_dto.ResponseFollowersDto

sealed class FollowerState {
    data object Loading:FollowerState()
    data class Success(val response: ResponseFollowersDto):FollowerState()
    data class Error(val message: String):FollowerState()
}