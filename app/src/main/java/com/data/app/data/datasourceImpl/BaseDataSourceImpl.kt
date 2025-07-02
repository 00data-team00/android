package com.data.app.data.datasourceImpl

import com.data.app.data.datasource.BaseDataSource
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
import com.data.app.data.response_dto.ResponseMyPostDto
import com.data.app.data.response_dto.ResponseProfileDto
import com.data.app.data.response_dto.ResponseQuizDto
import com.data.app.data.response_dto.ResponseRegisterDto
import com.data.app.data.response_dto.ResponseUserGameInfoDto
import com.data.app.data.service.BaseService
import okhttp3.MultipartBody
import retrofit2.Response
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

    // community
    override suspend fun getUserProfile(token: String, userId: Int): ResponseProfileDto = baseService.getUserProfile(token, userId)
    override suspend fun writePost(
        token: String,
        content: String,
        image: MultipartBody.Part?
    ): ResponseMyPostDto.PostDto = baseService.writePost(token, content, image)

    // home
    override suspend fun getUserGameInfo(token: String): ResponseUserGameInfoDto = baseService.getUserGameInfo(token)

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

    // quiz
    override suspend fun getQuiz(token: String, requestQuizDto: RequestQuizDto): ResponseQuizDto = baseService.getQuiz(token, requestQuizDto)
    override suspend fun quizComplete(toke: String, level: Int): Response<Unit> = baseService.quizComplete(toke, level)

    // explore
    override suspend fun getAllPrograms(
        isFree: Boolean,
        sort: String,
        page: Int,
        size: Int
    ): ResponseAllProgramDto = baseService.getAllPrograms(isFree, sort, page, size)

    override suspend fun getDeadLinePrograms(): ResponseDeadlineDto = baseService.getDeadLinePrograms()

    // my
    override suspend fun getMyProfile(token: String): ResponseProfileDto = baseService.getProfile(token)
    override suspend fun getMyPosts(token: String): ResponseMyPostDto = baseService.getMyPost(token)
    override suspend fun editProfile(token: String, image: MultipartBody.Part): ResponseEditProfileDto = baseService.editProfile(token, image)

    // followers
    override suspend fun getFollowerList(token: String): ResponseFollowersDto = baseService.getFollowerList(token)
    // following


}