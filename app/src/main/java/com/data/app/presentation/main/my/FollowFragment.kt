package com.data.app.presentation.main.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.data.app.R
import com.data.app.data.response_dto.community.ResponseFollowListDto
import com.data.app.data.shared_preferences.AppPreferences
import com.data.app.databinding.FragmentFollowBinding
import com.data.app.extension.community.FollowListState
import com.data.app.extension.community.FollowState
import com.data.app.presentation.main.MainViewModel
import com.data.app.presentation.main.community.CommunityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class FollowFragment : Fragment() {
    private var _binding: FragmentFollowBinding? = null
    private val binding: FragmentFollowBinding
        get() = requireNotNull(_binding) { "follow fragment is null" }

    private val followFragmentArgs: FollowFragmentArgs by navArgs()
    private val followViewModel: FollowViewModel by viewModels()
    private val mainViewModel : MainViewModel by activityViewModels()
    private lateinit var followAdapter: FollowAdapter

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFollowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
    }

    private fun setting() {
       /* val token=mainViewModel.accessToken.value
        if (token != null) {
            followViewModel.saveToken(token)
            Timber.d("token: $token")
        }*/

        setFollow()

        val title = followFragmentArgs.title
        binding.tvTitle.text = (if(title=="follower") getString(R.string.follow_list_follower) else getString(R.string.follow_list_following))

        followAdapter = FollowAdapter(clickProfile = { userId ->
            val action =
                FollowFragmentDirections.actionFollowFragmentToOtherProfileFragment(userId.toString())
            findNavController().navigate(action)
        },
            clickFollow = {isFollow, userId ->
                if(isFollow) followViewModel.follow(appPreferences.getAccessToken()!!, userId)
                else followViewModel.unFollow(appPreferences.getAccessToken()!!, userId)
        })
        binding.rvFollowList.adapter = followAdapter
        binding.rvFollowList.itemAnimator = null

        lifecycleScope.launch {
            followViewModel.followListState.collect{state->
                when(state){
                    is FollowListState.Success->{
                        binding.ivNointernet.visibility = View.GONE
                        binding.tvNointernet.visibility = View.GONE
                        binding.btnRefresh.visibility = View.GONE
                        binding.rvFollowList.visibility = View.VISIBLE

                        followAdapter.submitList(
                            if (title == "follower") {
                                state.response.messages
                            }
                            else state.response.messages
                        )
                        searchList(title, state.response.messages)
                    }
                    is FollowListState.Loading->{
                        Timber.d("follower state loading")
                        binding.ivNointernet.visibility = View.VISIBLE
                        binding.tvNointernet.visibility = View.VISIBLE
                        binding.btnRefresh.visibility = View.VISIBLE
                        binding.rvFollowList.visibility = View.GONE
                    }
                    is FollowListState.Error->{
                        Timber.e("setChats start chat state is error!!")
                        if(state.message.contains("No address")) {
                            binding.ivNointernet.visibility = View.VISIBLE
                            binding.tvNointernet.visibility = View.VISIBLE
                            binding.btnRefresh.visibility = View.VISIBLE
                            binding.rvFollowList.visibility = View.GONE
                        }
                    }
                }
            }
        }

        val userId = followFragmentArgs.userId.toInt()
        if(title=="follower") followViewModel.getFollowers(appPreferences.getAccessToken()!!, userId)
        else if(title=="following") followViewModel.getFollowing(appPreferences.getAccessToken()!!, userId)
        clickBackButton()

        binding.btnRefresh.setOnClickListener{
            if(title=="follower") followViewModel.getFollowers(appPreferences.getAccessToken()!!, userId)
            else if(title=="following") followViewModel.getFollowing(appPreferences.getAccessToken()!!, userId)
        }
    }

    private fun setFollow(){
        lifecycleScope.launch {
            followViewModel.followState.collect{
                when(it){
                    is FollowState.Success->{
                        followViewModel.resetFollowState()
                        followAdapter.resetBtn()
                    }
                    is FollowState.Loading->{
                        Timber.d("follower state loading")
                    }
                    is FollowState.Error->{
                        Timber.e("setChats start chat state is error!!")
                    }
                }
            }
        }
    }

    private fun searchList(title:String, followlist:List<ResponseFollowListDto.Follower>){
        binding.etSearch.doOnTextChanged{ text, _, _, _ ->
            val keyword = text.toString().trim()

            Timber.d("keyword: $keyword")

            if (keyword.isEmpty()) {
                followAdapter.submitList(followlist)
            } else {
                val filteredList = followlist.filter {
                    it.name.contains(keyword, ignoreCase = true)
                }
                followAdapter.submitList(filteredList)
            }
        }
    }

    private fun clickBackButton() {
        binding.btnBack.setOnClickListener {
            findNavController().previousBackStackEntry
                ?.savedStateHandle
                ?.set("should_refresh_profile", true)

            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().previousBackStackEntry
                ?.savedStateHandle
                ?.set("should_refresh_profile", true)

            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}