package com.data.app.data.response_dto.community

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetIdFromTokenDto (
    @SerialName("contentId")
    val contentId:Int,
    @SerialName("contentType")
    val contentType:String,
)