package com.data.app.presentation.main.community.other

import android.content.Intent
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
import coil3.load
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import com.data.app.R
import com.data.app.databinding.FragmentOtherProfileBinding
import com.data.app.extension.OtherState
import com.data.app.presentation.main.my.FollowFragment
import com.data.app.presentation.main.my.MyFragmentDirections
import kotlinx.coroutines.launch
import timber.log.Timber

class OtherProfileFragment : Fragment() {
    private var _binding: FragmentOtherProfileBinding? = null
    private val binding: FragmentOtherProfileBinding
        get() = requireNotNull(_binding) { "home fragment is null" }

    private val otherProfileFragmentArgs: OtherProfileFragmentArgs by navArgs()
    private val otherProfileViewModel: OtherProfileViewModel by viewModels()
    private lateinit var otherProfileAdapter: OtherProfileAdapter

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
        showProfile(profile, name)
        showPosts()
        makeList(profile, name)
        clickFollowButton()
        clickFollow()
        clickBackButton()
    }

    private fun showProfile(profile: Int, name: String) {
        with(binding) {
            ivProfile.load(profile) {
                transformations(CircleCropTransformation())
            }
            tvName.text = name
            tvCountry.text = getString(R.string.other_profile_country_mock)
            tvPostCount.text = "4"
            tvFollowerCount.text = "50"
            tvFollowingCount.text = "50"
        }
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

    private fun makeList(profile: Int, name: String) {
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
                val action = OtherProfileFragmentDirections.actionOtherProfileFragmentToFollowFragment(title)
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