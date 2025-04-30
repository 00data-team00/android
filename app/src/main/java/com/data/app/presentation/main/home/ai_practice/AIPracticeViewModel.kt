package com.data.app.presentation.main.home.ai_practice

import android.adservices.topics.Topic
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.app.R
import com.data.app.data.AIPractice
import com.data.app.domain.repository.BaseRepository
import com.data.app.extension.AIChatTopicsState
import com.data.app.extension.LoginState
import com.data.app.extension.SendMailState
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
class AIPracticeViewModel @Inject constructor(
    private val baseRepository: BaseRepository
):ViewModel() {
    private var _aiTopicsState = MutableStateFlow<AIChatTopicsState>(AIChatTopicsState.Loading)
    val aiTopicsState: StateFlow<AIChatTopicsState> = _aiTopicsState.asStateFlow()

    val essentialTopics = MutableLiveData<List<Topic>>()
    val cultureTopics = MutableLiveData<List<Topic>>()
    val workTopics = MutableLiveData<List<Topic>>()

    fun getAiTopics(token:String){
        viewModelScope.launch {
            baseRepository.getAIChatTopics(token).onSuccess{response->
                _aiTopicsState.value=AIChatTopicsState.Success(response)

               /* essentialTopics.value = response.filter { it.category == "필수/일상" }
                cultureTopics.value = response.filter { it.category == "문화/여가" }
                workTopics.value = response.filter { it.category == "취업/업무" }*/

                Timber.d("get ai topics is success!!")
            }.onFailure {
                _aiTopicsState.value= AIChatTopicsState.Error("Error response failure: ${it.message}")
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

    val mockDailyList = listOf(
        AIPractice(R.drawable.bg_gray, "title daily", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title daily", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title daily", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title daily", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title daily", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title daily", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title daily", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title daily", "subtitle"),
    )

    val mockCultureList =  listOf(
        AIPractice(R.drawable.bg_gray, "title culture", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title culture", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title culture", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title culture", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title culture", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title culture", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title culture", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title culture", "subtitle"),
    )

    val mockJobList =  listOf(
        AIPractice(R.drawable.bg_gray, "title job", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title job", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title job", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title job", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title job", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title job", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title job", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title job", "subtitle"),
    )
}