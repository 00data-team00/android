package com.data.app.data.repositoryImpl

import com.data.app.data.datasource.BaseDataSource
import com.data.app.data.request_dto.RequestLoginDto
import com.data.app.data.request_dto.RequestRegisterDto
import com.data.app.data.request_dto.RequestSendMailDto
import com.data.app.data.request_dto.RequestVerifyMailDto
import com.data.app.data.response_dto.ResponseAITopicsDto
import com.data.app.data.response_dto.ResponseAllProgramDto
import com.data.app.data.response_dto.ResponseLoginDto
import com.data.app.data.response_dto.ResponseRegisterDto
import com.data.app.domain.repository.BaseRepository
import timber.log.Timber
import javax.inject.Inject

class BaseRepositoryImpl @Inject constructor(
    private val baseDataSource: BaseDataSource
):BaseRepository {
    override suspend fun sendMail(email: String): Result<ResponseRegisterDto> {
        return runCatching {
            baseDataSource.sendMail(RequestSendMailDto(email))
        }.onFailure {
            Timber.e("base repository sendMail fail: $it")
        }
    }

    override suspend fun verifyMail(
        email: String,
        verificationCode: String
    ): Result<ResponseRegisterDto> {
        return runCatching {
            baseDataSource.verifyMail(RequestVerifyMailDto(email, verificationCode))
        }.onFailure {
            Timber.e("base repository verify mail fail: $it")
        }
    }

    override suspend fun register(
        email: String,
        name: String,
        pw: String,
        nation: Int
    ): Result<ResponseRegisterDto> {
        return runCatching {
            baseDataSource.register(RequestRegisterDto(email, name, pw, nation))
        }.onFailure {
            Timber.e("base repository register fail: $it")
        }
    }

    override suspend fun login(email: String, pw: String): Result<ResponseLoginDto> {
        return runCatching {
            baseDataSource.login(RequestLoginDto(email, pw))
        }.onFailure {
            Timber.e("base repository login fail: $it")
        }
    }

    override suspend fun getAIChatTopics(): Result<ResponseAITopicsDto> {
        return runCatching {
            baseDataSource.getAIChatTopics()
        }.onFailure {
            Timber.d("base repository get ai chat topics fail: $it")
        }
    }

    override suspend fun getAllPrograms(
        isFree: Boolean,
        page: Int,
        size: Int
    ): Result<ResponseAllProgramDto> {
        return runCatching {
            baseDataSource.getAllPrograms(isFree, "regDt", page, size)
        }.onFailure {
            Timber.d("base repository get all programs fail: $it")
        }
    }
}