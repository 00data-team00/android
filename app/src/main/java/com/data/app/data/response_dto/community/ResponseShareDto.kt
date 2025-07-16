package com.data.app.data.response_dto.community

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseShareDto (
    @SerialName("shareUrl")
    val shareUrl:String,
    @SerialName("token")
    val token:String,
)