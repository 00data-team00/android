package com.data.app.presentation.main.community

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.data.app.R
import com.data.app.data.shared_preferences.AppPreferences
import com.data.app.databinding.FragmentCommunityBinding
import com.data.app.extension.community.GetAllTimeLineState
import com.data.app.presentation.main.MainViewModel
import com.data.app.presentation.main.OnTabReselectedListener
import com.data.app.presentation.main.community.write.WritePostActivity
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class CommunityFragment:Fragment(), OnTabReselectedListener {
    private var _binding: FragmentCommunityBinding? = null
    private val binding: FragmentCommunityBinding
        get() = requireNotNull(_binding) { "home fragment is null" }

    private val mainViewModel:MainViewModel by activityViewModels()
    private val communityViewModel:CommunityViewModel by viewModels()
    private lateinit var postsAdapter:PostsAdapter

    @Inject
    lateinit var appPreferences: AppPreferences

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
        writePost()
    }

    private fun showFeeds(){
        lifecycleScope.launch {
            communityViewModel.getAllTimeLineState.collect { state ->
                when (state) {
                    is GetAllTimeLineState.Success -> {
                        postsAdapter= PostsAdapter(
                            clickPost = {post->
                                val action=CommunityFragmentDirections
                                    .actionCommunityFragmentToPostDetailFragment(post.toString())
                                findNavController().navigate(action)
                            },
                            clickOtherUser = {userId->
                                val action=CommunityFragmentDirections
                                    .actionCommunityFragmentToOtherProfileFragment(userId.toString())
                                findNavController().navigate(action)
                            }
                        )
                        binding.rvPosts.adapter=postsAdapter
                        postsAdapter.getList(state.data)
                        communityViewModel.resetTimeLineState()
                    }

                    is GetAllTimeLineState.Error -> {
                        Timber.e("get time line state error!")
                    }

                    is GetAllTimeLineState.Loading -> {
                        Timber.d("get time line state loading")
                    }
                }
            }
        }

        setupTabs()
    }

    private fun setupTabs(){
        communityViewModel.getAllTimeLine(appPreferences.getAccessToken()!!)

        binding.tlCommunity.apply {
            addTab(newTab().setText(getString(R.string.community_all)))
            addTab(newTab().setText(getString(R.string.community_following)))
            addTab(newTab().setText(getString(R.string.community_user_country)))
        }

        applyTabMargins()

        binding.tlCommunity.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> communityViewModel.getAllTimeLine(appPreferences.getAccessToken()!!)
                    1 -> communityViewModel.getFollowingTimeLine(appPreferences.getAccessToken()!!)
                    2 -> communityViewModel.getNationTimeLine(appPreferences.getAccessToken()!!)
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

    private fun writePost(){
        binding.btnWritePost.setOnClickListener{
            val intent=Intent(requireActivity(), WritePostActivity::class.java)
            //intent.putExtra("accessToken", token)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
       /* lifecycleScope.launch {
            mainViewModel.accessToken.observe(viewLifecycleOwner){token->
                binding.btnWritePost.setOnClickListener{
                    val intent=Intent(requireActivity(), WritePostActivity::class.java)
                    intent.putExtra("accessToken", token)
                    startActivity(intent)
                    requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
                }
            }
        }*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun onTabReselected() {
        binding.rvPosts.smoothScrollToPosition(0)

    }
}