package com.data.app.domain.repository

import com.data.app.data.response_dto.home.ai.ResponseAIPreviousChatMessagesDto
import com.data.app.data.response_dto.home.ai.ResponseAIPreviousRecordsDto
import com.data.app.data.response_dto.home.ai.ResponseAITopicsDto
import com.data.app.data.response_dto.explore.ResponseAllProgramDto
import com.data.app.data.response_dto.home.ai.ResponseChatAiMessageDto
import com.data.app.data.response_dto.home.ai.ResponseChatStartDto
import com.data.app.data.response_dto.explore.ResponseDeadlineDto
import com.data.app.data.response_dto.community.ResponseFollowersDto
import com.data.app.data.response_dto.login.ResponseLoginDto
import com.data.app.data.response_dto.community.ResponseEditProfileDto
import com.data.app.data.response_dto.community.ResponsePostDetailDto
import com.data.app.data.response_dto.community.ResponseTimeLineDto
import com.data.app.data.response_dto.my.ResponseMyPostDto
import com.data.app.data.response_dto.my.ResponseProfileDto
import com.data.app.data.response_dto.home.quiz.ResponseQuizDto
import com.data.app.data.response_dto.login.ResponseRegisterDto
import com.data.app.data.response_dto.home.ResponseUserGameInfoDto
import okhttp3.MultipartBody
import retrofit2.Response

interface BaseRepository {
    // sign up
    suspend fun sendMail(
        email:String,
    ):Result<ResponseRegisterDto>

    suspend fun verifyMail(
        email:String,
        verificationCode:String,
    ):Result<ResponseRegisterDto>

    suspend fun register(
        email:String,
        name:String,
        pw:String,
        nation:Int,
    ):Result<ResponseRegisterDto>

    // login
    suspend fun login(
        email:String,
        pw:String,
    ):Result<ResponseLoginDto>

    // community
    suspend fun getAllTimeLine(
        token:String,
    ):Result<ResponseTimeLineDto>

    suspend fun getNationTimeLine(
        token:String,
    ):Result<ResponseTimeLineDto>

    suspend fun getFollowingTimeLine(
        token:String,
    ):Result<ResponseTimeLineDto>

    suspend fun getPostDetail(
        token:String,
        postId:Int,
    ):Result<ResponsePostDetailDto>

    suspend fun likePost(
        token:String,
        postId:Int,
    ):Result<Response<Unit>>

    suspend fun unlikePost(
        token:String,
        postId:Int,
    ):Result<Response<Unit>>

    suspend fun writeComment(
        token:String,
        postId:Int,
        content:String,
    ):Result<ResponsePostDetailDto.CommentDto>

    suspend fun getUserProfile(
        token:String,
        userId:Int,
    ):Result<ResponseProfileDto>

    suspend fun writePost(
        token:String,
        content:String,
        image:MultipartBody.Part?
    ):Result<ResponseMyPostDto.PostDto>

    // home
    suspend fun getUserGameInfo(
        token:String,
    ):Result<ResponseUserGameInfoDto>

    // ai chat
    suspend fun getAIChatTopics():Result<ResponseAITopicsDto>

    suspend fun startChat(
        token: String,
        topicId:Int,
    ):Result<ResponseChatStartDto>

    suspend fun getAiChat(
        token:String,
        chatRoomId: Int,
        text:String,
    ):Result<ResponseChatAiMessageDto>

    suspend fun getAIPreviousList(
        token:String,
    ):Result<ResponseAIPreviousRecordsDto>

    suspend fun getAIPreviousChatMessages(
        token:String,
        chatRoomId:Int,
    ):Result<ResponseAIPreviousChatMessagesDto>

    // quiz
    suspend fun getQuiz(
        token:String,
        level:Int,
        userLang: String
    ):Result<ResponseQuizDto>

    suspend fun quizComplete(
        token:String,
        level:Int,
    ): Result<Response<Unit>>

    // explore
    suspend fun getAllPrograms(
        isFree:Boolean,
        page:Int,
        size:Int,
    ):Result<ResponseAllProgramDto>

    suspend fun getDeadLinePrograms():Result<ResponseDeadlineDto>

    // my
    suspend fun getMyProfile(
        token:String
    ):Result<ResponseProfileDto>

    suspend fun getMyPosts(
        token:String,
    ):Result<ResponseMyPostDto>

    suspend fun editProfile(
        token:String,
        image:MultipartBody.Part
    ):Result<ResponseEditProfileDto>

    // followers
    suspend fun getFollowerList(
        token:String
    ):Result<ResponseFollowersDto>
}