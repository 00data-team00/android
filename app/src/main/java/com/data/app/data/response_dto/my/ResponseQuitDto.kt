package com.data.app.data.response_dto.my

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseQuitDto (
    @SerialName("success")
    val success:Boolean,
    @SerialName("msg")
    val msg:String
)