package com.data.app.extension.home.aichat

import com.data.app.data.response_dto.home.ai.ResponseAIPreviousRecordsDto

sealed class AIPreviousPracticeState {
    data object Loading:AIPreviousPracticeState()
    data class Success(val response: ResponseAIPreviousRecordsDto):AIPreviousPracticeState()
    data class Error(val message:String):AIPreviousPracticeState()
}