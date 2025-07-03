package com.data.app.presentation.main.community.other

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
import com.data.app.extension.community.GetUserPostState
import com.data.app.extension.my.UserProfileState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class OtherProfileFragment : Fragment() {
    private var _binding: FragmentOtherProfileBinding? = null
    private val binding: FragmentOtherProfileBinding
        get() = requireNotNull(_binding) { "home fragment is null" }

    private val otherProfileFragmentArgs: OtherProfileFragmentArgs by navArgs()
    private val otherProfileViewModel: OtherProfileViewModel by viewModels()
    private lateinit var otherProfileAdapter: OtherProfileAdapter

    @Inject
    lateinit var appPreferences: AppPreferences

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
        clickFollowButton()
        clickFollow()
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
                                userProfileState.response.profileImage?.let { BuildConfig.BASE_URL.removeSuffix("/") + it }
                            ivProfile.load(profile) {
                                transformations(CircleCropTransformation())
                                placeholder(R.drawable.ic_profile)
                                error(R.drawable.ic_profile)
                            }
                            tvName.text = userProfileState.response.name
                            tvCountry.text = userProfileState.response.nationNameKo
                            tvPostCount.text = userProfileState.response.postCount.toString()
                            tvFollowerCount.text = userProfileState.response.followerCount.toString()
                            tvFollowingCount.text = userProfileState.response.followingCount.toString()
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

    private fun showPosts(userId: Int) {
        lifecycleScope.launch {
            otherProfileViewModel.getUserPostState.collect { getUserPostState ->
                when (getUserPostState) {
                    is GetUserPostState.Success -> {
                        Timber.d("otherState is success")
                        otherProfileAdapter = OtherProfileAdapter(clickPost = { userId ->
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
    private fun clickFollowButton() {
        with(binding.btnFollow) {
            setOnClickListener {
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