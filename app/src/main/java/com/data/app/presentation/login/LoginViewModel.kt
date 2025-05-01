package com.data.app.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.app.domain.repository.BaseRepository
import com.data.app.extension.LoginState
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
class LoginViewModel @Inject constructor(
    private val baseRepository: BaseRepository
):ViewModel() {
    private var _loginState = MutableStateFlow<LoginState>(LoginState.Loading)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun login(email:String, pw:String){
        viewModelScope.launch {
            baseRepository.login(email, pw).onSuccess { response->
                _loginState.value=LoginState.Success(response)
                Timber.d("login state success")
            }.onFailure {
                _loginState.value=LoginState.Error("Error response failure: ${it.message}")
                if (it is HttpException) {
                    try {
                        val errorBody: ResponseBody? = it.response()?.errorBody()
                        val errorBodyString = errorBody?.string() ?: ""
                        httpError(errorBodyString)
                    } catch (e: Exception) {
                        // JSON 파싱 실패 시 로깅
                        Timber.e("Error parsing error body: ${e}")
                        _loginState.value = LoginState.Error("알 수 없는 에러가 발생했습니다.")
                    }
                }else {
                    _loginState.value = LoginState.Error("네트워크 에러 또는 알 수 없는 오류: ${it.message}")
                }
            }
        }
    }

    private fun httpError(errorBody: String) {
        try {
            val jsonObject = JSONObject(errorBody)
            val errorMessage = jsonObject.optString("msg", "알 수 없는 오류입니다.")
            Timber.e("Parsed error message: $errorMessage")

            _loginState.value = LoginState.Error(errorMessage) // ✅ 여기서 실제 메시지로 상태 전달
        } catch (e: Exception) {
            Timber.e("JSON 파싱 실패: $e")
            _loginState.value = LoginState.Error("에러 응답을 처리할 수 없습니다.")
        }
    }
}