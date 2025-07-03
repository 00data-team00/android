package com.data.app.presentation.main.home

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.app.domain.repository.BaseRepository
import com.data.app.extension.login.LoginState
import com.data.app.extension.home.UserGameInfoState
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
class HomeViewModel @Inject constructor(
    val baseRepository: BaseRepository
): ViewModel() {
    private var _userGameInfoState = MutableStateFlow<UserGameInfoState>(UserGameInfoState.Loading)
    val userGameInfoState: StateFlow<UserGameInfoState> = _userGameInfoState.asStateFlow()

    fun getUserGameInfo(token:String){
        viewModelScope.launch {
            baseRepository.getUserGameInfo(token).onSuccess { response->
                _userGameInfoState.value= UserGameInfoState.Success(response)
            }.onFailure {
                _userGameInfoState.value= UserGameInfoState.Error("user game info error!")
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