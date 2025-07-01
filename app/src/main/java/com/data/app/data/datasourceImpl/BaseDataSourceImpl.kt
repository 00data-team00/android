package com.data.app.data.datasourceImpl

import com.data.app.data.datasource.BaseDataSource
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
import com.data.app.data.response_dto.ResponseFollowersDto
import com.data.app.data.response_dto.ResponseLoginDto
import com.data.app.data.response_dto.ResponseRegisterDto
import com.data.app.data.service.BaseService
import javax.inject.Inject

class BaseDataSourceImpl @Inject constructor(
    private val baseService: BaseService
):BaseDataSource {
    // sign up
    override suspend fun sendMail(email: RequestSendMailDto): ResponseRegisterDto = baseService.sendMail(email)
    override suspend fun verifyMail(requestVerifyMailDto: RequestVerifyMailDto): ResponseRegisterDto = baseService.verifyMail(requestVerifyMailDto)
    override suspend fun register(requestRegisterDto: RequestRegisterDto): ResponseRegisterDto = baseService.register(requestRegisterDto)

    // login
    override suspend fun login(requestLoginDto: RequestLoginDto): ResponseLoginDto = baseService.login(requestLoginDto)

    // ai chat
    override suspend fun getAIChatTopics(): ResponseAITopicsDto = baseService.getAIChatTopics()
    override suspend fun startChat(token: String, topicId: Int): ResponseChatStartDto = baseService.startChat(token, topicId)
    override suspend fun getAiChat(
        token: String,
        requestChatAiMessageDto: RequestChatAiMessageDto
    ): ResponseChatAiMessageDto = baseService.getAiChat(token,requestChatAiMessageDto)
    override suspend fun getAIPreviousList(token: String): ResponseAIPreviousRecordsDto = baseService.getAIPreviousList(token)
    override suspend fun getAIPreviousChatMessages(
        token: String,
        chatRoomId: Int
    ): ResponseAIPreviousChatMessagesDto = baseService.getAIPreviousChatMessages(token, chatRoomId)

    // explore
    override suspend fun getAllPrograms(
        isFree: Boolean,
        sort: String,
        page: Int,
        size: Int
    ): ResponseAllProgramDto = baseService.getAllPrograms(isFree, sort, page, size)

    override suspend fun getDeadLinePrograms(): ResponseDeadlineDto = baseService.getDeadLinePrograms()

    // followers
    override suspend fun getFollowerList(token: String): ResponseFollowersDto = baseService.getFollowerList(token)
    // following


}