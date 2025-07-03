package com.data.app.presentation.main.community.other

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.data.app.extension.my.UserProfileState
import com.data.app.presentation.main.MainViewModel
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
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
        getFollowState()
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
                                userProfileState.response.profileImage?.let {
                                    BuildConfig.BASE_URL.removeSuffix(
                                        "/"
                                    ) + it
                                }
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

                            with(btnFollow) {
                                if (mainViewModel.getUserId() == userProfileState.response.userId) {
                                    setBackgroundResource(R.drawable.btn_profile_share)
                                    setTextColor(resources.getColor(R.color.bnv_unclicked_black))
                                    text = getString(R.string.my_profile_edit)

                                    clickFollow()
                                    clickEditButton()
                                } else {
                                    setBackgroundResource(R.drawable.btn_follow)
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

    private fun clickEditButton(){
        binding.btnFollow.setOnClickListener {
            val currentUserId = mainViewModel.getUserId()
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
    }

    private fun getFollowState() {
        lifecycleScope.launch {
            otherProfileViewModel.followState.collect { followState ->
                when (followState) {
                    is FollowState.Success -> {
                        Timber.d("followState is success")
                        isRequesting=false
                        binding.btnFollow.isEnabled=true
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
                if (isRequesting) return@setOnClickListener

                isRequesting = true
                isEnabled = false // 버튼 비활성화
                isSelected = !isSelected

                text = context.getString(
                    if (isSelected) R.string.community_follow
                    else R.string.community_following
                )
                setTextColor(
                    ContextCompat.getColor(
                        context,
                        if (isSelected) R.color.white else R.color.bnv_unclicked_black
                    )
                )

                if (isSelected) otherProfileViewModel.follow(
                    appPreferences.getAccessToken()!!,
                    userId
                )
                else otherProfileViewModel.unFollow(appPreferences.getAccessToken()!!, userId)


            }
        }
    }

    private fun clickFollow() {
        listOf(
            binding.vFollower to "follower",
            binding.vFollowing to "following"
        ).forEach { (view, title) ->
            view.setOnClickListener {
                val action =
                    OtherProfileFragmentDirections.actionOtherProfileFragmentToFollowFragment(title)
                findNavController().navigate(action)
            }
        }
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