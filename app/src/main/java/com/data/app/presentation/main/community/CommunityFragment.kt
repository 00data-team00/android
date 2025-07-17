package com.data.app.presentation.main.community

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.data.app.BuildConfig
import com.data.app.R
import com.data.app.data.response_dto.community.ResponseTimeLineDto
import com.data.app.data.response_dto.explore.ResponseAllProgramDto
import com.data.app.data.shared_preferences.AppPreferences
import com.data.app.databinding.FragmentCommunityBinding
import com.data.app.extension.community.GetAllTimeLineState
import com.data.app.extension.community.LikePostState
import com.data.app.extension.main.GetIdFromTokenState
import com.data.app.extension.my.SharePostState
import com.data.app.presentation.main.MainViewModel
import com.data.app.presentation.main.OnTabReselectedListener
import com.data.app.presentation.main.community.write.WritePostActivity
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class CommunityFragment : Fragment(), OnTabReselectedListener {
    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding is null")
    //private val args: CommunityFragmentArgs by navArgs()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val communityViewModel: CommunityViewModel by viewModels()
    private lateinit var postsAdapter: PostsAdapter
    private var afterWrite = false


    @Inject
    lateinit var appPreferences: AppPreferences

    private val writePostLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            afterWrite = true
            when (communityViewModel.selectedTab.value) {
                CommunityViewModel.CommunityTab.ALL ->
                    communityViewModel.getAllTimeLine(appPreferences.getAccessToken()!!)

                CommunityViewModel.CommunityTab.FOLLOWING ->
                    communityViewModel.getFollowingTimeLine(appPreferences.getAccessToken()!!)

                CommunityViewModel.CommunityTab.COUNTRY ->
                    communityViewModel.getNationTimeLine(appPreferences.getAccessToken()!!)
            }
        }
    }

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
        if (checkAndNavigateToProfile()) return

        showFeeds()
        writePost()
        refresh()
    }

    private fun checkAndNavigateToProfile(): Boolean {
        val state = mainViewModel.getIdFromTokenState.value
        if (!communityViewModel.alreadyNavigated && state is GetIdFromTokenState.Success) {
            communityViewModel.alreadyNavigated = true

            val contentId = state.response.contentId
            Timber.d("✅ contentId: $contentId")

            val navHost = requireActivity()
                .supportFragmentManager
                .findFragmentByTag("tab_${R.id.menu_community}") as? NavHostFragment

            val navController = navHost?.navController

            val action = CommunityFragmentDirections
                .actionCommunityFragmentToOtherProfileFragment(contentId.toString())

            navController?.navigate(action)

            return true
        }

        /*if (!alreadyNavigated && state is GetIdFromTokenState.Success) {
            alreadyNavigated = true

            val contentId = state.response.contentId
            Timber.d("contentId: ${contentId}")

            val navController = findNavController()
            Timber.d("📦 Graph ID: ${navController.graph.id}, Current Dest ID: ${navController.currentDestination?.id}")

            val action = CommunityFragmentDirections
                .actionCommunityFragmentToOtherProfileFragment(contentId.toString())
            findNavController().navigate(action)

            return true
        }*/

        return false
    }

    private fun refresh(){
        binding.btnRefresh.setOnClickListener{
            when (communityViewModel.selectedTab.value) {
                CommunityViewModel.CommunityTab.ALL ->
                    communityViewModel.getAllTimeLine(appPreferences.getAccessToken()!!)

                CommunityViewModel.CommunityTab.FOLLOWING ->
                    communityViewModel.getFollowingTimeLine(appPreferences.getAccessToken()!!)

                CommunityViewModel.CommunityTab.COUNTRY ->
                    communityViewModel.getNationTimeLine(appPreferences.getAccessToken()!!)
            }
        }
    }


    private fun showFeeds() {
        postsAdapter = PostsAdapter(
            clickPost = { post ->
                val action = CommunityFragmentDirections
                    .actionCommunityFragmentToPostDetailFragment(post.toString())
                findNavController().navigate(action)
            },
            clickOtherUser = { userId ->
                val action = CommunityFragmentDirections
                    .actionCommunityFragmentToOtherProfileFragment(userId.toString())
                findNavController().navigate(action)
            },
            clickLikeBtn = { postId, isLike ->
                clickLikeBtn(postId, isLike)
            },
            clickShareBtn = {postId ->
                sharePost(postId)
            }
        )
        binding.rvPosts.adapter = postsAdapter
        postsAdapter.setLoading(true)

        lifecycleScope.launch {
            communityViewModel.getAllTimeLineState.collect { state ->
                when (state) {
                    is GetAllTimeLineState.Success -> {
                        binding.ivNointernet.visibility = View.GONE
                        binding.tvNointernet.visibility = View.GONE
                        binding.btnRefresh.visibility = View.GONE
                        binding.btnWritePost.visibility = View.VISIBLE
                        binding.rvPosts.visibility = View.VISIBLE

                        var filteredList = state.data.filter { it.post.authorName != "탈퇴한 사용자" }

                        // 2. 필요한 처리
                        searchList(filteredList)

                        val keyword = binding.etSearch.text.toString().trim()

                        if (keyword.isNotEmpty()){
                            filteredList = filteredList.filter {
                                it.post.content.contains(keyword, ignoreCase = true)
                            }
                        }

                        postsAdapter.setLoading(false)
                        postsAdapter.getList(filteredList) // 필터링된 리스트 전달

                        /*searchList(state.data)*/
                        /*postsAdapter.setLoading(false)
                        postsAdapter.getList(state.data)*/
                        //binding.rvPosts.scrollToPosition(0)

                        // 이전 스크롤 위치 가져옴
                        if (communityViewModel.recyclerViewState != null) {
                            binding.rvPosts.layoutManager?.onRestoreInstanceState(communityViewModel.recyclerViewState)
                            communityViewModel.recyclerViewState = null // 재사용 방지
                        }

                        if(afterWrite){
                            binding.rvPosts.scrollToPosition(0) // RecyclerView 맨 위로
                            afterWrite = false
                        }
                        communityViewModel.resetTimeLineState()
                    }

                    is GetAllTimeLineState.Error -> {
                        Timber.e("get time line state error!")
                        if(state.message.contains("No address")) {
                            binding.ivNointernet.visibility = View.VISIBLE
                            binding.tvNointernet.visibility = View.VISIBLE
                            binding.btnRefresh.visibility = View.VISIBLE
                            binding.btnWritePost.visibility = View.GONE
                            binding.rvPosts.visibility = View.GONE
                        }
                    }

                    is GetAllTimeLineState.Loading -> {
                        Timber.d("get time line state loading")
                    }
                }
            }
        }

        setupTabs()
    }

    private fun clickLikeBtn(postId: Int, isLike: Boolean) {
        lifecycleScope.launch {
            communityViewModel.likePostState.collect { state ->
                when (state) {
                    is LikePostState.Success -> {
                        Timber.d("like post state success!")
                        communityViewModel.resetLikeState()
                    }

                    is LikePostState.Error -> {
                        Timber.e("like post state error!")
                    }

                    is LikePostState.Loading -> {
                        Timber.d("like post state loading")
                    }
                }
            }
        }

        if (isLike) communityViewModel.likePost(appPreferences.getAccessToken()!!, postId)
        else communityViewModel.unLikePost(appPreferences.getAccessToken()!!, postId)

    }

    private fun setupTabs() {
        // communityViewModel.getAllTimeLine(appPreferences.getAccessToken()!!)

        val currentTab = when (communityViewModel.selectedTab.value) {
            CommunityViewModel.CommunityTab.ALL -> 0
            CommunityViewModel.CommunityTab.FOLLOWING -> 1
            CommunityViewModel.CommunityTab.COUNTRY -> 2
        }

        when (communityViewModel.selectedTab.value) {
            CommunityViewModel.CommunityTab.ALL ->
                communityViewModel.getAllTimeLine(appPreferences.getAccessToken()!!)

            CommunityViewModel.CommunityTab.FOLLOWING ->
                communityViewModel.getFollowingTimeLine(appPreferences.getAccessToken()!!)

            CommunityViewModel.CommunityTab.COUNTRY ->
                communityViewModel.getNationTimeLine(appPreferences.getAccessToken()!!)
        }

        binding.tlCommunity.apply {
            addTab(newTab().setText(getString(R.string.community_all)))
            addTab(newTab().setText(getString(R.string.community_following)))
            addTab(newTab().setText(getString(R.string.community_user_country)))
            getTabAt(currentTab)?.select()  //  이전 탭 복원
        }

        applyTabMargins()

        binding.tlCommunity.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        communityViewModel.getAllTimeLine(appPreferences.getAccessToken()!!)
                        communityViewModel.selectTab(CommunityViewModel.CommunityTab.ALL)
                    }

                    1 -> {
                        communityViewModel.getFollowingTimeLine(appPreferences.getAccessToken()!!)
                        communityViewModel.selectTab(CommunityViewModel.CommunityTab.FOLLOWING)
                    }

                    2 -> {
                        communityViewModel.getNationTimeLine(appPreferences.getAccessToken()!!)
                        communityViewModel.selectTab(CommunityViewModel.CommunityTab.COUNTRY)
                    }
                }
                //binding.rvPosts.scrollToPosition(0)
                Timber.d("tab position: ${tab?.position}")
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {
                //if(communityViewModel.shouldScrollToTop) binding.rvPosts.scrollToPosition(0)
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

    private fun writePost() {
        binding.btnWritePost.setOnClickListener {
            val intent = Intent(requireActivity(), WritePostActivity::class.java)
            //intent.putExtra("accessToken", token)
            writePostLauncher.launch(intent)
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

    private fun searchList(followlist: List<ResponseTimeLineDto.TimelinePostItem>){
        binding.etSearch.doOnTextChanged{ text, _, _, _ ->
            val keyword = text.toString().trim()

            Timber.d("keyword: $keyword")

            if (keyword.isEmpty()) {
                postsAdapter.updateList(followlist)
            } else {
                val filteredList = followlist.filter {
                    it.post.content.contains(keyword, ignoreCase = true)
                }
                postsAdapter.updateList(filteredList)
            }
        }
    }

    private fun sharePost(postId: Int) {
        lifecycleScope.launch {
            communityViewModel.sharePostState.collect { state ->
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

        communityViewModel.sharePost(postId)
    }

    private fun copyToClipboard(text: String) {
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Profile URL", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), "링크가 복사되었습니다.", Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        // 스크롤 위치 저장
        communityViewModel.recyclerViewState = binding.rvPosts.layoutManager?.onSaveInstanceState()
    }

    override fun onResume() {
        super.onResume()
        /* binding.rvPosts.smoothScrollToPosition(0)

         when (communityViewModel.selectedTab.value) {
             CommunityViewModel.CommunityTab.ALL ->
                 communityViewModel.getAllTimeLine(appPreferences.getAccessToken()!!)
             CommunityViewModel.CommunityTab.FOLLOWING ->
                 communityViewModel.getFollowingTimeLine(appPreferences.getAccessToken()!!)
             CommunityViewModel.CommunityTab.COUNTRY ->
                 communityViewModel.getNationTimeLine(appPreferences.getAccessToken()!!)
         }*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onTabReselected() {
        binding.rvPosts.smoothScrollToPosition(0)

        when (communityViewModel.selectedTab.value) {
            CommunityViewModel.CommunityTab.ALL ->
                communityViewModel.getAllTimeLine(appPreferences.getAccessToken()!!)

            CommunityViewModel.CommunityTab.FOLLOWING ->
                communityViewModel.getFollowingTimeLine(appPreferences.getAccessToken()!!)

            CommunityViewModel.CommunityTab.COUNTRY ->
                communityViewModel.getNationTimeLine(appPreferences.getAccessToken()!!)
        }
    }
}