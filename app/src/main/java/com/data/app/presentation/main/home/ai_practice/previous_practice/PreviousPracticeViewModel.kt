package com.data.app.presentation.main.home.ai_practice.previous_practice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.app.R
import com.data.app.data.PreviousPractice
import com.data.app.domain.repository.BaseRepository
import com.data.app.extension.AIPreviousChatMessageState
import com.data.app.extension.AIPreviousPracticeState
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
class PreviousPracticeViewModel @Inject constructor(
    private val baseRepository: BaseRepository
) :ViewModel() {
    private val _accessToken = MutableLiveData<String>()
    val accessToken: LiveData<String> get()=_accessToken

    private val _aiPreviousRecordsState = MutableStateFlow<AIPreviousPracticeState>(AIPreviousPracticeState.Loading)
    private val _aiPreviousChatMessagesState = MutableStateFlow<AIPreviousChatMessageState>(AIPreviousChatMessageState.Loading)

    val aiPreviousRecordsState:StateFlow<AIPreviousPracticeState> = _aiPreviousRecordsState.asStateFlow()
    val aiPreviousChatMessagesState:StateFlow<AIPreviousChatMessageState> = _aiPreviousChatMessagesState.asStateFlow()

    fun saveToken(token:String){
        _accessToken.value=token
    }

    fun getAIPreviousRecords(){
        viewModelScope.launch {
            _accessToken.value?.let {
                baseRepository.getAIPreviousList(it).onSuccess { response->
                    _aiPreviousRecordsState.value=AIPreviousPracticeState.Success(response)
                    Timber.d("ai previous records state is success!")
                }.onFailure {
                    _aiPreviousRecordsState.value=AIPreviousPracticeState.Error("ai previous records state error: $it")
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

    fun getMessages(id:Int){
        viewModelScope.launch {
            _accessToken.value?.let{
                baseRepository.getAIPreviousChatMessages(it, id).onSuccess { response->
                    _aiPreviousChatMessagesState.value=AIPreviousChatMessageState.Success(id, response)
                    Timber.d("ai previous chat messages state is success!")
                }.onFailure {
                    _aiPreviousChatMessagesState.value=AIPreviousChatMessageState.Error("ai previous chat message state error: $it")
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

    private fun httpError(errorBody: String) {
        // 전체 에러 바디를 로깅하여 디버깅
        Timber.e("Full error body: $errorBody")

        // JSONObject를 사용하여 메시지 추출
        val jsonObject = JSONObject(errorBody)
        val errorMessage = jsonObject.optString("message", "Unknown error")

        // 추출된 에러 메시지 로깅
        Timber.e( "Error message: $errorMessage")
    }

    val mockPracticeRecordList = listOf(
        // 적금 상담
        PreviousPractice(
            title = "하이금리 적금 상담 기록",
            titleEn = "Conversation record about HighGumlee",
            summation = "고금리 적금 상품에 대해 금리, 가입 조건, 해지 가능 여부를 중심으로 AI 상담을 진행함.",
            date = "2025-04-17",
            type = "예금/적금",
            chatList = listOf(
                PreviousPractice.ChatItem.My("안녕하세요, 적금 상품에 대해 궁금한 게 있어요.", "14:00"),
                PreviousPractice.ChatItem.My("요즘 금리가 높은 상품이 뭐가 있나요?", "14:00"),
                PreviousPractice.ChatItem.My("그리고 모바일로 가입 가능한가요?", "14:01"),
                PreviousPractice.ChatItem.Ai("AI 은행 상담사", R.drawable.ic_profile, "현재 금리가 가장 높은 적금 상품은 '하이금리 정기적금'입니다. 최대 연 4.5% 이율을 제공합니다.", "14:01"),
                PreviousPractice.ChatItem.Ai("AI 은행 상담사", R.drawable.ic_profile, "해당 상품은 모바일 앱에서도 쉽게 가입 가능하며, 신분증 인증만 완료하시면 됩니다.", "14:02")
            )
        ),

        // 대출 상담
        PreviousPractice(
            title = "소액 대출 상담 기록",
            titleEn = "Conversation record about Soek rent",
            summation = "소액 비상금 대출에 대해 금리, 상환 방식, 조건에 대해 AI 상담을 진행했습니다.",
            date = "2025-04-12",
            type = "대출",
            chatList = listOf(
                PreviousPractice.ChatItem.My("소액 대출 받고 싶은데요, 어떻게 신청하나요?", "10:30"),
                PreviousPractice.ChatItem.My("신용 등급이 낮아도 가능한가요?", "10:30"),
                PreviousPractice.ChatItem.Ai("AI 대출 상담사", R.drawable.ic_profile, "소액 비상금 대출은 모바일 앱을 통해 신청 가능하며, 간단한 심사 후 바로 입금됩니다.", "10:31"),
                PreviousPractice.ChatItem.Ai("AI 대출 상담사", R.drawable.ic_profile, "신용 등급이 낮더라도 통신 데이터, 금융 이력 등으로 대체 평가가 가능해요.", "10:32"),
                PreviousPractice.ChatItem.My("상환은 어떻게 하나요?", "10:33"),
                PreviousPractice.ChatItem.Ai("AI 대출 상담사", R.drawable.ic_profile, "원리금 균등 방식으로 자동이체됩니다. 조기상환 수수료는 없습니다.", "10:33")
            )
        ),

        // 카드 상담
        PreviousPractice(
            title = "신용카드 발급 상담 기록",
            titleEn = "Conversation record about credit card",
            summation = "연회비, 혜택, 발급 조건 등에 대해 AI 카드 상담사와 질의응답을 진행했습니다.",
            date = "2025-04-05",
            type = "카드",
            chatList = listOf(
                PreviousPractice.ChatItem.My("신용카드 만들고 싶은데, 추천 좀 해주세요.", "09:10"),
                PreviousPractice.ChatItem.My("연회비가 낮고 혜택이 많은 게 좋겠어요.", "09:11"),
                PreviousPractice.ChatItem.Ai("AI 카드 상담사", R.drawable.ic_profile, "‘제로페이 카드’는 연회비 1만원에 주유, 마트, 커피 5% 할인 혜택이 있습니다.", "09:12"),
                PreviousPractice.ChatItem.My("발급 조건은 어떤가요?", "09:13"),
                PreviousPractice.ChatItem.Ai("AI 카드 상담사", R.drawable.ic_profile, "최근 3개월 소득이 있거나, 직장인이라면 온라인 신청 후 발급 가능합니다.", "09:13")
            )
        ),

        // 환전 상담
        PreviousPractice(
            title = "외화 환전 상담 기록",
            titleEn = "Conversation record about exchange",
            summation = "달러 환전 수수료, 방법, 환율 우대 조건에 대해 AI 상담을 진행했습니다.",
            date = "2025-03-28",
            type = "외화/환전",
            chatList = listOf(
                PreviousPractice.ChatItem.My("달러 환전하려면 어떻게 해야 하나요?", "11:20"),
                PreviousPractice.ChatItem.My("환율 우대 혜택도 있나요?", "11:20"),
                PreviousPractice.ChatItem.Ai("AI 외환 상담사", R.drawable.ic_profile, "모바일 앱에서 환전 신청 후 가까운 영업점에서 수령하시면 됩니다.", "11:21"),
                PreviousPractice.ChatItem.Ai("AI 외환 상담사", R.drawable.ic_profile, "모바일 환전 시 최대 90% 환율 우대가 적용됩니다.", "11:21")
            )
        )
    )



}