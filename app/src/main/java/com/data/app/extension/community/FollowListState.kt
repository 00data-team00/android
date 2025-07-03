package com.data.app.extension.community

import com.data.app.data.response_dto.community.ResponseFollowListDto

sealed class FollowListState {
    data object Loading:FollowListState()
    data class Success(val response: ResponseFollowListDto):FollowListState()
    data class Error(val message: String):FollowListState()
}