package com.data.app.presentation.main.community

import androidx.lifecycle.ViewModel
import com.data.app.R
import com.data.app.data.Post

class CommunityViewModel:ViewModel() {
    val allFeeds = listOf(
        Post(
            profile = "https://avatars.githubusercontent.com/u/71327548?v=4",
            id = "Luna",
            time = 2,
            isFollowing = false,
            content = "[전체] 고양이랑 남산 산책했어요! 🐾 너무 평화롭고 좋았어요.",
            images = listOf(
                R.drawable.ic_image4,
                R.drawable.ic_image,
                R.drawable.ic_image2,
            ),
            like = 120,
            comments = listOf(
                Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Emma", "와 남산 고양이 너무 귀여워요!", 8),
                Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Noah", "부럽다.. 다음엔 같이가요!", 5)
            )
        ),
        Post(
            profile = "https://avatars.githubusercontent.com/u/71327548?v=4",
            id = "Leo",
            time = 5,
            isFollowing = false,
            content = "[전체] 고양이 용품 싸게 살 수 있는 오프라인 매장 어디 있을까요?",
            images = null,
            like = 70,
            comments = listOf(
                Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Mina", "을지로 근처에 있어요!", 10)
            )
        ),
        Post(
            profile = "https://avatars.githubusercontent.com/u/71327548?v=4",
            id = "Isla",
            time = 8,
            isFollowing = false,
            content = "[전체] 한국 고양이 이름 짓기 너무 재밌어요~ 전 ‘참치’라고 지었어요 ㅎㅎ",
            images = listOf(
                R.drawable.ic_image2,
                R.drawable.ic_image3,
                R.drawable.ic_image4
            ),
            like = 98,
            comments = listOf(
                Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Tom", "참치 ㅋㅋㅋ 귀엽다", 6),
                Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Liam", "우리 집은 연어예요 ㅋㅋ", 4),
                Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Yuki", "고양이 이름 고민 중인데 참고해야겠다", 3)
            )
        )
    )

    val followFeeds = listOf(
        Post(
            profile = "https://avatars.githubusercontent.com/u/71327548?v=4",
            id = "Milo",
            time = 1,
            isFollowing = true,
            content = "[팔로우] 어제 고양이 미용했는데 표정이 완전 정색이에요 😾",
            images = null,
            like = 134,
            comments = listOf(
                Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Sophie", "미용한 뒤 애들이 다 그래요 ㅋㅋ", 7),
                Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Jack", "사진 공유해줘요!!", 6)
            )
        ),
        Post(
            profile = "https://avatars.githubusercontent.com/u/71327548?v=4",
            id = "Sasha",
            time = 4,
            isFollowing = true,
            content = "[팔로우] 우리 고양이 건강검진 받고 왔어요. 다행히 건강하대요!",
            images = listOf(
                R.drawable.ic_image,
                R.drawable.ic_image2,
                R.drawable.ic_image4
            ),
            like = 150,
            comments = listOf(
                Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Leo", "건강하다니 다행이에요! ❤️", 10)
            )
        ),
        Post(
            profile = "https://avatars.githubusercontent.com/u/71327548?v=4",
            id = "Yuna",
            time = 6,
            isFollowing = true,
            content = "[팔로우] 냥이랑 첫 외출! 백팩 안에 들어가서 얌전히 있어줬어요 🐱🎒",
            images = listOf(
                R.drawable.ic_image3,
                R.drawable.ic_image4
            ),
            like = 210,
            comments = listOf(
                Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Ben", "와 대박! 우리 애는 탈출 시도함 ㅠ", 13),
                Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Olivia", "용감한 고양이네요!", 8),
                Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Nina", "귀엽다앙앙", 4)
            )
        )
    )

    val countryFeeds = listOf(
        Post(
            profile = "https://avatars.githubusercontent.com/u/71327548?v=4",
            id = "Jin",
            time = 3,
            isFollowing = false,
            content = "[같은 국가] 한국 와서 처음 길고양이 밥주는 분 봤어요. 감동받았어요 😿",
            images = listOf(
                R.drawable.ic_image3,
                R.drawable.ic_image,
                R.drawable.ic_image4
            ),
            like = 85,
            comments = listOf(
                Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Suji", "맞아요. 정 많으신 분들 많더라구요", 11),
                Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Min", "고양이들 천국이네요", 6)
            )
        ),
        Post(
            profile = "https://avatars.githubusercontent.com/u/71327548?v=4",
            id = "Soo",
            time = 7,
            isFollowing = false,
            content = "[같은 국가] 한국 고양이 간식 중에 추천해줄 만한 거 있을까요?",
            images =listOf(
                R.drawable.ic_image4
            ),
            like = 100,
            comments = listOf(
                Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Hoon", "츄르 맛별로 다 좋아요 ㅋㅋ", 5),
                Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Yena", "참치맛 강추!", 3),
                Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Ray", "베베몬도 괜찮았어요!", 4)
            )
        ),
        Post(
            profile = "https://avatars.githubusercontent.com/u/71327548?v=4",
            id = "Daniel",
            time = 6,
            isFollowing = false,
            content = "[같은 국가] 한국에서 입양 절차 어떤가요? 유기묘 생각 중입니다",
            images = listOf(
                R.drawable.ic_image,
                R.drawable.ic_image3,
                R.drawable.ic_image4
            ),
            like = 172,
            comments = listOf(
                Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Jisoo", "지자체 보호소 추천드려요!", 9),
                Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Hyun", "절차는 생각보다 간단했어요!", 7)
            )
        )
    )
}