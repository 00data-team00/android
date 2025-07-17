package com.data.app.presentation.main.community.other

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import coil.transform.CircleCropTransformation
import com.data.app.BuildConfig
import com.data.app.R
import com.data.app.data.shared_preferences.AppPreferences
import com.data.app.databinding.FragmentOtherProfileBinding
import com.data.app.extension.community.FollowState
import com.data.app.extension.community.GetUserPostState
import com.data.app.extension.community.LikePostState
import com.data.app.extension.my.MyProfileState
import com.data.app.extension.my.SharePostState
import com.data.app.extension.my.ShareProfileState
import com.data.app.extension.my.UserProfileState
import com.data.app.presentation.main.MainViewModel
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class OtherProfileFragment : Fragment() {
    private var _binding: FragmentOtherProfileBinding? = null
    private val binding: FragmentOtherProfileBinding
        get() = requireNotNull(_binding) { "home fragment is null" }

    private val otherProfileFragmentArgs: OtherProfileFragmentArgs by navArgs()
    private val otherProfileViewModel: OtherProfileViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var otherProfileAdapter: OtherProfileAdapter
    private var isRequesting = false
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    companion object {
        private const val GALLERY_PERMISSION_CODE = 101
    }


    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 갤러리에서 이미지 선택하기 위한 launcher
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val selectedImageUri = result.data?.data
                    selectedImageUri?.let { uri ->
                        Timber.d("갤러리에서 선택된 URI: $uri")
                        startCrop(uri)  // 선택된 이미지 크롭 시작
                    }
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOtherProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
    }

    private fun setting() {
        val userId = otherProfileFragmentArgs.userId.toInt()
        showProfile(userId)
        showPosts(userId)
        getFollowState(userId)
        clickBackButton()
    }

    private fun showProfile(userId: Int) {
        lifecycleScope.launch {
            otherProfileViewModel.userProfileState.collect { userProfileState ->
                when (userProfileState) {
                    is UserProfileState.Success -> {
                        Timber.d("userProfileState is success")
                        with(binding) {
                            val profile =
                                userProfileState.response.profileImage
                            ivProfile.load(profile) {
                                transformations(CircleCropTransformation())
                                placeholder(R.drawable.ic_profile)
                                error(R.drawable.ic_profile)
                            }
                            tvName.text = userProfileState.response.name
                            tvCountry.text = userProfileState.response.nationNameKo
                            tvPostCount.text = userProfileState.response.postCount.toString()
                            tvFollowerCount.text =
                                userProfileState.response.followerCount.toString()
                            tvFollowingCount.text =
                                userProfileState.response.followingCount.toString()
                            clickFollow(userId)

                            with(btnFollow) {
                                val loginUserId = appPreferences.getUserId()
                                if (loginUserId == userProfileState.response.userId) {
                                    setBackgroundResource(R.drawable.btn_profile_share)
                                    setTextColor(resources.getColor(R.color.bnv_unclicked_black))
                                    text = getString(R.string.my_profile_edit)
                                    clickEditButton()
                                } else {
                                    setBackgroundResource(R.drawable.btn_other_profile_follow)
                                    Timber.d("isSelected: $isSelected")
                                    if (userProfileState.response.isFollowing) {
                                        isSelected = true
                                        text = getString(R.string.community_following)
                                        setTextColor(resources.getColor(R.color.white))
                                    } else {
                                        isSelected = false
                                        text = getString(R.string.community_follow)
                                        setTextColor(resources.getColor(R.color.bnv_unclicked_black))
                                    }

                                    clickFollowButton(userId)
                                }
                            }

                            btnShare.setOnClickListener {
                                shareProfile(userId)
                            }
                        }
                    }

                    is UserProfileState.Loading -> {
                        Timber.d("userProfileState is loading")
                    }

                    is UserProfileState.Error -> {
                        Timber.d("userProfileState is error")
                    }
                }
            }
        }

        otherProfileViewModel.getUserProfile(appPreferences.getAccessToken()!!, userId)
    }

    private fun clickEditButton() {
        binding.btnFollow.setOnClickListener {
            val currentUserId = appPreferences.getUserId()
            val thisProfileUserId = otherProfileFragmentArgs.userId.toInt()

            if (currentUserId != thisProfileUserId) return@setOnClickListener // 방어 코드

            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }

            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(permission), GALLERY_PERMISSION_CODE)
            } else {
                openGalleryPicker()
            }
        }
    }

    private fun openGalleryPicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    private fun startCrop(uri: Uri) {
        val destinationUri = Uri.fromFile(
            File(
                requireContext().cacheDir,
                "cropped_${System.currentTimeMillis()}.jpg"
            )
        )

        val options = UCrop.Options().apply {
            setCircleDimmedLayer(true)     // ✅ 원형 마스크
            setShowCropFrame(false)        // ❗ 프레임 안 보이게
            setShowCropGrid(false)         // ❗ 그리드 숨기기
            setHideBottomControls(true)    // ✅ 비율 고정이면 하단 버튼 숨기기 좋음
            setToolbarTitle("프로필 사진 자르기")
        }

        // UCrop 실행
        UCrop.of(uri, destinationUri)
            .withAspectRatio(1f, 1f) // ✅ 1:1 비율 고정
            .withOptions(options)
            .start(requireContext(), this)
    }

    private fun showPosts(userId: Int) {
        lifecycleScope.launch {
            otherProfileViewModel.getUserPostState.collect { getUserPostState ->
                when (getUserPostState) {
                    is GetUserPostState.Success -> {
                        Timber.d("otherState is success")
                        otherProfileAdapter = OtherProfileAdapter(
                            clickPost = { userId ->
                                val action =
                                    OtherProfileFragmentDirections.actionOtherProfileFragmentToPostDetailFragment(
                                        userId.toString()
                                    )
                                findNavController().navigate(action)
                            },
                            clickLike = { postId, isLike ->
                                if (isLike) otherProfileViewModel.likePost(
                                    appPreferences.getAccessToken()!!,
                                    postId
                                )
                                else otherProfileViewModel.unLikePost(
                                    appPreferences.getAccessToken()!!,
                                    postId
                                )
                            },
                            clickShare = { postId ->
                                sharePost(postId)
                            })
                        binding.rvPosts.adapter = otherProfileAdapter
                        otherProfileAdapter.getList(getUserPostState.data)
                    }

                    is GetUserPostState.Loading -> {
                        Timber.d("otherState is loading")
                    }

                    is GetUserPostState.Error -> {
                        Timber.d("otherState is error")
                    }
                }
            }
        }
        otherProfileViewModel.getUserPost(appPreferences.getAccessToken()!!, userId)
        setLike()
    }

    private fun getFollowState(userId: Int) {
        lifecycleScope.launch {
            otherProfileViewModel.followState.collect { followState ->
                when (followState) {
                    is FollowState.Success -> {
                        Timber.d("followState is success")
                        //  isRequesting = false
                        otherProfileViewModel.getUserProfile(
                            appPreferences.getAccessToken()!!,
                            userId
                        )
                        otherProfileViewModel.resetFollowState()
                    }

                    is FollowState.Loading -> {
                        Timber.d("followState is loading")
                    }

                    is FollowState.Error -> {
                        Timber.e("followState is error: ${followState.message}")
                    }
                }
            }
        }
    }


    private fun clickFollowButton(userId: Int) {
        with(binding.btnFollow) {
            setOnClickListener {
                //if (isRequesting) return@setOnClickListener

                /*  isRequesting = true
                  isEnabled = false // 버튼 비활성화
  */
                /* with(binding.btnFollow){
                     //isSelected=!isSelected

                     text = context.getString(
                         if (isSelected) R.string.community_following
                         else R.string.community_follow
                     )
                     setTextColor(
                         ContextCompat.getColor(
                             context,
                             if (isSelected)R.color.bnv_unclicked_black else R.color.white
                         )
                     )
                     //isEnabled = true
                 }*/

                if (isSelected) {
                    otherProfileViewModel.unFollow(
                        appPreferences.getAccessToken()!!,
                        userId
                    )
                } else otherProfileViewModel.follow(appPreferences.getAccessToken()!!, userId)

                //

            }
        }
    }

    private fun clickFollow(userId: Int) {
        listOf(
            binding.vFollower to "follower",
            binding.vFollowing to "following"
        ).forEach { (view, title) ->
            view.setOnClickListener {
                val action =
                    OtherProfileFragmentDirections.actionOtherProfileFragmentToFollowFragment(
                        title,
                        userId.toString()
                    )
                findNavController().navigate(action)
            }
        }
    }

    private fun setLike() {
        lifecycleScope.launch {
            otherProfileViewModel.likePostState.collect {
                when (it) {
                    is LikePostState.Success -> {
                        Timber.d("like post state success!")
                        otherProfileViewModel.resetLikeState()
                    }

                    is LikePostState.Loading -> {}
                    is LikePostState.Error -> {
                        Timber.e("like post state error!")
                    }
                }

            }
        }
    }

    private fun shareProfile(userId: Int) {
        lifecycleScope.launch {
            otherProfileViewModel.shareProfileState.collect { state ->
                when (state) {
                    is ShareProfileState.Loading -> {
                        Timber.d("share profile loading...")
                    }

                    is ShareProfileState.Success -> {
                        val url = BuildConfig.BASE_URL.removeSuffix("/") + state.response.shareUrl
                        copyToClipboard(url)
                        this.cancel() // 종료
                    }

                    is ShareProfileState.Error -> {
                        Timber.e("share profile error!")
                        this.cancel() // 종료
                    }
                }
            }
        }

        otherProfileViewModel.shareProfile(userId)
    }

    private fun sharePost(postId: Int) {
        lifecycleScope.launch {
            otherProfileViewModel.sharePostState.collect { state ->
                when (state) {
                    is SharePostState.Loading -> {
                        Timber.d("share post loading...")
                    }

                    is SharePostState.Success -> {
                        val url = BuildConfig.BASE_URL.removeSuffix("/") + state.response.shareUrl
                        copyToClipboard(url)
                        this.cancel() // 종료
                    }

                    is SharePostState.Error -> {
                        Timber.e("share post error!")
                        this.cancel() // 종료
                    }
                }
            }
        }

        otherProfileViewModel.sharePost(postId)
    }

    private fun copyToClipboard(text: String) {
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Profile URL", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), "링크가 복사되었습니다.", Toast.LENGTH_SHORT).show()
    }


    private fun clickBackButton() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}