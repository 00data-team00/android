package com.data.app.presentation.main.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.app.domain.repository.BaseRepository
import com.data.app.extension.my.QuitState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class QuitViewModel @Inject constructor(
    private val baseRepository: BaseRepository
): ViewModel() {
    private val _quitState = MutableStateFlow<QuitState>(QuitState.Loading)
    val quitState: StateFlow<QuitState> = _quitState

    fun resetLoading(){
        _quitState.value = QuitState.Loading
    }

    fun quit(token:String){
        viewModelScope.launch {
            baseRepository.quit(token).onSuccess { response->
                _quitState.value = QuitState.Success(response)
            }.onFailure {
                _quitState.value = QuitState.Error(it.message.toString())
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