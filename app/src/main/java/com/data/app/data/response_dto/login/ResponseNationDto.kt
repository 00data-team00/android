package com.data.app.data.response_dto.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseNationDto(
    @SerialName("totalCount")
    val totalCount: Int,
    @SerialName("nations")
    val nations: List<Nations>
) {
    @Serializable
    data class Nations(
        @SerialName("id")
        val id: Int,
        @SerialName("code")
        val code: String,
        @SerialName("name")
        val name: String,
        @SerialName("nameKo")
        val nameKo: String
    )
}