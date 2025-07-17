package com.data.app.presentation.main.community

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.app.R
import com.data.app.data.Post
import com.data.app.domain.repository.BaseRepository
import com.data.app.extension.community.GetAllTimeLineState
import com.data.app.extension.community.GetFollowingTimeLineState
import com.data.app.extension.community.GetNationTimeLineState
import com.data.app.extension.community.LikePostState
import com.data.app.extension.my.SharePostState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val baseRepository: BaseRepository
) : ViewModel() {
    enum class CommunityTab {
        ALL, FOLLOWING, COUNTRY
    }

    private val _selectedTab = MutableStateFlow(CommunityTab.ALL)
    val selectedTab: StateFlow<CommunityTab> = _selectedTab

    var recyclerViewState: Parcelable? = null

    private val _getAllTimeLineState =
        MutableStateFlow<GetAllTimeLineState>(GetAllTimeLineState.Loading)
    val getAllTimeLineState: StateFlow<GetAllTimeLineState> = _getAllTimeLineState.asStateFlow()

    private val _getNationTimeLineState =
        MutableStateFlow<GetNationTimeLineState>(GetNationTimeLineState.Loading)
    val getNationTimeLineState: StateFlow<GetNationTimeLineState> =
        _getNationTimeLineState.asStateFlow()

    private val _getFollowingTimeLineState =
        MutableStateFlow<GetFollowingTimeLineState>(GetFollowingTimeLineState.Loading)
    val getFollowingTimeLineState: StateFlow<GetFollowingTimeLineState> =
        _getFollowingTimeLineState.asStateFlow()

    private val _likePostState = MutableStateFlow<LikePostState>(LikePostState.Loading)
    val likePostState: StateFlow<LikePostState> = _likePostState.asStateFlow()

    private val _sharePostState = MutableSharedFlow<SharePostState>()
    val sharePostState: SharedFlow<SharePostState> = _sharePostState.asSharedFlow()

    var alreadyNavigated = false


    fun selectTab(tab: CommunityTab) {
        _selectedTab.value = tab
    }

    fun getAllTimeLine(token: String) {
        viewModelScope.launch {
            baseRepository.getAllTimeLine(token).onSuccess { response ->
                _getAllTimeLineState.value = GetAllTimeLineState.Success(response.posts)
            }.onFailure {
                _getAllTimeLineState.value = GetAllTimeLineState.Error(it.message.toString())
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

    fun getNationTimeLine(token: String) {
        viewModelScope.launch {
            baseRepository.getNationTimeLine(token).onSuccess { response ->
                _getAllTimeLineState.value = GetAllTimeLineState.Success(response.posts)
            }.onFailure {
                _getAllTimeLineState.value = GetAllTimeLineState.Error(it.message.toString())
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

    fun getFollowingTimeLine(token: String) {
        viewModelScope.launch {
            baseRepository.getFollowingTimeLine(token).onSuccess { response ->
                _getAllTimeLineState.value = GetAllTimeLineState.Success(response.posts)
            }.onFailure {
                _getAllTimeLineState.value = GetAllTimeLineState.Error(it.message.toString())
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

    fun likePost(token: String, postId: Int){
        viewModelScope.launch {
            baseRepository.likePost(token, postId).onSuccess {
                _likePostState.value=LikePostState.Success("좋아요 성공")
            }.onFailure {
                _likePostState.value=LikePostState.Error(it.message.toString())
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

    fun unLikePost(token: String, postId: Int){
        viewModelScope.launch {
            baseRepository.unlikePost(token, postId).onSuccess {
                _likePostState.value=LikePostState.Success("좋아요 취소 성공")
            }.onFailure {
                _likePostState.value=LikePostState.Error(it.message.toString())
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

    fun resetTimeLineState(){
        _getAllTimeLineState.value=GetAllTimeLineState.Loading
    }

    fun resetLikeState(){
        _likePostState.value=LikePostState.Loading
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