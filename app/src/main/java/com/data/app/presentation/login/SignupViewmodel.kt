package com.data.app.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.app.domain.repository.BaseRepository
import com.data.app.extension.login.NationState
import com.data.app.extension.login.RegisterState
import com.data.app.extension.login.SendMailState
import com.data.app.extension.login.VerifyMailState
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
class SignupViewmodel @Inject constructor(
    private val baseRepository: BaseRepository
) : ViewModel() {
    private var _sendMailState = MutableStateFlow<SendMailState>(SendMailState.Loading)
    private var _verifyMailState=  MutableStateFlow<VerifyMailState>(VerifyMailState.Loading)
    private var _registerState =  MutableStateFlow<RegisterState>(RegisterState.Loading)
    private val _nationState = MutableStateFlow<NationState>(NationState.Loading)

    val sendMailState:StateFlow<SendMailState> = _sendMailState.asStateFlow()
    val verifyMailState:StateFlow<VerifyMailState> = _verifyMailState.asStateFlow()
    val registerState:StateFlow<RegisterState> = _registerState.asStateFlow()
    val nationState: StateFlow<NationState> = _nationState.asStateFlow()

    private lateinit var email:String
    lateinit var username: String
    var nationality: Int = 0
    lateinit var password: String

    fun sendMail(newEmail:String){
        viewModelScope.launch {
            baseRepository.sendMail(newEmail).onSuccess { response->
                _sendMailState.value=SendMailState.Success(response)
                email=newEmail
                Timber.d("send mail is success!!")
            }.onFailure {
                _sendMailState.value=SendMailState.Error("Error response failure: ${it.message}")
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

    fun verifyMail(verificationCode:String){
        viewModelScope.launch {
            baseRepository.verifyMail(email, verificationCode).onSuccess { response->
                _verifyMailState.value=VerifyMailState.Success(response)
                Timber.d("verify mail is success")
            }.onFailure {
                _verifyMailState.value=VerifyMailState.Error("verify mail error response failure: ${it.message}")
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

    fun register(name:String, pw:String, nation:Int){
        viewModelScope.launch {
            baseRepository.register(email, name, pw, nation).onSuccess { response->
                _registerState.value=RegisterState.Success(response)
                Timber.d("register is success")
            }.onFailure {
                _registerState.value=RegisterState.Error("register error response failure: ${it.message}")
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

    fun getNation(){
        viewModelScope.launch {
            baseRepository.getNation().onSuccess { response ->
                _nationState.value = NationState.Success(response)
                Timber.d("get nation is success")
            }.onFailure {
                _nationState.value = NationState.Error("get nation error response failure: ${it.message}")
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