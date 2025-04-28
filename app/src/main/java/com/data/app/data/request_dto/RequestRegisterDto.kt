package com.data.app.data.request_dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestRegisterDto (
    @SerialName("email")
    val email:String,
    @SerialName("name")
    val name:String,
    @SerialName("password")
    val pw:String,
    @SerialName("nations")
    val nation:Int,
)