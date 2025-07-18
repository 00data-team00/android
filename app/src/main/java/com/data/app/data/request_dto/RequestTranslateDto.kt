package com.data.app.data.request_dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestTranslateDto (
    @SerialName("messageId")
    val messageId:Int,
    @SerialName("userLang")
    val userLang:String,
)