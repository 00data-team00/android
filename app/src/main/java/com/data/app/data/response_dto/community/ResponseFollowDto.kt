package com.data.app.data.response_dto.community

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseFollowDto(
    @SerialName("success")
    val success: Boolean,
    @SerialName("msg")
    val msg: String
)