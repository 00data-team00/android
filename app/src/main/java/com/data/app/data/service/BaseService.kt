package com.data.app.data.service

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
import com.data.app.data.response_dto.community.ResponsePostDetailDto
import com.data.app.data.response_dto.community.ResponseTimeLineDto
import com.data.app.data.response_dto.my.ResponseMyPostDto
import com.data.app.data.response_dto.my.ResponseProfileDto
import com.data.app.data.response_dto.home.quiz.ResponseQuizDto
import com.data.app.data.response_dto.login.ResponseRegisterDto
import com.data.app.data.response_dto.home.ResponseUserGameInfoDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
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

    // community

    @GET("api/posts/timeline")
    suspend fun getAllTimeLine(
        @Header("Authorization") token:String,
    ): ResponseTimeLineDto

    @GET("api/me/posts/timeline/nation")
    suspend fun getNationTimeLine(
        @Header("Authorization") token:String,
    ): ResponseTimeLineDto

    @GET("api/me/posts/timeline/following")
    suspend fun getFollowingTimeLine(
        @Header("Authorization") token:String,
    ): ResponseTimeLineDto

    @GET("api/posts/{postId}/detail")
    suspend fun getPostDetail(
        @Header("Authorization") token:String,
        @Path("postId") postId:Int,
    ): ResponsePostDetailDto

    @POST("api/me/posts/{postId}/like")
    suspend fun likePost(
        @Header("Authorization") token:String,
        @Path("postId") postId:Int,
    ):Response<Unit>

    @DELETE("api/me/posts/{postId}/like")
    suspend fun unlikePost(
        @Header("Authorization") token:String,
        @Path("postId") postId:Int,
    ):Response<Unit>

    @POST("api/me/posts/{postId}/comments")
    suspend fun writeComment(
        @Header("Authorization") token:String,
        @Path("postId") postId:Int,
        @Body content:String,
    ): ResponsePostDetailDto.CommentDto

    @GET("api/users/{userId}/profile")
    suspend fun getUserProfile(
        @Header("Authorization") token:String,
        @Path("userId") userId:Int,
    ): ResponseProfileDto

    @Multipart
    @POST("api/me/posts")
    suspend fun writePost(
        @Header("Authorization") token:String,
        @Part("content") content: String,
        @Part image: MultipartBody.Part?
    ): ResponseMyPostDto.PostDto

    // home
    @GET("api/me/user-game-info")
    suspend fun getUserGameInfo(
        @Header("Authorization") token:String,
    ): ResponseUserGameInfoDto


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

    // quiz
    @POST("api/game/quiz")
    suspend fun getQuiz(
        @Header("Authorization") token: String,
        @Body requestQuizDto: RequestQuizDto
    ): ResponseQuizDto

    @PATCH("api/game/me/complete")
    suspend fun quizComplete(
        @Header("Authorization") token: String,
        @Query("quizId") level:Int,
    ): Response<Unit>

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

    // my

    @GET("api/me/profile")
    suspend fun getProfile(
        @Header("Authorization") token:String
    ): ResponseProfileDto

    @GET("api/me/posts")
    suspend fun getMyPost(
        @Header("Authorization") token:String
    ): ResponseMyPostDto

    @Multipart
    @POST("api/me/profile/image")
    suspend fun editProfile(
        @Header("Authorization") token:String,
        @Part image: MultipartBody.Part
    ): ResponseEditProfileDto
    // followers
    @GET("api/me/followers")
    suspend fun getFollowerList(
        @Header("Authorization") token:String
    ):ResponseFollowersDto
}