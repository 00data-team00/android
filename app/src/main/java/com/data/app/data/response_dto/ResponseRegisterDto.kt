package com.data.app.data.response_dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ResponseRegisterDto (
    @SerialName ("success")
    val success:Boolean,
    @SerialName("msg")
    val msg:String,
)