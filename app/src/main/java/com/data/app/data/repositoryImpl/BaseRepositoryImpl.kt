package com.data.app.data.repositoryImpl

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
import com.data.app.domain.repository.BaseRepository
import okhttp3.MultipartBody
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class BaseRepositoryImpl @Inject constructor(
    private val baseDataSource: BaseDataSource
) : BaseRepository {
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

    override suspend fun getNation(): Result<ResponseNationDto> {
        return runCatching {
            baseDataSource.getNation()
        }.onFailure {
            Timber.e("base repository get nation fail: $it")
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

    // community
    override suspend fun getAllTimeLine(token: String): Result<ResponseTimeLineDto> {
        return runCatching {
            baseDataSource.getAllTimeLine(token)
        }.onFailure {
            Timber.e("base repository get all time line fail!: $it")
        }
    }

    override suspend fun getNationTimeLine(token: String): Result<ResponseTimeLineDto> {
        return runCatching {
            baseDataSource.getNationTimeLine(token)
        }.onFailure {
            Timber.e("base repository get my time line fail!: $it")
        }
    }

    override suspend fun getFollowingTimeLine(token: String): Result<ResponseTimeLineDto> {
        return runCatching {
            baseDataSource.getFollowingTimeLine(token)
        }.onFailure {
            Timber.e("base repository get follow time line fail!: $it")
        }
    }

    override suspend fun getPostDetail(token: String, postId: Int): Result<ResponsePostDetailDto> {
        return runCatching {
            baseDataSource.getPostDetail(token, postId)
        }.onFailure {
            Timber.e("base repository get post detail fail!: $it")
        }
    }

    override suspend fun likePost(token: String, postId: Int): Result<Response<Unit>> {
        return runCatching {
            baseDataSource.likePost(token, postId)
        }.onFailure {
            Timber.e("base repository like post fail!: $it")
        }
    }

    override suspend fun unlikePost(token: String, postId: Int): Result<Response<Unit>> {
        return runCatching {
            baseDataSource.unlikePost(token, postId)
        }.onFailure {
            Timber.e("base repository unlike post fail!: $it")
        }
    }

    override suspend fun writeComment(
        token: String,
        postId: Int,
        content: String
    )
            : Result<ResponsePostDetailDto.CommentDto> {
        return runCatching {
            baseDataSource.writeComment(token, postId, content)
        }.onFailure {
            Timber.e("base repository write comment fail!: $it")
        }
    }

    override suspend fun getUserProfile(token: String, userId: Int): Result<ResponseProfileDto> {
        return runCatching {
            baseDataSource.getUserProfile(token, userId)
        }.onFailure {
            Timber.e("base repository get user profile fail!: $it")
        }
    }

    override suspend fun getUserPosts(token: String, userId: Int): Result<ResponseTimeLineDto> {
        return runCatching {
            baseDataSource.getUserPosts(token, userId)
        }.onFailure {
            Timber.e("base repository get user posts fail!: $it")
        }
    }

    override suspend fun writePost(
        token: String,
        content: String,
        image: MultipartBody.Part?
    ): Result<ResponseMyPostDto.PostDto> {
        return runCatching {
            baseDataSource.writePost(token, content, image)
        }.onFailure {
            Timber.e("base repository write post fail!: $it")
        }
    }

    override suspend fun deletePost(token: String, postId: Int): Result<ResponseDeletePostDto> {
        return runCatching {
            baseDataSource.deletePost(token, postId)
        }.onFailure {
            Timber.e("base repository delete post fail!: $it")
        }
    }

    override suspend fun follow(token: String, userId: Int): Result<ResponseFollowDto> {
        return runCatching {
            baseDataSource.follow(token, userId)
        }.onFailure {
            Timber.e("base repository follow fail!: $it")
        }
    }

    override suspend fun unFollow(token: String, userId: Int): Result<ResponseFollowDto> {
        return runCatching {
            baseDataSource.unFollow(token, userId)
        }.onFailure {
            Timber.e("base repository unfollow fail!: $it")
        }
    }

    override suspend fun getFollowerList(token: String): Result<ResponseFollowListDto> {
        return runCatching {
            baseDataSource.getFollowerList(token)
        }.onFailure {
            Timber.e("base repository get follower list fail: $it")
        }
    }

    override suspend fun getFollowingList(token: String): Result<ResponseFollowListDto> {
        return runCatching {
            baseDataSource.getFollowingList(token)
        }.onFailure {
            Timber.e("base repository get following list fail: $it")
        }
    }

    // home
    override suspend fun getUserGameInfo(token: String): Result<ResponseUserGameInfoDto> {
        return runCatching {
            baseDataSource.getUserGameInfo(token)
        }.onFailure {
            Timber.e("base repository get user game info fail!: $it")
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
            baseDataSource.startChat(token, topicId)
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

    // translate
    override suspend fun getTranslate(
        token: String,
        messageId: Int,
        userLang: String
    ): Result<ResponseTranslateDto> {
        return runCatching {
            baseDataSource.getTranslate(token, RequestTranslateDto(messageId, userLang))
        }.onFailure {
            Timber.e("base repository get translate message fail: $it")
        }
    }

    // quiz
    override suspend fun getQuiz(
        token: String,
        level: Int,
        userLang: String
    ): Result<ResponseQuizDto> {
        return runCatching {
            baseDataSource.getQuiz(token, RequestQuizDto(level, userLang))
        }.onFailure {
            Timber.e("base repository get quiz fail: $it")
        }
    }

    override suspend fun quizComplete(token: String, level: Int): Result<Response<Unit>> {
        return runCatching {
            baseDataSource.quizComplete(token, level)
        }.onFailure {
            Timber.e("base repository quiz complete fail: $it")
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

    // my
    override suspend fun getMyPosts(token: String): Result<ResponseMyPostDto> {
        return runCatching {
            baseDataSource.getMyPosts(token)
        }.onFailure {
            Timber.e("base repository get my posts fail: $it")
        }
    }

    override suspend fun getMyProfile(token: String): Result<ResponseProfileDto> {
        return runCatching {
            baseDataSource.getMyProfile(token)
        }.onFailure {
            Timber.e("base repository get profile fail: $it")
        }
    }

    override suspend fun editProfile(
        token: String,
        image: MultipartBody.Part
    ): Result<ResponseEditProfileDto> {
        return runCatching {
            baseDataSource.editProfile(token, image)
        }.onFailure {
            Timber.e("base repository profile edit fail: $it")
        }
    }

    override suspend fun quit(token: String): Result<ResponseQuitDto> {
        return runCatching {
            baseDataSource.quit(token)
        }.onFailure {
            Timber.e("base repository quit fail: $it")
        }
    }
}