package com.data.app.presentation.main.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.app.data.Follow
import com.data.app.domain.repository.BaseRepository
import com.data.app.extension.community.FollowListState
import com.data.app.extension.community.FollowState
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
class FollowViewModel @Inject constructor(
    private val baseRepository: BaseRepository
) : ViewModel() {
    private var accessToken: String? = null
    private val _followListState = MutableStateFlow<FollowListState>(FollowListState.Loading)
    val followListState: StateFlow<FollowListState> = _followListState.asStateFlow()
    
    private val _followState = MutableStateFlow<FollowState>(FollowState.Loading)
    val followState: StateFlow<FollowState> = _followState.asStateFlow()

    fun saveToken(token: String) {
        accessToken = token
    }

    fun getFollowers() {
        viewModelScope.launch {
            accessToken?.let {
                baseRepository.getFollowerList(accessToken!!).onSuccess { response ->
                    _followListState.value = FollowListState.Success(response)
                    Timber.d("Get followers success!")
                }.onFailure {
                    _followListState.value = FollowListState.Error("Get followers failed!")
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
    }

    fun getFollowing() {
        viewModelScope.launch {
            accessToken?.let {
                baseRepository.getFollowingList(accessToken!!).onSuccess { response ->
                    _followListState.value = FollowListState.Success(response)
                    Timber.d("Get following")
                }.onFailure {
                    _followListState.value = FollowListState.Error("Get following failed!")
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

    fun resetFollowListState() {
        _followListState.value = FollowListState.Loading
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


    val followerList = listOf(
        Follow(name = "Emily Johnson", id = "emily_j", isFollow = true),
        Follow(name = "Liam Smith", id = "liam_smith23", isFollow = true),
        Follow(name = "Olivia Brown", id = "olivia_b", isFollow = true),
        Follow(name = "Noah Davis", id = "noah_d", isFollow = true),
        Follow(name = "Ava Wilson", id = "ava_w", isFollow = true),
        Follow(name = "James Miller", id = "james_mil", isFollow = true),
        Follow(name = "Sophia Moore", id = "sophia.moore", isFollow = true),
        Follow(name = "Benjamin Taylor", id = "ben_tay", isFollow = true),
        Follow(name = "Isabella Anderson", id = "bella_anderson", isFollow = true),
        Follow(name = "Lucas Thomas", id = "lucas_t", isFollow = true)
    )

    val followingList = listOf(
        Follow(name = "Mason Martin", id = "mason_m", isFollow = true),
        Follow(name = "Mia White", id = "mia_white88", isFollow = false),
        Follow(name = "Ethan Jackson", id = "ethan_jx", isFollow = true),
        Follow(name = "Harper Lee", id = "harper.lee", isFollow = false),
        Follow(name = "Logan Harris", id = "logan_h", isFollow = true),
        Follow(name = "Aria Clark", id = "aria_c", isFollow = false),
        Follow(name = "Elijah Lewis", id = "elijah_l", isFollow = true),
        Follow(name = "Grace Robinson", id = "grace_rb", isFollow = true),
        Follow(name = "Henry Walker", id = "henrywalk", isFollow = false),
        Follow(name = "Chloe Hall", id = "chloe_hall", isFollow = true)
    )
}