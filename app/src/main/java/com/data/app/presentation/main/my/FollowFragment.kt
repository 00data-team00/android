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
import com.data.app.data.response_dto.community.ResponseFollowersDto
import com.data.app.databinding.FragmentFollowBinding
import com.data.app.extension.community.FollowerState
import com.data.app.presentation.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class FollowFragment : Fragment() {
    private var _binding: FragmentFollowBinding? = null
    private val binding: FragmentFollowBinding
        get() = requireNotNull(_binding) { "follow fragment is null" }

    private val followFragmentArgs: FollowFragmentArgs by navArgs()
    private val followViewModel: FollowViewModel by viewModels()
    private val mainViewModel : MainViewModel by activityViewModels()
    private lateinit var followAdapter: FollowAdapter

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
        val token=mainViewModel.accessToken.value
        if (token != null) {
            followViewModel.saveToken(token)
            Timber.d("token: $token")
        }

        val title = followFragmentArgs.title
        binding.tvTitle.text = (if(title=="follower") getString(R.string.follow_list_follower) else getString(R.string.follow_list_following))

        lifecycleScope.launch {
            followViewModel.followerState.collect{state->
                when(state){
                    is FollowerState.Success->{
                        followAdapter = FollowAdapter(clickProfile = { userId ->
                            val action =
                                FollowFragmentDirections.actionFollowFragmentToOtherProfileFragment(userId.toString())
                            findNavController().navigate(action)
                        })
                        binding.rvFollowList.adapter = followAdapter
                        binding.rvFollowList.itemAnimator = null
                        followAdapter.submitList(
                            if (title == "follower") {
                                state.response.messages
                            }
                            else state.response.messages
                        )
                        searchList(title, state.response.messages)
                    }
                    is FollowerState.Loading->{
                        Timber.d("follower state loading")
                    }
                    is FollowerState.Error->{
                        Timber.e("setChats start chat state is error!!")
                    }
                }
            }
        }

        followViewModel.getFollowers()
        clickBackButton()
    }

    private fun searchList(title:String, followlist:List<ResponseFollowersDto.Follower>){
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