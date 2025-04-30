package com.data.app.data.service

import com.data.app.data.request_dto.RequestLoginDto
import com.data.app.data.request_dto.RequestRegisterDto
import com.data.app.data.request_dto.RequestSendMailDto
import com.data.app.data.request_dto.RequestVerifyMailDto
import com.data.app.data.response_dto.ResponseAITopicsDto
import com.data.app.data.response_dto.ResponseAllProgramDto
import com.data.app.data.response_dto.ResponseLoginDto
import com.data.app.data.response_dto.ResponseRegisterDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface BaseService {
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


    @POST("api/login")
    suspend fun login(
        @Body requestLoginDto: RequestLoginDto
    ):ResponseLoginDto


    @GET("api/chat/topics")
    suspend fun getAIChatTopics(
        //@Header("Authorization") token:String,
    ):ResponseAITopicsDto

    @GET("api/edu-info")
    suspend fun getAllPrograms(
        @Query ("isFree") isFree:Boolean,
        @Query("sort") sort:String,
        @Query("page") page:Int,
        @Query("size") size:Int,
    ):ResponseAllProgramDto
}