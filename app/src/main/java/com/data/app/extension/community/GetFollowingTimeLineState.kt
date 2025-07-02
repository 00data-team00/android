package com.data.app.extension.community

import com.data.app.data.response_dto.community.ResponseTimeLineDto

sealed class GetFollowingTimeLineState {
    object Loading : GetFollowingTimeLineState()
    data class Success(val data: List<ResponseTimeLineDto.Posts>) : GetFollowingTimeLineState()
    data class Error(val message: String) : GetFollowingTimeLineState()
}