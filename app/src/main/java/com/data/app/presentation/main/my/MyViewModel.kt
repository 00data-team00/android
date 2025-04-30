package com.data.app.presentation.main.my

import androidx.lifecycle.ViewModel
import com.data.app.R
import com.data.app.data.Post
import com.data.app.extension.MyState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MyViewModel:ViewModel() {
    private val profile = R.drawable.ic_my
    private val id="구구"

    private val _myState = MutableStateFlow<MyState>(MyState.Loading)
    val myState:StateFlow<MyState> = _myState.asStateFlow()

    fun getPosts(){
        val myPosts = listOf(
            Post(
                profile = profile,
                id = id,
                time = 2,
                isFollowing = false,
                content = "고양이 생일파티 했어요 🎉 생선 케이크도 준비함ㅋㅋ",
                images = listOf(R.drawable.ic_image2, R.drawable.ic_image3),
                like = 101,
                comments = listOf(
                    Post.Comments(R.drawable.ic_profile4, "Liam", "와 생일 축하해요!!", 5),
                    Post.Comments(R.drawable.ic_profile3, "Emma", "케이크 진짜 맛있어보여요", 3)
                )
            ),
            Post(
                profile = profile,
                id = id,
                time = 5,
                isFollowing = false,
                content = "냥이랑 소파에서 뒹굴뒹굴~ 이런 날이 제일 힐링이죠 ☁️",
                images = listOf(R.drawable.ic_image, R.drawable.ic_image4),
                like = 78,
                comments = listOf(
                    Post.Comments(R.drawable.ic_profile, "Ben", "저도 같이 뒹굴고 싶네요 ㅎㅎ", 2)
                )
            ),
            Post(
                profile = profile,
                id = id,
                time = 7,
                isFollowing = false,
                content = "고양이 옷 처음 입혀봤는데... 기분 나빴는지 삐졌어요ㅋㅋ",
                images = listOf(R.drawable.ic_image3),
                like = 96,
                comments = listOf(
                    Post.Comments(R.drawable.ic_profile2, "Sasha", "그래도 너무 귀여워요!", 4),
                    Post.Comments(R.drawable.ic_profile4, "Noah", "저희 집도 삐져요ㅠㅠ", 3)
                )
            ),
            Post(
                profile = profile,
                id = id,
                time = 11,
                isFollowing = false,
                content = "캣타워 새로 설치했는데 하루종일 거기서 안 내려옴ㅋㅋ 만족한 듯!",
                images = listOf(R.drawable.ic_image, R.drawable.ic_image2, R.drawable.ic_image4),
                like = 132,
                comments = listOf(
                    Post.Comments(R.drawable.ic_profile, "Jisoo", "우리 애는 무서워서 안 올라가요ㅠ", 2),
                    Post.Comments(R.drawable.ic_profile3, "Nina", "캣타워 정보 공유 가능할까요?", 1)
                )
            )
        )

        _myState.value=MyState.Success(myPosts)
    }
}