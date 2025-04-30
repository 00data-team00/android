package com.data.app.data.datasource

import com.data.app.data.request_dto.RequestLoginDto
import com.data.app.data.request_dto.RequestRegisterDto
import com.data.app.data.request_dto.RequestSendMailDto
import com.data.app.data.request_dto.RequestVerifyMailDto
import com.data.app.data.response_dto.ResponseAITopicsDto
import com.data.app.data.response_dto.ResponseAllProgramDto
import com.data.app.data.response_dto.ResponseLoginDto
import com.data.app.data.response_dto.ResponseRegisterDto

interface BaseDataSource {
    suspend fun sendMail(
        email:RequestSendMailDto,
    ):ResponseRegisterDto

    suspend fun verifyMail(
        requestVerifyMailDto: RequestVerifyMailDto
    ):ResponseRegisterDto

    suspend fun register(
        requestRegisterDto: RequestRegisterDto
    ):ResponseRegisterDto

    suspend fun login(
        requestLoginDto: RequestLoginDto
    ):ResponseLoginDto

    suspend fun getAIChatTopics(
        token:String,
    ):ResponseAITopicsDto

    suspend fun getAllPrograms(
        isFree:Boolean,
        sort:String,
        page:Int,
        size:Int,
    ):ResponseAllProgramDto
}