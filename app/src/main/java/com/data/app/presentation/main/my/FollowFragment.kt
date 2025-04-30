package com.data.app.presentation.main.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.data.app.R
import com.data.app.databinding.FragmentFollowBinding
import com.data.app.databinding.FragmentOtherProfileBinding
import timber.log.Timber

class FollowFragment : Fragment() {
    private var _binding: FragmentFollowBinding? = null
    private val binding: FragmentFollowBinding
        get() = requireNotNull(_binding) { "follow fragment is null" }

    private val followFragmentArgs: FollowFragmentArgs by navArgs()
    private val followViewModel: FollowViewModel by viewModels()
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
        val title = followFragmentArgs.title
        binding.tvTitle.text = (if(title=="follower") getString(R.string.follow_list_follower) else getString(R.string.follow_list_following))

        followAdapter = FollowAdapter(clickProfile = { profile, name ->
            val action =
                FollowFragmentDirections.actionFollowFragmentToOtherProfileFragment(profile, name)
            findNavController().navigate(action)
        })
        binding.rvFollowList.adapter = followAdapter
        binding.rvFollowList.itemAnimator = null
        followAdapter.submitList(
            if (title == "follower") {
                followViewModel.followerList
            }
            else followViewModel.followingList
        )

        searchList(title)
        clickBackButton()
    }

    private fun searchList(title:String){
        binding.etSearch.doOnTextChanged{ text, _, _, _ ->
            val keyword = text.toString().trim()

            Timber.d("keyword: $keyword")

            val originList = if (title == "follower") {
                followViewModel.followerList
            } else {
                followViewModel.followingList
            }

            if (keyword.isEmpty()) {
                followAdapter.submitList(originList)
            } else {
                val filteredList = originList.filter {
                    it.name.contains(keyword, ignoreCase = true) || it.id.contains(keyword, ignoreCase = true)
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