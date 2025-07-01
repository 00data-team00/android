package com.data.app.data.request_dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestQuizDto (
    @SerialName("level")
    val level:Int,
    @SerialName("userLang")
    val userLang:String
)