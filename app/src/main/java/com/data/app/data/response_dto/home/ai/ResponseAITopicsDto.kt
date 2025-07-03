package com.data.app.data.response_dto.home.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseAITopicsDto(
    @SerialName("essentialTopics")
    val essentialTopics: List<TopicDto>,

    @SerialName("culturalTopics")
    val culturalTopics: List<TopicDto>,

    @SerialName("businessTopics")
    val businessTopics: List<TopicDto>
){
    @Serializable
    data class TopicDto(
        @SerialName("id")
        val id:Int,
        @SerialName("category")
        val category: String,

        @SerialName("title")
        val title: String,

        @SerialName("description")
        val description: String,

        @SerialName("userRole")
        val userRole: String,

        @SerialName("aiRole")
        val aiRole: String
    )

}