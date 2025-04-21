package com.data.app.presentation.community

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.data.app.R
import com.data.app.databinding.FragmentCommunityBinding
import com.google.android.material.tabs.TabLayout
import timber.log.Timber

class CommunityFragment:Fragment() {
    private var _binding: FragmentCommunityBinding? = null
    private val binding: FragmentCommunityBinding
        get() = requireNotNull(_binding) { "home fragment is null" }

    private val communityViewModel:CommunityViewModel by viewModels()
    private lateinit var postsAdapter:PostsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
    }

    private fun setting() {
        showFeeds()
    }

    private fun showFeeds(){
        postsAdapter= PostsAdapter(
            clickPost = {post->
                val action=CommunityFragmentDirections
                    .actionCommunityFragmentToPostDetailFragment(post)
                findNavController().navigate(action)
            }
        )
        binding.rvPosts.adapter=postsAdapter
        postsAdapter.getList(communityViewModel.allFeeds)

        setupTabs()

    }

    private fun setupTabs(){
        binding.tlCommunity.apply {
            addTab(newTab().setText(getString(R.string.community_all)))
            addTab(newTab().setText(getString(R.string.community_following)))
            addTab(newTab().setText(getString(R.string.community_user_country)))
        }

        applyTabMargins()

        binding.tlCommunity.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> postsAdapter.getList(communityViewModel.allFeeds)
                    1 -> postsAdapter.getList(communityViewModel.followFeeds)
                    2 -> postsAdapter.getList(communityViewModel.countryFeeds)
                }
                binding.rvPosts.scrollToPosition(0)
                Timber.d("tab position: ${tab?.position}")
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {
                binding.rvPosts.scrollToPosition(0)
            }
        })
    }

    private fun applyTabMargins() {
        binding.tlCommunity.post {
            val tabLayout = binding.tlCommunity.getChildAt(0) as? ViewGroup ?: return@post
            for (i in 0 until tabLayout.childCount) {
                val tab = tabLayout.getChildAt(i)
                val layoutParams = tab.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.marginStart = 6.dpToPx()
                layoutParams.marginEnd = 8.dpToPx()
                tab.layoutParams = layoutParams
                tab.requestLayout()
            }
        }
    }

    private fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}