package com.data.app.domain.repository

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
import com.data.app.data.response_dto.community.ResponseGetIdFromTokenDto
import com.data.app.data.response_dto.community.ResponsePostDetailDto
import com.data.app.data.response_dto.community.ResponseShareDto
import com.data.app.data.response_dto.community.ResponseTimeLineDto
import com.data.app.data.response_dto.my.ResponseMyPostDto
import com.data.app.data.response_dto.my.ResponseProfileDto
import com.data.app.data.response_dto.home.quiz.ResponseQuizDto
import com.data.app.data.response_dto.login.ResponseRegisterDto
import com.data.app.data.response_dto.home.ResponseUserGameInfoDto
import com.data.app.data.response_dto.home.ai.ResponseTranslateDto
import com.data.app.data.response_dto.login.ResponseNationDto
import com.data.app.data.response_dto.my.ResponseQuitDto
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

    suspend fun getNation():Result<ResponseNationDto>

    // login
    suspend fun login(
        email:String,
        pw:String,
    ):Result<ResponseLoginDto>
    suspend fun refresh(
        refreshToken:String
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

    suspend fun getUserPosts(
        token:String,
        userId:Int,
    ):Result<ResponseTimeLineDto>

    suspend fun writePost(
        token:String,
        content:String,
        image:MultipartBody.Part?
    ):Result<ResponseMyPostDto.PostDto>

    suspend fun deletePost(
        token:String,
        postId:Int,
    ):Result<ResponseDeletePostDto>

    suspend fun follow(
        token:String,
        userId:Int,
    ):Result<ResponseFollowDto>

    suspend fun unFollow(
        token:String,
        userId:Int,
    ):Result<ResponseFollowDto>


    suspend fun getFollowerList(
        token:String,
        userId:Int
    ): Result<ResponseFollowListDto>

    suspend fun getFollowingList(
        token:String,
        userId:Int
    ):Result<ResponseFollowListDto>

    suspend fun shareProfile(
        userId:Int
    ):Result<ResponseShareDto>

    suspend fun sharePost(
        postId:Int
    ):Result<ResponseShareDto>

    suspend fun getIdFromToken(
        token:String
    ):Result<ResponseGetIdFromTokenDto>

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

    // translate
    suspend fun getTranslate(
        token:String,
        messageId:Int,
        userLang:String
    ):Result<ResponseTranslateDto>

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

    suspend fun logout(
        refreshToken:String
    ):Result<ResponseRegisterDto>
    suspend fun quit(
        token:String,
    ):Result<ResponseQuitDto>

}