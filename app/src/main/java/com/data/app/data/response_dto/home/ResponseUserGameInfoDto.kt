package com.data.app.data.response_dto.home

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseUserGameInfoDto (
    @SerialName("userName")
    val userName:String,
    @SerialName("totalQuizzesSolved")
    val totalQuizSolved:Int,
    @SerialName("quizzesSolvedToday")
    val quizSolvedToday:Int,
    @SerialName("chatRoomsCreated")
    val chatRoomsCreated:Int,
    @SerialName("levelCompleted")
    val levelCompleted:Int,
)