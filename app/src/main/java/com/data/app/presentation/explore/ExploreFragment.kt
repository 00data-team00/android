package com.data.app.presentation.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.data.app.R
import com.data.app.databinding.FragmentExploreBinding
import com.data.app.presentation.OnTabReselectedListener

class ExploreFragment : Fragment(), OnTabReselectedListener {
    private var _binding: FragmentExploreBinding? = null
    private val binding: FragmentExploreBinding
        get() = requireNotNull(_binding) { "home fragment is null" }

    private val exploreViewModel: ExploreViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
    }

    private fun setting() {
        getDeadLineList()
        clickPriceButton()
    }

    private fun getDeadLineList() {
        val deadlineAdapter = ExploreDeadLineAdapter()
        binding.rvDeadline.adapter = deadlineAdapter
        deadlineAdapter.getList(exploreViewModel.mockDeadlineList)
    }

    private fun clickPriceButton(){
        val programAdapter = ExploreProgramAdapter()
        binding.rvAllProgram.adapter=programAdapter
        programAdapter.getList(exploreViewModel.freeProgramList)
        with(binding){
            btnFree.isSelected=true
            btnFree.setTextColor(getResources().getColor(R.color.white))

            btnFree.setOnClickListener{
                btnFree.isSelected=true
                btnFree.setTextColor(getResources().getColor(R.color.white))
                btnPaid.isSelected=false
                btnPaid.setTextColor(getResources().getColor(R.color.mock_ai_practice_title_gray))
                programAdapter.getList(exploreViewModel.freeProgramList)
            }
            btnPaid.setOnClickListener{
                btnFree.isSelected=false
                btnFree.setTextColor(getResources().getColor(R.color.mock_ai_practice_title_gray))
                btnPaid.isSelected=true
                btnPaid.setTextColor(getResources().getColor(R.color.white))
                programAdapter.getList(exploreViewModel.paidProgramList)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onTabReselected() {
        binding.nsvExplore.smoothScrollTo(0,0)
    }
}