package com.data.app.data.datasource

import com.data.app.data.request_dto.RequestChatAiMessageDto
import com.data.app.data.request_dto.RequestLoginDto
import com.data.app.data.request_dto.RequestQuizDto
import com.data.app.data.request_dto.RequestRegisterDto
import com.data.app.data.request_dto.RequestSendMailDto
import com.data.app.data.request_dto.RequestVerifyMailDto
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
import com.data.app.data.response_dto.community.ResponseTimeLineDto
import com.data.app.data.response_dto.my.ResponseMyPostDto
import com.data.app.data.response_dto.my.ResponseProfileDto
import com.data.app.data.response_dto.home.quiz.ResponseQuizDto
import com.data.app.data.response_dto.login.ResponseRegisterDto
import com.data.app.data.response_dto.home.ResponseUserGameInfoDto
import okhttp3.MultipartBody
import retrofit2.Response

interface BaseDataSource {
    // sign up
    suspend fun sendMail(
        email:RequestSendMailDto,
    ):ResponseRegisterDto

    suspend fun verifyMail(
        requestVerifyMailDto: RequestVerifyMailDto
    ):ResponseRegisterDto

    suspend fun register(
        requestRegisterDto: RequestRegisterDto
    ):ResponseRegisterDto

    // login
    suspend fun login(
        requestLoginDto: RequestLoginDto
    ):ResponseLoginDto

    // community

    suspend fun getAllTimeLine(
        token:String,
    ): ResponseTimeLineDto

    suspend fun getNationTimeLine(
        token:String,
    ): ResponseTimeLineDto

    suspend fun getFollowingTimeLine(
        token:String,
    ): ResponseTimeLineDto

    suspend fun getUserProfile(
        token:String,
        userId:Int,
    ):ResponseProfileDto

    suspend fun writePost(
        token:String,
        content:String,
        image:MultipartBody.Part?
    ): ResponseMyPostDto.PostDto

    // home
    suspend fun getUserGameInfo(
        token:String,
    ): ResponseUserGameInfoDto

    // ai chat
    suspend fun getAIChatTopics(
        //token:String,
    ):ResponseAITopicsDto

    suspend fun startChat(
        token: String,
        topicId:Int,
    ):ResponseChatStartDto

    suspend fun getAiChat(
        token:String,
        requestChatAiMessageDto: RequestChatAiMessageDto
    ):ResponseChatAiMessageDto

    suspend fun getAIPreviousList(
        token:String,
    ):ResponseAIPreviousRecordsDto

    suspend fun getAIPreviousChatMessages(
        token:String,
        chatRoomId:Int
    ):ResponseAIPreviousChatMessagesDto

    // quiz
    suspend fun getQuiz(
        token:String,
        requestQuizDto: RequestQuizDto
    ): ResponseQuizDto

    suspend fun quizComplete(
        toke:String,
        level:Int,
    ): Response<Unit>

    // explore
    suspend fun getAllPrograms(
        isFree:Boolean,
        sort:String,
        page:Int,
        size:Int,
    ):ResponseAllProgramDto

    suspend fun getDeadLinePrograms():ResponseDeadlineDto

    // my
    suspend fun getMyProfile(
        token:String
    ): ResponseProfileDto

    suspend fun getMyPosts(
        token:String,
    ): ResponseMyPostDto

    suspend fun editProfile(
        token:String,
        image: MultipartBody.Part
    ): ResponseEditProfileDto

    // followers
    suspend fun getFollowerList(
        token: String
    ):ResponseFollowersDto

    // followings


}