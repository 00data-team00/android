package com.data.app.data.datasource

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

    // explore
    suspend fun getAllPrograms(
        isFree:Boolean,
        sort:String,
        page:Int,
        size:Int,
    ):ResponseAllProgramDto

    suspend fun getDeadLinePrograms():ResponseDeadlineDto

    // followers
    suspend fun getFollowerList(
        token: String
    ):ResponseFollowersDto

    // followings


}