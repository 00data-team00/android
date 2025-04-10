package com.data.app.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.data.app.R
import com.data.app.databinding.FragmentHomeBinding

class HomeFragment:Fragment() {
    private var _binding:FragmentHomeBinding?=null
    private val binding:FragmentHomeBinding
        get() = requireNotNull(_binding){"home fragment is null"}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
    }

    private fun setting(){
        inputMockData()
    }

    private fun inputMockData(){
        with(binding){
            tvTitleKor.text=getString(R.string.home_title_kor, "구구")
            tvTitleEng.text=getString(R.string.home_title_eng, "GooGoo")
            tvNotGiveUp.text=getString(R.string.home_not_give_up, "구구")
            tvContinueStudy.text=getString(R.string.home_continue_study, 13)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}