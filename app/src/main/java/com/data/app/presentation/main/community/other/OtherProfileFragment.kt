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
import com.data.app.R
import com.data.app.data.shared_preferences.AppPreferences
import com.data.app.databinding.FragmentOtherProfileBinding
import com.data.app.extension.community.OtherState
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
        val profile = otherProfileFragmentArgs.otherProfile
        val name = otherProfileFragmentArgs.otherName
        showProfile()
        showPosts()
        makeList(profile.toString(), name)
        clickFollowButton()
        clickFollow()
        clickBackButton()
    }

    private fun showProfile() {
        lifecycleScope.launch {
            otherProfileViewModel.userProfileState.collect { userProfileState ->
                when (userProfileState) {
                    is UserProfileState.Success -> {
                        Timber.d("userProfileState is success")
                        with(binding) {
                            ivProfile.load(userProfileState.response.profileImage) {
                                transformations(CircleCropTransformation())
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

        otherProfileViewModel.getUserProfile(appPreferences.getAccessToken()!!, 1)


    }

    private fun showPosts() {
        lifecycleScope.launch {
            otherProfileViewModel.otherState.collect { otherState ->
                when (otherState) {
                    is OtherState.Success -> {
                        Timber.d("otherState is success")
                        otherProfileAdapter = OtherProfileAdapter(clickPost = { post ->
                            val action =
                                OtherProfileFragmentDirections.actionOtherProfileFragmentToPostDetailFragment(
                                    post
                                )
                            findNavController().navigate(action)
                        })
                        binding.rvPosts.adapter = otherProfileAdapter
                        otherProfileAdapter.getList(otherState.response)
                    }

                    is OtherState.Loading -> {
                        Timber.d("otherState is loading")
                    }

                    is OtherState.Error -> {
                        Timber.d("otherState is error")
                    }
                }
            }
        }
    }

    private fun makeList(profile: String, name: String) {
        otherProfileViewModel.getOtherProfile(profile, name)
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