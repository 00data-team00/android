package com.data.app.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.app.data.response_dto.ResponseLoginDto
import com.data.app.data.shared_preferences.AppPreferences
import com.data.app.domain.repository.BaseRepository
import com.data.app.extension.login.LoginState
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
    private val baseRepository: BaseRepository,
    private val appPreferences: AppPreferences
):ViewModel() {
    private var _loginState = MutableStateFlow<LoginState>(LoginState.Loading)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    /**
     * 앱 시작 시 저장된 액세스 토큰이 있는지 확인합니다.
     * 실제 유효성 검증은 API 호출 시 서버에서 이루어집니다.
     * 여기서는 토큰 존재 유무만으로 간단히 판단합니다.
     */
    fun checkForSavedLogin() {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val accessToken = appPreferences.getAccessToken()
            if (accessToken != null) {
                // 토큰이 존재하면, 일단 성공으로 간주하고 메인 화면으로 보냅니다.
                // 실제 API 호출에서 토큰이 만료되었다면, 그때 로그아웃 처리됩니다.
                // (더 나은 방법: 토큰 유효성 검증 API를 호출하거나, 토큰 자체에 만료시간 정보가 있다면 클라에서 확인)
                Timber.d("ViewModel: Found saved access token. Assuming valid for now.")
                _loginState.value = LoginState.Success(
                    ResponseLoginDto(accessToken, null, true, "success")
                )
            } else {
                Timber.d("ViewModel: No saved access token found.")
                _loginState.value = LoginState.Error("저장된 로그인 정보 없음")
            }
        }
    }

    fun login(email:String, pw:String){
        viewModelScope.launch {
            baseRepository.login(email, pw).onSuccess { response->
                /*if (response.success == true && response.accessToken != null) {*/
                    appPreferences.saveAccessToken("Bearer ${response.accessToken}") // 토큰 저장
                    Timber.d("Login successful. Token saved: ${response.accessToken}")
                    _loginState.value = LoginState.Success(response)
                /*} else {
                    val errorMessage = response.msg ?: "로그인 실패: 서버 응답 오류"
                    Timber.w("Login API call was successful but response indicates failure: $errorMessage")
                    _loginState.value = LoginState.Error(errorMessage)
                }*/
                /*_loginState.value=LoginState.Success(response)
                Timber.d("login state success")*/
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