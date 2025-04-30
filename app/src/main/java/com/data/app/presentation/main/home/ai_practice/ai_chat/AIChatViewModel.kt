package com.data.app.presentation.main.home.ai_practice.ai_chat

import androidx.lifecycle.ViewModel
import com.data.app.R
import com.data.app.data.PreviousPractice

class AIChatViewModel:ViewModel() {
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
            PreviousPractice.ChatItem.Ai("AI 은행 상담사", R.drawable.ic_profile, "현재 금리가 가장 높은 적금 상품은 '하이금리 정기적금'입니다. 최대 연 4.5% 이율을 제공합니다.", "14:01"),
            PreviousPractice.ChatItem.Ai("AI 은행 상담사", R.drawable.ic_profile, "해당 상품은 모바일 앱에서도 쉽게 가입 가능하며, 신분증 인증만 완료하시면 됩니다.", "14:02")
        )
    )
    public fun addchat(item: PreviousPractice.ChatItem){
        mockAIChat.chatList += item
    }
}