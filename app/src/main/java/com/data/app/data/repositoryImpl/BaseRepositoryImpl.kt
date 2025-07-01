package com.data.app.data.repositoryImpl

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
import com.data.app.domain.repository.BaseRepository
import timber.log.Timber
import javax.inject.Inject

class BaseRepositoryImpl @Inject constructor(
    private val baseDataSource: BaseDataSource
):BaseRepository {
    // sign up
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

    // login
    override suspend fun login(email: String, pw: String): Result<ResponseLoginDto> {
        return runCatching {
            baseDataSource.login(RequestLoginDto(email, pw))
        }.onFailure {
            Timber.e("base repository login fail: $it")
        }
    }

    // ai chat
    override suspend fun getAIChatTopics(): Result<ResponseAITopicsDto> {
        return runCatching {
            baseDataSource.getAIChatTopics()
        }.onFailure {
            Timber.e("base repository get ai chat topics fail: $it")
        }
    }

    override suspend fun startChat(token: String, topicId: Int): Result<ResponseChatStartDto> {
        return runCatching {
            baseDataSource.startChat(token,topicId)
        }.onFailure {
            Timber.e("base repository start chat fail: $it")
        }
    }

    override suspend fun getAiChat(
        token: String,
        chatRoomId: Int,
        text: String
    ): Result<ResponseChatAiMessageDto> {
        return runCatching {
            baseDataSource.getAiChat(token, RequestChatAiMessageDto(chatRoomId, text, true))
        }.onFailure {
            Timber.e("base repository get ai chat fail: $it")
        }
    }

    override suspend fun getAIPreviousList(token: String): Result<ResponseAIPreviousRecordsDto> {
        return runCatching {
            baseDataSource.getAIPreviousList(token)
        }.onFailure {
            Timber.e("base repository get ai previous list fail: $it")
        }
    }

    override suspend fun getAIPreviousChatMessages(
        token: String,
        chatRoomId: Int
    ): Result<ResponseAIPreviousChatMessagesDto> {
        return runCatching {
            baseDataSource.getAIPreviousChatMessages(token, chatRoomId)
        }.onFailure {
            Timber.e("base repository get ai previous chat message fail: $it")
        }
    }

    // explore
    override suspend fun getAllPrograms(
        isFree: Boolean,
        page: Int,
        size: Int
    ): Result<ResponseAllProgramDto> {
        return runCatching {
            baseDataSource.getAllPrograms(isFree, "regDt", page, size)
        }.onFailure {
            Timber.e("base repository get all programs fail: $it")
        }
    }

    override suspend fun getDeadLinePrograms(): Result<ResponseDeadlineDto> {
        return runCatching {
            baseDataSource.getDeadLinePrograms()
        }.onFailure {
            Timber.e("base repository get deadline programs fail: $it")
        }
    }

    // followers
    override suspend fun getFollowerList(token: String): Result<ResponseFollowersDto> {
        return runCatching {
            baseDataSource.getFollowerList(token)
        }.onFailure {
            Timber.e("base repository get follower list fail: $it")
        }
    }
}