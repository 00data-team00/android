package com.data.app.presentation.main.my

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil3.load
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import com.data.app.R
import com.data.app.databinding.FragmentMyBinding
import com.data.app.extension.MyState
import kotlinx.coroutines.launch
import timber.log.Timber

class MyFragment:Fragment() {
    private var _binding: FragmentMyBinding? = null
    private val binding: FragmentMyBinding
        get() = requireNotNull(_binding) { "home fragment is null" }

    private val myViewModel: MyViewModel by viewModels()
    private lateinit var myAdapter: _root_ide_package_.com.data.app.presentation.main.my.MyAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
    }

    private fun setting(){
        showPosts()
        makeList()
        clickFollow()
    }

    private fun showPosts(){
        lifecycleScope.launch {
            myViewModel.myState.collect{myState->
                when(myState){
                    is MyState.Success->{
                        Timber.d("myState is success")
                        showProfile(myState.response[0].profile, myState.response[0].id)
                        myAdapter=
                            _root_ide_package_.com.data.app.presentation.main.my.MyAdapter(clickPost = { post ->
                                val action =
                                    MyFragmentDirections.actionMyFragmentToMyPostDetailFragment(post)
                                findNavController().navigate(action)
                            })
                        binding.rvPosts.adapter=myAdapter
                        myAdapter.getList(myState.response)
                    }
                    is MyState.Loading->{
                        Timber.d("myState is loading")
                    }
                    is MyState.Error ->{
                        Timber.d("myState is error")
                    }
                }
            }
        }

        binding.tvId.text="kkuming"
    }

    private fun showProfile(profile: Int, name: String) {
        with(binding){
            ivProfile.load(profile){
                transformations(CircleCropTransformation())
            }
            tvName.text=name
            tvCountry.text="한국"
            tvPostCount.text="4"
            tvFollowerCount.text="60"
            tvFollowingCount.text="60"
        }
    }

    private fun makeList() {
       myViewModel.getPosts()
    }

    private fun clickFollow(){
        listOf(
            binding.vFollower to "follower",
            binding.vFollowing to "following"
        ).forEach { (view, title) ->
            view.setOnClickListener {
                val action=MyFragmentDirections.actionMyFragmentToFollowFragment(title)
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}