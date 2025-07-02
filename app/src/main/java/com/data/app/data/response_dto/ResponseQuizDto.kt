package com.data.app.data.response_dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseQuizDto (
    @SerialName("quizDtoList")
    val quizDtoList: List<QuizDto>
){
    @Serializable
    data class QuizDto(
        @SerialName("quizId")
        val QuizId:Int,
        @SerialName("category")
        val category: String,
        @SerialName("quizText")
        val quizText: String,
        @SerialName("choices")
        val choices: List<ChoiceDto>,
        @SerialName("image")
        val image: String,
        @SerialName("voice")
        val voice: String?,
        @SerialName("answer")
        val answer: Int,
        @SerialName("wordScript")
        val wordScript: String,
        @SerialName("answerScript")
        val answerScript: String
    ): java.io.Serializable{
        @Serializable
        data class ChoiceDto(
            @SerialName("id")
            val id: Int,
            @SerialName("word")
            val word: String,
            @SerialName("description")
            val description: String
        ): java.io.Serializable
    }
}