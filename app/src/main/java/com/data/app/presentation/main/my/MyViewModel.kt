package com.data.app.presentation.main.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.app.R
import com.data.app.data.Post
import com.data.app.domain.repository.BaseRepository
import com.data.app.extension.community.LikePostState
import com.data.app.extension.community.WriteCommentState
import com.data.app.extension.my.EditProfileState
import com.data.app.extension.my.MyPostState
import com.data.app.extension.my.MyProfileState
import com.data.app.extension.my.MyState
import com.data.app.extension.my.SharePostState
import com.data.app.extension.my.ShareProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    val baseRepository: BaseRepository
) : ViewModel() {
    private val _myProfileState = MutableStateFlow<MyProfileState>(MyProfileState.Loading)
    val myProfileState: StateFlow<MyProfileState> = _myProfileState.asStateFlow()

    private val _myPostState = MutableStateFlow<MyPostState>(MyPostState.Loading)
    val myPostState: StateFlow<MyPostState> = _myPostState.asStateFlow()

    private val _editProfileState = MutableStateFlow<EditProfileState>(EditProfileState.Loading)
    val editProfileState: StateFlow<EditProfileState> = _editProfileState.asStateFlow()

    private val _likePostState = MutableStateFlow<LikePostState>(LikePostState.Loading)
    val likePostState: StateFlow<LikePostState> = _likePostState.asStateFlow()

    private val _shareProfileState = MutableSharedFlow<ShareProfileState>()
    val shareProfileState: SharedFlow<ShareProfileState> = _shareProfileState.asSharedFlow()

    private val _sharePostState = MutableSharedFlow<SharePostState>()
    val sharePostState: SharedFlow<SharePostState> = _sharePostState.asSharedFlow()


    fun getProfile(token: String) {
        viewModelScope.launch {
            _myProfileState.value = MyProfileState.Loading

            baseRepository.getMyProfile(token).onSuccess { response ->
                _myProfileState.value = MyProfileState.Success(response)
            }.onFailure {
                _myProfileState.value = MyProfileState.Error(it.message.toString())
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

    fun getMyPosts(token: String) {
        viewModelScope.launch {
            baseRepository.getMyPosts(token).onSuccess { response ->
                _myPostState.value = MyPostState.Success(response)
            }.onFailure {
                _myPostState.value = MyPostState.Error(it.message.toString())
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

    fun editProfile(token: String, image: MultipartBody.Part) {
        viewModelScope.launch {
            baseRepository.editProfile(token, image).onSuccess { response ->
                _editProfileState.value = EditProfileState.Success(response)
            }.onFailure {
                _editProfileState.value = EditProfileState.Error(it.message.toString())
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

    fun resetPostState() {
        _myPostState.value = MyPostState.Loading
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
        Timber.e("Error message: $errorMessage")
    }
}