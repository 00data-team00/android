package com.data.app.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.app.domain.repository.BaseRepository
import com.data.app.extension.main.GetIdFromTokenState
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
class MainViewModel @Inject constructor(
    private val baseRepository: BaseRepository
):ViewModel() {
    private val _getIdFromTokenState = MutableStateFlow<GetIdFromTokenState>(GetIdFromTokenState.Loading)
    val getIdFromTokenState:MutableStateFlow<GetIdFromTokenState> get()=_getIdFromTokenState

    fun getIdFromToken(token:String){
        viewModelScope.launch {
            baseRepository.getIdFromToken(token).onSuccess { response->
                _getIdFromTokenState.value=GetIdFromTokenState.Success(response)
            }.onFailure {
                _getIdFromTokenState.value=GetIdFromTokenState.Error(it.message.toString())
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