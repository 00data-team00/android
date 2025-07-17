package com.data.app.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.app.data.response_dto.login.ResponseLoginDto
import com.data.app.data.shared_preferences.AppPreferences
import com.data.app.domain.repository.BaseRepository
import com.data.app.extension.login.LoginState
import com.data.app.extension.login.RefreshState
import com.data.app.extension.my.MyProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import okhttp3.internal.applyConnectionSpec
import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val baseRepository: BaseRepository,
    private val appPreferences: AppPreferences
) : ViewModel() {
    private var _loginState = MutableStateFlow<LoginState>(LoginState.Loading)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private var _refreshState = MutableStateFlow<RefreshState>(RefreshState.Loading)
    val refreshState: StateFlow<RefreshState> = _refreshState.asStateFlow()

    private val _myProfileState = MutableStateFlow<MyProfileState>(MyProfileState.Loading)
    val myProfileState: StateFlow<MyProfileState> = _myProfileState.asStateFlow()

    fun checkForSavedLogin() {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            delay(100)
            val expiresAt = appPreferences.getExpiresAt()
            if (appPreferences.getAccessToken() != null && expiresAt != null && appPreferences.getUserId()!=null) {
                if (expiresAt < System.currentTimeMillis()) {
                    // 토큰이 존재하면, 일단 성공으로 간주하고 메인 화면으로 보냅니다.
                    // 실제 API 호출에서 토큰이 만료되었다면, refresh 토큰으로 새로운 토큰을 받아옵니다.
                    Timber.d("ViewModel: Found saved access token: ${appPreferences.getAccessToken()}. Assuming valid for now.")
                    _loginState.value = LoginState.Success(appPreferences.getAccessToken()!!)
                } else {
                    Timber.d("ViewModel: token expired: ${appPreferences.getAccessToken()}")
                    _loginState.value = LoginState.Error("token expired")
                }
            }else {
                Timber.d("ViewModel: No saved access token found.")
                _loginState.value = LoginState.Error("저장된 로그인 정보 없음")
            }
        }
    }

    fun login(email: String, pw: String) {
        viewModelScope.launch {
            baseRepository.login(email, pw).onSuccess { response ->
                /*if (response.success == true && response.accessToken != null) {*/
                val expiresAt =
                    System.currentTimeMillis() + (response.expiresIn * 1000)        // ms단위로 저장
                appPreferences.saveDtoInfo(ResponseLoginDto("Bearer ${response.accessToken}", response.refreshToken, response.tokenType, expiresAt.toInt()), )

                Timber.d("Login successful. Token saved: ${response.accessToken}")
                _loginState.value = LoginState.Success("Bearer ${response.accessToken}")
                /*} else {
                    val errorMessage = response.msg ?: "로그인 실패: 서버 응답 오류"
                    Timber.w("Login API call was successful but response indicates failure: $errorMessage")
                    _loginState.value = LoginState.Error(errorMessage)
                }*/
                /*_loginState.value=LoginState.Success(response)
                Timber.d("login state success")*/
            }.onFailure {
                _loginState.value = LoginState.Error("Error response failure: ${it.message}")
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
                } else {
                    _loginState.value = LoginState.Error("네트워크 에러 또는 알 수 없는 오류: ${it.message}")
                }
            }
        }
    }

    fun refresh(){
        viewModelScope.launch {
            baseRepository.refresh(appPreferences.getLoginInfo()!!.refreshToken!!).onSuccess { response->
                _refreshState.value= RefreshState.Success(response)
                appPreferences.saveDtoInfo(response)
                Timber.d("refresh success")
            }.onFailure {
                _refreshState.value=RefreshState.Error("Error response failure: ${it.message}")
                if (it is HttpException) {
                    try {
                        val errorBody: ResponseBody? = it.response()?.errorBody()
                        val errorBodyString = errorBody?.string() ?: ""
                        httpError(errorBodyString)
                    } catch (e: Exception) {
                        // JSON 파싱 실패 시 로깅
                        Timber.e("Error parsing error body: ${e}")
                        _refreshState.value = RefreshState.Error("알 수 없는 에러가 발생했습니다.")
                    }
                } else {
                    _refreshState.value = RefreshState.Error("네트워크 에러 또는 알 수 없는 오류: ${it.message}")
                }
            }
        }
    }

    fun getProfile(token:String){
        viewModelScope.launch {
            baseRepository.getMyProfile(token).onSuccess { response->
                appPreferences.saveUserInfo(response.userId, response.name)
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