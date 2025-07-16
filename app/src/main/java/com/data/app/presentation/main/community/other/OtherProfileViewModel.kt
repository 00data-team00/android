package com.data.app.presentation.main.community.other

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.app.R
import com.data.app.data.Post
import com.data.app.domain.repository.BaseRepository
import com.data.app.extension.community.FollowState
import com.data.app.extension.community.GetUserPostState
import com.data.app.extension.community.LikePostState
import com.data.app.extension.community.OtherState
import com.data.app.extension.my.SharePostState
import com.data.app.extension.my.ShareProfileState
import com.data.app.extension.my.UserProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OtherProfileViewModel @Inject constructor(
    private val baseRepository: BaseRepository
) :ViewModel() {
    private val _userProfileState = MutableStateFlow<UserProfileState>(UserProfileState.Loading)
    val userProfileState: StateFlow<UserProfileState> = _userProfileState.asStateFlow()
    private val _otherState = MutableStateFlow<OtherState>(OtherState.Loading)
    val otherState:StateFlow<OtherState> = _otherState.asStateFlow()

    private val _getUserPostState = MutableStateFlow<GetUserPostState>(GetUserPostState.Loading)
    val getUserPostState: StateFlow<GetUserPostState> = _getUserPostState.asStateFlow()

    private val _followState = MutableStateFlow<FollowState>(FollowState.Loading)
    val followState: StateFlow<FollowState> = _followState.asStateFlow()

    private val _likePostState = MutableStateFlow<LikePostState>(LikePostState.Loading)
    val likePostState: StateFlow<LikePostState> = _likePostState.asStateFlow()

    private val _shareProfileState = MutableSharedFlow<ShareProfileState>()
    val shareProfileState: SharedFlow<ShareProfileState> = _shareProfileState.asSharedFlow()

    private val _sharePostState = MutableSharedFlow<SharePostState>()
    val sharePostState: SharedFlow<SharePostState> = _sharePostState.asSharedFlow()

    fun getUserProfile(token:String, userId:Int){
        viewModelScope.launch {
            baseRepository.getUserProfile(token, userId).onSuccess{response->
                _userProfileState.value = UserProfileState.Success(response)
            }.onFailure {
                _userProfileState.value = UserProfileState.Error(it.message.toString())
                if (it is HttpException) {
                    try {
                        val errorBody: ResponseBody? = it.response()?.errorBody()
                        val errorBodyString = errorBody?.string() ?: ""
                        httpError(errorBodyString)
                    } catch (e: Exception) {
                        // JSON 파싱 실패 시 로깅
                        Timber.e("Error parsing error body: ${e}")
                    }
                }
            }
        }
    }

    fun getUserPost(token:String, userId:Int){
        viewModelScope.launch {
            baseRepository.getUserPosts(token, userId).onSuccess { response ->
                _getUserPostState.value = GetUserPostState.Success(response.posts)
            }.onFailure {
                _getUserPostState.value = GetUserPostState.Error(it.message.toString())
                if (it is HttpException) {
                    try {
                        val errorBody: ResponseBody? = it.response()?.errorBody()
                        val errorBodyString = errorBody?.string() ?: ""
                        httpError(errorBodyString)
                    } catch (e: Exception) {
                        // JSON 파싱 실패 시 로깅
                        Timber.e("Error parsing error body: ${e}")
                    }
                }
            }
        }
    }

    fun follow(token:String, userId:Int){
        viewModelScope.launch {
            baseRepository.follow(token, userId).onSuccess { response ->
                _followState.value = FollowState.Success(response)
            }.onFailure {
                _followState.value = FollowState.Error(it.message.toString())
                if (it is HttpException) {
                    try {
                        val errorBody: ResponseBody? = it.response()?.errorBody()
                        val errorBodyString = errorBody?.string() ?: ""
                        httpError(errorBodyString)
                    } catch (e: Exception) {
                        // JSON 파싱 실패 시 로깅
                        Timber.e("Error parsing error body: ${e}")
                    }
                }
            }
        }
    }

    fun unFollow(token:String, userId:Int){
        viewModelScope.launch {
            baseRepository.unFollow(token, userId).onSuccess { response ->
                _followState.value = FollowState.Success(response)
            }.onFailure {
                _followState.value = FollowState.Error(it.message.toString())
                if (it is HttpException) {
                    try {
                        val errorBody: ResponseBody? = it.response()?.errorBody()
                        val errorBodyString = errorBody?.string() ?: ""
                        httpError(errorBodyString)
                    } catch (e: Exception) {
                        // JSON 파싱 실패 시 로깅
                        Timber.e("Error parsing error body: ${e}")
                    }
                }
            }
        }
    }

    fun resetFollowState(){
        _followState.value=FollowState.Loading
    }

    fun likePost(token: String, postId: Int) {
        viewModelScope.launch {
            baseRepository.likePost(token, postId).onSuccess {
                _likePostState.value = LikePostState.Success("좋아요 성공")
            }.onFailure {
                _likePostState.value = LikePostState.Error(it.message.toString())
                if (it is HttpException) {
                    try {
                        val errorBody: ResponseBody? = it.response()?.errorBody()
                        val errorBodyString = errorBody?.string() ?: ""
                        httpError(errorBodyString)
                    } catch (e: Exception) {
                        // JSON 파싱 실패 시 로깅
                        Timber.e("Error parsing error body: ${e}")
                    }
                }
            }
        }
    }

    fun unLikePost(token: String, postId: Int) {
        viewModelScope.launch {
            baseRepository.unlikePost(token, postId).onSuccess {
                _likePostState.value = LikePostState.Success("좋아요 취소 성공")
            }.onFailure {
                _likePostState.value = LikePostState.Error(it.message.toString())
                if (it is HttpException) {
                    try {
                        val errorBody: ResponseBody? = it.response()?.errorBody()
                        val errorBodyString = errorBody?.string() ?: ""
                        httpError(errorBodyString)
                    } catch (e: Exception) {
                        // JSON 파싱 실패 시 로깅
                        Timber.e("Error parsing error body: ${e}")
                    }
                }
            }
        }
    }

    fun resetLikeState() {
        _likePostState.value = LikePostState.Loading
    }

    fun shareProfile(userId:Int){
        viewModelScope.launch {
            _shareProfileState.emit(ShareProfileState.Loading)

            baseRepository.shareProfile(userId).onSuccess { response ->
                _shareProfileState.emit(ShareProfileState.Success(response))
            }.onFailure {
                _shareProfileState.emit(ShareProfileState.Error(it.message.toString()))
                if (it is HttpException) {
                    try {
                        val errorBody: ResponseBody? = it.response()?.errorBody()
                        val errorBodyString = errorBody?.string() ?: ""
                        httpError(errorBodyString)
                    } catch (e: Exception) {
                        // JSON 파싱 실패 시 로깅
                        Timber.e("Error parsing error body: ${e}")
                    }
                }
            }
        }
    }

    fun sharePost(postId:Int){
        viewModelScope.launch {
            _sharePostState.emit(SharePostState.Loading)

            baseRepository.sharePost(postId).onSuccess { response ->
                _sharePostState.emit(SharePostState.Success(response))
            }.onFailure {
                _sharePostState.emit(SharePostState.Error(it.message.toString()))
                if (it is HttpException) {
                    try {
                        val errorBody: ResponseBody? = it.response()?.errorBody()
                        val errorBodyString = errorBody?.string() ?: ""
                        httpError(errorBodyString)
                    } catch (e: Exception) {
                        // JSON 파싱 실패 시 로깅
                        Timber.e("Error parsing error body: ${e}")
                    }
                }
            }
        }
    }


    private fun httpError(errorBody: String) {
        // 전체 에러 바디를 로깅하여 디버깅
        Timber.e("Full error body: $errorBody")

        // JSONObject를 사용하여 메시지 추출
        val jsonObject = JSONObject(errorBody)
        val errorMessage = jsonObject.optString("message", "Unknown error")

        // 추출된 에러 메시지 로깅
        Timber.e( "Error message: $errorMessage")
    }
}