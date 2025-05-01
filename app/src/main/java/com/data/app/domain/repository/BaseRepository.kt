package com.data.app.domain.repository

import com.data.app.data.response_dto.ResponseAIPreviousChatMessagesDto
import com.data.app.data.response_dto.ResponseAIPreviousRecordsDto
import com.data.app.data.response_dto.ResponseAITopicsDto
import com.data.app.data.response_dto.ResponseAllProgramDto
import com.data.app.data.response_dto.ResponseDeadlineDto
import com.data.app.data.response_dto.ResponseLoginDto
import com.data.app.data.response_dto.ResponseRegisterDto

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

    // login
    suspend fun login(
        email:String,
        pw:String,
    ):Result<ResponseLoginDto>

    // ai chat
    suspend fun getAIChatTopics():Result<ResponseAITopicsDto>

    suspend fun getAIPreviousList(
        token:String,
    ):Result<ResponseAIPreviousRecordsDto>

    suspend fun getAIPreviousChatMessages(
        token:String,
        chatRoomId:Int,
    ):Result<ResponseAIPreviousChatMessagesDto>

    // explore
    suspend fun getAllPrograms(
        isFree:Boolean,
        page:Int,
        size:Int,
    ):Result<ResponseAllProgramDto>

    suspend fun getDeadLinePrograms():Result<ResponseDeadlineDto>
}