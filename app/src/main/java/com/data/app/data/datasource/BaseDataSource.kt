package com.data.app.data.datasource

import com.data.app.data.request_dto.RequestChatAiMessageDto
import com.data.app.data.request_dto.RequestLoginDto
import com.data.app.data.request_dto.RequestQuizDto
import com.data.app.data.request_dto.RequestRegisterDto
import com.data.app.data.request_dto.RequestSendMailDto
import com.data.app.data.request_dto.RequestVerifyMailDto
import com.data.app.data.response_dto.ResponseAIPreviousChatMessagesDto
import com.data.app.data.response_dto.ResponseAIPreviousRecordsDto
import com.data.app.data.response_dto.ResponseAITopicsDto
import com.data.app.data.response_dto.ResponseAllProgramDto
import com.data.app.data.response_dto.ResponseChatAiMessageDto
import com.data.app.data.response_dto.ResponseChatStartDto
import com.data.app.data.response_dto.ResponseDeadlineDto
import com.data.app.data.response_dto.ResponseFollowersDto
import com.data.app.data.response_dto.ResponseLoginDto
import com.data.app.data.response_dto.ResponseEditProfileDto
import com.data.app.data.response_dto.ResponseQuizDto
import com.data.app.data.response_dto.ResponseRegisterDto
import com.data.app.data.response_dto.ResponseUserGameInfoDto
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