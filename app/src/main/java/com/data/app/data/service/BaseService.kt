package com.data.app.data.service

import com.data.app.data.request_dto.RequestChatAiMessageDto
import com.data.app.data.request_dto.RequestLoginDto
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
import com.data.app.data.response_dto.ResponseLoginDto
import com.data.app.data.response_dto.ResponseRegisterDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface BaseService {
    // sign up
    @POST("api/mail/send")
    suspend fun sendMail(
        @Body email:RequestSendMailDto,
    ):ResponseRegisterDto

    @POST("api/mail/verify")
    suspend fun verifyMail(
        @Body requestVerifyMailDto: RequestVerifyMailDto
    ):ResponseRegisterDto

    @POST("api/user/register")
    suspend fun register(
        @Body requestRegisterDto: RequestRegisterDto
    ):ResponseRegisterDto

    // login
    @POST("api/login")
    suspend fun login(
        @Body requestLoginDto: RequestLoginDto
    ):ResponseLoginDto

    // ai chat
    @GET("api/chat/topics")
    suspend fun getAIChatTopics(
        //@Header("Authorization") token:String,
    ):ResponseAITopicsDto

    @POST("api/chat/me/start")
    suspend fun startChat(
        @Header("Authorization") token:String,
        @Query("topicId") topicId:Int
    ):ResponseChatStartDto

    @POST("api/chat/me/receive")
    suspend fun getAiChat(
        @Header("Authorization") token:String,
        @Body requestAiChatDto: RequestChatAiMessageDto,
    ):ResponseChatAiMessageDto

    @GET("api/me/chat/chatrooms")
    suspend fun getAIPreviousList(
        @Header("Authorization") token:String,
    ):ResponseAIPreviousRecordsDto

    @GET("api/me/chat/messages")
    suspend fun getAIPreviousChatMessages(
        @Header("Authorization") token:String,
        @Query("chatRoomId") chatRoomId:Int
    ):ResponseAIPreviousChatMessagesDto

    // explore
    @GET("api/edu-info")
    suspend fun getAllPrograms(
        @Query ("isFree") isFree:Boolean,
        @Query("sort") sort:String,
        @Query("page") page:Int,
        @Query("size") size:Int,
    ):ResponseAllProgramDto

    @GET("api/edu-info/closing-soon")
    suspend fun getDeadLinePrograms():ResponseDeadlineDto
}