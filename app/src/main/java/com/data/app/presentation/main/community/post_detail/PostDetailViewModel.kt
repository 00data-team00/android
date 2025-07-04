package com.data.app.presentation.main.community.post_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.app.domain.repository.BaseRepository
import com.data.app.extension.community.DeletePostState
import com.data.app.extension.community.LikePostState
import com.data.app.extension.community.PostDetailState
import com.data.app.extension.community.WriteCommentState
import com.data.app.extension.my.MyProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val baseRepository: BaseRepository
) : ViewModel() {
    private val _postDetailState = MutableStateFlow<PostDetailState>(PostDetailState.Loading)
    val postDetailState: StateFlow<PostDetailState> = _postDetailState

    private val _myProfileState = MutableStateFlow<MyProfileState>(MyProfileState.Loading)
    val myProfileState: StateFlow<MyProfileState> = _myProfileState.asStateFlow()

    private val _likePostState = MutableStateFlow<LikePostState>(LikePostState.Loading)
    val likePostState: StateFlow<LikePostState> = _likePostState.asStateFlow()

    private val _writeCommentState = MutableStateFlow<WriteCommentState>(WriteCommentState.Loading)
    val writeCommentState: StateFlow<WriteCommentState> = _writeCommentState.asStateFlow()

    private val _deletePostState = MutableStateFlow<DeletePostState>(DeletePostState.Loading)
    val deletePostState: StateFlow<DeletePostState> = _deletePostState.asStateFlow()

    fun getPostDetail(token: String, postId: Int) {
        viewModelScope.launch {
            baseRepository.getPostDetail(token, postId).onSuccess {
                _postDetailState.value = PostDetailState.Success(it)
            }.onFailure {
                _postDetailState.value = PostDetailState.Error(it.message.toString())
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

    fun getProfile(token:String){
        viewModelScope.launch {
            baseRepository.getMyProfile(token).onSuccess { response->
                _myProfileState.value=MyProfileState.Success(response)
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

    fun writeComment(token: String, postId: Int, content: String) {
        viewModelScope.launch {
            baseRepository.writeComment(token, postId, content).onSuccess {
                _writeCommentState.value = WriteCommentState.Success(it)
            }.onFailure {
                _writeCommentState.value = WriteCommentState.Error(it.message.toString())
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

    fun deletePost(token:String, postId:Int){
        viewModelScope.launch {
            baseRepository.deletePost(token, postId).onSuccess { response->
                _deletePostState.value = DeletePostState.Success(response)
            }.onFailure {
                _deletePostState.value = DeletePostState.Error(it.message.toString())
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