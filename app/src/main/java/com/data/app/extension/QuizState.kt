package com.data.app.extension

import com.data.app.data.response_dto.ResponseQuizDto

sealed class QuizState {
    object Loading : QuizState()
    data class Success(val questions: List<ResponseQuizDto.QuizDto>) : QuizState()
    data class Error(val message: String) : QuizState()
}