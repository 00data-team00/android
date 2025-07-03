package com.data.app.data.response_dto.home.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseTranslateDto (
    @SerialName("translationId")
    val translationId:Int,
    @SerialName("lang")
    val lang:String,
    @SerialName("text")
    val text:String
)