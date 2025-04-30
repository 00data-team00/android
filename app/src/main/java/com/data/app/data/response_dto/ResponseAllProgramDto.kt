package com.data.app.data.response_dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseAllProgramDto (
    @SerialName("content")
    val content: List<ProgramDto>,

    @SerialName("pageable")
    val pageable: PageableDto,

    @SerialName("totalPages")
    val totalPages: Int,

    @SerialName("totalElements")
    val totalElements: Int,

    @SerialName("last")
    val last: Boolean,

    @SerialName("size")
    val size: Int,

    @SerialName("number")
    val number: Int,

    @SerialName("sort")
    val sort: SortDto,

    @SerialName("numberOfElements")
    val numberOfElements: Int,

    @SerialName("first")
    val first: Boolean,

    @SerialName("empty")
    val empty: Boolean
){
    @Serializable
    data class ProgramDto(
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
    @Serializable
    data class PageableDto(
        @SerialName("pageNumber")
        val pageNumber: Int,

        @SerialName("pageSize")
        val pageSize: Int,

        @SerialName("sort")
        val sort: SortDto,

        @SerialName("offset")
        val offset: Int,

        @SerialName("paged")
        val paged: Boolean,

        @SerialName("unpaged")
        val unpaged: Boolean
    )
    @Serializable
    data class SortDto(
        @SerialName("empty")
        val empty: Boolean,

        @SerialName("sorted")
        val sorted: Boolean,

        @SerialName("unsorted")
        val unsorted: Boolean
    )
}