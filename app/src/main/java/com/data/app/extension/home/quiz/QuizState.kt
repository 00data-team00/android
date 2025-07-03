package com.data.app.extension.home.quiz

import com.data.app.data.response_dto.home.quiz.ResponseQuizDto

sealed class QuizState {
    object Loading : QuizState()
    data class Success(val questions: List<ResponseQuizDto.QuizDto>) : QuizState()
    data class Error(val message: String) : QuizState()
}