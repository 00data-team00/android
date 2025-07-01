package com.data.app.extension

import com.data.app.data.response_dto.ResponseQuizDto

sealed class QuizCompleteState {
    object Loading : QuizCompleteState()
    object Success: QuizCompleteState()
    data class Error(val message: String) : QuizCompleteState()
}