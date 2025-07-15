package com.data.app.data.response_dto.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseLoginDto (
    @SerialName("accessToken")
    val accessToken: String? = null,
    @SerialName("refreshToken")
    val refreshToken: String? = null,
    @SerialName("tokenType")
    val tokenType:String,
    @SerialName("expiresIn")
    val expiresIn:Int,
)