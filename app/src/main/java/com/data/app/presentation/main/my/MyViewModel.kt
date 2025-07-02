package com.data.app.presentation.main.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.app.R
import com.data.app.data.Post
import com.data.app.domain.repository.BaseRepository
import com.data.app.extension.EditProfileState
import com.data.app.extension.MyState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    val baseRepository: BaseRepository
) :ViewModel() {
    private val profile = "https://avatars.githubusercontent.com/u/71327548?v=4"
    private val id="구구"

    private val _myState = MutableStateFlow<MyState>(MyState.Loading)
    val myState:StateFlow<MyState> = _myState.asStateFlow()

    private val _editProfileState = MutableStateFlow<EditProfileState>(EditProfileState.Loading)
    val editProfileState:StateFlow<EditProfileState> = _editProfileState.asStateFlow()

    fun editProfile(token: String, image: MultipartBody.Part){
        viewModelScope.launch {
            baseRepository.editProfile(token, image).onSuccess { response->
                _editProfileState.value=EditProfileState.Success(response)
            }.onFailure {
                _editProfileState.value=EditProfileState.Error(it.message.toString())
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
                    Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Liam", "와 생일 축하해요!!", 5),
                    Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Emma", "케이크 진짜 맛있어보여요", 3)
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
                    Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Ben", "저도 같이 뒹굴고 싶네요 ㅎㅎ", 2)
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
                    Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Sasha", "그래도 너무 귀여워요!", 4),
                    Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Noah", "저희 집도 삐져요ㅠㅠ", 3)
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
                    Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Jisoo", "우리 애는 무서워서 안 올라가요ㅠ", 2),
                    Post.Comments("https://avatars.githubusercontent.com/u/71327548?v=4", "Nina", "캣타워 정보 공유 가능할까요?", 1)
                )
            )
        )

        _myState.value=MyState.Success(myPosts)
    }
}