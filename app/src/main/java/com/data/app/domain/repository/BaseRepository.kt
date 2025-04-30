package com.data.app.domain.repository

import com.data.app.data.response_dto.ResponseAITopicsDto
import com.data.app.data.response_dto.ResponseAllProgramDto
import com.data.app.data.response_dto.ResponseLoginDto
import com.data.app.data.response_dto.ResponseRegisterDto

interface BaseRepository {
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

    suspend fun login(
        email:String,
        pw:String,
    ):Result<ResponseLoginDto>

    suspend fun getAIChatTopics(
    ):Result<ResponseAITopicsDto>

    suspend fun getAllPrograms(
        isFree:Boolean,
        page:Int,
        size:Int,
    ):Result<ResponseAllProgramDto>
}