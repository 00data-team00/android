package com.data.app.presentation.main.home.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.app.data.GameLevel
import com.data.app.data.Week
import com.data.app.domain.repository.BaseRepository
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
class GameTabViewModel @Inject constructor(
    private val baseRepository: BaseRepository
) :ViewModel() {
    private val _accessToken = MutableLiveData<String>()
    val accessToken:LiveData<String> get()=_accessToken

    private var _userGameInfoState = MutableStateFlow<UserGameInfoState>(UserGameInfoState.Loading)
    val userGameInfoState: StateFlow<UserGameInfoState> = _userGameInfoState.asStateFlow()

    fun saveToken(token:String){
        _accessToken.value=token
    }

    fun getUserGameInfo(){
        viewModelScope.launch {
            baseRepository.getUserGameInfo(_accessToken.value!!).onSuccess { response->
                _userGameInfoState.value= UserGameInfoState.Success(response)
                updateList(response.levelCompleted?:0)
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

    fun resetUserGameInfoState() {
        _userGameInfoState.value = UserGameInfoState.Loading // 또는 Idle 같은 상태 정의
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

    private fun updateList(levelComplete:Int){
        for(i in 1 .. levelComplete){
            if(i>=3) return
            levels[i].isOpen=true
        }
    }
    val dayNames = listOf("월", "화", "수", "목", "금", "토", "일")

    val levels= mutableListOf(
        GameLevel("LV.1", true),
        GameLevel("LV.2", false),
        GameLevel("LV.3", false),
        GameLevel("준비중", false),
        GameLevel("준비중", false),
        GameLevel("준비중", false),
        GameLevel("준비중", false),
        GameLevel("준비중", false),
        GameLevel("준비중", false),
        GameLevel("준비중", false),
    )
}