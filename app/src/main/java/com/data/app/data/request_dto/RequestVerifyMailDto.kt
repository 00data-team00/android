package com.data.app.data.request_dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestVerifyMailDto (
    @SerialName ("email")
    val email:String,
    @SerialName("verificationCode")
    val verificationCode:String,
)