package com.data.app.presentation.main.home.game.quiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.app.data.response_dto.home.quiz.ResponseQuizDto
import com.data.app.domain.repository.BaseRepository
import com.data.app.extension.home.UserGameInfoState
import com.data.app.extension.home.quiz.QuizCompleteState
import com.data.app.extension.home.quiz.QuizState
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
class GameQuizViewModel @Inject constructor(
    val baseRepository: BaseRepository
) : ViewModel() {
    private val _accessToken = MutableLiveData<String>()
    val accessToken: LiveData<String> get() = _accessToken

    private val _level = MutableLiveData<Int>()
    val level: LiveData<Int> get() = _level

    private val _quizState =
        MutableStateFlow<QuizState>(QuizState.Loading) // 초기 상태는 Empty 또는 Loading
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()

    private val _quizCompleteState =
        MutableStateFlow<QuizCompleteState>(QuizCompleteState.Loading)
    val quizCompleteState: StateFlow<QuizCompleteState> = _quizCompleteState.asStateFlow()

    private var _userGameInfoState = MutableStateFlow<UserGameInfoState>(UserGameInfoState.Loading)
    val userGameInfoState: StateFlow<UserGameInfoState> = _userGameInfoState.asStateFlow()

    private val _quiz = MutableLiveData<List<ResponseQuizDto.QuizDto>>()
    val quiz: LiveData<List<ResponseQuizDto.QuizDto>> = _quiz

    fun saveToken(token: String) {
        _accessToken.value = token
    }

    fun saveLevel(level: Int) {
        _level.value = level
    }

    fun getQuiz(userLang: String) {
        viewModelScope.launch {
            _accessToken.let {
                baseRepository.getQuiz(_accessToken.value!!, _level.value!!, userLang)
                    .onSuccess { response ->
                        _quizState.value = QuizState.Success(response.quizDtoList)
                        _quiz.value = response.quizDtoList
                    }.onFailure {
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

    fun completeQuiz(token: String, quizId: Int) {
        Timber.d("quizId: ${quizId}")
        viewModelScope.launch {
            baseRepository.quizComplete(token, quizId).onSuccess { response ->
                _quizCompleteState.value = QuizCompleteState.Success
            }.onFailure {
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

    fun getUserGameInfo(){
        viewModelScope.launch {
            baseRepository.getUserGameInfo(_accessToken.value!!).onSuccess { response->
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
        Timber.e("Error message: $errorMessage")
    }

    val dayNames = listOf("월", "화", "수", "목", "금", "토", "일")
}