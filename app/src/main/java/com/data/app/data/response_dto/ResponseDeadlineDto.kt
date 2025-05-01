package com.data.app.data.response_dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseDeadlineDto(
    @SerialName("eduPrograms")
    val eduPrograms: List<EduProgram>
){
    @Serializable
    data class EduProgram(
        @SerialName("id")
        val id: Int,

        @SerialName("titleNm")
        val titleNm: String,

        @SerialName("appQual")
        val appQual: String,

        @SerialName("tuitEtc")
        val tuitEtc: String,

        @SerialName("appEndDate")
        val appEndDate: String
    )
}