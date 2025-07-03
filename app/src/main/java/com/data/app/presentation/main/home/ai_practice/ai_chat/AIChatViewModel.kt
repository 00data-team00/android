package com.data.app.presentation.main.home.ai_practice.ai_chat

import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.app.R
import com.data.app.data.PreviousPractice
import com.data.app.domain.repository.BaseRepository
import com.data.app.extension.home.aichat.AiChatState
import com.data.app.extension.home.aichat.StartChatState
import com.data.app.extension.TranslateState
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
class AIChatViewModel @Inject constructor(
    private val baseRepository: BaseRepository
) : ViewModel() {
    private val _accessToken = MutableLiveData<String>()
    val accessToken: LiveData<String> get() = _accessToken

    private val _startChatState = MutableStateFlow<StartChatState>(StartChatState.Loading)
    private val _aiChatState = MutableStateFlow<AiChatState>(AiChatState.Loading)
    private val _translateState = MutableStateFlow<TranslateState>(TranslateState.Loading)

    val startChatState: StateFlow<StartChatState> = _startChatState.asStateFlow()
    val aiChatState: StateFlow<AiChatState> = _aiChatState.asStateFlow()
    val translateState: StateFlow<TranslateState> = _translateState.asStateFlow()

    private var chatRoomId:Int = 0

    fun saveToken(token: String) {
        _accessToken.value = token
    }

    fun getTranslate(messageId: Int, userLang: String){
        viewModelScope.launch {
            baseRepository.getTranslate(_accessToken.value!!, messageId, userLang).onSuccess { response ->
                _translateState.value = TranslateState.Success(response)
                Timber.d("translate success!")
            }.onFailure {
                _translateState.value = TranslateState.Error("get translate state erro!")
            }
        }
    }

    fun startChat(topicId: Int) {
        viewModelScope.launch {
            baseRepository.startChat(_accessToken.value!!, topicId).onSuccess { response ->
                _startChatState.value = StartChatState.Success(response)
                chatRoomId=response.chatRoomId
                Timber.d("start chat state success!")
            }.onFailure {
                _startChatState.value = StartChatState.Error("start chat state error!")
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

    fun getAiChat(text:String){
        viewModelScope.launch {
            baseRepository.getAiChat(_accessToken.value!!, chatRoomId, text).onSuccess { response->
                _aiChatState.value=AiChatState.Success(response)
            }.onFailure {
                _aiChatState.value=AiChatState.Error("ai chat state error!")
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
        Timber.e("Error message: $errorMessage")
    }

    var mockAIChat = PreviousPractice(
        title = "하이금리 적금 상담 기록",
        titleEn = "Conversation record about HighGumlee",
        summation = "고금리 적금 상품에 대해 금리, 가입 조건, 해지 가능 여부를 중심으로 AI 상담을 진행함.",
        date = "2025-04-17",
        type = "예금/적금",
        chatList = listOf(
            PreviousPractice.ChatItem.My("안녕하세요, 적금 상품에 대해 궁금한 게 있어요.", "14:00"),
            PreviousPractice.ChatItem.My("요즘 금리가 높은 상품이 뭐가 있나요?", "14:00"),
            PreviousPractice.ChatItem.My("그리고 모바일로 가입 가능한가요?", "14:01"),
            PreviousPractice.ChatItem.Ai(
                "AI 은행 상담사",
                R.drawable.ic_profile,
                "현재 금리가 가장 높은 적금 상품은 '하이금리 정기적금'입니다. 최대 연 4.5% 이율을 제공합니다.",
                "14:01"
            ),
            PreviousPractice.ChatItem.Ai(
                "AI 은행 상담사",
                R.drawable.ic_profile,
                "해당 상품은 모바일 앱에서도 쉽게 가입 가능하며, 신분증 인증만 완료하시면 됩니다.",
                "14:02"
            )
        )
    )

    public fun addchat(item: PreviousPractice.ChatItem) {
        mockAIChat.chatList += item
    }
}