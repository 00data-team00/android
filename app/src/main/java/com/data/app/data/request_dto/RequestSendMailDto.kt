package com.data.app.data.request_dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestSendMailDto (
    @SerialName("email")
    val email:String,
)