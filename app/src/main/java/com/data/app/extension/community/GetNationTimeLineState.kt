package com.data.app.extension.community

import com.data.app.data.response_dto.community.ResponseTimeLineDto

sealed class GetNationTimeLineState {
    object Loading : GetNationTimeLineState()
    data class Success(val data: List<ResponseTimeLineDto.TimelinePostItem>) : GetNationTimeLineState()
    data class Error(val message: String) : GetNationTimeLineState()
}