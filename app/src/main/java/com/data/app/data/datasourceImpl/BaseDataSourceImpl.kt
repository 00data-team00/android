package com.data.app.data.datasourceImpl

import com.data.app.data.datasource.BaseDataSource
import com.data.app.data.request_dto.RequestChatAiMessageDto
import com.data.app.data.request_dto.RequestLoginDto
import com.data.app.data.request_dto.RequestQuizDto
import com.data.app.data.request_dto.RequestRegisterDto
import com.data.app.data.request_dto.RequestSendMailDto
import com.data.app.data.request_dto.RequestTranslateDto
import com.data.app.data.request_dto.RequestVerifyMailDto
import com.data.app.data.response_dto.community.ResponseDeletePostDto
import com.data.app.data.response_dto.home.ai.ResponseAIPreviousChatMessagesDto
import com.data.app.data.response_dto.home.ai.ResponseAIPreviousRecordsDto
import com.data.app.data.response_dto.home.ai.ResponseAITopicsDto
import com.data.app.data.response_dto.explore.ResponseAllProgramDto
import com.data.app.data.response_dto.home.ai.ResponseChatAiMessageDto
import com.data.app.data.response_dto.home.ai.ResponseChatStartDto
import com.data.app.data.response_dto.explore.ResponseDeadlineDto
import com.data.app.data.response_dto.community.ResponseFollowListDto
import com.data.app.data.response_dto.login.ResponseLoginDto
import com.data.app.data.response_dto.community.ResponseEditProfileDto
import com.data.app.data.response_dto.community.ResponseFollowDto
import com.data.app.data.response_dto.community.ResponsePostDetailDto
import com.data.app.data.response_dto.community.ResponseTimeLineDto
import com.data.app.data.response_dto.my.ResponseMyPostDto
import com.data.app.data.response_dto.my.ResponseProfileDto
import com.data.app.data.response_dto.home.quiz.ResponseQuizDto
import com.data.app.data.response_dto.login.ResponseRegisterDto
import com.data.app.data.response_dto.home.ResponseUserGameInfoDto
import com.data.app.data.response_dto.home.ai.ResponseTranslateDto
import com.data.app.data.response_dto.login.ResponseNationDto
import com.data.app.data.response_dto.my.ResponseQuitDto
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
    override suspend fun getNation(): ResponseNationDto = baseService.getNation()

    // login
    override suspend fun login(requestLoginDto: RequestLoginDto): ResponseLoginDto = baseService.login(requestLoginDto)

    // community
    override suspend fun getAllTimeLine(token: String): ResponseTimeLineDto = baseService.getAllTimeLine(token)
    override suspend fun getNationTimeLine(token: String): ResponseTimeLineDto = baseService.getNationTimeLine(token)
    override suspend fun getFollowingTimeLine(token: String): ResponseTimeLineDto = baseService.getFollowingTimeLine(token)
    override suspend fun getPostDetail(token: String, postId: Int): ResponsePostDetailDto = baseService.getPostDetail(token, postId)
    override suspend fun likePost(token: String, postId: Int): Response<Unit> = baseService.likePost(token, postId)
    override suspend fun unlikePost(token: String, postId: Int): Response<Unit> = baseService.unlikePost(token, postId)
    override suspend fun writeComment(token: String, postId: Int, content: String): ResponsePostDetailDto.CommentDto = baseService.writeComment(token, postId, content)
    override suspend fun getUserProfile(token: String, userId: Int): ResponseProfileDto = baseService.getUserProfile(token, userId)
    override suspend fun getUserPosts(token: String, userId: Int): ResponseTimeLineDto = baseService.getUserPosts(token, userId)
    override suspend fun writePost(
        token: String,
        content: String,
        image: MultipartBody.Part?
    ): ResponseMyPostDto.PostDto = baseService.writePost(token, content, image)

    override suspend fun deletePost(token: String, postId: Int): ResponseDeletePostDto = baseService.deletePost(token, postId)
    override suspend fun follow(token: String, userId: Int): ResponseFollowDto = baseService.follow(token, userId)
    override suspend fun unFollow(token: String, userId: Int): ResponseFollowDto = baseService.unFollow(token, userId)
    override suspend fun getFollowerList(token: String): ResponseFollowListDto = baseService.getFollowerList(token)
    override suspend fun getFollowingList(token: String): ResponseFollowListDto = baseService.getFollowingList(token)

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

    // translate
    override suspend fun getTranslate(
        token: String,
        requestTranslateDto: RequestTranslateDto
    ): ResponseTranslateDto = baseService.getTranslate(token,requestTranslateDto)

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
    override suspend fun quit(token: String): ResponseQuitDto = baseService.quit(token)
}