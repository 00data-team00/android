package com.data.app.presentation.main.community.other

import androidx.lifecycle.ViewModel
import com.data.app.R
import com.data.app.data.Post
import com.data.app.extension.OtherState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OtherProfileViewModel:ViewModel() {
    private val _otherState = MutableStateFlow<OtherState>(OtherState.Loading)
    val otherState:StateFlow<OtherState> = _otherState.asStateFlow()

    fun getOtherProfile(profile: String, id:String){
        val posts = listOf(
            Post(
                profile = profile,
                id = id,
                time = 1,
                isFollowing = false,
                content = "고양이랑 도심 속 카페 다녀왔어요. 사람보다 고양이가 더 인기 많았음ㅋㅋ",
                images = listOf(R.drawable.ic_image2, R.drawable.ic_image4),
                like = 87,
                comments = listOf(
                    Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Yuna", "와 진짜 귀엽다ㅠㅠ", 4),
                    Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Min", "거기 어디에요? 알려주세요!", 3)
                )
            ),
            Post(
                profile = profile,
                id = id,
                time = 3,
                isFollowing = false,
                content = "오늘 날씨 좋아서 고양이랑 산책했어요. 벚꽃이랑 같이 찍은 사진 공개 🌸",
                images = listOf(R.drawable.ic_image, R.drawable.ic_image3),
                like = 112,
                comments = listOf(
                    Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Leo", "고양이도 벚꽃 좋아하나요? ㅎㅎ", 5)
                )
            ),
            Post(
                profile = profile,
                id = id,
                time = 6,
                isFollowing = false,
                content = "우리집 고양이 처음으로 츄르를 먹었어요. 중독된 듯한 눈빛ㅋㅋㅋ",
                images = listOf(R.drawable.ic_image4),
                like = 56,
                comments = listOf()
            ),
            Post(
                profile = profile,
                id = id,
                time = 10,
                isFollowing = false,
                content = "냥이랑 첫 여행! 캐리어 안에서도 얌전하게 있어줘서 감동...",
                images = listOf(R.drawable.ic_image, R.drawable.ic_image2, R.drawable.ic_image3),
                like = 145,
                comments = listOf(
                    Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Sophie", "고양이도 여행 좋아하나봐요~", 6),
                    Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Daniel", "사진 너무 예뻐요", 4),
                    Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Mina", "여행 용품 추천해줘요!", 2),
                    Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Olivia", "부럽다 고양이랑 여행이라니", 3)
                )
            )
        )

        _otherState.value=OtherState.Success(posts)
    }
}