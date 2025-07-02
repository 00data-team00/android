package com.data.app.data.response_dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseAllProgramDto(
    @SerialName("totalPages")
    val totalPages: Int,
    @SerialName("totalElements")
    val totalElements: Int,
    @SerialName("size")
    val size: Int,
    @SerialName("content")
    val content: List<ProgramDto>,
    @SerialName("number")
    val number: Int,
    @SerialName("sort")
    val sort: SortDto,
    @SerialName("numberOfElements")
    val numberOfElements: Int,
    @SerialName("pageable")
    val pageable: PageableDto,
    @SerialName("first")
    val first: Boolean,
    @SerialName("last")
    val last: Boolean,
    @SerialName("empty")
    val empty: Boolean
) {
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
        val appEndDate: String, // "YYYY-MM-DD" 형식의 문자열
        @SerialName("isFree")
        val isFree: Boolean,
        @SerialName("appLink")
        val appLink: String,
        @SerialName("thumbnailUrl")
        val thumbnailUrl:String?
    )

    @Serializable
    data class PageableDto(
        @SerialName("offset")
        val offset: Int,
        @SerialName("sort")
        val sort: SortDto, // 중첩된 SortDto
        @SerialName("paged")
        val paged: Boolean,
        @SerialName("pageNumber")
        val pageNumber: Int,
        @SerialName("pageSize")
        val pageSize: Int,
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