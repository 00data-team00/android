package com.data.app.extension.home.quiz

sealed class QuizCompleteState {
    object Loading : QuizCompleteState()
    object Success: QuizCompleteState()
    data class Error(val message: String) : QuizCompleteState()
}