package com.data.app.data.datasourceImpl

import com.data.app.data.datasource.BaseDataSource
import com.data.app.data.request_dto.RequestLoginDto
import com.data.app.data.request_dto.RequestRegisterDto
import com.data.app.data.request_dto.RequestSendMailDto
import com.data.app.data.request_dto.RequestVerifyMailDto
import com.data.app.data.response_dto.ResponseAITopicsDto
import com.data.app.data.response_dto.ResponseAllProgramDto
import com.data.app.data.response_dto.ResponseLoginDto
import com.data.app.data.response_dto.ResponseRegisterDto
import com.data.app.data.service.BaseService
import javax.inject.Inject

class BaseDataSourceImpl @Inject constructor(
    private val baseService: BaseService
):BaseDataSource {
    override suspend fun sendMail(email: RequestSendMailDto): ResponseRegisterDto = baseService.sendMail(email)

    override suspend fun verifyMail(requestVerifyMailDto: RequestVerifyMailDto): ResponseRegisterDto = baseService.verifyMail(requestVerifyMailDto)

    override suspend fun register(requestRegisterDto: RequestRegisterDto): ResponseRegisterDto = baseService.register(requestRegisterDto)

    override suspend fun login(requestLoginDto: RequestLoginDto): ResponseLoginDto = baseService.login(requestLoginDto)

    override suspend fun getAIChatTopics(): ResponseAITopicsDto = baseService.getAIChatTopics()

    override suspend fun getAllPrograms(
        isFree: Boolean,
        sort: String,
        page: Int,
        size: Int
    ): ResponseAllProgramDto = baseService.getAllPrograms(isFree, sort, page, size)
}